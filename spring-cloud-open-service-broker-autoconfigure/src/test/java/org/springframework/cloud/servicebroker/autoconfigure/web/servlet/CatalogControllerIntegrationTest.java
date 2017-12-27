/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture.PlanFixture;
import org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.when;
import static org.springframework.cloud.servicebroker.model.ServiceDefinitionRequires.SERVICE_REQUIRES_ROUTE_FORWARDING;
import static org.springframework.cloud.servicebroker.model.ServiceDefinitionRequires.SERVICE_REQUIRES_SYSLOG_DRAIN;
import static org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture.CatalogFixture.getCatalog;
import static org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture.CatalogFixture.getCatalogWithRequires;
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

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void catalogIsRetrievedCorrectly() throws Exception {
		when(catalogService.getCatalog()).thenReturn(getCatalog());

		ServiceDefinition service = ServiceFixture.getSimpleService();
		Plan[] plans = PlanFixture.getAllPlans();

		Map<String, Object> createServiceInstanceSchema = plans[1].getSchemas().getServiceInstanceSchema().getCreateMethodSchema().getConfigParametersSchema();
		Map<String, Object> updateServiceInstanceSchema = plans[1].getSchemas().getServiceInstanceSchema().getUpdateMethodSchema().getConfigParametersSchema();
		Map<String, Object> createServiceBindingSchema = plans[1].getSchemas().getServiceBindingSchema().getCreateMethodSchema().getConfigParametersSchema();

		this.mockMvc.perform(get("/v2/catalog")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.services", hasSize(1)))
				.andExpect(jsonPath("$.services[*].id", contains(service.getId())))
				.andExpect(jsonPath("$.services[*].name", contains(service.getName())))
				.andExpect(jsonPath("$.services[*].description", contains(service.getDescription())))
				.andExpect(jsonPath("$.services[*].bindable", contains(service.isBindable())))
				.andExpect(jsonPath("$.services[*].plan_updateable", contains(service.isPlanUpdateable())))
				.andExpect(jsonPath("$.services[*].requires[*]", empty()))
				.andExpect(jsonPath("$.services[*].plans[*]", hasSize(2)))
				.andExpect(jsonPath("$.services[*].plans[*].id", containsInAnyOrder(plans[0].getId(), plans[1].getId())))
				.andExpect(jsonPath("$.services[*].plans[*].name", containsInAnyOrder(plans[0].getName(), plans[1].getName())))
				.andExpect(jsonPath("$.services[*].plans[*].description", containsInAnyOrder(plans[0].getDescription(), plans[1].getDescription())))
				.andExpect(jsonPath("$.services[*].plans[*].metadata", contains(plans[1].getMetadata())))
				.andExpect(jsonPath("$.services[*].plans[*].bindable", hasSize(1)))
				.andExpect(jsonPath("$.services[*].plans[*].bindable", contains(plans[1].isBindable())))
				.andExpect(jsonPath("$.services[*].plans[*].free", containsInAnyOrder(plans[0].isFree(), plans[1].isFree())))
				.andExpect(jsonPath("$.services[*].plans[*].schemas.service_instance.create.parameters", contains(createServiceInstanceSchema)))
				.andExpect(jsonPath("$.services[*].plans[*].schemas.service_instance.update.parameters", contains(updateServiceInstanceSchema)))
				.andExpect(jsonPath("$.services[*].plans[*].schemas.service_binding.create.parameters", contains(createServiceBindingSchema)));
	}

	@Test
	public void catalogWithRequiresIsRetrievedCorrectly() throws Exception {
		when(catalogService.getCatalog()).thenReturn(getCatalogWithRequires());

		ServiceDefinition service = ServiceFixture.getServiceWithRequires();

		this.mockMvc.perform(get("/v2/catalog")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.services", hasSize(1)))
				.andExpect(jsonPath("$.services[*].id", contains(service.getId())))
				.andExpect(jsonPath("$.services[*].name", contains(service.getName())))
				.andExpect(jsonPath("$.services[*].description", contains(service.getDescription())))
				.andExpect(jsonPath("$.services[*].bindable", contains(service.isBindable())))
				.andExpect(jsonPath("$.services[*].plan_updateable", contains(service.isPlanUpdateable())))
				.andExpect(jsonPath("$.services[*].requires[*]", containsInAnyOrder(
						SERVICE_REQUIRES_SYSLOG_DRAIN.toString(),
						SERVICE_REQUIRES_ROUTE_FORWARDING.toString())
				));
	}

	@Test
	public void catalogIsRetrievedWithNoServiceDefinitions() throws Exception {
		when(catalogService.getCatalog()).thenReturn(Catalog.builder().build());

		this.mockMvc.perform(get("/v2/catalog")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.services", empty()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void catalogIsRetrievedCorrectlyWithCfInstanceId() throws Exception {
		when(catalogService.getCatalog()).thenReturn(getCatalog());

		ServiceDefinition service = ServiceFixture.getSimpleService();
		Plan[] plans = PlanFixture.getAllPlans();

		this.mockMvc.perform(get("/123/v2/catalog")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.services", hasSize(1)))
				.andExpect(jsonPath("$.services[*].id", contains(service.getId())))
				.andExpect(jsonPath("$.services[*].name", contains(service.getName())))
				.andExpect(jsonPath("$.services[*].description", contains(service.getDescription())))
				.andExpect(jsonPath("$.services[*].bindable", contains(service.isBindable())))
				.andExpect(jsonPath("$.services[*].plan_updateable", contains(service.isPlanUpdateable())))
				.andExpect(jsonPath("$.services[*].requires[*]", empty()))
				.andExpect(jsonPath("$.services[*].plans[*].id", containsInAnyOrder(plans[0].getId(), plans[1].getId())))
				.andExpect(jsonPath("$.services[*].plans[*].name", containsInAnyOrder(plans[0].getName(), plans[1].getName())))
				.andExpect(jsonPath("$.services[*].plans[*].description", containsInAnyOrder(plans[0].getDescription(), plans[1].getDescription())))
				.andExpect(jsonPath("$.services[*].plans[1].metadata", contains(plans[1].getMetadata())))
				.andExpect(jsonPath("$.services[*].plans[*].free", containsInAnyOrder(plans[0].isFree(), plans[1].isFree())));
	}
}
