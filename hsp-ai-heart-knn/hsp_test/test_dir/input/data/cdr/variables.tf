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

variable "fhir_version" {
  description = "FHIR version to use"
  type        = string
  sensitive   = false
  default     = "R4"
}

variable "iam" {
  description = "IAM base url"
  type        = string
  sensitive   = false
}
variable "project" {
  description = "Name of the project to run"
  type        = string
  default     = "HspHeartDemo"
}

variable "patient_id" {
  description = "Unique patient id from HSP CDR"
  type        = string
}

