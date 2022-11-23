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

package org.springframework.cloud.servicebroker.model.instance;

import jakarta.validation.constraints.NotEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.cloud.servicebroker.model.CloudFoundryContext;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.catalog.MaintenanceInfo;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;

/**
 * Details of a request to create a new service instance.
 *
 * <p>
 * Objects of this type are constructed by the framework from the headers, path variables, query parameters and message
 * body passed to the service broker by the platform.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#request-2">Open Service
 * 		Broker API specification</a>
 */
@SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"})
public class CreateServiceInstanceRequest extends AsyncParameterizedServiceInstanceRequest {

	@NotEmpty
	@JsonProperty("service_id")
	private final String serviceDefinitionId;

	@NotEmpty
	@JsonProperty("plan_id")
	private final String planId;

	/**
	 * remains in the model for marshalling support but test harnesses should not use
	 */
	@Deprecated
	@JsonProperty("organization_guid")
	private final String organizationGuid;

	/**
	 * remains in the model for marshalling support but test harnesses should not use
	 */
	@Deprecated
	@JsonProperty("space_guid")
	private final String spaceGuid;

	@JsonIgnore //mapped as path param
	private transient String serviceInstanceId;

	@JsonIgnore /*internal field*/
	private transient ServiceDefinition serviceDefinition;

	@JsonIgnore /*internal field*/
	private transient Plan plan;

	private final MaintenanceInfo maintenanceInfo;

	/**
	 * Construct a new {@link CreateServiceInstanceRequest}
	 */
	public CreateServiceInstanceRequest() {
		this(null, null, null, null, null, null, null, false, null, null, null, null, null);
	}

	/**
	 * Construct a new {@link CreateServiceInstanceRequest}
	 *
	 * @param serviceDefinitionId the service definition ID
	 * @param serviceInstanceId the service instance ID
	 * @param planId the plan ID
	 * @param serviceDefinition the service definition
	 * @param plan the plan
	 * @param parameters the parameters
	 * @param context the context
	 * @param asyncAccepted does the platform accept asynchronous requests
	 * @param platformInstanceId the platform instance ID
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentity identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 * @param maintenanceInfo maintenance info sent by the platform
	 */
	public CreateServiceInstanceRequest(String serviceDefinitionId, String serviceInstanceId, String planId,
			ServiceDefinition serviceDefinition, Plan plan, Map<String, Object> parameters, Context context,
			boolean asyncAccepted, String platformInstanceId, String apiInfoLocation, Context originatingIdentity,
			String requestIdentity, MaintenanceInfo maintenanceInfo) {
		this(serviceDefinitionId, serviceInstanceId, planId, serviceDefinition, plan, parameters, context,
				asyncAccepted, platformInstanceId, apiInfoLocation, originatingIdentity, requestIdentity, null, null,
				maintenanceInfo);
	}

