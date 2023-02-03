terraform {
  required_providers {
    vault = {
      source  = "hashicorp/vault"
      version = "3.8.2"
    }
  }
}

provider "vault" {
  # Configuration options
  address          = var.hsp_vault_endpoint
  skip_child_token = true
  auth_login {
    path = "auth/approle/login"

    parameters = {
      role_id   = var.hsp_vault_role_id
      secret_id = var.hsp_vault_secret_id
    }
  }
}

resource "vault_generic_secret" "data" {
  path = var.hsp_vault_secret_path

  data_json = var.hsp_vault_data_json
}
