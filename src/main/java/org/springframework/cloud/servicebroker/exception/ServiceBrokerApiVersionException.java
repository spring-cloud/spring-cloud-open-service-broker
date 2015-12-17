package org.springframework.cloud.servicebroker.exception;

/**
 * Thrown to indicate that the version of the API supported by the broker doesn't match the version
 * in a request.
 */
public class ServiceBrokerApiVersionException extends RuntimeException {

	private static final long serialVersionUID = -6792404679608443775L;

	public ServiceBrokerApiVersionException(String expectedVersion, String providedVersion) {
		super("The provided service broker API version is not supported: "
				+ "expected version=" + expectedVersion
				+ ", provided version = " + providedVersion);
	}
	
}
