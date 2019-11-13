/*
 * Copyright 2002-2019 the original author or authors.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.cloud.servicebroker.autoconfigure.web.ServiceInstanceBindingIntegrationTest;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController;
import org.springframework.cloud.servicebroker.service.NonBindableServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(MockitoExtension.class)
class NonBindableServiceInstanceBindingControllerIntegrationTest extends ServiceInstanceBindingIntegrationTest {

	private WebTestClient client;

	@BeforeEach
	void setUp() {
		ServiceInstanceBindingService serviceInstanceBindingService = new NonBindableServiceInstanceBindingService();
		ServiceInstanceBindingController controller =
				new ServiceInstanceBindingController(catalogService, serviceInstanceBindingService);

		this.client = WebTestClient.bindToController(controller)
				.build();
	}

	@Test
	void createBindingToAppFails() {
		setupCatalogService();

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is5xxServerError()
				.expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Test
	void deleteBindingFails() {
		client.delete().uri(buildDeleteUrl())
				.exchange()
				.expectStatus().is5xxServerError()
				.expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
