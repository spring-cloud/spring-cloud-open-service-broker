package org.springframework.cloud.servicebroker.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a request to delete a service instance.
 *
 * @author krujos
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DeleteServiceInstanceRequest extends AsyncServiceInstanceRequest {
	/**
	 * The Cloud Controller GUID of the service instance to deprovision.
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
	 * The {@link ServiceDefinition} of the service to deprovision. This is resolved from the
	 * <code>serviceDefinitionId</code> as a convenience to the broker.
	 */
	private transient final ServiceDefinition serviceDefinition;

	public DeleteServiceInstanceRequest(String instanceId, String serviceId,
										String planId, ServiceDefinition serviceDefinition,
										boolean asyncAccepted) {
		this.serviceInstanceId = instanceId;
		this.serviceDefinitionId = serviceId;
		this.planId = planId;
		this.serviceDefinition = serviceDefinition;
		this.asyncAccepted = asyncAccepted;
	}

	public DeleteServiceInstanceRequest(String instanceId, String serviceId,
										String planId, ServiceDefinition serviceDefinition) {
		this(instanceId, serviceId, planId, serviceDefinition, false);
	}

	public DeleteServiceInstanceRequest withAsyncAccepted(boolean asyncAccepted) {
		this.asyncAccepted = asyncAccepted;
		return this;
	}
}
