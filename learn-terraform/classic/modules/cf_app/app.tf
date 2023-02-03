terraform {
  required_providers {
    cloudfoundry = {
      source  = "cloudfoundry-community/cloudfoundry"
      version = "0.50.0"
    }
  }
}

data "cloudfoundry_service_instance" "psql" {
  name_or_id = "psqldemo"
  space      = var.space_id
}

data "cloudfoundry_domain" "apps" {
  name = "cloud.pcftest.com"
}

resource "cloudfoundry_route" "app_route" {
  domain   = data.cloudfoundry_domain.apps.id
  space    = var.space_id
  hostname = "tutorial-ms1"
}

resource "cloudfoundry_app" "myapp" {
  name = "tutorial-ms1"
  path = "${path.module}/target/microservice1-1.0.0.jar"

  instances = 1
  space     = var.space_id
  memory    = 1000
  stack     = "cflinuxfs3"
  buildpack = "https://github.com/cloudfoundry/java-buildpack"
  service_binding {
    service_instance = var.hsp_vault_service_id
  }
  service_binding {
    service_instance = data.cloudfoundry_service_instance.psql.id
  }
  routes {
    route = cloudfoundry_route.app_route.id
  }
  environment = {
    "SPRING_PROFILES_ACTIVE"  = "cloud"
    "JBP_CONFIG_OPEN_JDK_JRE" = "{ jre: { version: 11.+}}"
  }
}