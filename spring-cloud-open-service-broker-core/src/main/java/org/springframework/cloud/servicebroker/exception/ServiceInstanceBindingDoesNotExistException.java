/*
 * Copyright 2002-2020 the original author or authors.
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
 * Thrown to indicate that a request includes a service binding ID that is not known to the service broker.
 *
 * <p>
 * Throwing this exception will result in different HTTP status codes being returned to the platform, depending on
 * the condition. The default HTTP status code is {@literal 422 UNPROCESSABLE ENTITY}. If this exception is thrown
 * when retrieving a service instance binding, an HTTP status {@literal 404 NOT FOUND} is returned. If this exception
 * is thrown when deleting a service instance binding, an HTTP status {@literal 410 GONE} is returned.
 *
 * @see org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController
 */
public class ServiceInstanceBindingDoesNotExistException extends ServiceBrokerException {

	private static final long serialVersionUID = -1879753092397657116L;

	/**
	 * Construct an exception with a default message that includes the provided service binding ID.
	 *
	 * @param bindingId the ID of the service binding
	 */
	public ServiceInstanceBindingDoesNotExistException(String bindingId) {
		super(buildMessage(bindingId));
	}

	/**
	 * Construct an exception with a default message that includes the provided IDs.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param bindingId the ID of the service binding
	 */
	public ServiceInstanceBindingDoesNotExistException(String errorCode, String bindingId) {
		super(errorCode, buildMessage(bindingId));
	}

	private static String buildMessage(String bindingId) {
		return "Service binding does not exist: id=" + bindingId;
	}

}
