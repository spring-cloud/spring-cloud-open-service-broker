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

package org.springframework.cloud.servicebroker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.service.CatalogService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class CatalogControllerTest {

	@Mock
	private CatalogService catalogService;

	@BeforeEach
	void setUp() {
		openMocks(this);
	}

	@Test
	void catalogIsReturned() throws Exception {
		Catalog expectedCatalog = Catalog.builder().build();

		given(catalogService.getCatalog())
				.willReturn(Mono.just(expectedCatalog));

		given(this.catalogService.getResponseEntityCatalog(any()))
				.willReturn(Mono.empty());

		CatalogController controller = new CatalogController(catalogService);
		Catalog actualCatalog = controller.getCatalog(null).block().getBody();
		assertThat(actualCatalog).isEqualTo(expectedCatalog);
	}

}
