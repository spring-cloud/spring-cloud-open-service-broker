package org.springframework.cloud.servicebroker.webmvc.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;
import org.springframework.cloud.servicebroker.service.NonBindableServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class NonBindableServiceInstanceBindingControllerIntegrationTest extends ServiceInstanceBindingIntegrationTest {

	private MockMvc mockMvc;

	@Before
	public void setup() {
		ServiceInstanceBindingService serviceInstanceBindingService = new NonBindableServiceInstanceBindingService();
		ServiceInstanceBindingController controller =
				new ServiceInstanceBindingController(catalogService, serviceInstanceBindingService);

		this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}

	@Test
	public void createBindingToAppFails() throws Exception {
		setupCatalogService(createRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(createRequest, false))
				.content(DataFixture.toJson(createRequest))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void deleteBindingFails() throws Exception {
		setupCatalogService(deleteRequest.getServiceDefinitionId());

		mockMvc.perform(delete(buildUrl(deleteRequest, false))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

}
