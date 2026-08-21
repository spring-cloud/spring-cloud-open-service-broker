/*
 * Copyright 2002-2026 the original author or authors.
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

package org.springframework.cloud.servicebroker.acceptance;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class RequestIdentityTests {

	@Autowired
	private WebTestClient client;

	@Test
	void requestIdentityHeaderIsEchoedInResponse() {
		this.client.get()
			.uri("/v2/catalog")
			.header(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, "request-id")
			.exchange()
			.expectBody()
			.consumeWith((result) -> assertThat(
					result.getResponseHeaders().getFirst(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER))
				.isEqualTo("request-id"));
	}

}
