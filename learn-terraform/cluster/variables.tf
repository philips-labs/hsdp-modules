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
