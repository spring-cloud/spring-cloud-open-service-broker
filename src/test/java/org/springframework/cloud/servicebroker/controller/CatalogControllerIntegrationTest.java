package org.springframework.cloud.servicebroker.controller;

import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.fixture.PlanFixture;
import org.springframework.cloud.servicebroker.model.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.springframework.cloud.servicebroker.model.ServiceDefinitionRequires.SERVICE_REQUIRES_ROUTE_FORWARDING;
import static org.springframework.cloud.servicebroker.model.ServiceDefinitionRequires.SERVICE_REQUIRES_SYSLOG_DRAIN;
import static org.springframework.cloud.servicebroker.model.fixture.CatalogFixture.getCatalog;
import static org.springframework.cloud.servicebroker.model.fixture.CatalogFixture.getCatalogWithRequires;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
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
	public void catalogIsRetrievedCorrectly() throws Exception {
		when(catalogService.getCatalog()).thenReturn(getCatalog());

		ServiceDefinition service = ServiceFixture.getSimpleService();
		List<Plan> plans = PlanFixture.getAllPlans();

		this.mockMvc.perform(get("/v2/catalog")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.services.", hasSize(1)))
				.andExpect(jsonPath("$.services[*].id", contains(service.getId())))
				.andExpect(jsonPath("$.services[*].name", contains(service.getName())))
				.andExpect(jsonPath("$.services[*].description", contains(service.getDescription())))
				.andExpect(jsonPath("$.services[*].bindable", contains(service.isBindable())))
				.andExpect(jsonPath("$.services[*].plan_updateable", contains(service.isPlanUpdateable())))
				.andExpect(jsonPath("$.services[*].requires[*]", empty()))
				.andExpect(jsonPath("$.services[*].plans[*].id", containsInAnyOrder(plans.get(0).getId(), plans.get(1).getId())))
				.andExpect(jsonPath("$.services[*].plans[*].name", containsInAnyOrder(plans.get(0).getName(), plans.get(1).getName())))
				.andExpect(jsonPath("$.services[*].plans[*].description", containsInAnyOrder(plans.get(0).getDescription(), plans.get(1).getDescription())))
				.andExpect(jsonPath("$.services[*].plans[*].metadata", containsInAnyOrder(Collections.EMPTY_MAP, plans.get(1).getMetadata())));
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
				.andExpect(jsonPath("$.services.", hasSize(1)))
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
		when(catalogService.getCatalog()).thenReturn(new Catalog());

		this.mockMvc.perform(get("/v2/catalog")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.services", empty()));
	}

}
