terraform {
  required_providers {
    cloudfoundry = {
      source  = "cloudfoundry-community/cloudfoundry"
      version = "0.50.0"
    }
  }
}

provider "cloudfoundry" {
  # Configuration options
  api_url             = var.cf_api_url
  user                = var.cf_user_username
  password            = var.cf_user_password
  uaa_client_id       = var.cf_client_id
  uaa_client_secret   = var.cf_client_secret
  skip_ssl_validation = true
  app_logs_max        = 30
}

data "cloudfoundry_org" "my_org" {
  name = var.cf_org
}

data "cloudfoundry_space" "my_space" {
  name = var.cf_space
  org  = data.cloudfoundry_org.my_org.id
}

data "cloudfoundry_service" "vault" {
  name = var.vault_service
}

resource "cloudfoundry_service_instance" "myvault" {
  name         = var.my_vault
  space        = data.cloudfoundry_space.my_space.id
  service_plan = data.cloudfoundry_service.vault.service_plans["${var.vault_service_plan}"]
}

resource "cloudfoundry_service_key" "myvault_key" {
  name             = "${var.my_vault}-key"
  service_instance = cloudfoundry_service_instance.myvault.id

  depends_on = [
    cloudfoundry_service_instance.myvault
  ]
}

module "cf_vault" {
  source = "./modules/cf_vault"

  hsp_vault_endpoint  = cloudfoundry_service_key.myvault_key.credentials["endpoint"]
  hsp_vault_role_id   = cloudfoundry_service_key.myvault_key.credentials["role_id"]
  hsp_vault_secret_id = cloudfoundry_service_key.myvault_key.credentials["secret_id"]

  hsp_vault_secret_path = replace("${cloudfoundry_service_key.myvault_key.credentials["service_secret_path"]}/data", "/v1", "")
  hsp_vault_data_json   = file("${path.module}/secrets.json")
}

module "cf_app" {
  source = "./modules/cf_app"

  space_id             = data.cloudfoundry_space.my_space.id
  hsp_vault_service_id = cloudfoundry_service_instance.myvault.id
}