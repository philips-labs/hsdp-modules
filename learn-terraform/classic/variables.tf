variable "vault_service" {
  description = "CF vault service name"
  type        = string
  default     = "hsdp-vault"
}

variable "vault_service_plan" {
  description = "CF vault service plan"
  type        = string
  default     = "vault-us-east-1"
}

variable "cf_api_url" {
  description = "CF API url"
  type        = string
  default     = "https://api.cloud.pcftest.com"
}

variable "cf_org" {
  description = "CF organization"
  type        = string
}

variable "cf_space" {
  description = "CF space"
  type        = string
}

variable "cf_user_password" {
  description = "CF login user password"
  type        = string
  sensitive   = true
}

variable "cf_user_username" {
  description = "CF username"
  type        = string
}

variable "cf_client_secret" {
  description = "CF uaa client secret"
  type        = string
  sensitive   = true
}

variable "cf_client_id" {
  description = "CF uaa client id"
  type        = string
}

variable "my_vault" {
  description = "My vault service name"
  type        = string
  default     = "tutorial-vault"
}