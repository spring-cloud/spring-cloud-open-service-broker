#!/bin/bash

set -euo pipefail

readonly SKIP_TESTS="${SKIP_TESTS:-false}"

# shellcheck source=common.sh
source "$(dirname "$0")/common.sh"
repository=$(pwd)/distribution-repository
if [ "$SKIP_TESTS" == "true" ]; then
	build_task=assemble
else
	build_task=build
fi

pushd git-repo >/dev/null
./gradlew clean "${build_task}" publish \
	-PpublicationRepository="${repository}"
popd >/dev/null
