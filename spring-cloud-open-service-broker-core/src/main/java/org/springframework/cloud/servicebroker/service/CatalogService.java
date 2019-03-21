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

package org.springframework.cloud.servicebroker.service;

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;

/**
 * This interface is implemented by service brokers to process requests to retrieve the service catalog.
 *
 * @author sgreenberg@pivotal.io
 */
public interface CatalogService {

	/**
	 * Return the catalog of services provided by the service broker.
	 *
	 * @return the catalog of services
	 */
	Mono<Catalog> getCatalog();

	/**
	 * Get a service definition from the catalog by ID.
	 *
	 * @param serviceId  The ID of the service definition in the catalog
	 * @return the service definition, or null if it doesn't exist
	 */
	Mono<ServiceDefinition> getServiceDefinition(String serviceId);

}
