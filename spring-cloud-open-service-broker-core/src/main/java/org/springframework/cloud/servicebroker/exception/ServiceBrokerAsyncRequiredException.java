/*
 * Copyright 2002-2018 the original author or authors.
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
 * Thrown to indicate that a broker requires <code>accepts_incomplete</code> to be <code>true</code> to
 * successfully process a request.
 */
public class ServiceBrokerAsyncRequiredException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiceBrokerAsyncRequiredException(String message) {
		super("Service broker requires async operations: " + message);
	}
}
