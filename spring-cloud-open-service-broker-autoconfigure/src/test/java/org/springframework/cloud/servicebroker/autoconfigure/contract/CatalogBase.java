/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.servicebroker.autoconfigure.contract;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;

import static org.mockito.Mockito.when;

public class CatalogBase {

	@Mock
	private CatalogService catalogService;

	@Before
	public void setup() {
		ServiceDefinition serviceDefinition = ServiceFixture.getSimpleService();
		Catalog catalog = Catalog.builder()
				.serviceDefinitions(serviceDefinition)
				.build();
		MockitoAnnotations.initMocks(this);
		when(this.catalogService.getCatalog()).thenReturn(catalog);
		RestAssuredMockMvc.standaloneSetup(new CatalogController(this.catalogService));
	}

}
