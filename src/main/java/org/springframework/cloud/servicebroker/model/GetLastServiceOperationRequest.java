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

package org.springframework.cloud.servicebroker.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a request to get the state of the last operation on a service instance.
 *
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GetLastServiceOperationRequest extends ServiceBrokerRequest {
	/**
	 * The Cloud Controller GUID of the service instance to get the status of.
	 */
	private final String serviceInstanceId;

	/**
	 * The ID of the service to deprovision, from the broker catalog.
	 */
	private final String serviceDefinitionId;

	/**
	 * The ID of the plan to deprovision within the service, from the broker catalog.
	 */
	private final String planId;

	/**
	 * The field optionally returned by the service broker on async provision, update,
	 * deprovision responses. Represents any state the service broker responded with as a
	 * URL encoded string. Can be <code>null</code> to indicate that an operation state is
	 * not provided.
	 */
	protected String operation;

	public GetLastServiceOperationRequest(String instanceId) {
		this.serviceInstanceId = instanceId;
		this.serviceDefinitionId = null;
		this.planId = null;
	}

	public GetLastServiceOperationRequest(String instanceId, String serviceId,
			String planId, String operation) {
		this.serviceInstanceId = instanceId;
		this.serviceDefinitionId = serviceId;
		this.planId = planId;
		this.operation = operation;
	}

	public GetLastServiceOperationRequest withCfInstanceId(String cfInstanceId) {
		this.cfInstanceId = cfInstanceId;
		return this;
	}

	public GetLastServiceOperationRequest withApiInfoLocation(String apiInfoLocation) {
		this.apiInfoLocation = apiInfoLocation;
		return this;
	}
}
