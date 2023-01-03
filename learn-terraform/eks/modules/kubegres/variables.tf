variable "namespace" {
  description = "Kubernetes namespace"
  type        = string
}

variable "pg_admin_pwd" {
  description = "PostgreSQL super user password"
  type        = string
  sensitive   = true
}

variable "pg_repl_admin_pwd" {
  description = "PostgreSQL replication super user password"
  type        = string
  sensitive   = true
}