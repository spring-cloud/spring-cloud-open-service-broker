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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The catalog of services offered by the service broker.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 */
class Catalog {

	/**
	 * A list of service offerings provided by the service broker.
	 */
	private List<ServiceDefinition> services = new ArrayList<>();

	public List<ServiceDefinition> getServices() {
		return this.services;
	}

	public void setServices(List<ServiceDefinition> services) {
		this.services.addAll(services);
	}

	public org.springframework.cloud.servicebroker.model.catalog.Catalog toModel() {
		List<org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition> modelServiceDefinitions =
				this.services.stream()
						.map(ServiceDefinition::toModel)
						.collect(Collectors.toList());

		return org.springframework.cloud.servicebroker.model.catalog.Catalog.builder()
				.serviceDefinitions(modelServiceDefinitions)
				.build();
	}

}
