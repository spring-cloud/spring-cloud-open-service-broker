/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web.reactive;

import java.nio.charset.Charset;

import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ApiVersionWebFilterIntegrationTest {

	private static final String CATALOG_PATH = "/v2/catalog";

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	@InjectMocks
	private CatalogController controller;

	@Mock
	@SuppressWarnings("unused")
	private CatalogService catalogService;

	@Before
	public void setUp() {
		Catalog expectedCatalog = Catalog.builder().build();
		given(catalogService.getCatalog())
				.willReturn(Mono.just(expectedCatalog));
	}

	@Test
	public void noHeaderSent() throws Exception {
		mockWithExpectedVersion().get().uri(CATALOG_PATH)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.PRECONDITION_FAILED)
				.expectBody()
				.consumeWith(result -> {
					String responseBody = new String(result.getResponseBody(), UTF_8);
					String description = JsonPath.read(responseBody, "$.description");
					assertThat(description).contains("expected-version");
				});
	}

	@Test
	public void incorrectHeaderSent() throws Exception {
		mockWithExpectedVersion().get().uri(CATALOG_PATH)
				.header(BrokerApiVersion.DEFAULT_API_VERSION_HEADER, "wrong-version")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.PRECONDITION_FAILED)
				.expectBody()
				.consumeWith(result -> {
					String responseBody = new String(result.getResponseBody(), UTF_8);
					String description = JsonPath.read(responseBody, "$.description");
					assertThat(description).contains("expected-version");
					assertThat(description).contains("wrong-version");
				});
	}

	@Test
	public void matchingHeaderSent() throws Exception {
		mockWithExpectedVersion().get().uri(CATALOG_PATH)
				.header(BrokerApiVersion.DEFAULT_API_VERSION_HEADER, "expected-version")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void anyHeaderNotSent() throws Exception {
		mockWithDefaultVersion().get().uri(CATALOG_PATH)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void anyHeaderSent() throws Exception {
		mockWithDefaultVersion().get().uri(CATALOG_PATH)
				.header(BrokerApiVersion.DEFAULT_API_VERSION_HEADER, "ignored-version")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}

	private WebTestClient mockWithDefaultVersion() {
		return WebTestClient.bindToController(controller)
				.webFilter(new ApiVersionWebFilter(new BrokerApiVersion()))
				.build();
	}

	private WebTestClient mockWithExpectedVersion() {
		return WebTestClient.bindToController(controller)
				.webFilter(new ApiVersionWebFilter(new BrokerApiVersion("expected-version")))
				.build();
	}

}
