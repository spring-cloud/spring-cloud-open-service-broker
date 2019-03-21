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
 * Thrown to indicate that the version of the API supported by the broker doesn't match the version
 * provided by the platform.
 *
 * <p>
 * Throwing this exception will result in an HTTP status code {@literal 412 PRECONDITION FAILED}
 * being returned to the platform.
 */
public class ServiceBrokerApiVersionException extends ServiceBrokerException {
	private static final long serialVersionUID = -6792404679608443775L;

	/**
	 * Construct an exception with the expected and provided versions.
	 *
	 * @param expectedVersion the version expected by the service broker
	 * @param providedVersion the version provided by the platform
	 */
	public ServiceBrokerApiVersionException(String expectedVersion, String providedVersion) {
		super(ServiceBrokerApiVersionErrorMessage.from(expectedVersion, providedVersion).toString());
	}

	/**
	 * Construct an exception with an error code and the expected and provided versions.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param expectedVersion the version expected by the service broker
	 * @param providedVersion the version provided by the platform
	 */
	public ServiceBrokerApiVersionException(String errorCode, String expectedVersion, String providedVersion) {
		super(errorCode, ServiceBrokerApiVersionErrorMessage.from(expectedVersion, providedVersion).toString());
	}
}
