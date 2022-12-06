terraform {
  required_providers {
    hsdp = {
      source  = "philips-software/hsdp"
      version = ">= 0.37.0"
    }
    restapi = {
      source  = "mastercard/restapi"
      version = ">=1.17.0"
    }
  }
}

provider "hsdp" {
  # Configuration
  region             = "us-east"
  environment        = "client-test"
  oauth2_client_id   = var.client_id
  oauth2_password    = var.client_secret
  org_admin_username = var.admin_username
  org_admin_password = var.admin_password
}

data "hsdp_iam_token" "iam" {
}

locals {
  cdr_patient_observations_url = format("%s/%s/Observation?subject=%s", var.cdr, var.root_org_id, var.patient_id)
  cdr_accept_header = var.fhir_version == "R4" ? "application/fhir+json;fhirVersion=4.0" : "application/fhir+json"
}

resource "null_resource" "observations" {

  provisioner "local-exec" {
    command = "curl --request GET ${local.cdr_patient_observations_url} --header 'API-Version: 1' --header 'Authorization: Bearer ${data.hsdp_iam_token.iam.access_token}' --header 'Accept: ${local.cdr_accept_header}' > fhir_bundle.json"
  }

  provisioner "local-exec" {
    command = "pip install -r requirements.txt"
  }

  provisioner "local-exec" {
    command = "python preprocess.py"
  }
}
