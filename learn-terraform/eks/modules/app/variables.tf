variable "namespace" {
  description = "Kubernetes namespace"
  type        = string
}

variable "pg_hostname" {
  description = "PostgreSQL master hostname"
  type        = string
}

variable "registry_username" {
  description = "Docker registry user name"
  type        = string
}

variable "registry_password" {
  description = "Docker registry password"
  type        = string
  sensitive   = true
}

variable "registry_email" {
  description = "Docker registry user email"
  type        = string
}

variable "registry_server" {
  description = "Docker registry server"
  type        = string
}
