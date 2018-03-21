/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Service instance JSON Schemas.
 *
 * @author Sam Gunaratne
 * @author Roy Clarkson
 */
class ServiceInstanceSchema {

	/**
	 * The JSON schema for configuration parameters when creating a service instance.
	 */
	@NestedConfigurationProperty
	private MethodSchema create;

	/**
	 * The JSON schema for configuration parameters when updating a service instance.
	 */
	@NestedConfigurationProperty
	private MethodSchema update;

	public MethodSchema getCreate() {
		return this.create;
	}

	public void setCreate(MethodSchema create) {
		this.create = create;
	}

	public MethodSchema getUpdate() {
		return this.update;
	}

	public void setUpdate(MethodSchema update) {
		this.update = update;
	}

	public org.springframework.cloud.servicebroker.model.catalog.ServiceInstanceSchema toModel() {
		return org.springframework.cloud.servicebroker.model.catalog.ServiceInstanceSchema.builder()
				.createMethodSchema(this.create != null ? this.create.toModel() : null)
				.updateMethodSchema(this.update != null ? this.update.toModel() : null)
				.build();
	}

}
