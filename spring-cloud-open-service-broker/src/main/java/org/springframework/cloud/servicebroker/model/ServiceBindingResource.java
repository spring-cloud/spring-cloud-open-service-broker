package org.springframework.cloud.servicebroker.model;

/**
 * The list of acceptable keys for the <code>bindResource</code> map in a service instance binding request.
 */
public enum ServiceBindingResource {
	BIND_RESOURCE_KEY_APP("app_guid"),
	BIND_RESOURCE_KEY_ROUTE("route");

	private final String value;

	ServiceBindingResource(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
