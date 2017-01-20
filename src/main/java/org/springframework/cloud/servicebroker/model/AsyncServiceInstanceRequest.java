package org.springframework.cloud.servicebroker.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a request that supports asynchronous behavior.
 *
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AsyncServiceInstanceRequest extends ServiceBrokerRequest {
	public final static String ASYNC_REQUEST_PARAMETER = "accepts_incomplete";

	/**
	 * Indicates whether clients of the service broker allow the broker to complete the request asynchronously. A
	 * <code>false</code> value indicates that clients do not allow asynchronous processing of requests, a
	 * <code>true</code> value indicates that clients do allow asynchronous processing.
	 */
	protected boolean asyncAccepted;
	
	public AsyncServiceInstanceRequest() {
	}
}
