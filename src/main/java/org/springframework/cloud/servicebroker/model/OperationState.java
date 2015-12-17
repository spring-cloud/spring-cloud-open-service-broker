package org.springframework.cloud.servicebroker.model;

/**
 * The list of acceptable states for a request that is being processed asynchronously.
 */
public enum OperationState {
	/**
	 * Indicates that a request is still being processed. Cloud Foundry will continue polling for the current state.
	 */
	IN_PROGRESS("in progress"),

	/**
	 * Indicates that a request completed successfully. Cloud Foundry will stop polling for the current state.
	 */
	SUCCEEDED("succeeded"),

	/**
	 * Indicates that a request completed unsuccessfully. Cloud Foundry will stop polling for the current state.
	 */
	FAILED("failed");

	private final String state;

	OperationState(String state) {
		this.state = state;
	}

	public String getValue() {
		return state;
	}
}
