/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web.reactive;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.autoconfigure.web.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.Schemas;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.when;
import static org.springframework.cloud.servicebroker.model.catalog.ServiceDefinitionRequires.SERVICE_REQUIRES_ROUTE_FORWARDING;
import static org.springframework.cloud.servicebroker.model.catalog.ServiceDefinitionRequires.SERVICE_REQUIRES_SYSLOG_DRAIN;

@RunWith(MockitoJUnitRunner.class)
public class CatalogControllerIntegrationTest {

	private WebTestClient client;

	@InjectMocks
	private CatalogController controller;

	@Mock
	private CatalogService catalogService;

	private ServiceDefinition serviceDefinition;

	@Before
	public void setUp() {
		this.client = WebTestClient.bindToController(this.controller).build();
		this.serviceDefinition = ServiceFixture.getSimpleService();

		Catalog catalog = Catalog.builder()
				.serviceDefinitions(this.serviceDefinition)
				.build();

		when(this.catalogService.getCatalog()).thenReturn(Mono.just(catalog));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void catalogIsRetrieved() {
		assertCatalog("/v2/catalog");
	}

	@Test
	public void catalogIsRetrievedWithPlatformInstanceId() {
		assertCatalog("/123/v2/catalog");
	}

	private void assertCatalog(final String uri) {
		List<Plan> plans = serviceDefinition.getPlans();
		Schemas schemas = plans.get(1).getSchemas();
		Map<String, Object> createServiceInstanceSchema = schemas.getServiceInstanceSchema().getCreateMethodSchema().getParameters();
		Map<String, Object> updateServiceInstanceSchema = schemas.getServiceInstanceSchema().getUpdateMethodSchema().getParameters();
		Map<String, Object> createServiceBindingSchema = schemas.getServiceBindingSchema().getCreateMethodSchema().getParameters();

		client.get().uri(uri)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
				.expectBody()
				.jsonPath("$.services").isNotEmpty()
				.jsonPath("$.services").isArray()
				.jsonPath("$.services[0]").isNotEmpty()
				.jsonPath("$.services[0].id").isEqualTo(serviceDefinition.getId())
				.jsonPath("$.services[0].name").isEqualTo(serviceDefinition.getName())
				.jsonPath("$.services[0].description").isEqualTo(serviceDefinition.getDescription())
				.jsonPath("$.services[0].bindable").isEqualTo(serviceDefinition.isBindable())
				.jsonPath("$.services[0].plan_updateable").doesNotExist()
				.jsonPath("$.services[0].instances_retrievable").doesNotExist()
				.jsonPath("$.services[0].bindings_retrievable").doesNotExist()
				.jsonPath("$.services[0].requires").isNotEmpty()
				.jsonPath("$.services[0].requires").isArray()
				.jsonPath("$.services[0].requires[0]").isEqualTo(SERVICE_REQUIRES_SYSLOG_DRAIN.toString())
				.jsonPath("$.services[0].requires[1]").isEqualTo(SERVICE_REQUIRES_ROUTE_FORWARDING.toString())
				.jsonPath("$.services[0].requires[2]").doesNotExist()
				.jsonPath("$.services[0].plans").isNotEmpty()
				.jsonPath("$.services[0].plans").isArray()
				.jsonPath("$.services[0].plans[0].id").isEqualTo(plans.get(0).getId())
				.jsonPath("$.services[0].plans[0].name").isEqualTo(plans.get(0).getName())
				.jsonPath("$.services[0].plans[0].description").isEqualTo(plans.get(0).getDescription())
				.jsonPath("$.services[0].plans[0].free").isEqualTo(plans.get(0).isFree())
				.jsonPath("$.services[0].plans[1].id").isEqualTo(plans.get(1).getId())
				.jsonPath("$.services[0].plans[1].name").isEqualTo(plans.get(1).getName())
				.jsonPath("$.services[0].plans[1].description").isEqualTo(plans.get(1).getDescription())
				.jsonPath("$.services[0].plans[1].metadata").isEqualTo(plans.get(1).getMetadata())
				.jsonPath("$.services[0].plans[1].bindable").isEqualTo(plans.get(1).isBindable())
				.jsonPath("$.services[0].plans[1].free").isEqualTo(plans.get(1).isFree())
				.jsonPath("$.services[0].plans[1].schemas.service_instance.create.parameters").isEqualTo(createServiceInstanceSchema)
				.jsonPath("$.services[0].plans[1].schemas.service_instance.update.parameters").isEqualTo(updateServiceInstanceSchema)
				.jsonPath("$.services[0].plans[1].schemas.service_binding.create.parameters").isEqualTo(createServiceBindingSchema)
			  	.jsonPath("$.services[0].plans[2].id").isEqualTo(plans.get(2).getId())
			  	.jsonPath("$.services[0].plans[2].name").isEqualTo(plans.get(2).getName())
			  	.jsonPath("$.services[0].plans[2].description").isEqualTo(plans.get(2).getDescription())
			  	.jsonPath("$.services[0].plans[2].free").isEqualTo(plans.get(2).isFree())
			  	.jsonPath("$.services[0].plans[3]").doesNotExist()
				.jsonPath("$.services[1]").doesNotExist();
	}

}
