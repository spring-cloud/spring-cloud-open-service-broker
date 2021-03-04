/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.exception;

/**
 * Thrown to indicate that a request to create a service instance or binding was received while asynchronous creation of
 * the identical service instance or binding is in progress.
 *
 * <p>
 * Throwing this exception will result in an HTTP status code {@literal 202 ACCEPTED} being returned to the platform.
 *
 * @author Roy Clarkson
 */
public class ServiceBrokerCreateOperationInProgressException extends ServiceBrokerOperationInProgressException {

	private static final long serialVersionUID = -1248042363905670885L;

	/**
	 * Construct an exception with a default message.
	 */
	public ServiceBrokerCreateOperationInProgressException() {
		super();
	}

	/**
	 * Construct an exception with a default message that includes the provided {@literal operation} description.
	 *
	 * @param operation an identifier representing the operation in progress
	 */
	public ServiceBrokerCreateOperationInProgressException(String operation) {
		super(operation);
	}

}
