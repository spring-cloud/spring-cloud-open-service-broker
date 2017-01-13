package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
	@JsonSerialize(using = ToStringSerializer.class)
	private OperationState state;

	/**
	 * A user-facing message displayed to the Cloud Controller API client. Can be used to tell the user details
	 * about the status of the operation. Can be <code>null</code>.
	 */
	@JsonSerialize
	private String description;

	/**
	 * Should be set to <code>true</code> in response to a request for the status of an asynchronous delete request,
	 * and <code>false</code> otherwise.
	 */
	private boolean deleteOperation;

	public GetLastServiceOperationResponse withOperationState(final OperationState operationState) {
		this.state = operationState;
		return this;
	}

	public GetLastServiceOperationResponse withDescription(final String description) {
		this.description = description;
		return this;
	}

	public GetLastServiceOperationResponse withDeleteOperation(final boolean deleteOperation) {
		this.deleteOperation = deleteOperation;
		return this;
	}
}
