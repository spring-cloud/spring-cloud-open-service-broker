/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.exception;

import org.springframework.cloud.servicebroker.model.error.ErrorMessage;

/**
 * Thrown to indicate underlying service broker errors (like connectivity to the service
 * being brokered) not covered by a more specific exception.
 *
 * <p>
 * Throwing this exception will result in an HTTP status code {@literal 500 INTERNAL SERVER ERROR}
 * being returned to the platform.
 *
 * @author sgreenberg@pivotal.io
 */
public class ServiceBrokerException extends RuntimeException {

	private static final long serialVersionUID = -5544859893499349135L;
	private final ErrorMessage errorMessage;

	/**
	 * Construct an exception with the provided message.
	 *
	 * @param message the exception message
	 */
	public ServiceBrokerException(String message) {
		super(message);
		this.errorMessage = new ErrorMessage(message);
	}

	/**
	 * Construct an exception with the provided error code and message.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param message the exception message
	 */
	public ServiceBrokerException(String errorCode, String message) {
		super(message);
		this.errorMessage = new ErrorMessage(errorCode, message);
	}

	/**
	 * Construct an exception with the provided message and cause.
	 *
	 * @param message the exception message
	 * @param cause the cause of the exception
	 */
	public ServiceBrokerException(String message, Throwable cause) {
		super(message, cause);
		this.errorMessage = new ErrorMessage(message);
	}

	/**
	 * Construct an exception with the provided error code, message and cause.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param message the exception message
	 * @param cause the cause of the exception
	 */
	public ServiceBrokerException(String errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorMessage = new ErrorMessage(errorCode, message);
	}

	/**
	 * Construct an exception without a message and with the provided cause.
	 *
	 * @param cause the cause of the exception
	 */
	public ServiceBrokerException(Throwable cause) {
		super(cause);
		this.errorMessage = new ErrorMessage();
	}

	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}
}