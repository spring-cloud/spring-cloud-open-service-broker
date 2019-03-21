/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
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
 * <p>
 * Objects of this type are constructed by the framework from the headers, path variables, query parameters
 * and message body passed to the service broker by the platform.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#request-2">Open Service Broker API specification</a>
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"})
public class CreateServiceInstanceRequest extends AsyncParameterizedServiceInstanceRequest {

	@NotEmpty
	@JsonProperty("service_id")
	private final String serviceDefinitionId;

	@NotEmpty
	private final String planId;

	@Deprecated
	private final String organizationGuid;

	@Deprecated
	private final String spaceGuid;

	private transient String serviceInstanceId;

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

	/**
	 * Get the ID of the service instance to create. This value is assigned by the platform. It must be unique within
	 * the platform and can be used to correlate any resources associated with the service instance.
	 *
	 * <p>
	 * This value is set from the {@literal :instance_id} path element of the request from the platform.
	 *
	 * @return the service instance ID
	 */
	public String getServiceInstanceId() {
		return this.serviceInstanceId;
	}

	/**
	 * This method is intended to be used internally only; use {@link #builder()} to construct an object of this
	 * type and set all field values.
	 *
	 * @param serviceInstanceId the ID of the service instance to create
	 */
	public void setServiceInstanceId(final String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	/**
	 * Get the ID of the service definition for to the service instance to create. This will match one of the service
	 * definition IDs provided in the {@link org.springframework.cloud.servicebroker.model.catalog.Catalog}.
	 *
	 * <p>
	 * This value is set from the {@literal service_id} field in the body of the request from the platform
	 *
	 * @return the service definition ID
	 */
	public String getServiceDefinitionId() {
		return this.serviceDefinitionId;
	}

	/**
	 * Get the ID of the plan for to the service instance to create. This will match one of the plan IDs provided
	 * in the {@link org.springframework.cloud.servicebroker.model.catalog.Catalog} within the specified
	 * {@link ServiceDefinition}.
	 *
	 * <p>
	 * This value is set from the {@literal plan_id} field in the body of the request from the platform.
	 *
	 * @return the plan ID
	 */
	public String getPlanId() {
		return this.planId;
	}

	/**
	 * Get the GUID of the Cloud Foundry organization that the service instance is being created in.
	 *
	 * <p>
	 * This value is set from the {@literal organization_guid} field in the body of the request from the platform.
	 *
	 * @return the organization GUID
	 * @deprecated {@link #getContext} provides platform-neutral access to platform context details
	 */
	@Deprecated
	public String getOrganizationGuid() {
		return this.organizationGuid;
	}

	/**
	 * Get the GUID of the Cloud Foundry space that the service instance is being created in.
	 *
	 * <p>
	 * This value is set from the {@literal space_guid} field in the body of the request from the platform.
	 *
	 * @return the space GUID
	 * @deprecated {@link #getContext} provides platform-neutral access to platform context details
	 */
	@Deprecated
	public String getSpaceGuid() {
		return this.spaceGuid;
	}

	/**
	 * Get the service definition of the service to create.
	 *
	 * <p>
	 * The service definition is retrieved from the
	 * {@link org.springframework.cloud.servicebroker.model.catalog.Catalog} as a convenience.
	 *
	 * @return the service definition
	 */
	public ServiceDefinition getServiceDefinition() {
		return this.serviceDefinition;
	}

	/**
	 * This method is intended to be used internally only; use {@link #builder()} to construct an object of this
	 * type and set all field values.
	 *
	 * @param serviceDefinition the service definition of the service to create
	 */
	public void setServiceDefinition(ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal CreateServiceInstanceRequest}.
	 *
	 * <p>
	 * This builder is provided to support testing of
	 * {@link org.springframework.cloud.servicebroker.service.ServiceInstanceService} implementations.
	 *
	 * @return the builder
	 */
	public static CreateServiceInstanceRequestBuilder builder() {
		return new CreateServiceInstanceRequestBuilder();
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * Provides a fluent API for constructing a {@link CreateServiceInstanceRequest}.
	 */
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

		/**
		 * Set the service instance ID as would be provided in the request from the platform.
		 *
		 * @param serviceInstanceId the service instance ID
		 * @return the builder
		 * @see #getServiceInstanceId()
		 */
		public CreateServiceInstanceRequestBuilder serviceInstanceId(String serviceInstanceId) {
			this.serviceInstanceId = serviceInstanceId;
			return this;
		}

		/**
		 * Set the service definition ID as would be provided in the request from the platform.
		 *
		 * @param serviceDefinitionId the service definition ID
		 * @return the builder
		 * @see #getServiceDefinitionId()
		 */
		public CreateServiceInstanceRequestBuilder serviceDefinitionId(String serviceDefinitionId) {
			this.serviceDefinitionId = serviceDefinitionId;
			return this;
		}

		/**
		 * Set the fully resolved service definition.
		 *
		 * @param serviceDefinition the service definition
		 * @return the builder
		 * @see #getServiceDefinition()
		 */
		public CreateServiceInstanceRequestBuilder serviceDefinition(ServiceDefinition serviceDefinition) {
			this.serviceDefinition = serviceDefinition;
			return this;
		}

		/**
		 * Set the plan ID as would be provided in the request from the platform.
		 *
		 * @param planId the plan ID
		 * @return the builder
		 * @see #getPlanId()
		 */
		public CreateServiceInstanceRequestBuilder planId(String planId) {
			this.planId = planId;
			return this;
		}

		/**
		 * Add a set of parameters from the provided {@literal Map} to the request parameters
		 * as would be provided in the request from the platform.
		 *
		 * @param parameters the parameters to add
		 * @return the builder
		 * @see #getParameters()
		 */
		public CreateServiceInstanceRequestBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		/**
		 * Add a key/value pair to the request parameters as would be provided in the request from the platform.
		 *
		 * @param key the parameter key to add
		 * @param value the parameter value to add
		 * @return the builder
		 * @see #getParameters()
		 */
		public CreateServiceInstanceRequestBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		/**
		 * Set the {@link Context} as would be provided in the request from the platform.
		 *
		 * @param context the context
		 * @return the builder
		 * @see #getContext()
		 */
		public CreateServiceInstanceRequestBuilder context(Context context) {
			this.context = context;
			return this;
		}

		/**
		 * Set the value of the flag indicating whether the platform supports asynchronous operations.
		 *
		 * @param asyncAccepted the boolean value of the flag
		 * @return the builder
		 * @see #isAsyncAccepted()
		 */
		public CreateServiceInstanceRequestBuilder asyncAccepted(boolean asyncAccepted) {
			this.asyncAccepted = asyncAccepted;
			return this;
		}

		/**
		 * Set the ID of the platform instance as would be provided in the request from the platform.
		 *
		 * @param platformInstanceId the platform instance ID
		 * @return the builder
		 * @see #getPlatformInstanceId() 
		 */
		public CreateServiceInstanceRequestBuilder platformInstanceId(String platformInstanceId) {
			this.platformInstanceId = platformInstanceId;
			return this;
		}

		/**
		 * Set the location of the API info endpoint as would be provided in the request from the platform.
		 *
		 * @param apiInfoLocation the API info endpoint location
		 * @return the builder
		 * @see #getApiInfoLocation()
		 */
		public CreateServiceInstanceRequestBuilder apiInfoLocation(String apiInfoLocation) {
			this.apiInfoLocation = apiInfoLocation;
			return this;
		}

		/**
		 * Set the identity of the user making the request as would be provided in the request from the platform.
		 *
		 * @param originatingIdentity the user identity
		 * @return the builder
		 * @see #getOriginatingIdentity()
		 */
		public CreateServiceInstanceRequestBuilder originatingIdentity(Context originatingIdentity) {
			this.originatingIdentity = originatingIdentity;
			return this;
		}

		/**
		 * Construct a {@link CreateServiceInstanceRequest} from the provided values.
		 *
		 * @return the newly constructed {@literal CreateServiceInstanceRequest}
		 */
		public CreateServiceInstanceRequest build() {
			return new CreateServiceInstanceRequest(serviceDefinitionId, serviceInstanceId, planId,
					serviceDefinition, parameters, context, asyncAccepted,
					platformInstanceId, apiInfoLocation, originatingIdentity);
		}
	}
}
