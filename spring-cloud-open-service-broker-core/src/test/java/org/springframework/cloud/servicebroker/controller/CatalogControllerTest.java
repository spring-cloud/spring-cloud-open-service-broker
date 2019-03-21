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

package org.springframework.cloud.servicebroker.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.service.CatalogService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class CatalogControllerTest {
	@Mock
	private CatalogService catalogService;

	@Before
	public void setUp() {
		initMocks(this);
	}

	@Test
	public void catalogIsReturned() {
		Catalog expectedCatalog = Catalog.builder().build();
		
		when(catalogService.getCatalog()).thenReturn(expectedCatalog);

		CatalogController controller = new CatalogController(catalogService);

		Catalog actualCatalog = controller.getCatalog();

		assertThat(actualCatalog).isEqualTo(expectedCatalog);
	}
}
