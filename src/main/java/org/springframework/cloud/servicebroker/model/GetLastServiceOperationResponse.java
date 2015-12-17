package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a response to a request to get the state of the last operation on a service instance.
 *
 * @author Scott Frederick
 */
@Getter
@ToString
@EqualsAndHashCode
@JsonAutoDetect
public class GetLastServiceOperationResponse {
	/**
	 * The current state of the asynchronous request.
	 */
	private final OperationState state;

	/**
	 * A user-facing message displayed to the Cloud Controller API client. Can be used to tell the user details
	 * about the status of the operation. Can be <code>null</code>.
	 */
	@JsonSerialize
	private final String description;

	/**
	 * Should be set to <code>true</code> in response to a request for the status of an asynchronous delete request,
	 * and <code>false</code> otherwise.
	 */
	private final boolean deleteOperation;

	public GetLastServiceOperationResponse(final OperationState operationState, final String description,
										   final boolean deleteOperation) {
		this.state = operationState;
		this.description = description;
		this.deleteOperation = deleteOperation;
	}
	
	public GetLastServiceOperationResponse(final OperationState operationState, final String description) {
		this(operationState, description, false);
	}

	public GetLastServiceOperationResponse(final OperationState operationState) {
		this(operationState, null, false);
	}

	@JsonSerialize
	@JsonProperty("state")
	public String getStateValue() {
		return state.getValue();
	}
}
