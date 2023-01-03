resource "null_resource" "kubectl_kubegres" {
  provisioner "local-exec" {
    command     = "kubectl apply -f https://raw.githubusercontent.com/reactive-tech/kubegres/v1.16/kubegres.yaml"
    interpreter = ["/bin/bash", "-c"]
  }
}

# locals {
#   registry_server   = "https://index.docker.io/v1/"
#   registry_username = "pshinoj"
#   registry_password = "5LccS!P4gEdJKc@"
#   registry_email    = "shinoj.prabhakaran@gmail.com"
# }

# resource "kubernetes_secret" "hub_docker_secret" {
#   metadata {
#     name      = "hub-docker-cfg"
#     namespace = var.namespace
#   }

#   type = "kubernetes.io/dockerconfigjson"

#   data = {
#     ".dockerconfigjson" = jsonencode({
#       auths = {
#         "${local.registry_server}" = {
#           "username" = local.registry_username
#           "password" = local.registry_password
#           "email"    = local.registry_email
#           "auth"     = base64encode("${local.registry_username}:${local.registry_password}")
#         }
#       }
#     })
#   }
# }

resource "kubernetes_secret" "postgres_secrets" {
  metadata {
    name      = "postgres-secret"
    namespace = var.namespace
  }
  type = "Opaque"
  data = {
    superUserPassword       = var.pg_admin_pwd
    replicationUserPassword = var.pg_repl_admin_pwd
  }
}

resource "kubernetes_manifest" "postgres" {
  manifest = {
    apiVersion = "kubegres.reactive-tech.io/v1"
    kind       = "Kubegres"
    metadata = {
      name      = "tutorial-postgres"
      namespace = var.namespace
    }

    spec = {
      replicas = "3"
      image : "postgres:14.6"

      database = {
        size : "200Mi"
      }

      resources = {
        limits = {
          cpu    = "1.0"
          memory = "500Mi"
        }
        requests = {
          cpu    = "500m"
          memory = "250Mi"
        }
      }

     env = [{
        name = "POSTGRES_PASSWORD"
        valueFrom = {
          secretKeyRef = {
            name = "postgres-secret"
            key  = "superUserPassword"
          }
        }
        },
        {
          name = "POSTGRES_REPLICATION_PASSWORD"
          valueFrom = {
            secretKeyRef = {
              name = "postgres-secret"
              key  = "replicationUserPassword"
            }
          }
      }]
    }
  }
}