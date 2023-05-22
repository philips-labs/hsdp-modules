#!/bin/bash
# Update the agent with hostname
HSCLOUD_APP_HOST=${HOSTNAME:=}
sed -i -e "s~{GCLOUD_RW_API_KEY}~${GCLOUD_RW_API_KEY}~g" ./agent/grafana-agent.yaml
sed -i -e "s~{GCLOUD_HOSTED_METRICS_URL}~${GCLOUD_HOSTED_METRICS_URL}~g" ./agent/grafana-agent.yaml
sed -i -e "s~{GCLOUD_HOSTED_METRICS_ID}~${GCLOUD_HOSTED_METRICS_ID}~g" ./agent/grafana-agent.yaml
sed -i -e "s~{GCLOUD_SCRAPE_INTERVAL}~${GCLOUD_SCRAPE_INTERVAL}~g" ./agent/grafana-agent.yaml
sed -i -e "s~{GCLOUD_HOSTED_LOGS_URL}~${GCLOUD_HOSTED_LOGS_URL}~g" ./agent/grafana-agent.yaml
sed -i -e "s~{GCLOUD_HOSTED_LOGS_ID}~${GCLOUD_HOSTED_LOGS_ID}~g" ./agent/grafana-agent.yaml
sed -i -e "s~{HSCLOUD_APP_HOST}~${HSCLOUD_APP_HOST}~g" ./agent/grafana-agent.yaml

# Start the first process
./agent/grafana-agent-linux-${ARCH} --config.file=./agent/grafana-agent.yaml &
  
# Start the second process
java -jar  ./app/hsp-grafana-sample.jar
