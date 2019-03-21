/*
 * Copyright 2002-2018 the original author or authors.
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

import org.junit.Before;
import org.junit.Test;

import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanCatalogServiceTest {

	private static final String SVC_DEF_ID = "svc-def-id";

	private BeanCatalogService service;

	private Catalog catalog;

	private ServiceDefinition serviceDefinition;

	@Before
	public void setUp() {
		serviceDefinition = ServiceDefinition.builder()
				.id(SVC_DEF_ID)
				.name("Name")
				.description("Description")
				.bindable(true)
				.build();

		catalog = Catalog.builder()
				.serviceDefinitions(serviceDefinition)
				.build();

		service = new BeanCatalogService(catalog);
	}

	@Test
	public void catalogIsReturnedSuccessfully() {
		assertThat(service.getCatalog().block()).isEqualTo(catalog);
	}

	@Test
	public void serviceDefinitionIsFound() {
		assertThat(service.getServiceDefinition(SVC_DEF_ID).block()).isEqualTo(serviceDefinition);
	}

	@Test
	public void serviceDefinitionIsNotFound() {
		assertThat(service.getServiceDefinition("NOT_THERE").block()).isNull();
	}

}
