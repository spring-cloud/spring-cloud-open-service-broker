package org.springframework.cloud.servicebroker.exception;

/**
 * Thrown to indicate that a request is received for an unknown service instance binding ID.
 */
public class ServiceInstanceBindingDoesNotExistException extends RuntimeException {

	private static final long serialVersionUID = -1879753092397657116L;

	public ServiceInstanceBindingDoesNotExistException(String bindingId) {
		super("Service binding does not exist: id=" + bindingId);
	}

}
