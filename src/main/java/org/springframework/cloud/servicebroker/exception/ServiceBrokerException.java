package org.springframework.cloud.servicebroker.exception;

/**
 * General exception for underlying broker errors (like connectivity to the service
 * being brokered).
 * 
 * @author sgreenberg@pivotal.io
 *
 */
public class ServiceBrokerException extends RuntimeException {

	private static final long serialVersionUID = -5544859893499349135L;

	public ServiceBrokerException(String message) {
		super(message);
	}

	public ServiceBrokerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceBrokerException(Throwable cause) {
		super(cause);
	}

}