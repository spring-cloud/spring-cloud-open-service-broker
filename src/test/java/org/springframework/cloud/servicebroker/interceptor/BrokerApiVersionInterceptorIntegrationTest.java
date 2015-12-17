package org.springframework.cloud.servicebroker.interceptor;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
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

@RunWith(MockitoJUnitRunner.class)
public class BrokerApiVersionInterceptorIntegrationTest {

	private final static String CATALOG_PATH = "/v2/catalog";

	private MockMvc mockMvc;

	@InjectMocks
	private CatalogController controller;

	@Mock
	private CatalogService catalogService;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.addInterceptors(new BrokerApiVersionInterceptor(new BrokerApiVersion("header", "expected-version")))
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}

	@Test
	public void noHeaderSent() throws Exception {
		this.mockMvc.perform(get(CATALOG_PATH)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isPreconditionFailed())
				.andExpect(jsonPath("$.description.", containsString("expected-version")));
	}

	@Test
	public void incorrectHeaderSent() throws Exception {
		this.mockMvc.perform(get(CATALOG_PATH)
				.header("header", "wrong-version")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isPreconditionFailed())
				.andExpect(jsonPath("$.description.", containsString("expected-version")))
				.andExpect(jsonPath("$.description.", containsString("wrong-version")));
	}

	@Test
	public void correctHeaderSent() throws Exception {
		this.mockMvc.perform(get(CATALOG_PATH)
				.header("header", "expected-version")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
