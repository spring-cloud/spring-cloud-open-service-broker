package org.springframework.cloud.servicebroker.webmvc.interceptor;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
public class ApiVersionInterceptorIntegrationTest {

	private final static String CATALOG_PATH = "/v2/catalog";

	@InjectMocks
	private CatalogController controller;

	@Mock
	private CatalogService catalogService;

	@Test
	public void noHeaderSent() throws Exception {
		mockWithExpectedVersion().perform(get(CATALOG_PATH)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isPreconditionFailed())
				.andExpect(jsonPath("$.description", containsString("expected-version")));
	}

	@Test
	public void incorrectHeaderSent() throws Exception {
		mockWithExpectedVersion().perform(get(CATALOG_PATH)
				.header(BrokerApiVersion.DEFAULT_API_VERSION_HEADER, "wrong-version")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isPreconditionFailed())
				.andExpect(jsonPath("$.description", containsString("expected-version")))
				.andExpect(jsonPath("$.description", containsString("wrong-version")));
	}

	@Test
	public void matchingHeaderSent() throws Exception {
		mockWithExpectedVersion().perform(get(CATALOG_PATH)
				.header(BrokerApiVersion.DEFAULT_API_VERSION_HEADER, "expected-version")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void anyHeaderNotSent() throws Exception {
		mockWithDefaultVersion().perform(get(CATALOG_PATH)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void anyHeaderSent() throws Exception {
		mockWithDefaultVersion().perform(get(CATALOG_PATH)
				.header(BrokerApiVersion.DEFAULT_API_VERSION_HEADER, "ignored-version")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	private MockMvc mockWithDefaultVersion() {
		return MockMvcBuilders.standaloneSetup(controller)
				.addInterceptors(new ApiVersionInterceptor(new BrokerApiVersion()))
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}

	private MockMvc mockWithExpectedVersion() {
		return MockMvcBuilders.standaloneSetup(controller)
				.addInterceptors(new ApiVersionInterceptor(new BrokerApiVersion("expected-version")))
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}
}
