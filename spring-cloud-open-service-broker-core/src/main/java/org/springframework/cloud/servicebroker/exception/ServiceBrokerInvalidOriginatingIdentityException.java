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
 * Thrown to indicate that parameters passed in the originating identity header are not
 * understood by the service broker.
 *
 * <p>
 * Throwing this exception will result in an HTTP status code
 * {@literal 422 UNPROCESSABLE ENTITY} being returned to the platform.
 *
 * @author Roy Clarkson
 */
public class ServiceBrokerInvalidOriginatingIdentityException
		extends ServiceBrokerException {

	private static final long serialVersionUID = -7027388607132362654L;

	private static final String MESSAGE_PREFIX = "Service broker originating identity parameters are invalid";

	/**
	 * Construct an exception with the provided message.
	 *
	 * @param message the exception message
	 */
	public ServiceBrokerInvalidOriginatingIdentityException(String message) {
		super(prependMessagePrefix(message));
	}

	/**
	 * Construct an exception with the provided message and cause.
	 *
	 * @param message the exception message
	 * @param cause the cause of the exception
	 */
	public ServiceBrokerInvalidOriginatingIdentityException(String message,
			Throwable cause) {
		super(prependMessagePrefix(message), cause);
	}

	private static String prependMessagePrefix(String message) {
		return MESSAGE_PREFIX + ": " + message;
	}
}
