/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web.reactive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RequestIdentityWebFilterIntegrationTest {

	private static final String CATALOG_PATH = "/v2/catalog";

	@InjectMocks
	private CatalogController controller;

	@Mock
	@SuppressWarnings("unused")
	private CatalogService catalogService;

	private WebTestClient client;

	@BeforeEach
	public void setUp() {
		this.client = WebTestClient.bindToController(controller)
				.webFilter(new RequestIdentityWebFilter())
				.build();
	}

	@Test
	public void noHeaderSent() throws Exception {
		client.get().uri(CATALOG_PATH)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectBody()
				.consumeWith(result -> assertThat(
						result.getResponseHeaders().getFirst(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER))
						.isNull());
	}

	@Test
	public void headerSent() throws Exception {
		client.get().uri(CATALOG_PATH)
				.header(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, "request-id")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectBody()
				.consumeWith(result -> assertThat(
						result.getResponseHeaders().getFirst(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER))
						.isEqualTo("request-id"));
	}

}
