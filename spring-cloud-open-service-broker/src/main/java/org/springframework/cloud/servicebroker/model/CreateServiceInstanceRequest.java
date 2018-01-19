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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Details of a request to create a new service instance.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"})
public class CreateServiceInstanceRequest extends AsyncParameterizedServiceInstanceRequest {

	/**
	 * The ID of the service to provision, from the broker catalog.
	 */
	@NotEmpty
	@JsonProperty("service_id")
	private final String serviceDefinitionId;

	/**
	 * The ID of the plan to provision within the service, from the broker catalog.
	 */
	@NotEmpty
	private final String planId;

	/**
	 * The Cloud Controller GUID of the organization under which the service is to be provisioned.
	 *
	 * @deprecated use {@link #context}
	 */
	@Deprecated
	private final String organizationGuid;

	/**
	 * The Cloud Controller GUID of the space under which the service is to be provisioned.
	 *
	 * @deprecated use {@link #context}
	 */
	@Deprecated
	private final String spaceGuid;

	/**
	 * The Cloud Controller GUID of the service instance to provision. This ID will be used for future
	 * requests for the same service instance (e.g. bind and deprovision), so the broker must use it to
	 * correlate any resource it creates.
	 */
	private transient String serviceInstanceId;

	/**
	 * The {@link ServiceDefinition} of the service to provision. This is resolved from the
	 * <code>serviceDefinitionId</code> as a convenience to the broker.
	 */
	private transient ServiceDefinition serviceDefinition;

	private CreateServiceInstanceRequest() {
		super(null, null);
		this.serviceDefinitionId = null;
		this.planId = null;
		this.organizationGuid = null;
		this.spaceGuid = null;
	}

	private CreateServiceInstanceRequest(String serviceDefinitionId, String planId,
										Map<String, Object> parameters, Context context) {
		super(parameters, context);
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;

		// deprecated fields - they should remain in the model for marshalling but test harnesses
		// should not use them
		this.organizationGuid = null;
		this.spaceGuid = null;
	}

	public String getServiceDefinitionId() {
		return this.serviceDefinitionId;
	}

	public String getPlanId() {
		return this.planId;
	}

	@Deprecated
	public String getOrganizationGuid() {
		return this.organizationGuid;
	}

	@Deprecated
	public String getSpaceGuid() {
		return this.spaceGuid;
	}

	public String getServiceInstanceId() {
		return this.serviceInstanceId;
	}

	public void setServiceInstanceId(final String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	public ServiceDefinition getServiceDefinition() {
		return this.serviceDefinition;
	}

	public void setServiceDefinition(ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}

	public static CreateServiceInstanceRequestBuilder builder() {
		return new CreateServiceInstanceRequestBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CreateServiceInstanceRequest)) return false;
		if (!super.equals(o)) return false;
		CreateServiceInstanceRequest that = (CreateServiceInstanceRequest) o;
		return that.canEqual(this) &&
				Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId) &&
				Objects.equals(organizationGuid, that.organizationGuid) &&
				Objects.equals(spaceGuid, that.spaceGuid) &&
				Objects.equals(serviceInstanceId, that.serviceInstanceId);
	}

	@Override
	public boolean canEqual(Object other) {
		return (other instanceof CreateServiceInstanceRequest);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceDefinitionId, planId,
				organizationGuid, spaceGuid, serviceInstanceId);
	}

	@Override
	public String toString() {
		return super.toString() +
				"CreateServiceInstanceRequest{" +
				"serviceDefinitionId='" + serviceDefinitionId + '\'' +
				", planId='" + planId + '\'' +
				", organizationGuid='" + organizationGuid + '\'' +
				", spaceGuid='" + spaceGuid + '\'' +
				", serviceInstanceId='" + serviceInstanceId + '\'' +
				'}';
	}

	public static class CreateServiceInstanceRequestBuilder {
		private String serviceDefinitionId;
		private String planId;
		private Context context;
		private Map<String, Object> parameters = new HashMap<>();

		CreateServiceInstanceRequestBuilder() {
		}

		public CreateServiceInstanceRequestBuilder serviceDefinitionId(String serviceDefinitionId) {
			this.serviceDefinitionId = serviceDefinitionId;
			return this;
		}

		public CreateServiceInstanceRequestBuilder planId(String planId) {
			this.planId = planId;
			return this;
		}

		public CreateServiceInstanceRequestBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		public CreateServiceInstanceRequestBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		public CreateServiceInstanceRequestBuilder context(Context context) {
			this.context = context;
			return this;
		}

		public CreateServiceInstanceRequest build() {
			return new CreateServiceInstanceRequest(serviceDefinitionId, planId, parameters, context);
		}
	}
}
