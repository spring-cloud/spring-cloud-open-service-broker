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
 * Provides a formatted message indicating that the service broker does not support the version of the
 * Open Service Broker API provided by the platform.
 */
public class ServiceBrokerApiVersionErrorMessage {

	private static final String MESSAGE_TEMPLATE = "The provided service broker API version is not supported: " +
			"expected version=%s, provided version=%s";

	private final String message;

	/**
	 * Construct a message with the expected and provided version numbers.
	 *
	 * @param expectedVersion the version expected by the service broker
	 * @param providedVersion the version provided by the platform
	 */
	private ServiceBrokerApiVersionErrorMessage(String expectedVersion, String providedVersion) {
		this.message = String.format(MESSAGE_TEMPLATE, expectedVersion, providedVersion);
	}

	@Override
	public String toString() {
		return message;
	}

	/**
	 * Construct a message with the expected and provided version numbers.
	 *
	 * @param expectedVersion the version expected by the service broker
	 * @param providedVersion the version provided by the platform
	 * @return error message
	 */
	public static ServiceBrokerApiVersionErrorMessage from(String expectedVersion, String providedVersion) {
		return new ServiceBrokerApiVersionErrorMessage(expectedVersion, providedVersion);
	}

}
