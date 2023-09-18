/*
 * Copyright 2002-2023 the original author or authors.
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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.cloud.servicebroker.model.AsyncServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;

/**
 * Details of a request to delete a service instance binding.
 *
 * <p>
 * Objects of this type are constructed by the framework from the headers, path variables, query parameters and message
 * body passed to the service broker by the platform.
 *
 * @author krujos
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#request-5">Open Service
 * 		Broker API specification</a>
 */
public class DeleteServiceInstanceBindingRequest extends AsyncServiceBrokerRequest {

	private transient final String serviceInstanceId;

	private transient final String bindingId;

	private transient final String serviceDefinitionId;

	private transient final String planId;

	private transient final ServiceDefinition serviceDefinition;

	private transient final Plan plan;

	/**
	 * Construct a new {@link DeleteServiceInstanceBindingRequest}
	 *
	 * @param serviceInstanceId the service instance ID
	 * @param serviceDefinitionId the service definition ID
	 * @param planId the plan ID
	 * @param bindingId the service binding ID
	 * @param serviceDefinition the service definition
	 * @param plan the plan
	 * @param acceptsAsync does the platform accept asynchronous requests
	 * @param platformInstanceId the platform instance ID
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentity identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 */
	public DeleteServiceInstanceBindingRequest(String serviceInstanceId, String serviceDefinitionId, String planId,
			String bindingId, ServiceDefinition serviceDefinition, Plan plan, boolean acceptsAsync,
			String platformInstanceId, String apiInfoLocation, Context originatingIdentity, String requestIdentity) {
		super(acceptsAsync, platformInstanceId, apiInfoLocation, originatingIdentity, requestIdentity);
		this.serviceInstanceId = serviceInstanceId;
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.bindingId = bindingId;
		this.serviceDefinition = serviceDefinition;
		this.plan = plan;
	}

	@JsonProperty(ASYNC_REQUEST_PARAMETER)
	@JsonIgnore(false)
	//in base class field is excluded, as other requests are passing this as query params
	@Override
	public boolean isAsyncAccepted() {
		return super.isAsyncAccepted();
	}

	/**
	 * Get the ID of the service instance associated with the binding.
	 *
	 * <p>
	 * This value is set from the {@literal :instance_id} path element of the request from the platform.
	 *
	 * @return the service instance ID
	 */
	@JsonIgnore // mapped as path param
	public String getServiceInstanceId() {
		return this.serviceInstanceId;
	}

	/**
	 * Get the ID of the service binding to delete.
	 *
	 * <p>
	 * This value is set from the {@literal :binding_id} path element of the request from the platform.
	 *
	 * @return the service instance ID
	 */
	@JsonIgnore // mapped as path param
	public String getBindingId() {
		return this.bindingId;
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
	@JsonProperty("service_id")
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
	@JsonProperty("plan_id")
	public String getPlanId() {
		return this.planId;
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
	@JsonIgnore /*internal field*/
	public ServiceDefinition getServiceDefinition() {
		return this.serviceDefinition;
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
	@JsonIgnore /*internal field*/
	public Plan getPlan() {
		return this.plan;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal DeleteServiceInstanceBindingRequest}.
	 *
	 * <p>
	 * This builder is provided to support testing of {@link org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService}
	 * implementations.
	 *
	 * @return the builder
	 */
	public static DeleteServiceInstanceBindingRequestBuilder builder() {
		return new DeleteServiceInstanceBindingRequestBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DeleteServiceInstanceBindingRequest)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		DeleteServiceInstanceBindingRequest that = (DeleteServiceInstanceBindingRequest) o;
		return that.canEqual(this) &&
				Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(bindingId, that.bindingId) &&
				Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId) &&
				Objects.equals(serviceDefinition, that.serviceDefinition) &&
				Objects.equals(plan, that.plan);
	}

	@Override
	public final boolean canEqual(Object other) {
		return other instanceof DeleteServiceInstanceBindingRequest;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceInstanceId, bindingId,
				serviceDefinitionId, planId, serviceDefinition, plan);
	}

	@Override
	public String toString() {
		return super.toString() +
				"DeleteServiceInstanceBindingRequest{" +
				"serviceInstanceId='" + serviceInstanceId + '\'' +
				", bindingId='" + bindingId + '\'' +
				", serviceDefinitionId='" + serviceDefinitionId + '\'' +
				", planId='" + planId + '\'' +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link DeleteServiceInstanceBindingRequest}.
	 */
	public static final class DeleteServiceInstanceBindingRequestBuilder {

		private String serviceInstanceId;

		private String serviceDefinitionId;

		private String planId;

		private String bindingId;

		private boolean asyncAccepted;

		private String platformInstanceId;

		private String apiInfoLocation;

		private Context originatingIdentity;

		private String requestIdentity;

		private ServiceDefinition serviceDefinition;

		private Plan plan;

		private DeleteServiceInstanceBindingRequestBuilder() {
		}

		/**
		 * Set the service instance ID as would be provided in the request from the platform.
		 *
		 * @param serviceInstanceId the service instance ID
		 * @return the builder
		 * @see #getServiceInstanceId()
		 */
		public DeleteServiceInstanceBindingRequestBuilder serviceInstanceId(String serviceInstanceId) {
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
		public DeleteServiceInstanceBindingRequestBuilder serviceDefinitionId(String serviceDefinitionId) {
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
		public DeleteServiceInstanceBindingRequestBuilder planId(String planId) {
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
		public DeleteServiceInstanceBindingRequestBuilder bindingId(String bindingId) {
			this.bindingId = bindingId;
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
		public DeleteServiceInstanceBindingRequestBuilder asyncAccepted(boolean asyncAccepted) {
			this.asyncAccepted = asyncAccepted;
			return this;
		}

		/**
		 * Set the fully resolved service definition.
		 *
		 * @param serviceDefinition the service definition
		 * @return the builder
		 * @see #getServiceDefinition()
		 */
		public DeleteServiceInstanceBindingRequestBuilder serviceDefinition(ServiceDefinition serviceDefinition) {
			this.serviceDefinition = serviceDefinition;
			return this;
		}

		/**
		 * Set the fully resolved plan.
		 *
		 * @param plan the plan
		 * @return the builder
		 * @see #getPlan()
		 */
		public DeleteServiceInstanceBindingRequestBuilder plan(Plan plan) {
			this.plan = plan;
			return this;
		}

		/**
		 * Set the ID of the platform instance as would be provided in the request from the platform.
		 *
		 * @param platformInstanceId the platform instance ID
		 * @return the builder
		 * @see #getPlatformInstanceId()
		 */
		public DeleteServiceInstanceBindingRequestBuilder platformInstanceId(String platformInstanceId) {
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
		public DeleteServiceInstanceBindingRequestBuilder apiInfoLocation(String apiInfoLocation) {
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
		public DeleteServiceInstanceBindingRequestBuilder originatingIdentity(Context originatingIdentity) {
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
		public DeleteServiceInstanceBindingRequestBuilder requestIdentity(String requestIdentity) {
			this.requestIdentity = requestIdentity;
			return this;
		}

		/**
		 * Construct a {@link DeleteServiceInstanceBindingRequest} from the provided values.
		 *
		 * @return the newly constructed {@literal DeleteServiceInstanceBindingRequest}
		 */
		public DeleteServiceInstanceBindingRequest build() {
			return new DeleteServiceInstanceBindingRequest(serviceInstanceId, serviceDefinitionId, planId,
					bindingId, serviceDefinition, plan, asyncAccepted, platformInstanceId, apiInfoLocation,
					originatingIdentity, requestIdentity);
		}

	}

}
