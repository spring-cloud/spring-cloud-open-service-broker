package org.springframework.cloud.servicebroker.model;

/**
 * The list of acceptable values for the <code>requires</code> field of a service definition.
 */
public enum ServiceDefinitionRequires {
	/**
	 * Indicates that the service broker allows Cloud Foundry to stream logs from bound applications to a
	 * service instance. If this permission is provided in a service definition, the broker should provide a
	 * non-null value in the <code>CreateServiceInstanceBindingResponse.syslogDrainUrl</code> field in response
	 * to a bind request.
	 */
	SERVICE_REQUIRES_SYSLOG_DRAIN("syslog_drain"),

	/**
	 * Indicates that the service broker allows Cloud Foundry to bind routes to a service instance. If this permission
	 * is provided in a service definition, the broker may receive bind requests with a <code>route</code> value in
	 * the <code>bindResource</code> field of a <code>CreateServiceInstanceBindingRequest</code>.
	 */
	SERVICE_REQUIRES_ROUTE_FORWARDING("route_forwarding");

	private final String value;

	ServiceDefinitionRequires(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
