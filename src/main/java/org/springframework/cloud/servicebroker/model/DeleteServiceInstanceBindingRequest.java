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
 * Details of a request to delete a service instance binding.
 *
 * @author krujos
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DeleteServiceInstanceBindingRequest extends ServiceBrokerRequest {

	/**
	 * The Cloud Controller GUID of the service instance to being unbound.
	 */
	private final String serviceInstanceId;

	/**
	 * The Cloud Controller GUID of the service binding being deleted.
	 */
	private final String bindingId;

	private final String serviceDefinitionId;
	private final String planId;
	private transient final ServiceDefinition serviceDefinition;

	public DeleteServiceInstanceBindingRequest(String serviceInstanceId, String bindingId,
			String serviceDefinitionId, String planId,
			ServiceDefinition serviceDefinition) {
		this.serviceInstanceId = serviceInstanceId;
		this.bindingId = bindingId;
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.serviceDefinition = serviceDefinition;
	}

	public DeleteServiceInstanceBindingRequest withCfInstanceId(String cfInstanceId) {
		this.cfInstanceId = cfInstanceId;
		return this;
	}

	public DeleteServiceInstanceBindingRequest withApiInfoLocation(
			String apiInfoLocation) {
		this.apiInfoLocation = apiInfoLocation;
		return this;
	}
}
