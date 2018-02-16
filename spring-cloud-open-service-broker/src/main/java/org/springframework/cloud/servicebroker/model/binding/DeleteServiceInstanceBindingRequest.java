/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;

import java.util.Objects;

/**
 *  Details of a request to delete a service instance binding.
 *
 * @author krujos
 * @author Scott Frederick
 */
public class DeleteServiceInstanceBindingRequest extends ServiceBrokerRequest {

	/**
	 * The Cloud Controller GUID of the service instance to being unbound.
	 */
	private transient String serviceInstanceId;

	/**
	 * The Cloud Controller GUID of the service binding being deleted.
	 */
	private transient String bindingId;

	private transient String serviceDefinitionId;

	private transient String planId;

	private transient ServiceDefinition serviceDefinition;

	DeleteServiceInstanceBindingRequest(String serviceInstanceId, String serviceDefinitionId, String planId,
										String bindingId, ServiceDefinition serviceDefinition,
										String platformInstanceId, String apiInfoLocation, Context originatingIdentity) {
		super(platformInstanceId, apiInfoLocation, originatingIdentity);
		this.serviceInstanceId = serviceInstanceId;
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.bindingId = bindingId;
		this.serviceDefinition = serviceDefinition;
	}

	public String getServiceInstanceId() {
		return this.serviceInstanceId;
	}

	public String getBindingId() {
		return this.bindingId;
	}

	public String getServiceDefinitionId() {
		return this.serviceDefinitionId;
	}

	public String getPlanId() {
		return this.planId;
	}

	public ServiceDefinition getServiceDefinition() {
		return this.serviceDefinition;
	}

	public static DeleteServiceInstanceBindingRequestBuilder builder() {
		return new DeleteServiceInstanceBindingRequestBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DeleteServiceInstanceBindingRequest)) return false;
		if (!super.equals(o)) return false;
		DeleteServiceInstanceBindingRequest that = (DeleteServiceInstanceBindingRequest) o;
		return that.canEqual(this) &&
				Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(bindingId, that.bindingId) &&
				Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId);
	}

	@Override
	public final boolean canEqual(Object other) {
		return (other instanceof DeleteServiceInstanceBindingRequest);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceInstanceId, bindingId,
				serviceDefinitionId, planId);
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

	public static class DeleteServiceInstanceBindingRequestBuilder {
		private String serviceInstanceId;
		private String serviceDefinitionId;
		private String planId;
		private String bindingId;
		private String platformInstanceId;
		private String apiInfoLocation;
		private Context originatingIdentity;
		private ServiceDefinition serviceDefinition;

		DeleteServiceInstanceBindingRequestBuilder() {
		}

		public DeleteServiceInstanceBindingRequestBuilder serviceInstanceId(String serviceInstanceId) {
			this.serviceInstanceId = serviceInstanceId;
			return this;
		}

		public DeleteServiceInstanceBindingRequestBuilder serviceDefinitionId(String serviceDefinitionId) {
			this.serviceDefinitionId = serviceDefinitionId;
			return this;
		}

		public DeleteServiceInstanceBindingRequestBuilder planId(String planId) {
			this.planId = planId;
			return this;
		}

		public DeleteServiceInstanceBindingRequestBuilder bindingId(String bindingId) {
			this.bindingId = bindingId;
			return this;
		}

		public DeleteServiceInstanceBindingRequestBuilder serviceDefinition(ServiceDefinition serviceDefinition) {
			this.serviceDefinition = serviceDefinition;
			return this;
		}

		public DeleteServiceInstanceBindingRequestBuilder platformInstanceId(String platformInstanceId) {
			this.platformInstanceId = platformInstanceId;
			return this;
		}

		public DeleteServiceInstanceBindingRequestBuilder apiInfoLocation(String apiInfoLocation) {
			this.apiInfoLocation = apiInfoLocation;
			return this;
		}

		public DeleteServiceInstanceBindingRequestBuilder originatingIdentity(Context originatingIdentity) {
			this.originatingIdentity = originatingIdentity;
			return this;
		}

		public DeleteServiceInstanceBindingRequest build() {
			return new DeleteServiceInstanceBindingRequest(serviceInstanceId, serviceDefinitionId, planId,
					bindingId, serviceDefinition,
					platformInstanceId, apiInfoLocation, originatingIdentity);
		}
	}
}
