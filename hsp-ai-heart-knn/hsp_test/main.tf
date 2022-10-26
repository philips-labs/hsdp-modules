terraform {
  required_providers {
    hsdp = {
      source  = "philips-software/hsdp"
      version = ">= 0.37.0"
    }
    aws = {
      source  = "hashicorp/aws"
      version = ">= 4.9.0"
    }
    restapi = {
      source  = "mastercard/restapi"
      version = ">=1.17.0"
    }
    http-full = {
      source  = "salrashid123/http-full"
      version = ">=1.3.0"
    }
  }
}

data "aws_caller_identity" "current" {}

data "aws_region" "current" {}

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

data "hsdp_config" "inference" {
  service = "inference"
}

data "hsdp_ai_inference_service_instance" "inference" {
  base_url        = var.inference
  organization_id = var.root_org_id
}

resource "hsdp_ai_inference_compute_environment" "compute" {
  endpoint = data.hsdp_ai_inference_service_instance.inference.endpoint

  name  = format("%s%s", var.project, "-Env")
  image = "${data.aws_caller_identity.current.account_id}.dkr.ecr.${data.aws_region.current.name}.amazonaws.com/${var.image}:latest"
}

data "hsdp_ai_inference_compute_targets" "targets" {
  endpoint = data.hsdp_ai_inference_service_instance.inference.endpoint
}

locals {
  compute_target_id = tolist(data.hsdp_ai_inference_compute_targets.targets.ids)[0]
}


provider "restapi" {
  # Configuration options
  uri = data.hsdp_ai_inference_service_instance.inference.endpoint
  headers = {
    API-Version   = "1"
    Authorization = "Bearer ${data.hsdp_iam_token.iam.access_token}"
    Content-Type  = "application/json"
  }
  write_returns_object = true
}

resource "restapi_object" "model" {
  path = "/Model"
  data = "{\n  \"resourceType\": \"Model\",\n  \"name\": \"${var.project}-Model\",\n \"version\": \"v1\",\n  \"description\": \"Heart data prediction using scikit learn from HSP\",\n  \"type\": \"service\",\n  \"computeEnvironment\": {\n  \"reference\": \"ComputeEnvironment/${hsdp_ai_inference_compute_environment.compute.id}\"\n  },\n  \"artifactPath\": \"s3://${var.sagemaker_bucket}/${var.project}/model/model.tar.gz\",\n  \"entryCommands\": [\n    \"python serve\"\n  ], \n  \"envVars\": [\n  ],\n  \"labels\": [\n    \"${var.project}\"\n  ]\n}"

  depends_on = [hsdp_ai_inference_compute_environment.compute]
}

resource "restapi_object" "inf_endpoint" {
  path = "/InferenceEndpoint"
  data = "{\n  \"resourceType\": \"InferenceEndpoint\",\n \"name\": \"${var.project}-Endpoint\",\n \"description\": \"Heart job Endpoint Using Hsp Heart Demo\",\n \"type\": \"async\",\n  \"variant\": [\n        {\n\"name\": \"${var.project}\",\n\"model\": [{\n            \"reference\": \"Model/${restapi_object.model.id}\"\n        }],\n\"computeTarget\": {\n            \"reference\": \"ComputeTarget/${local.compute_target_id}\"\n        },\n\"instanceCount\": 1\n        }\n  ],\n  \"labels\": [\n    \"${var.project}\"\n  ]\n}"

  depends_on = [restapi_object.model]
}
resource "time_sleep" "wait_5_minutes" {
  depends_on = [restapi_object.inf_endpoint]

  create_duration = "300s"
}

provider "http-full" {

}

