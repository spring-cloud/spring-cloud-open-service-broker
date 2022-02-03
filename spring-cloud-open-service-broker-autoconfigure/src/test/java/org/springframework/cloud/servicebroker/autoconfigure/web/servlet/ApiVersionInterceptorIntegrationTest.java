/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.controller.ServiceBrokerWebMvcExceptionHandler;
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

@ExtendWith(MockitoExtension.class)
class ApiVersionInterceptorIntegrationTest {

	private final static String CATALOG_PATH = "/v2/catalog";

	@InjectMocks
	private CatalogController controller;

	@Mock
	private CatalogService catalogService;

	@Test
	void noHeaderSent() throws Exception {
		mockWithExpectedVersion().perform(get(CATALOG_PATH)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", containsString("expected-version")));
	}

	@Test
	void incorrectHeaderSent() throws Exception {
		mockWithExpectedVersion().perform(get(CATALOG_PATH)
				.header(BrokerApiVersion.DEFAULT_API_VERSION_HEADER, "wrong-version")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isPreconditionFailed())
				.andExpect(jsonPath("$.description", containsString("expected-version")))
				.andExpect(jsonPath("$.description", containsString("wrong-version")));
	}

	@Test
	void matchingHeaderSent() throws Exception {
		given(catalogService.getCatalog())
				.willReturn(Mono.just(Catalog.builder().build()));
		mockWithExpectedVersion().perform(get(CATALOG_PATH)
				.header(BrokerApiVersion.DEFAULT_API_VERSION_HEADER, "expected-version")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void anyHeaderNotSent() throws Exception {
		given(catalogService.getCatalog())
				.willReturn(Mono.just(Catalog.builder().build()));
		mockWithDefaultVersion().perform(get(CATALOG_PATH)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void anyHeaderSent() throws Exception {
		given(catalogService.getCatalog())
				.willReturn(Mono.just(Catalog.builder().build()));
		mockWithDefaultVersion().perform(get(CATALOG_PATH)
				.header(BrokerApiVersion.DEFAULT_API_VERSION_HEADER, "ignored-version")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	private MockMvc mockWithDefaultVersion() {
		return MockMvcBuilders.standaloneSetup(controller)
				.addInterceptors(new ApiVersionInterceptor(new BrokerApiVersion()))
				.setControllerAdvice(ServiceBrokerWebMvcExceptionHandler.class)
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}

	private MockMvc mockWithExpectedVersion() {
		return MockMvcBuilders.standaloneSetup(controller)
				.addInterceptors(new ApiVersionInterceptor(new BrokerApiVersion("expected-version")))
				.setControllerAdvice(ServiceBrokerWebMvcExceptionHandler.class)
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}

}
