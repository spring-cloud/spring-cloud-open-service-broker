/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.cloud.servicebroker.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import java.util.Collections;
import java.util.List;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class CatalogTest {
	@Test
	public void emptyCatalogIsSerializedToJson() {
		Catalog catalog = Catalog.builder().build();
		String json = DataFixture.toJson(catalog);

		assertThat(json, isJson(withJsonPath("$.services", hasSize(0))));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void catalogWithServicesIsSerializedToJson() {
		List<ServiceDefinition> serviceDefinitions = Collections.singletonList(ServiceDefinition.builder()
				.id("service-definition-id-two")
				.name("service-definition-two")
				.description("Service Definition Two")
				.plans(Plan.builder().build())
				.build());

		Catalog catalog = Catalog.builder()
				.serviceDefinitions(
						ServiceDefinition.builder()
								.id("service-definition-id-one")
								.name("service-definition-one")
								.description("Service Definition One")
								.plans(Plan.builder().build())
								.build())
				.serviceDefinitions(serviceDefinitions)
				.build();

		String json = DataFixture.toJson(catalog);

		assertThat(json, isJson(allOf(
				withJsonPath("$.services", hasSize(2)),
				
				withJsonPath("$.services[0].id", equalTo("service-definition-id-one")),
				withJsonPath("$.services[0].name", equalTo("service-definition-one")),
				withJsonPath("$.services[0].description", equalTo("Service Definition One")),

				withJsonPath("$.services[1].id", equalTo("service-definition-id-two")),
				withJsonPath("$.services[1].name", equalTo("service-definition-two")),
				withJsonPath("$.services[1].description", equalTo("Service Definition Two"))
		)));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(Catalog.class).verify();
	}
}
