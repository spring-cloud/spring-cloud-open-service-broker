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
 * Details of a request to bind to a service instance binding.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"})
public class CreateServiceInstanceBindingRequest extends ServiceBrokerRequest {
	/**
	 * The ID of the service being bound, from the broker catalog.
	 */
	@NotEmpty
	@JsonProperty("service_id")
	private final String serviceDefinitionId;

	/**
	 * The ID of the plan being bound within the service, from the broker catalog.
	 */
	@NotEmpty
	private final String planId;

	/**
	 * The Cloud Controller GUID of the application the service instance will be bound to. Will be provided when
	 * users bind applications to service instances, or <code>null</code> if an application is not being bound.
	 *
	 * @deprecated The <code>bindResource</code> field will contain references to the resource being bound, and should
	 * be used instead of this field.
	 */
	@Deprecated
	private final String appGuid;

	/**
	 * The resource being bound to the service instance.
	 */
	private final BindResource bindResource;

	/**
	 * Parameters passed by the user in the form of a JSON structure. The service broker is responsible
	 * for validating the contents of the parameters for correctness or applicability.
	 */
	private final Map<String, Object> parameters;

	/**
	 * Platform specific contextual information under which the service instance is to be bound.
	 */
	private final Context context;

	/**
	 * The Cloud Controller GUID of the service instance to being bound.
	 */
	private transient String serviceInstanceId;

	/**
	 * The Cloud Controller GUID of the service binding being created. This ID will be used for future
	 * requests for the same service instance binding, so the broker must use it to correlate any resource it creates.
	 */
	private transient String bindingId;

	/**
	 * The {@link ServiceDefinition} of the service to provision. This is resolved from the
	 * <code>serviceDefinitionId</code> as a convenience to the broker.
	 */
	private transient ServiceDefinition serviceDefinition;

	CreateServiceInstanceBindingRequest() {
		serviceDefinitionId = null;
		planId = null;
		appGuid = null;
		bindResource = null;
		context = null;
		parameters = null;
	}

	CreateServiceInstanceBindingRequest(String serviceDefinitionId, String planId,
												BindResource bindResource, Map<String, Object> parameters,
												Context context) {
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.parameters = parameters;
		this.bindResource = bindResource;
		this.appGuid = (bindResource == null ? null : bindResource.getAppGuid());
		this.context = context;
	}

	public <T> T getParameters(Class<T> cls) {
		return ParameterBeanMapper.mapParametersToBean(parameters, cls);
	}

	public String getServiceDefinitionId() {
		return this.serviceDefinitionId;
	}

	public String getPlanId() {
		return this.planId;
	}

	@Deprecated
	public String getAppGuid() {
		return this.appGuid;
	}

	public BindResource getBindResource() {
		return this.bindResource;
	}

	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	public Context getContext() {
		return this.context;
	}

	public String getServiceInstanceId() {
		return this.serviceInstanceId;
	}

	public void setServiceInstanceId(final String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	public String getBindingId() {
		return this.bindingId;
	}

	public void setBindingId(final String bindingId) {
		this.bindingId = bindingId;
	}

	public ServiceDefinition getServiceDefinition() {
		return this.serviceDefinition;
	}

	public void setServiceDefinition(final ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}

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

	public static class CreateServiceInstanceBindingRequestBuilder {
		private String serviceDefinitionId;
		private String planId;
		private BindResource bindResource;
		private final Map<String, Object> parameters = new HashMap<>();
		private Context context;

		CreateServiceInstanceBindingRequestBuilder() {
		}

		public CreateServiceInstanceBindingRequestBuilder serviceDefinitionId(String serviceDefinitionId) {
			this.serviceDefinitionId = serviceDefinitionId;
			return this;
		}

		public CreateServiceInstanceBindingRequestBuilder planId(String planId) {
			this.planId = planId;
			return this;
		}

		public CreateServiceInstanceBindingRequestBuilder bindResource(BindResource bindResource) {
			this.bindResource = bindResource;
			return this;
		}

		public CreateServiceInstanceBindingRequestBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		public CreateServiceInstanceBindingRequestBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		public CreateServiceInstanceBindingRequestBuilder context(Context context) {
			this.context = context;
			return this;
		}

		public CreateServiceInstanceBindingRequest build() {
			return new CreateServiceInstanceBindingRequest(serviceDefinitionId, planId, bindResource,
					parameters, context);
		}
	}
}

