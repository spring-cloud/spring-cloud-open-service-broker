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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ApiVersionWebFilterTest {

	private static final String V2_API_PATH_PATTERN = "/v2/**";

	private MockServerWebExchange exchange;

	@Mock
	private WebFilterChain chain;

	@Before
	public void setUp() {
		MockServerHttpRequest request = MockServerHttpRequest
				.get(V2_API_PATH_PATTERN)
				.header("header", "9.9")
				.build();
		this.exchange = MockServerWebExchange.from(request);
		MockitoAnnotations.initMocks(this);
		exchange.getResponse().setStatusCode(HttpStatus.OK);
		when(chain.filter(exchange)).thenReturn(Mono.empty());
	}

	@Test
	public void noBrokerApiVersionConfigured() {
		ApiVersionWebFilter webFilter = new ApiVersionWebFilter();
		webFilter.filter(exchange, chain);
		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void anyVersionAccepted() {
		BrokerApiVersion brokerApiVersion = new BrokerApiVersion("header", BrokerApiVersion.API_VERSION_ANY);
		ApiVersionWebFilter webFilter = new ApiVersionWebFilter(brokerApiVersion);
		webFilter.filter(exchange, chain).block();
		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void versionsMatch() {
		BrokerApiVersion brokerApiVersion = new BrokerApiVersion("header", "9.9");
		ApiVersionWebFilter webFilter = new ApiVersionWebFilter(brokerApiVersion);
		webFilter.filter(exchange, chain).block();
		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void versionMismatch() {
		BrokerApiVersion brokerApiVersion = new BrokerApiVersion("header", "8.8");
		ApiVersionWebFilter webFilter = new ApiVersionWebFilter(brokerApiVersion);
		webFilter.filter(exchange, chain).block();
		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
	}

}
