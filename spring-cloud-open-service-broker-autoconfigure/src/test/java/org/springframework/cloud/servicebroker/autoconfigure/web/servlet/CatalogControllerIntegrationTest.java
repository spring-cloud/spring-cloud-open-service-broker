/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.autoconfigure.web.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.Schemas;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.cloud.servicebroker.model.catalog.ServiceDefinitionRequires.SERVICE_REQUIRES_ROUTE_FORWARDING;
import static org.springframework.cloud.servicebroker.model.catalog.ServiceDefinitionRequires.SERVICE_REQUIRES_SYSLOG_DRAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CatalogControllerIntegrationTest {

	private MockMvc mockMvc;

	@InjectMocks
	private CatalogController controller;

	@Mock
	private CatalogService catalogService;

	private ServiceDefinition serviceDefinition;

	private Catalog catalog;

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.controller)
				.setMessageConverters(new MappingJackson2HttpMessageConverter())
				.build();

		this.serviceDefinition = ServiceFixture.getSimpleService();

		this.catalog = Catalog.builder()
				.serviceDefinitions(this.serviceDefinition)
				.build();

		given(this.catalogService.getCatalog())
				.willReturn(Mono.just(catalog));

		given(this.catalogService.getResponseEntityCatalog(any()))
				.willReturn(Mono.empty());
	}

	@Test
	void catalogIsRetrieved() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(get("/v2/catalog")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();
		assertResult(mvcResult);
	}

	@Test
	void catalogIsRetrievedWithPlatformInstanceId() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(get("/123/v2/catalog")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();
		assertResult(mvcResult);
	}

	@Test
	void catalogWithEtagIsRetrieved() throws Exception {
		ResponseEntity<Catalog> responseEntity = ResponseEntity
				.ok()
				.eTag("12345")
				.body(catalog);

		given(this.catalogService.getResponseEntityCatalog(getHeaders()))
				.willReturn(Mono.just(responseEntity));

		MvcResult mvcResult = this.mockMvc.perform(get("/v2/catalog")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		ResultActions resultActions = assertResult(mvcResult);
		resultActions.andExpect(header().string(HttpHeaders.ETAG, "\"12345\""));
	}

	@Test
	void cachedCatalogWithSameEtagIsRetrieved() throws Exception {
		ResponseEntity<Catalog> responseEntity = ResponseEntity
				.status(304)
				.eTag("12345")
				.build();

		HttpHeaders headers = getHeaders();
		headers.setIfNoneMatch("12345");

		given(this.catalogService.getResponseEntityCatalog(headers))
				.willReturn(Mono.just(responseEntity));

		MvcResult mvcResult = this.mockMvc.perform(get("/v2/catalog")
						.header(HttpHeaders.IF_NONE_MATCH, "12345")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		ResultActions resultActions = this.mockMvc.perform(asyncDispatch(mvcResult));
		resultActions.andExpect(status().isNotModified());
		resultActions.andExpect(header().string(HttpHeaders.ETAG, "\"12345\""));
	}

	@Test
	void catalogWithDifferentEtagIsRetrieved() throws Exception {
		ResponseEntity<Catalog> responseEntity = ResponseEntity
				.ok()
				.eTag("22222")
				.body(catalog);

		HttpHeaders headers = getHeaders();
		headers.setIfNoneMatch("22221");

		given(this.catalogService.getResponseEntityCatalog(headers))
				.willReturn(Mono.just(responseEntity));

		MvcResult mvcResult = this.mockMvc.perform(get("/v2/catalog")
						.header(HttpHeaders.IF_NONE_MATCH, "22221")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		ResultActions resultActions = assertResult(mvcResult);
		resultActions.andExpect(header().string(HttpHeaders.ETAG, "\"22222\""));
	}

	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		return headers;
	}

	@SuppressWarnings("unchecked")
	private ResultActions assertResult(MvcResult mvcResult) throws Exception {
		List<String> features = (List<String>) serviceDefinition.getMetadata().get("features");

		List<Plan> plans = serviceDefinition.getPlans();
		Schemas schemas = plans.get(1).getSchemas();

		Map<String, Object> createServiceInstanceSchema = schemas.getServiceInstanceSchema().getCreateMethodSchema()
				.getParameters();
		Map<String, Object> updateServiceInstanceSchema = schemas.getServiceInstanceSchema().getUpdateMethodSchema()
				.getParameters();
		Map<String, Object> createServiceBindingSchema = schemas.getServiceBindingSchema().getCreateMethodSchema()
				.getParameters();

		ResultActions resultActions = this.mockMvc.perform(asyncDispatch(mvcResult));

		resultActions
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
				.andExpect(jsonPath("$.services[*].metadata.features[*]", contains(features.get(0), features.get(1))))
				.andExpect(jsonPath("$.services[*].plans[*]", hasSize(3)))
				.andExpect(jsonPath("$.services[*].plans[*].id",
						containsInAnyOrder(plans.get(0).getId(), plans.get(1).getId(), plans.get(2).getId())))
				.andExpect(jsonPath("$.services[*].plans[*].name",
						containsInAnyOrder(plans.get(0).getName(), plans.get(1).getName(), plans.get(2).getName())))
				.andExpect(jsonPath("$.services[*].plans[*].description",
						containsInAnyOrder(plans.get(0).getDescription(), plans.get(1).getDescription(),
								plans.get(2).getDescription())))
				.andExpect(jsonPath("$.services[*].plans[*].metadata", contains(plans.get(1).getMetadata())))
				.andExpect(jsonPath("$.services[*].plans[*].bindable", hasSize(1)))
				.andExpect(jsonPath("$.services[*].plans[*].bindable", contains(plans.get(1).isBindable())))
				.andExpect(jsonPath("$.services[*].plans[*].free",
						containsInAnyOrder(plans.get(0).isFree(), plans.get(1).isFree(), plans.get(2).isFree())))
				.andExpect(
						jsonPath("$.services[*].plans[*].plan_updateable", contains(plans.get(1).isPlanUpdateable())))
				.andExpect(jsonPath("$.services[*].plans[*].maximum_polling_duration",
						contains(plans.get(1).getMaximumPollingDuration())))
				.andExpect(jsonPath("$.services[*].plans[*].schemas.service_instance.create.parameters",
						contains(createServiceInstanceSchema)))
				.andExpect(jsonPath("$.services[*].plans[*].schemas.service_instance.update.parameters",
						contains(updateServiceInstanceSchema)))
				.andExpect(jsonPath("$.services[*].plans[*].schemas.service_binding.create.parameters",
						contains(createServiceBindingSchema)))
				.andExpect(jsonPath("$.services[*].plans[*].maintenance_info.version",
						contains(plans.get(0).getMaintenanceInfo().getVersion())))
				.andExpect(jsonPath("$.services[*].plans[*].maintenance_info.description",
						contains(plans.get(0).getMaintenanceInfo().getDescription())));

		return resultActions;
	}

}
