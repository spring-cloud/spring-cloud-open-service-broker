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
 * Thrown to indicate that a service instance create request was received for an instance that already exists
 * with parameters that are different from the existing instance.
 *
 * <p>
 * Throwing this exception will result in an HTTP status code {@literal 409 CONFLICT}
 * being returned to the platform.
 *
 * @author sgreenberg@pivotal.io
 */
public class ServiceInstanceExistsException extends ServiceBrokerException {
	private static final long serialVersionUID = -914571358227517785L;

	/**
	 * Construct an exception with a default message that includes the provided IDs.
	 *
	 * @param serviceInstanceId the ID of the service instance
	 * @param serviceDefinitionId the ID of the service definition
	 */
	public ServiceInstanceExistsException(String serviceInstanceId, String serviceDefinitionId) {
		super(buildMessage(serviceInstanceId, serviceDefinitionId));
	}

	/**
	 * Construct an exception with an error code and default message that includes the provided IDs.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param serviceInstanceId the ID of the service instance
	 * @param serviceDefinitionId the ID of the service definition
	 */
	public ServiceInstanceExistsException(String errorCode, String serviceInstanceId, String serviceDefinitionId) {
		super(errorCode, buildMessage(serviceInstanceId, serviceDefinitionId));
	}

	private static String buildMessage(String serviceInstanceId, String serviceDefinitionId) {
		return "Service instance with the given ID already exists: " +
				"serviceInstanceId=" + serviceInstanceId +
				", serviceDefinitionId=" + serviceDefinitionId;
	}
}
