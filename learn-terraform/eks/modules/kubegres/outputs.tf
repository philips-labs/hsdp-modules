output "postgres_hostname" {
  value = kubernetes_manifest.postgres.manifest.metadata.name
}