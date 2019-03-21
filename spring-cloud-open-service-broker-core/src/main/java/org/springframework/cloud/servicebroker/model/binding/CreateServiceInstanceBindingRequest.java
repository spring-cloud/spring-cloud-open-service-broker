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

package org.springframework.cloud.servicebroker.model.binding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.util.ParameterBeanMapper;

/**
 * Details of a request to create a service instance binding.
 *
 * <p>
 * Objects of this type are constructed by the framework from the headers, path variables, query parameters
 * and message body passed to the service broker by the platform.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#request-4">Open Service Broker API specification</a>
 * 
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"})
public class CreateServiceInstanceBindingRequest extends ServiceBrokerRequest {
	private transient String serviceInstanceId;

	private transient String bindingId;

	@NotEmpty
	@JsonProperty("service_id")
	private final String serviceDefinitionId;

	@NotEmpty
	private final String planId;

	@Deprecated
	private final String appGuid;

	private final BindResource bindResource;

	private final Map<String, Object> parameters;

	private final Context context;

	private transient ServiceDefinition serviceDefinition;

	@SuppressWarnings("unused")
	CreateServiceInstanceBindingRequest() {
		serviceDefinitionId = null;
		planId = null;
		appGuid = null;
		bindResource = null;
		context = null;
		parameters = null;
	}

	CreateServiceInstanceBindingRequest(String serviceInstanceId, String serviceDefinitionId, String planId,
										String bindingId, ServiceDefinition serviceDefinition,
										BindResource bindResource,
										Map<String, Object> parameters, Context context,
										String platformInstanceId, String apiInfoLocation, Context originatingIdentity) {
		super(platformInstanceId, apiInfoLocation, originatingIdentity);
		this.serviceInstanceId = serviceInstanceId;
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.bindingId = bindingId;
		this.serviceDefinition = serviceDefinition;
		this.parameters = parameters;
		this.bindResource = bindResource;
		this.appGuid = (bindResource == null ? null : bindResource.getAppGuid());
		this.context = context;
	}

	/**
	 * Get the ID of the service instance associated with the binding. This value is assigned by the platform.
	 * It must be unique within the platform and can be used to correlate any resources associated with the
	 * service instance.
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
	 * @param serviceInstanceId the ID of the service instance associated with the binding
	 */
	public void setServiceInstanceId(final String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	/**
	 * Get the ID of the service binding to create. This value is assigned by the platform.
	 * It must be unique within the platform and can be used to correlate any resources associated with the
	 * service binding.
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
	 * This method is intended to be used internally only; use {@link #builder()} to construct an object of this
	 * type and set all field values.
	 *
	 * @param bindingId the ID of the service binding to create
	 */
	public void setBindingId(final String bindingId) {
		this.bindingId = bindingId;
	}

	/**
	 * Get the ID of the service definition for the service instance associated with the binding. This will match one
	 * of the service definition IDs provided in the
	 * {@link org.springframework.cloud.servicebroker.model.catalog.Catalog}.
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
	 * Get the ID of the plan for to the service instance associated with the binding. This will match one of the
	 * plan IDs provided in the {@link org.springframework.cloud.servicebroker.model.catalog.Catalog} within
	 * the specified {@link ServiceDefinition}.
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
	 * Get the GUID of the application the service instance will be bound to. Will be provided when
	 * users bind applications to service instances, or {@literal null} if an application is not being bound.
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
	 * Get any parameters passed by the user, with the user-supplied JSON structure converted to a {@literal Map}.
	 *
	 * <p>
	 * This value is set from the {@literal parameters} field in the body of the request from the platform.
	 *
	 * <p>
	 * The platform will pass the user-supplied JSON structure to the service broker as-is. The service broker is
	 * responsible for validating the contents of the parameters for correctness or applicability.
	 *
	 * @return the populated {@literal Map}
	 */
	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	/**
	 * Get any parameters passed by the user, with the user-supplied JSON structure mapped to fields of the specified
	 * object type.
	 *
	 * <p>
	 * This value is set from the {@literal parameters} field in the body of the request from the platform.
	 *
	 * <p>
	 * An object of the specified type will be instantiated, and value from the parameters JSON will be mapped
	 * to the object using Java Bean mapping rules.
	 *
	 * <p>
	 * The platform will pass the user-supplied JSON structure to the service broker as-is. The service broker is
	 * responsible for validating the contents of the parameters for correctness or applicability.
	 *
	 * @param cls the {@link Class} representing the type of object to map the parameter key/value pairs to
	 * @param <T> the type of the object to instantiate and populate
	 * @return the instantiated and populated object
	 */
	public <T> T getParameters(Class<T> cls) {
		return ParameterBeanMapper.mapParametersToBean(parameters, cls);
	}

	/**
	 * Get the platform-specific contextual information for the service binding.
	 *
	 * <p>
	 * This value is set from the {@literal context} field in the body of the request from the platform.
	 *
	 * @return the contextual information
	 */
	public Context getContext() {
		return this.context;
	}

	/**
	 * Get the service definition of the service instance associated with the binding.
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
	 * @param serviceDefinition the service definition of the service instance associated with the binding
	 */
	public void setServiceDefinition(final ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal CreateServiceInstanceBindingRequest}.
	 *
	 * <p>
	 * This builder is provided to support testing of
	 * {@link org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService} implementations.
	 *
	 * @return the builder
	 */
	public static CreateServiceInstanceBindingRequestBuilder builder() {
		return new CreateServiceInstanceBindingRequestBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CreateServiceInstanceBindingRequest)) return false;
		if (!super.equals(o)) return false;
		CreateServiceInstanceBindingRequest that = (CreateServiceInstanceBindingRequest) o;
		return that.canEqual(this) &&
				Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId) &&
				Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(bindingId, that.bindingId) &&
				Objects.equals(appGuid, that.appGuid) &&
				Objects.equals(bindResource, that.bindResource) &&
				Objects.equals(parameters, that.parameters) &&
				Objects.equals(context, that.context);
	}

	@Override
	public final boolean canEqual(Object other) {
		return (other instanceof CreateServiceInstanceBindingRequest);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceDefinitionId, serviceInstanceId, planId, bindingId,
				appGuid, bindResource, parameters, context);
	}

	@Override
	public String toString() {
		return super.toString() +
				"CreateServiceInstanceBindingRequest{" +
				"serviceDefinitionId='" + serviceDefinitionId + '\'' +
				", planId='" + planId + '\'' +
				", appGuid='" + appGuid + '\'' +
				", bindResource=" + bindResource +
				", parameters=" + parameters +
				", context=" + context +
				", serviceInstanceId='" + serviceInstanceId + '\'' +
				", bindingId='" + bindingId + '\'' +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link CreateServiceInstanceBindingRequest}.
	 */
	public static class CreateServiceInstanceBindingRequestBuilder {
		private String serviceInstanceId;
		private String serviceDefinitionId;
		private String planId;
		private String bindingId;
		private ServiceDefinition serviceDefinition;
		private BindResource bindResource;
		private final Map<String, Object> parameters = new HashMap<>();
		private Context context;
		private String platformInstanceId;
		private String apiInfoLocation;
		private Context originatingIdentity;

		CreateServiceInstanceBindingRequestBuilder() {
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
		 * @param bindingId the binding ID
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
		 * Add a set of parameters from the provided {@literal Map} to the request parameters
		 * as would be provided in the request from the platform.
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
		 * @param apiInfoLocation the API info endpoint location
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
		 * Construct a {@link CreateServiceInstanceBindingRequest} from the provided values.
		 *
		 * @return the newly constructed {@literal CreateServiceInstanceBindingRequest}
		 */
		public CreateServiceInstanceBindingRequest build() {
			return new CreateServiceInstanceBindingRequest(serviceInstanceId, serviceDefinitionId, planId,
					bindingId, serviceDefinition, bindResource, parameters, context,
					platformInstanceId, apiInfoLocation, originatingIdentity);
		}
	}
}

