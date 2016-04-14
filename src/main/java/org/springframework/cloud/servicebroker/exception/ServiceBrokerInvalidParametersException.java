package org.springframework.cloud.servicebroker.exception;

/**
 * Thrown to indicate that parameters passed in a provision or update request are not understood.
 */
public class ServiceBrokerInvalidParametersException extends RuntimeException {

	private static final long serialVersionUID = 4719676639792071582L;

	public ServiceBrokerInvalidParametersException(String message) {
		super(message);
	}

	public ServiceBrokerInvalidParametersException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceBrokerInvalidParametersException(Throwable cause) {
		super(cause);
	}

}
