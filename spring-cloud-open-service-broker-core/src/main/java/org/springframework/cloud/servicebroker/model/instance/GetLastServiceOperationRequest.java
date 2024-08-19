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

package org.springframework.cloud.servicebroker.model.instance;

import java.util.Objects;

import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;

/**
 * Details of a request to get the state of the last operation on a service instance.
 *
 * <p>
 * Objects of this type are constructed by the framework from the headers, path variables, query parameters and message
 * body passed to the service broker by the platform.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#request-1">Open Service
 * 		Broker API specification</a>
 */
public class GetLastServiceOperationRequest extends ServiceBrokerRequest {

	private transient final String serviceInstanceId;

	private transient final String serviceDefinitionId;

	private transient final String planId;

	protected transient final String operation;

	/**
	 * Construct a new {@link GetLastServiceOperationRequest}
	 *
	 * @param serviceInstanceId the service instance ID
	 * @param serviceDefinitionId the service definition ID
	 * @param planId the plan ID
	 * @param operation description of the operation being performed
	 * @param platformInstanceId the platform instance ID
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentity identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 */
	public GetLastServiceOperationRequest(String serviceInstanceId, String serviceDefinitionId, String planId,
			String operation, String platformInstanceId, String apiInfoLocation, Context originatingIdentity,
			String requestIdentity) {
		super(platformInstanceId, apiInfoLocation, originatingIdentity, requestIdentity);
		this.serviceInstanceId = serviceInstanceId;
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.operation = operation;
	}

	/**
	 * Get the ID of the service instance.
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
	 * <p>
	 * Service brokers can optionally return an {@literal operation} value to the platform in the response from an async
	 * create, update, or delete request. The platform will pass this value back to the broker if provided.
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
	 * This builder is provided to support testing of {@link org.springframework.cloud.servicebroker.service.ServiceInstanceService}
	 * implementations.
	 *
	 * @return the builder
	 */
	public static GetLastServiceOperationRequestBuilder builder() {
		return new GetLastServiceOperationRequestBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof GetLastServiceOperationRequest)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		GetLastServiceOperationRequest that = (GetLastServiceOperationRequest) o;
		return that.canEqual(this) &&
				Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId) &&
				Objects.equals(operation, that.operation);
	}

	@Override
	public final boolean canEqual(Object other) {
		return other instanceof GetLastServiceOperationRequest;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), serviceInstanceId, serviceDefinitionId, planId, operation);
	}

	@Override
	public String toString() {
		return super.toString() +
				"GetLastServiceOperationRequest{" +
				"serviceInstanceId='" + serviceInstanceId + '\'' +
				", serviceDefinitionId='" + serviceDefinitionId + '\'' +
				", planId='" + planId + '\'' +
				", operation='" + operation + '\'' +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link GetLastServiceOperationRequest}.
	 */
	public static final class GetLastServiceOperationRequestBuilder {

		private String serviceInstanceId;

		private String serviceDefinitionId;

		private String planId;

		private String operation;

		private String platformInstanceId;

		private String apiInfoLocation;

		private Context originatingIdentity;

		private String requestIdentity;

		private GetLastServiceOperationRequestBuilder() {
		}

		/**
		 * Set the service instance ID as would be provided in the request from the platform.
		 *
		 * @param serviceInstanceId the service instance ID
		 * @return the builder
		 * @see #getServiceInstanceId()
		 */
		public GetLastServiceOperationRequestBuilder serviceInstanceId(String serviceInstanceId) {
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
		public GetLastServiceOperationRequestBuilder serviceDefinitionId(String serviceDefinitionId) {
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
		public GetLastServiceOperationRequestBuilder planId(String planId) {
			this.planId = planId;
			return this;
		}

		/**
		 * Set the operation as would be provided in the request from the platform.
		 *
		 * @param operation the operation
		 * @return the builder
		 */
		public GetLastServiceOperationRequestBuilder operation(String operation) {
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
		public GetLastServiceOperationRequestBuilder platformInstanceId(String platformInstanceId) {
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
		public GetLastServiceOperationRequestBuilder apiInfoLocation(String apiInfoLocation) {
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
		public GetLastServiceOperationRequestBuilder originatingIdentity(Context originatingIdentity) {
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
		public GetLastServiceOperationRequestBuilder requestIdentity(String requestIdentity) {
			this.requestIdentity = requestIdentity;
			return this;
		}

		/**
		 * Construct a {@link GetLastServiceOperationRequest} from the provided values.
		 *
		 * @return the newly constructed {@literal GetLastServiceOperationRequest}
		 */
		public GetLastServiceOperationRequest build() {
			return new GetLastServiceOperationRequest(serviceInstanceId, serviceDefinitionId, planId,
					operation, platformInstanceId, apiInfoLocation, originatingIdentity, requestIdentity);
		}

	}

}
