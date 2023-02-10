/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.exception;

/**
 * Thrown to indicate that a request to update a service instance can not be fulfilled by the service broker.
 *
 * <p>
 * Throwing this exception will result in an HTTP status code {@literal 422 UNPROCESSABLE ENTITY} being returned to the
 * platform.
 *
 * @author Roy Clarkson
 */
public class ServiceInstanceUpdateNotSupportedException extends ServiceBrokerException {

	private static final long serialVersionUID = 4719676639792071582L;

	private static final String MESSAGE_PREFIX = "Service instance update not supported";

	/**
	 * Construct an exception with a default message and the provided detail.
	 *
	 * @param message detail to add to the default message
	 */
	public ServiceInstanceUpdateNotSupportedException(String message) {
		super(MESSAGE_PREFIX + ": " + message);
	}

	/**
	 * Construct an exception with an error code, default message and the provided detail.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param message detail to add to the default message
	 */
	public ServiceInstanceUpdateNotSupportedException(String errorCode, String message) {
		super(errorCode, MESSAGE_PREFIX + ": " + message);
	}

	/**
	 * Construct an exception with a default message and the provided detail and a cause.
	 *
	 * @param message detail to add to the default message
	 * @param cause the cause of the exception
	 */
	public ServiceInstanceUpdateNotSupportedException(String message, Throwable cause) {
		super(MESSAGE_PREFIX + ": " + message, cause);
	}

	/**
	 * Construct an exception with a default message and the provided detail and a cause.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param message detail to add to the default message
	 * @param cause the cause of the exception
	 */
	public ServiceInstanceUpdateNotSupportedException(String errorCode, String message, Throwable cause) {
		super(errorCode, MESSAGE_PREFIX + ": " + message, cause);
	}

	/**
	 * Construct an exception with a default message and the provided detail and a cause.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param message detail to add to the default message
	 * @param instanceUsable If an update or deprovisioning operation failed, this flag indicates whether or not the
	 * 		Service Instance is still usable. If true, the Service Instance can still be used, false otherwise. This field
	 * 		MUST NOT be present for errors of other operations.
	 * @param updateRepeatable If an update operation failed, this flag indicates whether this update can be repeated
	 * 		or not. If true, the same update operation MAY be repeated and MAY succeed; if false, repeating the same
	 * 		update operation will fail again. This field MUST NOT be present for errors of other operations.
	 */
	public ServiceInstanceUpdateNotSupportedException(String errorCode, String message, Boolean instanceUsable,
			Boolean updateRepeatable) {
		super(errorCode, MESSAGE_PREFIX + ": " + message, instanceUsable, updateRepeatable);
	}

	/**
	 * Construct an exception with a default message and the provided detail and a cause.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param message detail to add to the default message
	 * @param instanceUsable If an update or deprovisioning operation failed, this flag indicates whether or not the
	 * 		Service Instance is still usable. If true, the Service Instance can still be used, false otherwise. This field
	 * 		MUST NOT be present for errors of other operations.
	 * @param updateRepeatable If an update operation failed, this flag indicates whether this update can be repeated
	 * 		or not. If true, the same update operation MAY be repeated and MAY succeed; if false, repeating the same
	 * 		update operation will fail again. This field MUST NOT be present for errors of other operations.
	 * @param cause the cause of the exception
	 */
	public ServiceInstanceUpdateNotSupportedException(String errorCode, String message, Boolean instanceUsable,
			Boolean updateRepeatable, Throwable cause) {
		super(errorCode, MESSAGE_PREFIX + ": " + message, instanceUsable, updateRepeatable, cause);
	}

	/**
	 * Construct an exception with a default message and the provided cause.
	 *
	 * @param cause the cause of the exception
	 */
	public ServiceInstanceUpdateNotSupportedException(Throwable cause) {
		super(MESSAGE_PREFIX, cause);
	}

}
