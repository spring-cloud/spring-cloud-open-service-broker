#!/usr/bin/env bash

set -euo pipefail

readonly FLY_TARGET="${FLY_TARGET:-"scosb"}"

set_pipeline() {
	local pipeline_name pipeline_definition branch ci_image_tag
	pipeline_name="${1:?pipeline name must be provided}"
	pipeline_definition="${2:?pipeline definition file must be provided}"
	branch="${3:?branch must be provided}"
	ci_image_tag="${4:-$branch}"

	echo "Setting $pipeline_name $branch pipeline..."
	fly --target "$FLY_TARGET" set-pipeline \
		--pipeline "$pipeline_name" \
		--config "$pipeline_definition" \
		--load-vars-from config-concourse.yml \
		--instance-var "branch=$branch" \
		--var "ci-image-tag=$ci_image_tag"
}

main() {
	# When a branch is retired, remove it from this list and optionally destroy its pipeline instance with:
	#   fly --target "$FLY_TARGET" destroy-pipeline --pipeline scosb --instance-var "branch=<branch>"
	local -r branches=("5.1.x" "5.0.x")

	pushd "$(dirname "$0")/../ci" >/dev/null

	for branch in "${branches[@]}"; do
		set_pipeline scosb pipeline.yml "$branch"
	done

	popd >/dev/null
}

main