	private CreateServiceInstanceRequest(String serviceDefinitionId, String serviceInstanceId, String planId,
			ServiceDefinition serviceDefinition, Plan plan, Map<String, Object> parameters, Context context,
			boolean asyncAccepted, String platformInstanceId, String apiInfoLocation, Context originatingIdentity,
			String requestIdentity, String organizationGuid, String spaceGuid, MaintenanceInfo maintenanceInfo) {
		super(parameters, context, asyncAccepted, platformInstanceId, apiInfoLocation, originatingIdentity,
				requestIdentity);
		this.serviceDefinitionId = serviceDefinitionId;
		this.serviceInstanceId = serviceInstanceId;
		this.planId = planId;
		this.serviceDefinition = serviceDefinition;
		this.plan = plan;
		this.organizationGuid = organizationGuid;
		this.spaceGuid = spaceGuid;
		this.maintenanceInfo = maintenanceInfo;
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
	 * This method is intended to be used internally only; use {@link #builder()} to construct an object of this type
	 * and set all field values.
	 *
	 * @param serviceInstanceId the service instance ID to create
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
	 * Get the ID of the plan for to the service instance to create. This will match one of the plan IDs provided in the
	 * {@link org.springframework.cloud.servicebroker.model.catalog.Catalog} within the specified {@link
	 * ServiceDefinition}.
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
	@JsonIgnore
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
	@JsonIgnore
	public String getSpaceGuid() {
		return this.spaceGuid;
	}

	/**
	 * Determine the space GUID
	 *
	 * @return the space GUID
	 */
	@JsonGetter("space_guid")
	protected String getSpaceGuidToSerialize() {
		//prefer explicitly set field if any
		String spaceGuid = this.spaceGuid;
		//then use cloudfoundry context if any
		if (spaceGuid == null && getContext() instanceof CloudFoundryContext) {
			spaceGuid = ((CloudFoundryContext) getContext()).getSpaceGuid();
		}
		//Otherwise, default to an arbitrary string
		if (spaceGuid == null) {
			spaceGuid = "default-undefined-value"; //OSB spec says "MUST be a non-empty string."
		}
		return spaceGuid;
	}

	/**
	 * Determine the organization GUID
	 *
	 * @return the organization GUID
	 */
	@JsonGetter("organization_guid")
	protected String getOrganizationGuidToSerialize() {
		//prefer explicitly set field if any
		String organizationGuid = this.organizationGuid;
		//then use cloudfoundry context if any
		if (organizationGuid == null && getContext() instanceof CloudFoundryContext) {
			organizationGuid = ((CloudFoundryContext) getContext()).getOrganizationGuid();
		}
		//Otherwise, default to an arbitrary string
		if (organizationGuid == null) {
			organizationGuid = "default-undefined-value"; //OSB spec says "MUST be a non-empty string."
		}
		return organizationGuid;
	}

	/**
	 * Get the service definition of the service to create.
	 *
	 * <p>
	 * The service definition is retrieved from the {@link org.springframework.cloud.servicebroker.model.catalog.Catalog}
	 * as a convenience.
	 *
	 * @return the service definition
	 */
	public ServiceDefinition getServiceDefinition() {
		return this.serviceDefinition;
	}

	/**
	 * This method is intended to be used internally only; use {@link #builder()} to construct an object of this type
	 * and set all field values.
	 *
	 * @param serviceDefinition the service definition of the service to create
	 */
	public void setServiceDefinition(ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}

	/**
	 * Get the plan of the service to create
	 *
	 * <p>
	 * The plan is retreved from the {@link org.springframework.cloud.servicebroker.model.catalog.Catalog} as a
	 * convenience.
	 *
	 * @return the plan
	 */
	public Plan getPlan() {
		return this.plan;
	}

	/**
	 * For internal use only. use {@link #builder()} to construct an object of this type and set all field values.
	 *
	 * @param plan the plan of the service to create
	 */
	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	/**
	 * Get the maintenance info of the service instance to create. This value is assigned by the platform.
	 *
	 * <p>
	 * This value is set from the {@literal :maintenance_info} field in the body of the request from the platform.
	 *
	 * @return a MaintenanceInfo or null if none was provided
	 */
	public MaintenanceInfo getMaintenanceInfo() {
		return maintenanceInfo;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal CreateServiceInstanceRequest}.
	 *
	 * <p>
	 * This builder is provided to support testing of {@link org.springframework.cloud.servicebroker.service.ServiceInstanceService}
	 * implementations.
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
		if (this == o) {
			return true;
		}
		if (!(o instanceof CreateServiceInstanceRequest)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		CreateServiceInstanceRequest that = (CreateServiceInstanceRequest) o;
		return that.canEqual(this) &&
				Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId) &&
				Objects.equals(organizationGuid, that.organizationGuid) &&
				Objects.equals(spaceGuid, that.spaceGuid) &&
				Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(serviceDefinition, that.serviceDefinition) &&
				Objects.equals(plan, that.plan) &&
				Objects.equals(maintenanceInfo, that.maintenanceInfo);
	}

	@Override
	public boolean canEqual(Object other) {
		return other instanceof CreateServiceInstanceRequest;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceDefinitionId, planId,
				organizationGuid, spaceGuid, serviceInstanceId, serviceDefinition, plan, maintenanceInfo);
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
				", maintenanceInfo='" + maintenanceInfo + '\'' +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link CreateServiceInstanceRequest}.
	 */
	public static final class CreateServiceInstanceRequestBuilder {

		private String serviceInstanceId;

		private String serviceDefinitionId;

		private String planId;

		private ServiceDefinition serviceDefinition;

		private Plan plan;

		private Context context;

		private final Map<String, Object> parameters = new HashMap<>();

		private boolean asyncAccepted;

		private String platformInstanceId;

		private String apiInfoLocation;

		private Context originatingIdentity;

		private String requestIdentity;

		private MaintenanceInfo maintenanceInfo;

		private CreateServiceInstanceRequestBuilder() {
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
		 * Set the fully resolved plan for the service definition
		 *
		 * @param plan the plan
		 * @return the builder
		 * @see #getPlan()
		 */
		public CreateServiceInstanceRequestBuilder plan(Plan plan) {
			this.plan = plan;
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
		 * Add a set of parameters from the provided {@literal Map} to the request parameters as would be provided in
		 * the request from the platform.
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
		 * @param apiInfoLocation location of the API info endpoint of the platform instance
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
		 * Set the identity of the request sent from the platform
		 *
		 * @param requestIdentity the request identity
		 * @return the builder
		 * @see #getRequestIdentity()
		 */
		public CreateServiceInstanceRequestBuilder requestIdentity(String requestIdentity) {
			this.requestIdentity = requestIdentity;
			return this;
		}

		/**
		 * Set the maintenance info related to the plan
		 *
		 * @param maintenanceInfo the maintenance info
		 * @return the builder
		 * @see #getMaintenanceInfo()
		 */
		public CreateServiceInstanceRequestBuilder maintenanceInfo(MaintenanceInfo maintenanceInfo) {
			this.maintenanceInfo = maintenanceInfo;
			return this;
		}

		/**
		 * Construct a {@link CreateServiceInstanceRequest} from the provided values.
		 *
		 * @return the newly constructed {@literal CreateServiceInstanceRequest}
		 */
		public CreateServiceInstanceRequest build() {
			return new CreateServiceInstanceRequest(serviceDefinitionId, serviceInstanceId, planId, serviceDefinition,
					plan, parameters, context, asyncAccepted, platformInstanceId, apiInfoLocation, originatingIdentity,
					requestIdentity, maintenanceInfo);
		}

	}

}
