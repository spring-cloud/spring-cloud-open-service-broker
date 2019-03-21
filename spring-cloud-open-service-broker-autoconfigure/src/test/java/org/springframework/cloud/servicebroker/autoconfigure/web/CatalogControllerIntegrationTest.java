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

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.Schemas;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.autoconfigure.web.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.when;
import static org.springframework.cloud.servicebroker.model.catalog.ServiceDefinitionRequires.SERVICE_REQUIRES_ROUTE_FORWARDING;
import static org.springframework.cloud.servicebroker.model.catalog.ServiceDefinitionRequires.SERVICE_REQUIRES_SYSLOG_DRAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CatalogControllerIntegrationTest {

	private MockMvc mockMvc;

	@InjectMocks
	private CatalogController controller;

	@Mock
	private CatalogService catalogService;
	private ServiceDefinition serviceDefinition;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();

		serviceDefinition = ServiceFixture.getSimpleService();

		Catalog catalog = Catalog.builder()
				.serviceDefinitions(serviceDefinition)
				.build();

		when(catalogService.getCatalog()).thenReturn(catalog);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void catalogIsRetrieved() throws Exception {
		ResultActions result = this.mockMvc.perform(get("/v2/catalog")
				.accept(MediaType.APPLICATION_JSON));

		assertResult(result);
	}

	@Test
	public void catalogIsRetrievedWithPlatformInstanceId() throws Exception {
		ResultActions result = this.mockMvc.perform(get("/123/v2/catalog")
				.accept(MediaType.APPLICATION_JSON));

		assertResult(result);
	}

	@SuppressWarnings("unchecked")
	private void assertResult(ResultActions result) throws Exception {
		List<Plan> plans = serviceDefinition.getPlans();
		Schemas schemas = plans.get(1).getSchemas();

		Map<String, Object> createServiceInstanceSchema = schemas.getServiceInstanceSchema().getCreateMethodSchema().getParameters();
		Map<String, Object> updateServiceInstanceSchema = schemas.getServiceInstanceSchema().getUpdateMethodSchema().getParameters();
		Map<String, Object> createServiceBindingSchema = schemas.getServiceBindingSchema().getCreateMethodSchema().getParameters();

		result
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.services", hasSize(1)))
				.andExpect(jsonPath("$.services[*].id", contains(serviceDefinition.getId())))
				.andExpect(jsonPath("$.services[*].name", contains(serviceDefinition.getName())))
				.andExpect(jsonPath("$.services[*].description", contains(serviceDefinition.getDescription())))
				.andExpect(jsonPath("$.services[*].bindable", contains(serviceDefinition.isBindable())))
				.andExpect(jsonPath("$.services[*].requires[*]", containsInAnyOrder(
						SERVICE_REQUIRES_SYSLOG_DRAIN.toString(),
						SERVICE_REQUIRES_ROUTE_FORWARDING.toString())
				))
				.andExpect(jsonPath("$.services[*].plans[*]", hasSize(3)))
				.andExpect(jsonPath("$.services[*].plans[*].id", containsInAnyOrder(plans.get(0).getId(), plans.get(1).getId(), plans.get(2).getId())))
				.andExpect(jsonPath("$.services[*].plans[*].name", containsInAnyOrder(plans.get(0).getName(), plans.get(1).getName(), plans.get(2).getName())))
				.andExpect(jsonPath("$.services[*].plans[*].description", containsInAnyOrder(plans.get(0).getDescription(), plans.get(1).getDescription(), plans.get(2).getDescription())))
				.andExpect(jsonPath("$.services[*].plans[*].metadata", contains(plans.get(1).getMetadata())))
				.andExpect(jsonPath("$.services[*].plans[*].bindable", hasSize(1)))
				.andExpect(jsonPath("$.services[*].plans[*].bindable", contains(plans.get(1).isBindable())))
				.andExpect(jsonPath("$.services[*].plans[*].free", containsInAnyOrder(plans.get(0).isFree(), plans.get(1).isFree(), plans.get(2).isFree())))
				.andExpect(jsonPath("$.services[*].plans[*].schemas.service_instance.create.parameters", contains(createServiceInstanceSchema)))
				.andExpect(jsonPath("$.services[*].plans[*].schemas.service_instance.update.parameters", contains(updateServiceInstanceSchema)))
				.andExpect(jsonPath("$.services[*].plans[*].schemas.service_binding.create.parameters", contains(createServiceBindingSchema)));
	}
}
