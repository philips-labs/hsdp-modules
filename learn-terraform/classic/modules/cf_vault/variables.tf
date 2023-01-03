variable "hsp_vault_role_id" {
  description = "Vault role id"
  type        = string
  sensitive   = true
}

variable "hsp_vault_secret_id" {
  description = "Vault secret id"
  type        = string
  sensitive   = true
}

variable "hsp_vault_endpoint" {
  description = "Vault proxy endpoint"
  type        = string
}

variable "hsp_vault_secret_path" {
  description = "Vault secret path"
  type        = string
}

variable "hsp_vault_data_json" {
  description = "Vault secret data to store"
  type        = string
  sensitive   = true
}
