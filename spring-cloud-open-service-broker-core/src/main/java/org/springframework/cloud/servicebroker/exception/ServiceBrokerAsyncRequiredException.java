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
 * Thrown to indicate that a service broker requires that the platform support asynchronous processing of operations.
 * This requires that the {@literal accepts_incomplete} request parameter be {@literal true} in requests
 * from the platform.
 * 
 * <p>
 * Throwing this exception will result in an HTTP status code {@literal 422 UNPROCESSABLE ENTITY}
 * being returned to the platform.
 */
public class ServiceBrokerAsyncRequiredException extends ServiceBrokerException {
	private static final long serialVersionUID = -6116656797448174365L;
	public final static String ASYNC_REQUIRED_ERROR = "AsyncRequired";

	/**
	 * Construct an exception with the provided message.
	 *
	 * @param message the exception message
	 */
	public ServiceBrokerAsyncRequiredException(String message) {
		super(ASYNC_REQUIRED_ERROR, "Service broker requires async operations: " + message);
	}
}
