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
 * that describe a Service binding JSON Schemas.
 *
 * @author sgunaratne@pivotal.io
 * @author Sam Gunaratne
 * @author Roy Clarkson
 * @see org.springframework.cloud.servicebroker.model.catalog.ServiceBindingSchema
 */
class ServiceBindingSchema {

	/**
	 * The JSON schema for configuration parameters when creating a service binding.
	 */
	@NestedConfigurationProperty
	private MethodSchema create;

	public MethodSchema getCreate() {
		return this.create;
	}

	public void setCreate(MethodSchema create) {
		this.create = create;
	}

	/**
	 * Converts this object to its corresponding model
	 *
	 * @return a ServiceBindingSchema model
	 * @see org.springframework.cloud.servicebroker.model.catalog.ServiceBindingSchema
	 */
	public org.springframework.cloud.servicebroker.model.catalog.ServiceBindingSchema toModel() {
		return org.springframework.cloud.servicebroker.model.catalog.ServiceBindingSchema.builder()
				.createMethodSchema(this.create != null ? this.create.toModel() : null)
				.build();
	}

}
