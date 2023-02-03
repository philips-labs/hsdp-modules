resource "null_resource" "kubectl_kubegres" {
  provisioner "local-exec" {
    command = "kubectl apply -f https://raw.githubusercontent.com/reactive-tech/kubegres/v1.16/kubegres.yaml"
    interpreter = ["/bin/bash", "-c"]
  }
}

resource "kubernetes_secret" "postgres_secrets" {
  metadata {
    name      = "postgres-secret"
    namespace = var.namespace
  }
  type = "Opaque"
  data = {
    superUserPassword = var.pg_admin_pwd
    replicationUserPassword = var.pg_repl_admin_pwd
  }
}

resource "kubernetes_manifest" "postgres" {
  manifest = {
    apiVersion = "kubegres.reactive-tech.io/v1"
    kind = "Kubegres"
    metadata = {
      name = "tutorial-postgres"
      namespace = var.namespace
    }

  spec = {
    replicas = "3"
    image: "postgres:14.6"

    database = {
      size: "200Mi"
    }

    env = [{
     name = "POSTGRES_PASSWORD"
     valueFrom = {
	secretKeyRef = {
	  name = "postgres-secret"
	  key = "superUserPassword"
	}
     }
    },
    {
     name = "POSTGRES_REPLICATION_PASSWORD"
     valueFrom = {
        secretKeyRef = {
          name = "postgres-secret"
          key = "replicationUserPassword"
        }
     }
    }]
  }
  }
}