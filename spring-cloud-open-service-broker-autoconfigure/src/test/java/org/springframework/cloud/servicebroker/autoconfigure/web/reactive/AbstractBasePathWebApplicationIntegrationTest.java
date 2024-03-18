/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web.reactive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.cloud.servicebroker.JsonUtils;
import org.springframework.cloud.servicebroker.autoconfigure.web.AbstractBasePathIntegrationTest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import static java.lang.String.format;

@TestPropertySource(properties = "spring.main.web-application-type=reactive")
@AutoConfigureWebTestClient
public abstract class AbstractBasePathWebApplicationIntegrationTest extends AbstractBasePathIntegrationTest {

	@Autowired
	protected WebTestClient client;

	@Override
	protected void assertFound(String baseUri, String expectedPlatform) {
		createExchange(baseUri)
				.expectStatus().isCreated()
				.expectBody().jsonPath("operation")
				.isEqualTo(format("platform: %s", expectedPlatform))
		;
	}

	@Override
	protected void assertNotFound(String baseUri) {
		createExchange(baseUri)
				.expectStatus().isNotFound();
	}

	private ResponseSpec createExchange(String baseUri) {
		String request = JsonUtils.toJson(CreateServiceInstanceRequest.builder()
				.serviceDefinitionId("default-service")
				.planId("default-plan")
				.build()
		);

		return client.put()
				.uri(format("%s/v2/service_instances/default-service", baseUri))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.exchange();
	}

}
