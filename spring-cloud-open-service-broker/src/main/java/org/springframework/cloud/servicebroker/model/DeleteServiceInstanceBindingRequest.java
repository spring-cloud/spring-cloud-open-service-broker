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

	public DeleteServiceInstanceBindingRequest() {
	}

	public String getServiceInstanceId() {
		return this.serviceInstanceId;
	}

	public void setServiceInstanceId(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	public String getBindingId() {
		return this.bindingId;
	}

	public void setBindingId(String bindingId) {
		this.bindingId = bindingId;
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

	public ServiceDefinition getServiceDefinition() {
		return this.serviceDefinition;
	}

	public void setServiceDefinition(ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
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

}
