variable "root_org_id" {
  description = "My org id"
  type        = string
  sensitive   = false
}

variable "client_id" {
  description = "OAuth client id for my org"
  type        = string
  sensitive   = true
}

variable "client_secret" {
  description = "OAuth client secret for my org"
  type        = string
  sensitive   = true
}

variable "admin_username" {
  description = "Admin username for my org"
  type        = string
  sensitive   = false
}

variable "admin_password" {
  description = "Admin password for my org"
  type        = string
  sensitive   = true
}

variable "cdr" {
  description = "CDR base url"
  type        = string
  sensitive   = false
}

variable "inference" {
  description = "AI inference base url"
  type        = string
  sensitive   = false
}

variable "training" {
  description = "AI training base url"
  type        = string
  sensitive   = false
}

variable "iam" {
  description = "IAM base url"
  type        = string
  sensitive   = false
}

variable "sagemaker_bucket" {
  description = "S3 bucket for Sagemaker"
  type        = string
  sensitive   = false
}

variable "project" {
  description = "Name of the project to run"
  type        = string
  default     = "HspHeartDemo"
}

variable "image" {
  description = "Name of the project to run"
  type        = string
  default     = "sagemaker-hsp_heart_demo"
}

