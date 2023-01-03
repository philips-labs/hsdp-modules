output "my_org" {
  value = data.cloudfoundry_org.my_org.id
}

output "my_space" {
  value = data.cloudfoundry_space.my_space.id
}

output "vault_service_plans" {
  value = data.cloudfoundry_service.vault.service_plans
}

output "my_vault_service" {
  value = cloudfoundry_service_instance.myvault.id
}

output "my_vault_credentials" {
  value = cloudfoundry_service_key.myvault_key.credentials
}