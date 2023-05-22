FROM ubuntu:22.04

RUN apt-get update && apt-get -y upgrade && \
    apt-get install -y curl && \
    apt-get install -y zip && \
    apt-get install -y openjdk-17-jre && \
    apt-get clean

WORKDIR /agent

ENV ARCH="amd64"
ENV GCLOUD_HOSTED_METRICS_URL="https://prometheus-your-instance-us-east-0.grafana.net/api/prom/push"
ENV GCLOUD_HOSTED_METRICS_ID="your-id"
ENV GCLOUD_SCRAPE_INTERVAL="60s"
ENV GCLOUD_HOSTED_LOGS_URL="https://logs-your-instance.grafana.net/loki/api/v1/push"
ENV GCLOUD_HOSTED_LOGS_ID="your-id"
ENV GCLOUD_RW_API_KEY="your-key"
COPY ./grafana-agent.yaml ./grafana-agent.yaml
COPY ./grafana-agent.sh ./grafana-agent.sh
RUN chmod 755 ./grafana-agent.sh
RUN ./grafana-agent.sh
WORKDIR /app
COPY ./build/libs/hsp-grafana-sample-0.0.1-SNAPSHOT.jar ./hsp-grafana-sample.jar
WORKDIR /
COPY ./startup.sh ./startup.sh
RUN chmod 755 ./startup.sh
ENTRYPOINT [ "./startup.sh"]

