/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
 * in a request.
 */
public class ServiceBrokerApiVersionException extends RuntimeException {

	private static final long serialVersionUID = -6792404679608443775L;

	public ServiceBrokerApiVersionException(String expectedVersion, String providedVersion) {
		super("The provided service broker API version is not supported: "
				+ "expected version=" + expectedVersion
				+ ", provided version = " + providedVersion);
	}

}
