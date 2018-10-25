package org.springframework.cloud.servicebroker.exception;

/**
 * Thrown to indicate that request is invalid. 400 response code should be returned.
 */
public class ServiceBrokerBadRequestException extends RuntimeException {

	private static final long serialVersionUID = 4719676639792071582L;

	public ServiceBrokerBadRequestException(String message) {
		super(message);
	}

	public ServiceBrokerBadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceBrokerBadRequestException(Throwable cause) {
		super(cause);
	}

}
