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

	/**
	 * The ID of the service to deprovision, from the broker catalog.
	 */
	private final String serviceDefinitionId;

	/**
	 * The ID of the plan to deprovision within the service, from the broker catalog.
	 */
	private final String planId;

	/**
	 * The field optionally returned by the service broker on Provision, Update, Deprovision async responses.
	 * Represents any state the service broker responsed with as a URL encoded string. Can be <code>null</code>
	 * to indicate that an operation state is not provided.
	 */
	protected String operation;

	public GetLastServiceOperationRequest(String instanceId, String serviceId, String planId) {
		this.serviceInstanceId = instanceId;
		this.serviceDefinitionId = serviceId;
		this.planId = planId;
	}

	public GetLastServiceOperationRequest withOperation(final String operation) {
		this.operation = operation;
		return this;
	}

}
