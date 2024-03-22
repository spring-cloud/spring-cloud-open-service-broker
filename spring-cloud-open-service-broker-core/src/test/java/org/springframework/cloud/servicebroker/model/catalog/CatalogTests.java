/*
 * Copyright 2002-2024 the original author or authors.
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

package org.springframework.cloud.servicebroker.model.catalog;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

class CatalogTests {

	@Test
	void emptyCatalogIsSerializedToJson() {
		Catalog catalog = Catalog.builder().build();

		assertThat(catalog.getServiceDefinitions()).hasSize(0);

		DocumentContext json = JsonUtils.toJsonPath(catalog);

		assertThat(json).hasListAtPath("$.services").hasSize(0);
	}

	@Test
	void catalogWithServicesIsSerializedToJson() {
		List<ServiceDefinition> serviceDefinitions = Collections.singletonList(ServiceDefinition.builder()
			.id("service-definition-id-two")
			.name("service-definition-two")
			.description("Service Definition Two")
			.plans(Plan.builder().build())
			.build());

		Catalog catalog = Catalog.builder()
			.serviceDefinitions(ServiceDefinition.builder()
				.id("service-definition-id-one")
				.name("service-definition-one")
				.description("Service Definition One")
				.plans(Plan.builder().build())
				.build())
			.serviceDefinitions(serviceDefinitions)
			.build();

		List<ServiceDefinition> actualDefinitions = catalog.getServiceDefinitions();
		assertThat(actualDefinitions.get(0).getId()).isEqualTo("service-definition-id-one");
		assertThat(actualDefinitions.get(0).getName()).isEqualTo("service-definition-one");
		assertThat(actualDefinitions.get(0).getDescription()).isEqualTo("Service Definition One");

		assertThat(actualDefinitions.get(1).getId()).isEqualTo("service-definition-id-two");
		assertThat(actualDefinitions.get(1).getName()).isEqualTo("service-definition-two");
		assertThat(actualDefinitions.get(1).getDescription()).isEqualTo("Service Definition Two");

		DocumentContext json = JsonUtils.toJsonPath(catalog);

		assertThat(json).hasListAtPath("$.services").hasSize(2);

		assertThat(json).hasPath("$.services[0].id").isEqualTo("service-definition-id-one");
		assertThat(json).hasPath("$.services[0].name").isEqualTo("service-definition-one");
		assertThat(json).hasPath("$.services[0].description").isEqualTo("Service Definition One");

		assertThat(json).hasPath("$.services[1].id").isEqualTo("service-definition-id-two");
		assertThat(json).hasPath("$.services[1].name").isEqualTo("service-definition-two");
		assertThat(json).hasPath("$.services[1].description").isEqualTo("Service Definition Two");
	}

	@Test
	void catalogWithServicesIsDeserializedFromJson() {
		Catalog catalog = JsonUtils.readTestDataFile("catalog.json", Catalog.class);

		assertThat(catalog.getServiceDefinitions().get(0).getId()).isEqualTo("service-one-id");
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).getMetadata()).contains(
				entry("displayName", "sample display name"), entry("bullets", Arrays.asList("bullet1", "bullet2")),
				entry("key1", "value1"), entry("key2", "value2"));

		@SuppressWarnings("unchecked")
		Map<String, Object> cost = ((List<Map<String, Object>>) catalog.getServiceDefinitions()
			.get(0)
			.getPlans()
			.get(1)
			.getMetadata()
			.get("costs")).get(0);
		assertThat(cost.get("unit")).isEqualTo("MONTHLY");

		@SuppressWarnings("unchecked")
		Map<String, Object> amount = (Map<String, Object>) cost.get("amount");
		assertThat(amount).containsOnly(entry("usd", 649.0));
	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier.forClass(Catalog.class).verify();
	}

}
