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

import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a request to update a service instance.
 *
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateServiceInstanceRequest
		extends AsyncParameterizedServiceInstanceRequest {

	/**
	 * The ID of the service to update, from the broker catalog.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("service_id")
	private final String serviceDefinitionId;

	/**
	 * The ID of the plan to update within the service, from the broker catalog.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("plan_id")
	private final String planId;

	/**
	 * The Cloud Controller GUID of the service instance to update.
	 */
	@JsonIgnore
	private transient String serviceInstanceId;

	/**
	 * The {@link ServiceDefinition} of the service to update. This is resolved from the
	 * <code>serviceDefinitionId</code> as a convenience to the broker.
	 */
	@JsonIgnore
	private transient ServiceDefinition serviceDefinition;

	public UpdateServiceInstanceRequest() {
		super(null);
		this.serviceDefinitionId = null;
		this.planId = null;
	}

	public UpdateServiceInstanceRequest(String serviceDefinitionId, String planId,
			Map<String, Object> parameters) {
		super(parameters);
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
	}

	public UpdateServiceInstanceRequest(String serviceDefinitionId, String planId) {
		this(serviceDefinitionId, planId, null);
	}

	public UpdateServiceInstanceRequest withServiceInstanceId(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
		return this;
	}

	public UpdateServiceInstanceRequest withServiceDefinition(
			ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
		return this;
	}

	public UpdateServiceInstanceRequest withAsyncAccepted(boolean asyncAccepted) {
		this.asyncAccepted = asyncAccepted;
		return this;
	}

	public UpdateServiceInstanceRequest withCfInstanceId(String cfInstanceId) {
		this.cfInstanceId = cfInstanceId;
		return this;
	}

	public UpdateServiceInstanceRequest withApiInfoLocation(String apiInfoLocation) {
		this.apiInfoLocation = apiInfoLocation;
		return this;
	}
}
