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

import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;

import java.util.Objects;

/**
 * Details of a request to retrieve a service instance binding.
 *
 * <p>
 * Objects of this type are constructed by the framework from the headers, path variables, query parameters
 * and message body passed to the service broker by the platform.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md">Open Service Broker API specification</a>
 *
 * @author Scott Frederick
 */
public class GetServiceInstanceBindingRequest extends ServiceBrokerRequest {
	private transient String serviceInstanceId;

	private transient String bindingId;

	GetServiceInstanceBindingRequest(String serviceInstanceId, String bindingId,
									String platformInstanceId, String apiInfoLocation, Context originatingIdentity) {
		super(platformInstanceId, apiInfoLocation, originatingIdentity);
		this.serviceInstanceId = serviceInstanceId;
		this.bindingId = bindingId;
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
		return serviceInstanceId;
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
		return bindingId;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal GetServiceInstanceBindingRequest}.
	 *
	 * <p>
	 * This builder is provided to support testing of
	 * {@link org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService} implementations.
	 *
	 * @return the builder
	 */
	public static GetServiceInstanceBindingRequestBuilder builder() {
		return new GetServiceInstanceBindingRequestBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GetServiceInstanceBindingRequest)) return false;
		if (!super.equals(o)) return false;
		GetServiceInstanceBindingRequest that = (GetServiceInstanceBindingRequest) o;
		return Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(bindingId, that.bindingId);
	}

	@Override
	public final boolean canEqual(Object other) {
		return (other instanceof GetServiceInstanceBindingRequest);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceInstanceId, bindingId);
	}

	@Override
	public String toString() {
		return super.toString() +
				"GetServiceInstanceBindingRequest{" +
				"serviceInstanceId='" + serviceInstanceId + '\'' +
				", bindingId='" + bindingId + '\'' +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link CreateServiceInstanceBindingRequest}.
	 */
	public static class GetServiceInstanceBindingRequestBuilder {
		private String serviceInstanceId;
		private String bindingId;
		private String platformInstanceId;
		private String apiInfoLocation;
		private Context originatingIdentity;

		GetServiceInstanceBindingRequestBuilder() {
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
		 * @param bindingId the binding ID
		 * @return the builder
		 * @see #getBindingId()
		 */
		public GetServiceInstanceBindingRequestBuilder bindingId(String bindingId) {
			this.bindingId = bindingId;
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
		 * @param apiInfoLocation the API info endpoint location
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
		 * Construct a {@link GetServiceInstanceBindingRequest} from the provided values.
		 *
		 * @return the newly constructed {@literal GetServiceInstanceBindingRequest}
		 */
		public GetServiceInstanceBindingRequest build() {
			return new GetServiceInstanceBindingRequest(serviceInstanceId, bindingId,
					platformInstanceId, apiInfoLocation, originatingIdentity);
		}
	}
}
