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

package org.springframework.cloud.servicebroker.model.binding;

import jakarta.validation.constraints.NotEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.instance.AsyncParameterizedServiceInstanceRequest;

/**
 * Details of a request to create a service instance binding.
 *
 * <p>
 * Objects of this type are constructed by the framework from the headers, path variables, query parameters and message
 * body passed to the service broker by the platform.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#request-4">Open Service
 * 		Broker API specification</a>
 */
@SuppressWarnings({"DeprecatedIsStillUsed"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateServiceInstanceBindingRequest extends AsyncParameterizedServiceInstanceRequest {

	@JsonIgnore //OSB field passed as path param
	private transient String serviceInstanceId;

	@JsonIgnore //OSB field passed as path param
	private transient String bindingId;

	@NotEmpty
	@JsonProperty("service_id")
	private final String serviceDefinitionId;

	@NotEmpty
	private final String planId;

	@Deprecated
	private final String appGuid;

	private final BindResource bindResource;

	@JsonIgnore //internal field
	private transient ServiceDefinition serviceDefinition;

	@JsonIgnore //internal field
	private transient Plan plan;

	/**
	 * Construct a new {@link CreateServiceInstanceBindingRequest}
	 */
	public CreateServiceInstanceBindingRequest() {
		super();
		serviceDefinitionId = null;
		planId = null;
		appGuid = null;
		bindResource = null;
	}

	/**
	 * Construct a new {@link CreateServiceInstanceBindingRequest}
	 *
	 * @param serviceInstanceId the service instance ID
	 * @param serviceDefinitionId the service definition ID
	 * @param planId the plan ID
	 * @param bindingId the service binding ID
	 * @param serviceDefinition the service definition
	 * @param plan the plan
	 * @param asyncAccepted does the platform accept asynchronous requests
	 * @param bindResource the binding resource
	 * @param parameters the parameters
	 * @param context the context
	 * @param platformInstanceId the platform instance ID
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentity identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 */
	public CreateServiceInstanceBindingRequest(String serviceInstanceId, String serviceDefinitionId, String planId,
			String bindingId, ServiceDefinition serviceDefinition, Plan plan, boolean asyncAccepted,
			BindResource bindResource, Map<String, Object> parameters, Context context, String platformInstanceId,
			String apiInfoLocation, Context originatingIdentity, String requestIdentity) {
		super(parameters, context, asyncAccepted, platformInstanceId, apiInfoLocation, originatingIdentity,
				requestIdentity);
		this.serviceInstanceId = serviceInstanceId;
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.bindingId = bindingId;
		this.serviceDefinition = serviceDefinition;
		this.plan = plan;
		this.bindResource = bindResource;
		this.appGuid = bindResource == null ? null : bindResource.getAppGuid();
	}

	/**
	 * Get the ID of the service instance associated with the binding. This value is assigned by the platform. It must
	 * be unique within the platform and can be used to correlate any resources associated with the service instance.
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
	 * @param serviceInstanceId the service instance ID associated with the binding
	 */
	public void setServiceInstanceId(final String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	/**
	 * Get the ID of the service binding to create. This value is assigned by the platform. It must be unique within the
	 * platform and can be used to correlate any resources associated with the service binding.
	 *
	 * <p>
	 * This value is set from the {@literal :binding_id} path element of the request from the platform.
	 *
	 * @return the service instance ID
	 */
	public String getBindingId() {
		return this.bindingId;
	}

	/**
	 * This method is intended to be used internally only; use {@link #builder()} to construct an object of this type
	 * and set all field values.
	 *
	 * @param bindingId the ID of the service binding to create
	 */
	public void setBindingId(final String bindingId) {
		this.bindingId = bindingId;
	}

	/**
	 * Get the ID of the service definition for the service instance associated with the binding. This will match one of
	 * the service definition IDs provided in the {@link org.springframework.cloud.servicebroker.model.catalog.Catalog}.
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
	 * Get the ID of the plan for to the service instance associated with the binding. This will match one of the plan
	 * IDs provided in the {@link org.springframework.cloud.servicebroker.model.catalog.Catalog} within the specified
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
	 * Get the GUID of the application the service instance will be bound to. Will be provided when users bind
	 * applications to service instances, or {@literal null} if an application is not being bound.
	 *
	 * <p>
	 * This value is set from the {@literal app_guid} field in the body of the request from the platform.
	 *
	 * @return the app GUID
	 * @deprecated {@link #getBindResource()} provides platform-neutral access to binding resource details
	 */
	@Deprecated
	public String getAppGuid() {
		return this.appGuid;
	}

	/**
	 * Get any details about the resource the binding is being created for (e.g. an application).
	 *
	 * <p>
	 * This value is set from the {@literal bind_resource} field in the body of the request from the platform.
	 *
	 * @return the binding resource details
	 */
	public BindResource getBindResource() {
		return this.bindResource;
	}

	/**
	 * Get the service definition of the service instance associated with the binding.
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
	 * @param serviceDefinition the service definition of the service instance associated with the binding
	 */
	public void setServiceDefinition(final ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}

	/**
	 * Get the plan of the service instance associated with the binding.
	 *
	 * <p>
	 * The plan is retrieved from the {@link org.springframework.cloud.servicebroker.model.catalog.Catalog} as a
	 * convenience.
	 *
	 * @return the plan
	 */
	public Plan getPlan() {
		return this.plan;
	}

	/**
	 * This method is intended to be used internally only; use {@link #builder()} to construct an object of this type
	 * and set all field values.
	 *
	 * @param plan the plan of the service instance associated with the binding
	 */
	public void setPlan(final Plan plan) {
		this.plan = plan;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal CreateServiceInstanceBindingRequest}.
	 *
	 * <p>
	 * This builder is provided to support testing of {@link org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService}
	 * implementations.
	 *
	 * @return the builder
	 */
	public static CreateServiceInstanceBindingRequestBuilder builder() {
		return new CreateServiceInstanceBindingRequestBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CreateServiceInstanceBindingRequest)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		CreateServiceInstanceBindingRequest that = (CreateServiceInstanceBindingRequest) o;
		return that.canEqual(this) &&
				Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId) &&
				Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(bindingId, that.bindingId) &&
				Objects.equals(appGuid, that.appGuid) &&
				Objects.equals(bindResource, that.bindResource) &&
				Objects.equals(serviceDefinition, that.serviceDefinition) &&
				Objects.equals(plan, that.plan);
	}

	@Override
	public final boolean canEqual(Object other) {
		return other instanceof CreateServiceInstanceBindingRequest;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceDefinitionId, serviceInstanceId, planId, bindingId,
				appGuid, bindResource, serviceDefinition, plan);
	}

	@Override
	public String toString() {
		return super.toString() +
				"CreateServiceInstanceBindingRequest{" +
				"serviceDefinitionId='" + serviceDefinitionId + '\'' +
				", planId='" + planId + '\'' +
				", appGuid='" + appGuid + '\'' +
				", bindResource=" + bindResource +
				", serviceInstanceId='" + serviceInstanceId + '\'' +
				", bindingId='" + bindingId + '\'' +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link CreateServiceInstanceBindingRequest}.
	 */
	public static final class CreateServiceInstanceBindingRequestBuilder {

		private String serviceInstanceId;

		private String serviceDefinitionId;

		private String planId;

		private String bindingId;

		private ServiceDefinition serviceDefinition;

		private Plan plan;

		private boolean asyncAccepted;

		private BindResource bindResource;

		private final Map<String, Object> parameters = new HashMap<>();

		private Context context;

		private String platformInstanceId;

		private String apiInfoLocation;

		private Context originatingIdentity;

		private String requestIdentity;

		private CreateServiceInstanceBindingRequestBuilder() {
		}

		/**
		 * Set the service instance ID as would be provided in the request from the platform.
		 *
		 * @param serviceInstanceId the service instance ID
		 * @return the builder
		 * @see #getServiceInstanceId()
		 */
		public CreateServiceInstanceBindingRequestBuilder serviceInstanceId(String serviceInstanceId) {
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
		public CreateServiceInstanceBindingRequestBuilder serviceDefinitionId(String serviceDefinitionId) {
			this.serviceDefinitionId = serviceDefinitionId;
			return this;
		}

		/**
		 * Set the plan ID as would be provided in the request from the platform.
		 *
		 * @param planId the plan ID
		 * @return the builder
		 * @see #getPlanId()
		 */
		public CreateServiceInstanceBindingRequestBuilder planId(String planId) {
			this.planId = planId;
			return this;
		}

		/**
		 * Set the binding ID as would be provided in the request from the platform.
		 *
		 * @param bindingId the service binding ID
		 * @return the builder
		 * @see #getBindingId()
		 */
		public CreateServiceInstanceBindingRequestBuilder bindingId(String bindingId) {
			this.bindingId = bindingId;
			return this;
		}

		/**
		 * Set the fully resolved service definition.
		 *
		 * @param serviceDefinition the service definition
		 * @return the builder
		 * @see #getServiceDefinition()
		 */
		public CreateServiceInstanceBindingRequestBuilder serviceDefinition(ServiceDefinition serviceDefinition) {
			this.serviceDefinition = serviceDefinition;
			return this;
		}

		/**
		 * Set the fully resolved plan
		 *
		 * @param plan the plan
		 * @return the builder
		 * @see #getPlan()
		 */
		public CreateServiceInstanceBindingRequestBuilder plan(Plan plan) {
			this.plan = plan;
			return this;
		}

		/**
		 * Set the value of the flag indicating whether the platform supports asynchronous operations as would be
		 * provided in the request from the platform.
		 *
		 * @param asyncAccepted the boolean value of the flag
		 * @return the builder
		 * @see #isAsyncAccepted()
		 */
		public CreateServiceInstanceBindingRequestBuilder asyncAccepted(boolean asyncAccepted) {
			this.asyncAccepted = asyncAccepted;
			return this;
		}

		/**
		 * Set the binding resource as would be provided in the request from the platform.
		 *
		 * @param bindResource the binding resource
		 * @return the builder
		 * @see #getBindResource()
		 */
		public CreateServiceInstanceBindingRequestBuilder bindResource(BindResource bindResource) {
			this.bindResource = bindResource;
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
		public CreateServiceInstanceBindingRequestBuilder parameters(Map<String, Object> parameters) {
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
		public CreateServiceInstanceBindingRequestBuilder parameters(String key, Object value) {
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
		public CreateServiceInstanceBindingRequestBuilder context(Context context) {
			this.context = context;
			return this;
		}

		/**
		 * Set the ID of the platform instance as would be provided in the request from the platform.
		 *
		 * @param platformInstanceId the platform instance ID
		 * @return the builder
		 * @see #getPlatformInstanceId()
		 */
		public CreateServiceInstanceBindingRequestBuilder platformInstanceId(String platformInstanceId) {
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
		public CreateServiceInstanceBindingRequestBuilder apiInfoLocation(String apiInfoLocation) {
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
		public CreateServiceInstanceBindingRequestBuilder originatingIdentity(Context originatingIdentity) {
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
		public CreateServiceInstanceBindingRequestBuilder requestIdentity(String requestIdentity) {
			this.requestIdentity = requestIdentity;
			return this;
		}

		/**
		 * Construct a {@link CreateServiceInstanceBindingRequest} from the provided values.
		 *
		 * @return the newly constructed {@literal CreateServiceInstanceBindingRequest}
		 */
		public CreateServiceInstanceBindingRequest build() {
			return new CreateServiceInstanceBindingRequest(serviceInstanceId, serviceDefinitionId, planId, bindingId,
					serviceDefinition, plan, asyncAccepted, bindResource, parameters, context, platformInstanceId,
					apiInfoLocation, originatingIdentity, requestIdentity);
		}

	}

}

