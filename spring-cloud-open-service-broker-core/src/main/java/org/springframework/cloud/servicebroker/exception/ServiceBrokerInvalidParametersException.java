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

/**
 * Thrown to indicate that parameters passed in a create or update request are not understood by the service broker.
 *
 * <p>
 * Throwing this exception will result in an HTTP status code {@literal 422 UNPROCESSABLE ENTITY}
 * being returned to the platform.
 *
 * @author Scott Frederick
 */
public class ServiceBrokerInvalidParametersException extends ServiceBrokerException {
	private static final long serialVersionUID = 4719676639792071582L;
	private static final String MESSAGE_PREFIX = "Service broker parameters are invalid";

	/**
	 * Construct an exception with the provided message.
	 *
	 * @param message the exception message
	 */
	public ServiceBrokerInvalidParametersException(String message) {
		super(prependMessagePrefix(message));
	}

	/**
	 * Construct an exception with the provided error code and message.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param message the exception message
	 */
	public ServiceBrokerInvalidParametersException(String errorCode, String message) {
		super(errorCode, prependMessagePrefix(message));
	}

	/**
	 * Construct an exception with the provided message and cause.
	 *
	 * @param message the exception message
	 * @param cause the cause of the exception
	 */
	public ServiceBrokerInvalidParametersException(String message, Throwable cause) {
		super(prependMessagePrefix(message), cause);
	}

	/**
	 * Construct an exception with the provided error code, message and cause.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param message the exception message
	 * @param cause the cause of the exception
	 */
	public ServiceBrokerInvalidParametersException(String errorCode, String message, Throwable cause) {
		super(errorCode, prependMessagePrefix(message), cause);
	}

	/**
	 * Construct an exception with a default message and the provided cause.
	 *
	 * @param cause the cause of the exception
	 */
	public ServiceBrokerInvalidParametersException(Throwable cause) {
		super(MESSAGE_PREFIX, cause);
	}

	private static String prependMessagePrefix(String message) {
		return MESSAGE_PREFIX + ": " + message;
	}
}
