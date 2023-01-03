resource "kubernetes_secret" "hsp_docker_secret" {
  metadata {
    name      = "hsp-docker-cfg"
    namespace = var.namespace
  }

  type = "kubernetes.io/dockerconfigjson"

  data = {
    ".dockerconfigjson" = jsonencode({
      auths = {
        "${var.registry_server}" = {
          "username" = var.registry_username
          "password" = var.registry_password
          "email"    = var.registry_email
          "auth"     = base64encode("${var.registry_username}:${var.registry_password}")
        }
      }
    })
  }
}

resource "kubernetes_ingress_v1" "app_ingress" {
  metadata {
    name = "tutorial-demo-app"
    namespace = var.namespace
     annotations = {
      "nginx.ingress.kubernetes.io/ssl-redirect" : "true"
      "nginx.ingress.kubernetes.io/force-ssl-redirect" : "true"
      "nginx.ingress.kubernetes.io/rewrite-target" : "/"
    }
 }

  spec {
    ingress_class_name = "nginx"

    default_backend {
    service {
      name = kubernetes_service.demo_app_svc.metadata.0.name
      port {
        number = 9080
      }
    }
  }

   rule {
      host = "ms1demo.us-east-1-07ad.f10325bd5163481d.hsp.philips.com"
      http {
        path {
          path = "/"
          backend {
            service {
              name = kubernetes_service.demo_app_svc.metadata.0.name
              port {
                number = 9080
              }
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "demo_app_svc" {
  metadata {
    name      = "tutorial-demo-app"
    namespace = var.namespace
  }

  spec {
    selector = {
      app = kubernetes_deployment.demo_app.metadata.0.labels.app
    }
    session_affinity = "ClientIP"
    port {
      port        = 9080
      target_port = 8080
    }
    # type = "LoadBalancer"
  }
}

resource "kubernetes_deployment" "demo_app" {
  metadata {
    name      = "tutorial-demo-app"
    namespace = var.namespace
    labels = {
      app = "tutorial-demo-app"
    }
  }

  spec {
    replicas = 2

    selector {
      match_labels = {
        app = "tutorial-demo-app"
      }
    }

    template {
      metadata {
        labels = {
          app = "tutorial-demo-app"
        }
      }

      spec {
        container {
          image = "docker.na1.hsdp.io/demo/ms1demo:v2.0.0"
          name  = "ms1demo"
          resources {
            limits = {
              cpu    = "1.0"
              memory = "1000Mi"
            }
            requests = {
              cpu    = "500m"
              memory = "500Mi"
            }
          }
          env {
            name = "POSTGRES_PASSWORD"
            value_from {
              secret_key_ref {
                name = "postgres-secret"
                key  = "superUserPassword"
              }
            }
          }
          args = [
            "--spring.datasource.url=jdbc:postgresql://${var.pg_hostname}:5432/postgres",
            "--spring.datasource.username=postgres",
            "--spring.datasource.password=$(POSTGRES_PASSWORD)",
            "--spring.datasource.hostname=${var.pg_hostname}"
          ]
        }
        image_pull_secrets {
          name = kubernetes_secret.hsp_docker_secret.metadata.0.name
        }
      }
    }
  }
}