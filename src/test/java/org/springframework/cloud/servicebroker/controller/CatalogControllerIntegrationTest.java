package org.springframework.cloud.servicebroker.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.cloud.servicebroker.model.ServiceDefinitionRequires.*;
import static org.springframework.cloud.servicebroker.model.fixture.CatalogFixture.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.fixture.PlanFixture;
import org.springframework.cloud.servicebroker.model.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

		this.mockMvc.perform(get("/v2/catalog").accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(
						content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.services.", hasSize(1)))
				.andExpect(jsonPath("$.services[*].id", contains(service.getId())))
				.andExpect(jsonPath("$.services[*].name", contains(service.getName())))
				.andExpect(jsonPath("$.services[*].description",
						contains(service.getDescription())))
				.andExpect(jsonPath("$.services[*].bindable",
						contains(service.isBindable())))
				.andExpect(jsonPath("$.services[*].plan_updateable",
						contains(service.isPlanUpdateable())))
				.andExpect(jsonPath("$.services[*].requires[*]", empty()))
				.andExpect(jsonPath("$.services[*].plans[*]", hasSize(2)))
				.andExpect(jsonPath("$.services[*].plans[*].id",
						containsInAnyOrder(plans.get(0).getId(), plans.get(1).getId())))
				.andExpect(jsonPath("$.services[*].plans[*].name",
						containsInAnyOrder(plans.get(0).getName(),
								plans.get(1).getName())))
				.andExpect(jsonPath("$.services[*].plans[*].description",
						containsInAnyOrder(plans.get(0).getDescription(),
								plans.get(1).getDescription())))
				.andExpect(jsonPath("$.services[*].plans[*].metadata",
						containsInAnyOrder(Collections.EMPTY_MAP,
								plans.get(1).getMetadata())))
				.andExpect(jsonPath("$.services[*].plans[*].bindable", hasSize(1)))
				.andExpect(jsonPath("$.services[*].plans[*].bindable",
						contains(plans.get(1).isBindable())))
				.andExpect(jsonPath("$.services[*].plans[*].free", containsInAnyOrder(
						plans.get(0).isFree(), plans.get(1).isFree())));
	}

	@Test
	public void catalogWithRequiresIsRetrievedCorrectly() throws Exception {
		when(catalogService.getCatalog()).thenReturn(getCatalogWithRequires());

		ServiceDefinition service = ServiceFixture.getServiceWithRequires();

		this.mockMvc.perform(get("/v2/catalog").accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(
						content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.services.", hasSize(1)))
				.andExpect(jsonPath("$.services[*].id", contains(service.getId())))
				.andExpect(jsonPath("$.services[*].name", contains(service.getName())))
				.andExpect(jsonPath("$.services[*].description",
						contains(service.getDescription())))
				.andExpect(jsonPath("$.services[*].bindable",
						contains(service.isBindable())))
				.andExpect(jsonPath("$.services[*].plan_updateable",
						contains(service.isPlanUpdateable())))
				.andExpect(
						jsonPath("$.services[*].requires[*]",
								containsInAnyOrder(
										SERVICE_REQUIRES_SYSLOG_DRAIN.toString(),
										SERVICE_REQUIRES_ROUTE_FORWARDING.toString())));
	}

	@Test
	public void catalogIsRetrievedWithNoServiceDefinitions() throws Exception {
		when(catalogService.getCatalog()).thenReturn(new Catalog());

		this.mockMvc.perform(get("/v2/catalog").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(
						content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.services", empty()));
	}

	@Test
	public void catalogIsRetrievedCorrectlyWithCfInstanceId() throws Exception {
		when(catalogService.getCatalog()).thenReturn(getCatalog());

		ServiceDefinition service = ServiceFixture.getSimpleService();
		List<Plan> plans = PlanFixture.getAllPlans();

		this.mockMvc.perform(get("/123/v2/catalog").accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(
						content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.services.", hasSize(1)))
				.andExpect(jsonPath("$.services[*].id", contains(service.getId())))
				.andExpect(jsonPath("$.services[*].name", contains(service.getName())))
				.andExpect(jsonPath("$.services[*].description",
						contains(service.getDescription())))
				.andExpect(jsonPath("$.services[*].bindable",
						contains(service.isBindable())))
				.andExpect(jsonPath("$.services[*].plan_updateable",
						contains(service.isPlanUpdateable())))
				.andExpect(jsonPath("$.services[*].requires[*]", empty()))
				.andExpect(jsonPath("$.services[*].plans[*].id",
						containsInAnyOrder(plans.get(0).getId(), plans.get(1).getId())))
				.andExpect(jsonPath("$.services[*].plans[*].name",
						containsInAnyOrder(plans.get(0).getName(),
								plans.get(1).getName())))
				.andExpect(jsonPath("$.services[*].plans[*].description",
						containsInAnyOrder(plans.get(0).getDescription(),
								plans.get(1).getDescription())))
				.andExpect(jsonPath("$.services[*].plans[*].metadata",
						containsInAnyOrder(Collections.EMPTY_MAP,
								plans.get(1).getMetadata())))
				.andExpect(jsonPath("$.services[*].plans[*].free", containsInAnyOrder(
						plans.get(0).isFree(), plans.get(1).isFree())));
	}
}
