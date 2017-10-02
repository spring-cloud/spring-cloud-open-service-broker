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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GetLastServiceOperationRequest extends ServiceBrokerRequest {
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
	 * The field optionally returned by the service broker on async provision, update, deprovision responses.
	 * Represents any state the service broker responded with as a URL encoded string. Can be <code>null</code>
	 * to indicate that an operation state is not provided.
	 */
	protected String operation;

	public GetLastServiceOperationRequest(String instanceId) {
		this.serviceInstanceId = instanceId;
		this.serviceDefinitionId = null;
		this.planId = null;
	}

	public GetLastServiceOperationRequest(String instanceId, String serviceId, String planId, String operation) {
		this.serviceInstanceId = instanceId;
		this.serviceDefinitionId = serviceId;
		this.planId = planId;
		this.operation = operation;
	}

	public GetLastServiceOperationRequest withCfInstanceId(String cfInstanceId) {
		this.cfInstanceId = cfInstanceId;
		return this;
	}

	public GetLastServiceOperationRequest withApiInfoLocation(String apiInfoLocation) {
		this.apiInfoLocation = apiInfoLocation;
		return this;
	}

	public GetLastServiceOperationRequest withOriginatingIdentity(Context originatingIdentity) {
		this.originatingIdentity = originatingIdentity;
		return this;
	}
}
