#!/usr/bin/env bash

set -euo pipefail

readonly FLY_TARGET="scosb"
readonly GITHUB_REPO="https://github.com/spring-cloud/spring-cloud-open-service-broker"

set_pipeline() {
	local pipeline_name pipeline_definition branch ci_image_tag
	pipeline_name="${1:?pipeline name must be provided}"
	pipeline_definition="${2:?pipeline definition file must be provided}"
	branch="${3:?branch must be provided}"
	ci_image_tag="${4:-$branch}"

	echo "Setting $pipeline_name pipeline..."
	fly --target "$FLY_TARGET" set-pipeline \
		--pipeline "$pipeline_name" \
		--config "$pipeline_definition" \
		--load-vars-from config-concourse.yml \
		--var "github-repo=$GITHUB_REPO" \
		--var "branch=$branch" \
		--var "ci-image-tag=$ci_image_tag"
}

main() {
	fly -t scosb sync

	pushd "$(dirname "$0")/../ci" >/dev/null

	set_pipeline scosb-3.6.x pipeline.yml main

	popd >/dev/null
}

main
