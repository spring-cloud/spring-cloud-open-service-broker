FROM harbor-repo.vmware.com/dockerhub-proxy-cache/library/ubuntu:jammy

ARG CONCOURSE_JAVA_SCRIPTS_VERSION=0.0.4
ARG CONCOURSE_RELEASE_SCRIPTS_VERSION=0.3.4

RUN apt-get update && \
    apt-get install --no-install-recommends -y \
    	ca-certificates \
    	curl \
    	git \
    	gnupg \
    	jq \
    	net-tools \
        openjdk-17-jdk-headless && \
    apt-get clean

ADD "https://raw.githubusercontent.com/spring-io/concourse-java-scripts/v$CONCOURSE_JAVA_SCRIPTS_VERSION/concourse-java.sh" /opt/
