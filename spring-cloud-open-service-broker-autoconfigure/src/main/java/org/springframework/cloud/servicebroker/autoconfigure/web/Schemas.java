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

package org.springframework.cloud.servicebroker.autoconfigure.web;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Internal class for marshaling {@link ServiceBrokerProperties} configuration properties
 * that describes a JSON Schemas available for a {@link Plan}.
 *
 * @author sgunaratne@pivotal.io
 * @author Sam Gunaratne
 * @author Roy Clarkson
 * @see org.springframework.cloud.servicebroker.model.catalog.Schemas
 */
class Schemas {

	/**
	 * The schemas available on a service instance.
	 */
	@NestedConfigurationProperty
	private ServiceInstanceSchema serviceInstance;

	/**
	 * The schemas available on a service binding.
	 */
	@NestedConfigurationProperty
	private ServiceBindingSchema serviceBinding;

	public ServiceInstanceSchema getServiceInstance() {
		return this.serviceInstance;
	}

	public void setServiceInstance(ServiceInstanceSchema serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	public ServiceBindingSchema getServiceBinding() {
		return this.serviceBinding;
	}

	public void setServiceBinding(ServiceBindingSchema serviceBinding) {
		this.serviceBinding = serviceBinding;
	}

	/**
	 * Converts this object into its corresponding model
	 *
	 * @return a Schemas model
	 * @see org.springframework.cloud.servicebroker.model.catalog.Schemas
	 */
	public org.springframework.cloud.servicebroker.model.catalog.Schemas toModel() {
		return org.springframework.cloud.servicebroker.model.catalog.Schemas.builder()
				.serviceInstanceSchema(this.serviceInstance != null ? this.serviceInstance.toModel() : null)
				.serviceBindingSchema(this.serviceBinding != null ? this.serviceBinding.toModel() : null)
				.build();
	}

}
