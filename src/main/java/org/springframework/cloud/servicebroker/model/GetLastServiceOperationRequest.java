package org.springframework.cloud.servicebroker.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a request to get the state of the last operation on a service instance.
 *
 * @author Scott Frederick
 */
@Getter
@ToString
@EqualsAndHashCode
public class GetLastServiceOperationRequest {
	/**
	 * The Cloud Controller GUID of the service instance to get the status of.
	 */
	private final String serviceInstanceId;

	public GetLastServiceOperationRequest(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}
}
