#!/bin/sh

set -eu

export TERM="xterm-256color"

readonly DOCKER_CONFIG_OUTPUT="${DOCKER_CONFIG_OUTPUT:?must be set}"

# Note that we don't use libs.sh here because that script requires bash, and
# we don't have bash in this container image

printf "%s" "$REGISTRY_PASSWORD" | docker login "$REGISTRY" --username "$REGISTRY_USERNAME" --password-stdin
cp -v ~/.docker/config.json "$DOCKER_CONFIG_OUTPUT/"
