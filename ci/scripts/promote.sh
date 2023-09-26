#!/bin/bash
set -euo pipefail

pushd artifactory-repo > /dev/null
version=$( get_revision_from_buildinfo )
popd > /dev/null

readonly BUILD_INFO_LOCATION="$(pwd)/artifactory-repo/build-info.json"

java -jar /concourse-release-scripts.jar promote "$RELEASE_TYPE" "$BUILD_INFO_LOCATION"

echo "Promotion complete"
echo "$version" > version/version
