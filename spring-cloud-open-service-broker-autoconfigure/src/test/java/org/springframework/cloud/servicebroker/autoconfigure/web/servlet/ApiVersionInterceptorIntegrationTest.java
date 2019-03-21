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

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.controller.ServiceBrokerExceptionHandler;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ApiVersionInterceptorIntegrationTest {

	private final static String CATALOG_PATH = "/v2/catalog";

	@InjectMocks
	private CatalogController controller;

	@Mock
	@SuppressWarnings("unused")
	private CatalogService catalogService;

	@Before
	public void setUp() {
		Catalog catalog = Catalog.builder().build();
		given(catalogService.getCatalog())
				.willReturn(Mono.just(catalog));
	}

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
				.setControllerAdvice(ServiceBrokerExceptionHandler.class)
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}

	private MockMvc mockWithExpectedVersion() {
		return MockMvcBuilders.standaloneSetup(controller)
				.addInterceptors(new ApiVersionInterceptor(new BrokerApiVersion("expected-version")))
				.setControllerAdvice(ServiceBrokerExceptionHandler.class)
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}
}
