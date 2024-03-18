/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.exception;

/**
 * Thrown to indicate that a request includes a plan ID that does not exist in the service broker catalog.
 *
 * <p>
 * Throwing this exception will result in an HTTP status code {@literal 400 BAD REQUEST} being returned to the
 * platform.
 *
 * @author vvpishh2
 */
public class ServiceDefinitionPlanDoesNotExistException extends ServiceBrokerException {

	private static final long serialVersionUID = -32490837045476718L;

	/**
	 * Construct an exception with a default message that includes the provided plan ID.
	 *
	 * @param planId the plan ID
	 */
	public ServiceDefinitionPlanDoesNotExistException(String planId) {
		super(buildMessage(planId));
	}

	/**
	 * Construct an exception with an error code and default message that includes the provided plan ID.
	 *
	 * @param errorCode a single word in camel case that uniquely identifies the error condition
	 * @param planId the plan ID
	 */
	public ServiceDefinitionPlanDoesNotExistException(String errorCode, String planId) {
		super(errorCode, buildMessage(planId));
	}

	private static String buildMessage(String planId) {
		return "Service Definition Plan does not exist: id=" + planId;
	}

}
