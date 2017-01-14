package org.springframework.cloud.servicebroker.interceptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.model.ApiInfoLocation;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.cloud.servicebroker.interceptor.ApiInfoLocationInterceptor.API_INFO_LOCATION_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ApiInfoLocationInterceptorIntegrationTest {

	private final static String CATALOG_PATH = "/v2/catalog";

	@InjectMocks
	private CatalogController controller;

	@Mock
	private CatalogService catalogService;

	private ApiInfoLocation apiInfoLocation;

	@Test
	public void locationHeaderSent() throws Exception {
		mockMvc().perform(get(CATALOG_PATH)
				.header(API_INFO_LOCATION_HEADER, "https://api.example.com")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		assertEquals("https://api.example.com", apiInfoLocation.getLocation());
	}

	@Test
	public void locationHeaderNotSent() throws Exception {
		mockMvc().perform(get(CATALOG_PATH)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		assertNull(apiInfoLocation.getLocation());
	}

	private MockMvc mockMvc() {
		apiInfoLocation = new ApiInfoLocation();
		return MockMvcBuilders.standaloneSetup(controller)
				.addInterceptors(new ApiInfoLocationInterceptor(apiInfoLocation))
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}
}
