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
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class RequestIdentityTests {

	@Autowired
	private RestTestClient rest;

	@Test
	void requestIdentityHeaderIsEchoedInResponse() {
		HttpHeaders responseHeaders = this.rest.get()
			.uri("/v2/catalog")
			.header(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, "request-id")
			.exchange()
			.returnResult()
			.getResponseHeaders();

		assertThat(responseHeaders.getFirst(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER)).isEqualTo("request-id");
	}

}
