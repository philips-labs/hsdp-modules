resource "kubernetes_deployment" "nginx_app" {
  metadata {
    name = "tutorial-app-nginx"
    namespace = var.namespace
    labels = {
      app = "tutorial-app-nginx"
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        app = "tutorial-app-nginx"
      }
    }
    template {
      metadata {
        namespace = var.namespace
        labels = {
          app = "tutorial-app-nginx"
        }
      }
      spec {
        container {
          image = "nginx:1.23.2"
          name  = "nginxdemo"

          port {
            container_port = 80
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "nginx_svc" {
  depends_on = [kubernetes_deployment.nginx_app]

  metadata {
    labels = {
      app = "tutorial-app-nginx"
    }
    name = "tutorial-app-nginx"
    namespace = var.namespace
  }

  spec {
    port {
      name = "api"
      port = 9002
      target_port = 80
    }
    selector = {
      app = "tutorial-app-nginx"
    }
    type = "LoadBalancer"
  }
}
