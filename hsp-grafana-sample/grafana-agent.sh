#!/usr/bin/env sh
# shellcheck shell=dash

# This script should run in all POSIX environments and Dash is POSIX compliant.
#
# grafanacloud-install-linux-other.sh installs the Grafana Agent on supported
# Linux systems for Grafana Cloud users. Those who aren't users of Grafana Cloud
# or need to install the Agent on a different platform should
# try another installation method.


set -eu
trap "exit 1" TERM
MY_PID=$$

  log() {
    echo "$@" >&2
  }

fatal() {
  log "$@"
  kill -s TERM "${MY_PID}"
}

SHA256_SUMS="
# BEGIN_SHA256_SUMS
93aa0c304fef41e9bb7d14167427661fe7b41c07d1f6a70ddff6fb0851f85380  grafana-agent-linux-amd64.zip
fe2b9d19ed89d72d25de8d77017314fa58d3a19bc4d17050b8201dc6d21ce7f2  grafana-agent-linux-arm64.zip
# END_SHA256_SUMS
"


#
# REQUIRED environment variables.
#
ARCH=${ARCH:=}                                             # System architecture
GCLOUD_HOSTED_METRICS_URL=${GCLOUD_HOSTED_METRICS_URL:=}   # Grafana Cloud Hosted Metrics url
GCLOUD_HOSTED_METRICS_ID=${GCLOUD_HOSTED_METRICS_ID:=}     # Grafana Cloud Hosted Metrics Instance ID
GCLOUD_SCRAPE_INTERVAL=${GCLOUD_SCRAPE_INTERVAL:=}       # Grafana Cloud Hosted Metrics scrape interval
GCLOUD_HOSTED_LOGS_URL=${GCLOUD_HOSTED_LOGS_URL:=}         # Grafana Cloud Hosted Logs url
GCLOUD_HOSTED_LOGS_ID=${GCLOUD_HOSTED_LOGS_ID:=}           # Grafana Cloud Hosted Logs Instance ID
GCLOUD_RW_API_KEY=${GCLOUD_RW_API_KEY:=}                   # Grafana Cloud API key

[ -z "${ARCH}" ] && fatal "Required environment variable \$ARCH not set."
[ -z "${GCLOUD_HOSTED_METRICS_URL}" ] && fatal "Required environment variable \$GCLOUD_HOSTED_METRICS_URL not set."
[ -z "${GCLOUD_HOSTED_METRICS_ID}" ]  && fatal "Required environment variable \$GCLOUD_HOSTED_METRICS_ID not set."
[ -z "${GCLOUD_SCRAPE_INTERVAL}" ]  && fatal "Required environment variable \$GCLOUD_SCRAPE_INTERVAL not set."
[ -z "${GCLOUD_HOSTED_LOGS_URL}" ] && fatal "Required environment variable \$GCLOUD_HOSTED_LOGS_URL not set."
[ -z "${GCLOUD_HOSTED_LOGS_ID}" ]  && fatal "Required environment variable \$GCLOUD_HOSTED_LOGS_ID not set."
[ -z "${GCLOUD_RW_API_KEY}" ]  && fatal "Required environment variable \$GCLOUD_RW_API_KEY not set."

#
# OPTIONAL environment variables.
#
#
# Global constants.
#
GRAFANA_AGENT_CONFIG="https://storage.googleapis.com/cloud-onboarding/agent/config/config.yaml"
RELEASE_VERSION="v0.33.1"
RELEASE_URL="https://github.com/grafana/agent/releases/download/${RELEASE_VERSION}"

main() {
  log "--- Downloading Grafana Agent version ${RELEASE_VERSION}"
  download_agent

  # log "--- Retrieving config and placing in './agent-config.yaml'"
  # download_config

  # log "---Starting the Grafana Agent"
  # ./grafana-agent-linux-"${ARCH}" --config.file=grafana-agent.yaml
}

download_agent() {
  ASSET_NAME="grafana-agent-linux-${ARCH}.zip"
  ASSET_URL="${RELEASE_URL}/${ASSET_NAME}"

  curl -O -L "${ASSET_URL}"
  log '---Verifying package checksum'
  check_sha

  unzip "${ASSET_NAME}";
  chmod a+x "${ASSET_NAME}";
}

# download_config downloads the config file for the Agent and replaces
# placeholders with actual values.
download_config() {
  # curl -fsSL "${GRAFANA_AGENT_CONFIG}" -o grafana-agent.yaml || fatal 'Failed to download config'
  sed -i -e "s~{GCLOUD_RW_API_KEY}~${GCLOUD_RW_API_KEY}~g" grafana-agent.yaml
  sed -i -e "s~{GCLOUD_HOSTED_METRICS_URL}~${GCLOUD_HOSTED_METRICS_URL}~g" grafana-agent.yaml
  sed -i -e "s~{GCLOUD_HOSTED_METRICS_ID}~${GCLOUD_HOSTED_METRICS_ID}~g" grafana-agent.yaml
  sed -i -e "s~{GCLOUD_SCRAPE_INTERVAL}~${GCLOUD_SCRAPE_INTERVAL}~g" grafana-agent.yaml
  sed -i -e "s~{GCLOUD_HOSTED_LOGS_URL}~${GCLOUD_HOSTED_LOGS_URL}~g" grafana-agent.yaml
  sed -i -e "s~{GCLOUD_HOSTED_LOGS_ID}~${GCLOUD_HOSTED_LOGS_ID}~g" grafana-agent.yaml
}

check_sha() {
  echo -n "${SHA256_SUMS}" | sha256sum -c - 2>&1 | grep "OK" || fatal 'Failed sha256sum check'
}

main
