/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
 * Details of a request to get the state of the last operation on a service instance binding.
 *
 * <p>
 * Objects of this type are constructed by the framework from the headers, path variables, query parameters
 * and message body passed to the service broker by the platform.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#request-2">Open Service Broker API specification</a>
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 */
public class GetLastServiceBindingOperationRequest extends ServiceBrokerRequest {

	private transient String serviceInstanceId;

	private transient String bindingId;

	private transient String serviceDefinitionId;

	private transient String planId;

	protected transient String operation;

	GetLastServiceBindingOperationRequest(String serviceInstanceId, String bindingId, String serviceDefinitionId,
										  String planId, String operation, String platformInstanceId,
										  String apiInfoLocation, Context originatingIdentity) {
		super(platformInstanceId, apiInfoLocation, originatingIdentity);
		this.serviceInstanceId = serviceInstanceId;
		this.bindingId = bindingId;
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.operation = operation;
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
	 * Get the ID of the service binding. This value is assigned by the platform.
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
	 * Get the value of the {@literal operation} field previously provided to the platform.
	 *
	 * Service brokers can optionally return an {@literal operation} value to the platform in the response from an
	 * async create, update, or delete request. The platform will pass this value back to the broker if provided.
	 *
	 * <p>
	 * This value is set from the {@literal operation} request parameter of the request from the platform.
	 *
	 * @return the operation value
	 */
	public String getOperation() {
		return this.operation;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal GetLastServiceOperationRequestBuilder}.
	 *
	 * <p>
	 * This builder is provided to support testing of
	 * {@link org.springframework.cloud.servicebroker.service.ServiceInstanceService} implementations.
	 *
	 * @return the builder
	 */
	public static GetLastServiceBindingOperationRequestBuilder builder() {
		return new GetLastServiceBindingOperationRequestBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GetLastServiceBindingOperationRequest)) return false;
		if (!super.equals(o)) return false;
		GetLastServiceBindingOperationRequest that = (GetLastServiceBindingOperationRequest) o;
		return Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(bindingId, that.bindingId) &&
				Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId) &&
				Objects.equals(operation, that.operation);
	}

	@Override
	public final boolean canEqual(Object other) {
		return (other instanceof GetLastServiceBindingOperationRequest);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceInstanceId, bindingId, serviceDefinitionId, planId, operation);
	}

	@Override
	public String toString() {
		return super.toString() +
				"GetLastServiceBindingOperationRequest{" +
				"serviceInstanceId='" + serviceInstanceId + '\'' +
				"bindingId='" + bindingId + '\'' +
				", serviceDefinitionId='" + serviceDefinitionId + '\'' +
				", planId='" + planId + '\'' +
				", operation='" + operation + '\'' +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link GetLastServiceBindingOperationRequest}.
	 */
	public static class GetLastServiceBindingOperationRequestBuilder {
		private String serviceInstanceId;
		private String bindingId;
		private String serviceDefinitionId;
		private String planId;
		private String operation;
		private String platformInstanceId;
		private String apiInfoLocation;
		private Context originatingIdentity;

		GetLastServiceBindingOperationRequestBuilder() {
		}

		/**
		 * Set the service instance ID as would be provided in the request from the platform.
		 *
		 * @param serviceInstanceId the service instance ID
		 * @return the builder
		 * @see #getServiceInstanceId()
		 */
		public GetLastServiceBindingOperationRequestBuilder serviceInstanceId(String serviceInstanceId) {
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
		public GetLastServiceBindingOperationRequestBuilder bindingId(String bindingId) {
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
		public GetLastServiceBindingOperationRequestBuilder serviceDefinitionId(String serviceDefinitionId) {
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
		public GetLastServiceBindingOperationRequestBuilder planId(String planId) {
			this.planId = planId;
			return this;
		}

		/**
		 * Set the operation as would be provided in the request from the platform.
		 *
		 * @param operation the operation
		 * @return the builder
		 */
		public GetLastServiceBindingOperationRequestBuilder operation(String operation) {
			this.operation = operation;
			return this;
		}

		/**
		 * Set the ID of the platform instance as would be provided in the request from the platform.
		 *
		 * @param platformInstanceId the platform instance ID
		 * @return the builder
		 * @see #getPlatformInstanceId()
		 */
		public GetLastServiceBindingOperationRequestBuilder platformInstanceId(String platformInstanceId) {
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
		public GetLastServiceBindingOperationRequestBuilder apiInfoLocation(String apiInfoLocation) {
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
		public GetLastServiceBindingOperationRequestBuilder originatingIdentity(Context originatingIdentity) {
			this.originatingIdentity = originatingIdentity;
			return this;
		}

		/**
		 * Construct a {@link GetLastServiceBindingOperationRequest} from the provided values.
		 *
		 * @return the newly constructed {@literal GetLastServiceOperationRequest}
		 */
		public GetLastServiceBindingOperationRequest build() {
			return new GetLastServiceBindingOperationRequest(serviceInstanceId, bindingId, serviceDefinitionId, planId,
					operation, platformInstanceId, apiInfoLocation, originatingIdentity);
		}
	}

}
