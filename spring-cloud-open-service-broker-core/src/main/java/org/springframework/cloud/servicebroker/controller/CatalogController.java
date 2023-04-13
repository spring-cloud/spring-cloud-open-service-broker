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

package org.springframework.cloud.servicebroker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.annotation.ServiceBrokerRestController;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Provide endpoints for the catalog API.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#catalog-management">Open
 * 		Service Broker API specification</a>
 */
@ServiceBrokerRestController
public class CatalogController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogController.class);

	/**
	 * Construct a new {@link CatalogController}
	 *
	 * @param service the catalog service
	 */
	public CatalogController(CatalogService service) {
		super(service);
	}

	/**
	 * REST controller for getting a catalog
	 *
	 * @return the catalog
	 */
	@GetMapping({"/v2/catalog", "{platformInstanceId}/v2/catalog"})
	public Mono<ResponseEntity<Catalog>> getCatalog(@RequestHeader HttpHeaders httpHeaders) {
		return catalogService.getResponseEntityCatalog(httpHeaders)
				.switchIfEmpty(catalogService.getCatalog()
						.doOnRequest(v -> {
							if (LOG.isInfoEnabled()) {
								LOG.info("Retrieving catalog");
							}
						})
						.doOnSuccess(catalog -> {
							if (LOG.isInfoEnabled()) {
								LOG.info("Retrieving catalog success");
							}
							if (LOG.isDebugEnabled()) {
								LOG.debug("catalog={}", catalog);
							}
						})
						.doOnError(e -> LOG.error("Error retrieving catalog. error=" + e.getMessage(), e))
						.flatMap(catalog -> Mono.just(ResponseEntity
								.ok()
								.body(catalog))));
	}

}
