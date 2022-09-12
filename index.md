# Useful modules and examples for HSP

Disclaimer: The modules and projects listed here are **not managed service offerings** from HSP, therefore please do **not** open
 ServiceNow tickets if you encounter issues. Instead, raise a Github issue on the respective project. The community is pretty responsive.

## Terraform modules

### Platform service onboarding modules

- [HSP DICOM](https://github.com/philips-labs/terraform-hsdp-dicom)
- [HSP Connect](https://github.com/philips-labs/terraform-hsdp-connect-onboarding)
- [HSP Notification Service](https://github.com/philips-labs/terraform-hsdp-notification)
- [HSP AI Inference](https://github.com/philips-labs/terraform-hsdp-ai-inference-onboarding)

### Cloud foundry modules

- [Thanos](https://github.com/philips-labs/terraform-cloudfoundry-thanos)
- [Grafana](https://github.com/philips-labs/terraform-cloudfoundry-grafana)
- [Alertmanager](https://github.com/philips-labs/terraform-cloudfoundry-alertmanager)
- [Kibana](https://github.com/philips-labs/terraform-cloudfoundry-kibana)
- [Kong](https://github.com/philips-labs/terraform-cloudfoundry-kong)
- [Logproxy](https://github.com/philips-labs/terraform-cloudfoundry-logproxy)
- [Variant](https://github.com/philips-labs/terraform-cloudfoundry-variant)

### Cloud foundry services with Prometheus scrapers built-in

- [terraform-hsdp-redis-service](https://github.com/philips-labs/terraform-hsdp-redis-service)
- [terraform-hsdp-postgres-service](https://github.com/philips-labs/terraform-hsdp-postgres-service)

### Off-the-shelf sofware on Container Host

- [Apache Superset](https://github.com/philips-labs/terraform-hsdp-apache-superset)
- [Apache Kafka](https://github.com/philips-labs/terraform-hsdp-kafka)
- [Apache Nifi](https://github.com/philips-labs/terraform-hsdp-nifi)
- [TimescaleDB](https://github.com/loafoe/terraform-hsdp-timescaledb)

## Example projects

- [Cloud foundry examples](https://github.com/philips-labs/cloudfoundry-examples)
- [API Gateway demo](https://github.com/philips-labs/terraform-cloudfoundry-gwdemo)
 
## Github Actions

- [IAM Service Login](https://github.com/philips-labs/iam-service-login)

## HSDP Function Tasks

- [HTTP Requests](https://github.com/loafoe/hsdp-task-http-request)
- [Redshift Queries](https://github.com/loafoe/hsdp-task-redshift)
- [S3 Bucket mirroring](https://github.com/loafoe/hsdp-task-s3mirror)

## Kong custom plugins

- [Kong IAM introspect plugin](https://github.com/loafoe/kong-plugin-introspect)
- [Kong IAM token injector plugin based on mTLS certificates](https://github.com/loafoe/kong-plugin-mtlsauth)
