/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.instance;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;

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
	 * The GUID of the organization under which the service is to be provisioned.
	 *
	 * @deprecated use {@link #context}
	 */
	@Deprecated
	private final String organizationGuid;

	/**
	 * The GUID of the space under which the service is to be provisioned.
	 *
	 * @deprecated use {@link #context}
	 */
	@Deprecated
	private final String spaceGuid;

	/**
	 * The GUID of the service instance to provision. This ID will be used for future
	 * requests for the same service instance (e.g. bind and deprovision), so the broker must use it to
	 * correlate any resource it creates.
	 */
	private transient String serviceInstanceId;

	/**
	 * The {@link ServiceDefinition} of the service to provision. This is resolved from the
	 * <code>serviceDefinitionId</code> as a convenience to the broker.
	 */
	private transient ServiceDefinition serviceDefinition;

	@SuppressWarnings("unused")
	CreateServiceInstanceRequest() {
		this.serviceDefinitionId = null;
		this.planId = null;
		this.organizationGuid = null;
		this.spaceGuid = null;
	}

	CreateServiceInstanceRequest(String serviceDefinitionId, String serviceInstanceId, String planId,
								 ServiceDefinition serviceDefinition,
								 Map<String, Object> parameters, Context context, boolean asyncAccepted,
								 String platformInstanceId, String apiInfoLocation, Context originatingIdentity) {
		super(parameters, context, asyncAccepted, platformInstanceId, apiInfoLocation, originatingIdentity);
		this.serviceDefinitionId = serviceDefinitionId;
		this.serviceInstanceId = serviceInstanceId;
		this.planId = planId;
		this.serviceDefinition = serviceDefinition;

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
				Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(serviceDefinition, that.serviceDefinition);
	}

	@Override
	public boolean canEqual(Object other) {
		return (other instanceof CreateServiceInstanceRequest);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceDefinitionId, planId,
				organizationGuid, spaceGuid, serviceInstanceId, serviceDefinition);
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
		private String serviceInstanceId;
		private String serviceDefinitionId;
		private String planId;
		private ServiceDefinition serviceDefinition;
		private Context context;
		private final Map<String, Object> parameters = new HashMap<>();
		private boolean asyncAccepted;
		private String platformInstanceId;
		private String apiInfoLocation;
		private Context originatingIdentity;

		CreateServiceInstanceRequestBuilder() {
		}

		public CreateServiceInstanceRequestBuilder serviceInstanceId(String serviceInstanceId) {
			this.serviceInstanceId = serviceInstanceId;
			return this;
		}

		public CreateServiceInstanceRequestBuilder serviceDefinitionId(String serviceDefinitionId) {
			this.serviceDefinitionId = serviceDefinitionId;
			return this;
		}

		public CreateServiceInstanceRequestBuilder serviceDefinition(ServiceDefinition serviceDefinition) {
			this.serviceDefinition = serviceDefinition;
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

		public CreateServiceInstanceRequestBuilder asyncAccepted(boolean asyncAccepted) {
			this.asyncAccepted = asyncAccepted;
			return this;
		}

		public CreateServiceInstanceRequestBuilder platformInstanceId(String platformInstanceId) {
			this.platformInstanceId = platformInstanceId;
			return this;
		}

		public CreateServiceInstanceRequestBuilder apiInfoLocation(String apiInfoLocation) {
			this.apiInfoLocation = apiInfoLocation;
			return this;
		}

		public CreateServiceInstanceRequestBuilder originatingIdentity(Context originatingIdentity) {
			this.originatingIdentity = originatingIdentity;
			return this;
		}

		public CreateServiceInstanceRequest build() {
			return new CreateServiceInstanceRequest(serviceDefinitionId, serviceInstanceId, planId,
					serviceDefinition, parameters, context, asyncAccepted,
					platformInstanceId, apiInfoLocation, originatingIdentity);
		}
	}
}
