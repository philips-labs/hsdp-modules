terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "2.16.0"
    }
  }
}

provider "kubernetes" {
  # Configuration options
  config_path = "~/.kube/config"
}

resource "kubernetes_namespace" "tutorial" {
  metadata {
    annotations = {
      name = "tutorial"
    }

    labels = {
      mylabel = "tutorial"
    }

    name = "tutorial"
  }
}

module "postgres_db" {
  source = "./modules/kubegres"

  namespace         = kubernetes_namespace.tutorial.metadata.0.name
  pg_admin_pwd      = var.pg_admin_pwd
  pg_repl_admin_pwd = var.pg_repl_admin_pwd

  depends_on = [kubernetes_namespace.tutorial]
}

module "demo_app" {
  source = "./modules/app"

  namespace         = kubernetes_namespace.tutorial.metadata.0.name
  pg_hostname       = module.postgres_db.postgres_hostname
  registry_server   = var.registry_server
  registry_email    = var.registry_email
  registry_username = var.registry_username
  registry_password = var.registry_password

  depends_on = [module.postgres_db]
}