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
 * Thrown to indicate that a request includes a service definition ID that does not exist in the
 * service broker catalog.
 *
 * <p>
 * Throwing this exception will result in an HTTP status code {@literal 422 UNPROCESSABLE ENTITY}
 * being returned to the platform.
 *
 * @author sgreenberg@pivotal.io
 */
public class ServiceDefinitionDoesNotExistException extends ServiceBrokerException {

	private static final long serialVersionUID = -62090827040416788L;

	/**
	 * Construct an exception with a default message that includes the provided service definition ID.
	 *
	 * @param serviceDefinitionId the ID of the service definition
	 */
	public ServiceDefinitionDoesNotExistException(String serviceDefinitionId) {
		super(buildMessage(serviceDefinitionId));
	}

	/**
	 * Construct an exception with an error code and default message that includes the
	 * provided service definition ID.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param serviceDefinitionId the ID of the service definition
	 */
	public ServiceDefinitionDoesNotExistException(String errorCode, String serviceDefinitionId) {
		super(errorCode, buildMessage(serviceDefinitionId));
	}

	private static String buildMessage(String serviceDefinitionId) {
		return "Service definition does not exist: id=" + serviceDefinitionId;
	}
}