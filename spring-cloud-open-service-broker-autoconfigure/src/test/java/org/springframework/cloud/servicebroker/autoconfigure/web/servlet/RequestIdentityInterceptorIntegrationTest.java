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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class RequestIdentityInterceptorIntegrationTest {

	private final static String CATALOG_PATH = "/v2/catalog";

	@InjectMocks
	private CatalogController controller;

	@Mock
	private CatalogService catalogService;

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		this.mvc = MockMvcBuilders.standaloneSetup(controller)
				.addInterceptors(new RequestIdentityInterceptor())
				.build();
		given(catalogService.getCatalog())
				.willReturn(Mono.just(Catalog.builder().build()));
	}

	@Test
	void noHeaderSent() throws Exception {
		mvc.perform(get(CATALOG_PATH)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(result -> assertThat(
						result.getResponse().getHeaderValue(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER)).isNull())
				.andReturn();
	}

	@Test
	void headerSent() throws Exception {
		mvc.perform(get(CATALOG_PATH)
				.header(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, "request-id")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(result -> assertThat(
						result.getResponse().getHeaderValue(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER))
						.isEqualTo("request-id"))
				.andReturn();
	}

}
