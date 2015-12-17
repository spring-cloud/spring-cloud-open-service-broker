package org.springframework.cloud.servicebroker.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a response that support asynchronous behavior.
 *
 * @author Scott Frederick
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class AsyncServiceInstanceResponse {
	/**
	 * Indicates whether the request to the service broker is complete. A <code>false</code> value indicates that the
	 * request was completed, a <code>true</code> value indicates that the broker is processing the request
	 * asynchronously.
	 */
	protected final boolean async;

	public AsyncServiceInstanceResponse(boolean async) {
		this.async = async;
	}
}
