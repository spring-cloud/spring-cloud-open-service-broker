/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model;

/**
 * The list of acceptable states for a request that is being processed asynchronously.
 */
public enum OperationState {
	/**
	 * Indicates that a request is still being processed. Cloud Foundry will continue
	 * polling for the current state.
	 */
	IN_PROGRESS("in progress"),

	/**
	 * Indicates that a request completed successfully. Cloud Foundry will stop polling
	 * for the current state.
	 */
	SUCCEEDED("succeeded"),

	/**
	 * Indicates that a request completed unsuccessfully. Cloud Foundry will stop polling
	 * for the current state.
	 */
	FAILED("failed");

	private final String state;

	OperationState(String state) {
		this.state = state;
	}

	public String getValue() {
		return state;
	}

	@Override
	public String toString() {
		return state;
	}
}
