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

import java.util.Objects;

/**
 * Details of a request to get the state of the last operation on a service instance.
 *
 * @author Scott Frederick
 */
public class GetLastServiceOperationRequest extends ServiceBrokerRequest {
	/**
	 * The Cloud Controller GUID of the service instance to get the status of.
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
	 * The field optionally returned by the service broker on async provision, update, deprovision responses.
	 * Represents any state the service broker responded with as a URL encoded string. Can be <code>null</code>
	 * to indicate that an operation state is not provided.
	 */
	protected transient String operation;

	public String getServiceInstanceId() {
		return this.serviceInstanceId;
	}

	public void setServiceInstanceId(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	public String getServiceDefinitionId() {
		return this.serviceDefinitionId;
	}

	public void setServiceDefinitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public String getPlanId() {
		return this.planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getOperation() {
		return this.operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GetLastServiceOperationRequest)) return false;
		if (!super.equals(o)) return false;
		GetLastServiceOperationRequest that = (GetLastServiceOperationRequest) o;
		return Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId) &&
				Objects.equals(operation, that.operation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), serviceInstanceId, serviceDefinitionId, planId, operation);
	}

	@Override
	public String toString() {
		return "GetLastServiceOperationRequest{" +
				"serviceInstanceId='" + serviceInstanceId + '\'' +
				", serviceDefinitionId='" + serviceDefinitionId + '\'' +
				", planId='" + planId + '\'' +
				", operation='" + operation + '\'' +
				'}';
	}
}