data "http" "inf_endpoint_status" {
  provider = http-full

  url    = format("%s/InferenceEndpoint/%s", data.hsdp_ai_inference_service_instance.inference.endpoint, restapi_object.inf_endpoint.id)
  method = "GET"
  request_headers = {
    Authorization = "Bearer ${data.hsdp_iam_token.iam.access_token}"
    API-Version   = "1"
    Accept        = "application/json"
  }

  depends_on = [time_sleep.wait_5_minutes]
}
locals {
  inf_endpoint_data   = jsondecode(data.http.inf_endpoint_status.body)
  inf_endpoint_status = local.inf_endpoint_data.status
}

resource "restapi_object" "score" {
  path = "/Score"
  data = "{\n  \"resourceType\": \"Score\",\n  \"name\": \"${var.project}-Predict\",\n \"description\": \"Heart job Prediction Using Hsp Heart Demo\",\n  \"type\": \"async\",\n  \"endPoint\": {\n \"reference\": \"InferenceEndpoint/${restapi_object.inf_endpoint.id}\"\n  },\n  \"input\": {\n      \"contentType\": \"text/csv\",\n  \"url\": \"s3://${var.sagemaker_bucket}/${var.project}/input/data/test/payload.csv\"\n  },\n  \"output\": {\n      \"contentType\": \"application/json\",\n \"url\": \"s3://${var.sagemaker_bucket}/${var.project}/output/data/response.json\"\n  },\n  \"labels\": [\n    \"${var.project}\"\n  ]\n}"

  lifecycle {
    precondition {
      condition     = local.inf_endpoint_status == "inservice" || local.inf_endpoint_status == "stopped" || local.inf_endpoint_status == "failed"
      error_message = "Inference endpoint is not in-service. Retry after sometime"
    }
  }
}

resource "time_sleep" "wait_5_seconds" {
  depends_on = [restapi_object.score]

  create_duration = "5s"
}

data "http" "score_results" {
  provider = http-full

  url    = format("%s/Score/%s", data.hsdp_ai_inference_service_instance.inference.endpoint, restapi_object.score.id)
  method = "GET"
  request_headers = {
    Authorization = "Bearer ${data.hsdp_iam_token.iam.access_token}"
    API-Version   = "1"
    Accept        = "application/json"
  }

  depends_on = [time_sleep.wait_5_seconds]
}


locals {
  inf_stop_endpoint = format("%s/InferenceEndpoint/%s/$stop", data.hsdp_ai_inference_service_instance.inference.endpoint, restapi_object.inf_endpoint.id)
  score_data        = jsondecode(data.http.score_results.body)
  score_status      = local.score_data.status
}

output "results_location" {
  value = local.score_data.output.url

  precondition {
    condition     = local.score_status == "completed" || local.score_status == "failed"
    error_message = "Inference prediction is not complete. Retry after sometime"
  }

  depends_on = [time_sleep.wait_5_seconds]
}

resource "null_resource" "download" {
  provisioner "local-exec" {
    command = "aws s3 cp ${local.score_data.output.url} ./test_dir/output/result.json"
  }

  depends_on = [
    data.http.score_results
  ]

  lifecycle {
    precondition {
      condition     = local.score_status == "completed" || local.score_status == "failed"
      error_message = "Inference prediction is not complete. Retry after sometime"
    }
  }
}

resource "time_sleep" "wait_3_seconds" {
  depends_on = [null_resource.download]

  create_duration = "3s"
}

resource "null_resource" "ai_stop_inf_endpoint" {

  provisioner "local-exec" {
    command = "curl --request POST '${local.inf_stop_endpoint}' --header 'API-Version: 1' --header 'Authorization: Bearer ${data.hsdp_iam_token.iam.access_token}'"
  }

  lifecycle {
    precondition {
      condition     = local.score_status == "completed" || local.score_status == "failed"
      error_message = "Inference prediction is not complete. Retry after sometime"
    }
  }

  depends_on = [
    time_sleep.wait_3_seconds
  ]
}

data "local_file" "result" {
  filename = "./test_dir/output/result.json"

  depends_on = [
    null_resource.download
  ]
}

output "predict_result" {
  value = data.local_file.result.content
}

