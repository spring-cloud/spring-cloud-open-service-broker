/*
 * Copyright 2002-2022 the original author or authors.
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
 * Thrown to indicate that a request includes a service instance ID that is not known to the service broker.
 *
 * <p>
 * Throwing this exception will result in different HTTP status codes being returned to the platform, depending on
 * the condition. The default HTTP status code is {@literal 422 UNPROCESSABLE ENTITY}. If this exception is thrown
 * when retrieving a service instance or service instance binding, an HTTP status {@literal 404 NOT FOUND} is
 * returned. If this exception is thrown when deleting a service instance, an HTTP status {@literal 410 GONE} is
 * returned.
 *
 * @author sgreenberg@pivotal.io
 * @author Roy Clarkson
 * @see org.springframework.cloud.servicebroker.controller.ServiceInstanceController
 * @see org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController
 */
public class ServiceInstanceDoesNotExistException extends ServiceBrokerException {

	private static final long serialVersionUID = -1879753092397657116L;

	/**
	 * Construct an exception with a default message that includes the provided service instance ID.
	 *
	 * @param serviceInstanceId the service instance ID
	 */
	public ServiceInstanceDoesNotExistException(String serviceInstanceId) {
		super(buildMessage(serviceInstanceId));
	}

	/**
	 * Construct an exception with an error code and default message that includes the provided service instance ID.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param serviceInstanceId the service instance ID
	 */
	public ServiceInstanceDoesNotExistException(String errorCode, String serviceInstanceId) {
		super(errorCode, buildMessage(serviceInstanceId));
	}

	/**
	 * Construct an exception with a default message and the provided detail and a cause.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param serviceInstanceId the service instance ID
	 * @param instanceUsable If an update or deprovisioning operation failed, this flag indicates whether or not the
	 * 		Service Instance is still usable. If true, the Service Instance can still be used, false otherwise. This field
	 * 		MUST NOT be present for errors of other operations.
	 * @param updateRepeatable If an update operation failed, this flag indicates whether this update can be repeated
	 * 		or not. If true, the same update operation MAY be repeated and MAY succeed; if false, repeating the same
	 * 		update operation will fail again. This field MUST NOT be present for errors of other operations.
	 */
	public ServiceInstanceDoesNotExistException(String errorCode, String serviceInstanceId, Boolean instanceUsable,
			Boolean updateRepeatable) {
		super(errorCode, buildMessage(serviceInstanceId), instanceUsable, updateRepeatable);
	}

	/**
	 * Construct an exception with a default message and the provided detail and a cause.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param serviceInstanceId the service instance ID
	 * @param instanceUsable If an update or deprovisioning operation failed, this flag indicates whether or not the
	 * 		Service Instance is still usable. If true, the Service Instance can still be used, false otherwise. This field
	 * 		MUST NOT be present for errors of other operations.
	 * @param updateRepeatable If an update operation failed, this flag indicates whether this update can be repeated
	 * 		or not. If true, the same update operation MAY be repeated and MAY succeed; if false, repeating the same
	 * 		update operation will fail again. This field MUST NOT be present for errors of other operations.
	 * @param cause the cause of the exception
	 */
	public ServiceInstanceDoesNotExistException(String errorCode, String serviceInstanceId, Boolean instanceUsable,
			Boolean updateRepeatable, Throwable cause) {
		super(errorCode, buildMessage(serviceInstanceId), instanceUsable, updateRepeatable, cause);
	}

	private static String buildMessage(String serviceInstanceId) {
		return "Service instance does not exist: id=" + serviceInstanceId;
	}

}
