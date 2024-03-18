/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.binding;

import java.util.Objects;

import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;

/**
 * Details of a request to retrieve a service instance binding.
 *
 * <p>
 * Objects of this type are constructed by the framework from the headers, path variables, query parameters and message
 * body passed to the service broker by the platform.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md">Open Service Broker API
 * 		specification</a>
 */
public class GetServiceInstanceBindingRequest extends ServiceBrokerRequest {

	private transient final String serviceInstanceId;

	private transient final String bindingId;

	private transient final String serviceDefinitionId;

	private transient final String planId;

	/**
	 * Construct a new {@link GetServiceInstanceBindingRequest}
	 *
	 * @param serviceInstanceId the service instance ID
	 * @param bindingId the service binding ID
	 * @param serviceDefinitionId the service definition ID
	 * @param planId the plan ID
	 * @param platformInstanceId the platform instance ID
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentity identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 */
	public GetServiceInstanceBindingRequest(String serviceInstanceId, String bindingId, String serviceDefinitionId,
			String planId, String platformInstanceId, String apiInfoLocation, Context originatingIdentity,
			String requestIdentity) {
		super(platformInstanceId, apiInfoLocation, originatingIdentity, requestIdentity);
		this.serviceInstanceId = serviceInstanceId;
		this.bindingId = bindingId;
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
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
		return serviceInstanceId;
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
		return bindingId;
	}

	/**
	 * Get the ID of the service definition of the service instance.
	 *
	 * <p>
	 * This value is set from the {@literal service_id} request parameter of the request from the platform
	 *
	 * @return the service definition ID
	 */
	public String getServiceDefinitionId() {
		return this.serviceDefinitionId;
	}

	/**
	 * Get the ID of the plan of the service instance.
	 *
	 * <p>
	 * This value is set from the {@literal plan_id} request parameter of the request from the platform.
	 *
	 * @return the plan ID
	 */
	public String getPlanId() {
		return this.planId;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal GetServiceInstanceBindingRequest}.
	 *
	 * <p>
	 * This builder is provided to support testing of {@link org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService}
	 * implementations.
	 *
	 * @return the builder
	 */
	public static GetServiceInstanceBindingRequestBuilder builder() {
		return new GetServiceInstanceBindingRequestBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof GetServiceInstanceBindingRequest)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		GetServiceInstanceBindingRequest that = (GetServiceInstanceBindingRequest) o;
		return that.canEqual(this) &&
				Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(bindingId, that.bindingId) &&
				Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId);
	}

	@Override
	public final boolean canEqual(Object other) {
		return other instanceof GetServiceInstanceBindingRequest;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceInstanceId, bindingId, serviceDefinitionId, planId);
	}

	@Override
	public String toString() {
		return super.toString() +
				"GetServiceInstanceBindingRequest{" +
				"serviceInstanceId='" + serviceInstanceId + '\'' +
				", bindingId='" + bindingId + '\'' +
				", serviceDefinitionId='" + serviceDefinitionId + '\'' +
				", planId='" + planId + '\'' +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link CreateServiceInstanceBindingRequest}.
	 */
	public static final class GetServiceInstanceBindingRequestBuilder {

		private String serviceInstanceId;

		private String bindingId;

		private String serviceDefinitionId;

		private String planId;

		private String platformInstanceId;

		private String apiInfoLocation;

		private Context originatingIdentity;

		private String requestIdentity;

		private GetServiceInstanceBindingRequestBuilder() {
		}

		/**
		 * Set the service instance ID as would be provided in the request from the platform.
		 *
		 * @param serviceInstanceId the service instance ID
		 * @return the builder
		 * @see #getServiceInstanceId()
		 */
		public GetServiceInstanceBindingRequestBuilder serviceInstanceId(String serviceInstanceId) {
			this.serviceInstanceId = serviceInstanceId;
			return this;
		}

		/**
		 * Set the binding ID as would be provided in the request from the platform.
		 *
		 * @param bindingId the service binding ID
		 * @return the builder
		 * @see #getBindingId()
		 */
		public GetServiceInstanceBindingRequestBuilder bindingId(String bindingId) {
			this.bindingId = bindingId;
			return this;
		}

		/**
		 * Set the service definition ID as would be provided in the request from the platform.
		 *
		 * @param serviceDefinitionId the service definition ID
		 * @return the builder
		 * @see #getServiceDefinitionId()
		 */
		public GetServiceInstanceBindingRequestBuilder serviceDefinitionId(String serviceDefinitionId) {
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
		public GetServiceInstanceBindingRequestBuilder planId(String planId) {
			this.planId = planId;
			return this;
		}

		/**
		 * Set the ID of the platform instance as would be provided in the request from the platform.
		 *
		 * @param platformInstanceId the platform instance ID
		 * @return the builder
		 * @see #getPlatformInstanceId()
		 */
		public GetServiceInstanceBindingRequestBuilder platformInstanceId(String platformInstanceId) {
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
		public GetServiceInstanceBindingRequestBuilder apiInfoLocation(String apiInfoLocation) {
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
		public GetServiceInstanceBindingRequestBuilder originatingIdentity(Context originatingIdentity) {
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
		public GetServiceInstanceBindingRequestBuilder requestIdentity(String requestIdentity) {
			this.requestIdentity = requestIdentity;
			return this;
		}

		/**
		 * Construct a {@link GetServiceInstanceBindingRequest} from the provided values.
		 *
		 * @return the newly constructed {@literal GetServiceInstanceBindingRequest}
		 */
		public GetServiceInstanceBindingRequest build() {
			return new GetServiceInstanceBindingRequest(serviceInstanceId, bindingId, serviceDefinitionId, planId,
					platformInstanceId, apiInfoLocation, originatingIdentity, requestIdentity);
		}

	}

}
