package org.springframework.cloud.servicebroker.exception;

/**
 * Thrown to indicate that a broker requires <code>accepts_incomplete</code> to be <code>true</code> to
 * successfully process a request.
 */
public class ServiceBrokerAsyncRequiredException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiceBrokerAsyncRequiredException(String message) {
		super(message);
	}
}
