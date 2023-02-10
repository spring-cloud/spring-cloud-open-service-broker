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

package org.springframework.cloud.servicebroker.service;

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 * This interface is implemented by service brokers to process requests to retrieve the service catalog.
 *
 * @author sgreenberg@pivotal.io
 * @author Roy Clarkson
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
	 * @param serviceId The ID of the service definition in the catalog
	 * @return the service definition, or null if it doesn't exist
	 */
	Mono<ServiceDefinition> getServiceDefinition(String serviceId);

	/**
	 * Return the {@link ResponseEntity} with catalog of services provided by the service broker. Implementing
	 * service brokers may use this method to manage ETag responses and caching of the catalog. This ResponseEntity
	 * returned by this method is directly returned in the {@link CatalogController#getCatalog(HttpHeaders)} method.
	 *
	 * @param httpHeaders the HttpHeaders from the request
	 * @return the ResponseEntity with catalog or an appropriate ETag response
	 */
	default Mono<ResponseEntity<Catalog>> getResponseEntityCatalog(HttpHeaders httpHeaders) {
		return Mono.empty();
	}

}
