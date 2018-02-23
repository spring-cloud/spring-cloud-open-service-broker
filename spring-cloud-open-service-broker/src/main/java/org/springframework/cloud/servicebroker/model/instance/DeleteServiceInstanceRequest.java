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

package org.springframework.cloud.servicebroker.model.instance;

import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;

import java.util.Objects;

/**
 * Details of a request to delete a service instance.
 *
 * @author krujos
 * @author Scott Frederick
 */
public class DeleteServiceInstanceRequest extends AsyncServiceInstanceRequest {
	/**
	 * The Cloud Controller GUID of the service instance to deprovision.
	 */
	private transient String serviceInstanceId;

	/**
	 * The ID of the service to deprovision, from the broker catalog.
	 */
	private transient String serviceDefinitionId;

	/**
	 * The ID of the plan to deprovision within the service, from the broker catalog.
	 */
	private transient String planId;

	/**
	 * The {@link ServiceDefinition} of the service to deprovision. This is resolved from the
	 * <code>serviceDefinitionId</code> as a convenience to the broker.
	 */
	private transient ServiceDefinition serviceDefinition;

	DeleteServiceInstanceRequest(String serviceInstanceId, String serviceDefinitionId,
								 String planId, ServiceDefinition serviceDefinition,
								 boolean asyncAccepted, String platformInstanceId,
								 String apiInfoLocation, Context originatingIdentity) {
		super(asyncAccepted, platformInstanceId, apiInfoLocation, originatingIdentity);
		this.serviceInstanceId = serviceInstanceId;
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.serviceDefinition = serviceDefinition;
	}

	public String getServiceInstanceId() {
		return this.serviceInstanceId;
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

	public static DeleteServiceInstanceRequestBuilder builder() {
		return new DeleteServiceInstanceRequestBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DeleteServiceInstanceRequest)) return false;
		if (!super.equals(o)) return false;
		DeleteServiceInstanceRequest that = (DeleteServiceInstanceRequest) o;
		return that.canEqual(this) &&
				Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId) &&
				Objects.equals(serviceDefinition, that.serviceDefinition);
	}

	@Override
	public final boolean canEqual(Object other) {
		return (other instanceof DeleteServiceInstanceRequest);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceInstanceId,
				serviceDefinitionId, planId, serviceDefinition);
	}

	@Override
	public String toString() {
		return super.toString() +
				"DeleteServiceInstanceRequest{" +
				"serviceInstanceId='" + serviceInstanceId + '\'' +
				", serviceDefinitionId='" + serviceDefinitionId + '\'' +
				", planId='" + planId + '\'' +
				'}';
	}

	public static class DeleteServiceInstanceRequestBuilder {
		private String serviceInstanceId;
		private String serviceDefinitionId;
		private ServiceDefinition serviceDefinition;
		private String planId;
		private boolean asyncAccepted;
		private String platformInstanceId;
		private String apiInfoLocation;
		private Context originatingIdentity;

		DeleteServiceInstanceRequestBuilder() {
		}

		public DeleteServiceInstanceRequestBuilder serviceInstanceId(String serviceInstanceId) {
			this.serviceInstanceId = serviceInstanceId;
			return this;
		}

		public DeleteServiceInstanceRequestBuilder serviceDefinitionId(String serviceDefinitionId) {
			this.serviceDefinitionId = serviceDefinitionId;
			return this;
		}

		public DeleteServiceInstanceRequestBuilder serviceDefinition(ServiceDefinition serviceDefinition) {
			this.serviceDefinition = serviceDefinition;
			return this;
		}

		public DeleteServiceInstanceRequestBuilder planId(String planId) {
			this.planId = planId;
			return this;
		}

		public DeleteServiceInstanceRequestBuilder asyncAccepted(boolean asyncAccepted) {
			this.asyncAccepted = asyncAccepted;
			return this;
		}

		public DeleteServiceInstanceRequestBuilder platformInstanceId(String platformInstanceId) {
			this.platformInstanceId = platformInstanceId;
			return this;
		}

		public DeleteServiceInstanceRequestBuilder apiInfoLocation(String apiInfoLocation) {
			this.apiInfoLocation = apiInfoLocation;
			return this;
		}

		public DeleteServiceInstanceRequestBuilder originatingIdentity(Context originatingIdentity) {
			this.originatingIdentity = originatingIdentity;
			return this;
		}

		public DeleteServiceInstanceRequest build() {
			return new DeleteServiceInstanceRequest(serviceInstanceId, serviceDefinitionId, planId,
					serviceDefinition, asyncAccepted,
					platformInstanceId, apiInfoLocation, originatingIdentity);
		}
	}

}
