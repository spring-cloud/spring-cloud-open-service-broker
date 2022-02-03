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

package org.springframework.cloud.servicebroker.autoconfigure.web.reactive;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

class RequestIdentityWebFilterTest {

	private static final String V2_API_PATH_PATTERN = "/v2/**";

	private MockServerWebExchange exchange;

	@Mock
	private WebFilterChain chain;

	@Test
	void requestIdentityHeader() {
		MockServerHttpRequest request = MockServerHttpRequest
				.get(V2_API_PATH_PATTERN)
				.header(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, "request-id")
				.build();
		this.exchange = MockServerWebExchange.from(request);
		openMocks(this);
		given(chain.filter(exchange)).willReturn(Mono.empty());
		RequestIdentityWebFilter webFilter = new RequestIdentityWebFilter();
		webFilter.filter(exchange, chain).block();
		assertThat(exchange.getResponse().getHeaders().getFirst(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER))
				.isEqualTo("request-id");
	}

	@Test
	void requestIdentityHeaderMissing() {
		MockServerHttpRequest request = MockServerHttpRequest
				.get(V2_API_PATH_PATTERN)
				.build();
		this.exchange = MockServerWebExchange.from(request);
		openMocks(this);
		given(chain.filter(exchange)).willReturn(Mono.empty());
		RequestIdentityWebFilter webFilter = new RequestIdentityWebFilter();
		webFilter.filter(exchange, chain).block();
		assertThat(exchange.getResponse().getHeaders().getFirst(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER)).isNull();
	}

}
