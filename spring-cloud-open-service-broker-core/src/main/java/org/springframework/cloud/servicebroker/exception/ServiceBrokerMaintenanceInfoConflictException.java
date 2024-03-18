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
 * Thrown to indicate that the {@literal maintenance_info.version} provided in the request does not match the {@literal
 * maintenance_info.version} described for the service plan in the service broker's catalog
 *
 * <p>
 * Throwing this exception will result in an HTTP status code {@literal 422 UNPROCESSABLE ENTITY} being returned to the
 * platform.
 *
 * @author Roy Clarkson
 */
public class ServiceBrokerMaintenanceInfoConflictException extends ServiceBrokerException {

	private static final long serialVersionUID = 3665795988607316116L;

	/**
	 * Error code representing that the {@literal maintenance_info.version} field provided in the request does not
	 * match the {@literal maintenance_info.version} field provided in the service broker's catalog.
	 */
	public static final String MAINTENANCE_INFO_CONFLICT_ERROR = "MaintenanceInfoConflict";

	/**
	 * Default error message for when the {@literal maintenance_info.version} does not match the catalog.
	 */
	public static final String MAINTENANCE_INFO_CONFLICT_MESSAGE = "The maintenance information for the requested Service Plan has changed.";

	/**
	 * Construct an exception with a default message.
	 */
	public ServiceBrokerMaintenanceInfoConflictException() {
		super(MAINTENANCE_INFO_CONFLICT_ERROR, MAINTENANCE_INFO_CONFLICT_MESSAGE);
	}

	/**
	 * Construct an exception with the provided message.
	 *
	 * @param message the exception message
	 */
	public ServiceBrokerMaintenanceInfoConflictException(String message) {
		super(MAINTENANCE_INFO_CONFLICT_ERROR, "Service broker maintenance info conflict: " + message);
	}

}
