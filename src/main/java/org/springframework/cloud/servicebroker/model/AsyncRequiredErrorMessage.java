package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An error returned when a broker requires an asynchronous request.
 *
 * @author krujos
 */
public class AsyncRequiredErrorMessage extends ErrorMessage {

	public final static String ASYNC_REQUIRED_ERROR = "AsyncRequired";
		
	/**
	 * This broker requires asynchronous processing. 
	 *
	 * @param description user facing error message.
	 */
	public AsyncRequiredErrorMessage(String description) {
		super(description);
	}

	@JsonProperty("error")
	public String getError() { 
		return ASYNC_REQUIRED_ERROR;
	}
}
