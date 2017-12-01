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
 * Thrown to indicate that the plan change requested is not supported or if the request
 * can not currently be fulfilled due to the state of the instance.
 */
public class ServiceInstanceUpdateNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = 4719676639792071582L;

	public ServiceInstanceUpdateNotSupportedException(String message) {
		super(message);
	}

	public ServiceInstanceUpdateNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceInstanceUpdateNotSupportedException(Throwable cause) {
		super(cause);
	}

}
