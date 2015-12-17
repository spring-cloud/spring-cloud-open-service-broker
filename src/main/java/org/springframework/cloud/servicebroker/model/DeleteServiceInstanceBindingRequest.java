package org.springframework.cloud.servicebroker.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 *  Details of a request to delete a service instance binding.
 *
 * @author krujos
 * @author Scott Frederick
 */
@Getter
@ToString
@EqualsAndHashCode
public class DeleteServiceInstanceBindingRequest {

	/**
	 * The Cloud Controller GUID of the service instance to being unbound.
	 */
	private final String serviceInstanceId;

	/**
	 * The Cloud Controller GUID of the service binding being deleted.
	 */
	private final String bindingId;

	private final String serviceDefinitionId;
	private final String planId;
	private transient final ServiceDefinition serviceDefinition;

	public DeleteServiceInstanceBindingRequest(String serviceInstanceId, String bindingId,
											   String serviceDefinitionId, String planId,
											   ServiceDefinition serviceDefinition) {
		this.serviceInstanceId = serviceInstanceId;
		this.bindingId = bindingId;
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.serviceDefinition = serviceDefinition;
	}
}
