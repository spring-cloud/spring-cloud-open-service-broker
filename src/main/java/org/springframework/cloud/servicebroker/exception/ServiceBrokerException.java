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
 * General exception for underlying broker errors (like connectivity to the service being
 * brokered).
 *
 * @author sgreenberg@pivotal.io
 *
 */
public class ServiceBrokerException extends RuntimeException {

	private static final long serialVersionUID = -5544859893499349135L;

	public ServiceBrokerException(String message) {
		super(message);
	}

	public ServiceBrokerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceBrokerException(Throwable cause) {
		super(cause);
	}

}