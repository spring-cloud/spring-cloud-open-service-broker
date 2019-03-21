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
 * Thrown to indicate that a service binding create request was received for a binding that already exists
 * with parameters that are different from the existing binding.
 *
 * <p>
 * Throwing this exception will result in an HTTP status code {@literal 409 CONFLICT}
 * being returned to the platform.
 *
 * @author sgreenberg@pivotal.io
 */
public class ServiceInstanceBindingExistsException extends ServiceBrokerException {

	private static final long serialVersionUID = -914571358227517785L;

	/**
	 * Construct an exception with a default message that includes the provided IDs.
	 *
	 * @param serviceInstanceId the ID of the service instance
	 * @param bindingId the ID of the service binding
	 */
	public ServiceInstanceBindingExistsException(String serviceInstanceId, String bindingId) {
		super(buildMessage(serviceInstanceId, bindingId));
	}

	/**
	 * Construct an exception with a default message that includes the provided IDs.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param serviceInstanceId the ID of the service instance
	 * @param bindingId the ID of the service binding
	 */
	public ServiceInstanceBindingExistsException(String errorCode, String serviceInstanceId, String bindingId) {
		super(errorCode, buildMessage(serviceInstanceId, bindingId));
	}

	private static String buildMessage(String serviceInstanceId, String bindingId) {
		return "Service instance binding already exists: "
				+ "serviceInstanceId=" + serviceInstanceId
				+ ", bindingId=" + bindingId;
	}
}
