/*
 * Copyright 2002-2019 the original author or authors.
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
 * Thrown to indicate that the {@link org.springframework.cloud.servicebroker.model.BrokerApiVersion.DEFAULT_API_VERSION_HEADER}
 * header is missing of the request.
 *
 * <p>
 * Throwing this exception will result in an HTTP status code {@literal 400 BAD REQUEST}
 * being returned to the platform.
 */
public class ServiceBrokerApiVersionMissingException extends ServiceBrokerException {

	private static final long serialVersionUID = 8299020417615147387L;

	/**
	 * Construct an exception with the expected.
	 *
	 * @param expectedVersion the version expected by the service broker
	 */
	public ServiceBrokerApiVersionMissingException(String expectedVersion) {
		super(ServiceBrokerApiVersionErrorMessage.from(expectedVersion, "null").toString());
	}
}
