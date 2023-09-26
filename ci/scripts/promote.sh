#!/bin/bash
set -euo pipefail

readonly BUILD_INFO_LOCATION="$(pwd)/artifactory-repo/build-info.json"
readonly VERSION=$( jq -r '.buildInfo.modules[0].id' < "$BUILD_INFO_LOCATION" | sed 's/.*:.*:\(.*\)/\1/' )

java -jar /concourse-release-scripts.jar promote "$RELEASE_TYPE" "$BUILD_INFO_LOCATION"

echo "Promotion complete"
echo "$VERSION" > version/version
