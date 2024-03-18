/*
 * Copyright 2002-2023 the original author or authors.
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

package org.springframework.cloud.servicebroker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class BeanCatalogServiceTests {

	private static final String SVC_DEF_ID = "svc-def-id";

	private BeanCatalogService service;

	private Catalog catalog;

	private ServiceDefinition serviceDefinition;

	@BeforeEach
	void setUp() {
		this.serviceDefinition = ServiceDefinition.builder()
			.id(SVC_DEF_ID)
			.name("Name")
			.description("Description")
			.bindable(true)
			.build();

		this.catalog = Catalog.builder().serviceDefinitions(this.serviceDefinition).build();

		this.service = new BeanCatalogService(this.catalog);
	}

	@Test
	void catalogIsReturnedSuccessfully() {
		assertThat(this.service.getCatalog().block()).isEqualTo(this.catalog);
	}

	@Test
	void serviceDefinitionIsFound() {
		assertThat(this.service.getServiceDefinition(SVC_DEF_ID).block()).isEqualTo(this.serviceDefinition);
	}

	@Test
	void serviceDefinitionIsNotFound() {
		assertThat(this.service.getServiceDefinition("NOT_THERE").block()).isNull();
	}

}
