/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.exception;

/**
 * Thrown to indicate that the service broker received a binding request without an application resource,
 * but only supports binding to applications.
 *
 * <p>
 * Throwing this exception will result in an HTTP status code {@literal 422 UNPROCESSABLE ENTITY}
 * being returned to the platform.
 *
 * @author Scott Frederick
 */
public class ServiceBrokerBindingRequiresAppException extends ServiceBrokerException {
	private static final long serialVersionUID = -5605297457258622988L;
	public final static String APP_REQUIRED_ERROR = "RequiresApp";

	/**
	 * Construct an exception with the provided message.
	 *
	 * @param message the exception message
	 */
	public ServiceBrokerBindingRequiresAppException(String message) {
		super(APP_REQUIRED_ERROR, message);
	}

	/**
	 * Construct an exception with the provided message and cause.
	 *
	 * @param message the exception message
	 * @param cause the exception cause
	 */
	public ServiceBrokerBindingRequiresAppException(String message, Throwable cause) {
		super(APP_REQUIRED_ERROR, message, cause);
	}
}