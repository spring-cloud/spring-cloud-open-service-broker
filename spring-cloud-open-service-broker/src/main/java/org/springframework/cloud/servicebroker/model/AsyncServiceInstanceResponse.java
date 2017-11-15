package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AsyncServiceInstanceResponse {
	/**
	 * Indicates whether the request to the service broker is complete. A <code>false</code> value indicates that the
	 * request was completed, a <code>true</code> value indicates that the broker is processing the request
	 * asynchronously.
	 */
	@JsonIgnore
	protected boolean async = false;


	/**
	 * For async responses, service brokers can return operation state as a string. This field will be provided back to
	 * the service broker on last_operation requests as a URL encoded query param. Can be <code>null</code> to indicate
	 * that an operation state is not provided.
	 */
	@JsonSerialize
	protected String operation;
}
