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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

class PlanTests {

	@Test
	void planWithDefaultsIsSerializedToJson() {
		Plan plan = Plan.builder().id("plan-id-one").name("plan-one").description("Plan One").build();

		assertThat(plan.getId()).isEqualTo("plan-id-one");
		assertThat(plan.getName()).isEqualTo("plan-one");
		assertThat(plan.getDescription()).isEqualTo("Plan One");
		assertThat(plan.getMetadata()).isNull();
		assertThat(plan.isFree()).isEqualTo(true);
		assertThat(plan.isBindable()).isNull();
		assertThat(plan.isPlanUpdateable()).isNull();
		assertThat(plan.getSchemas()).isNull();
		assertThat(plan.getMaximumPollingDuration()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(plan);

		assertThat(json).hasPath("$.id").isEqualTo("plan-id-one");
		assertThat(json).hasPath("$.name").isEqualTo("plan-one");
		assertThat(json).hasPath("$.description").isEqualTo("Plan One");
		assertThat(json).hasNoPath("$.metadata");
		assertThat(json).hasPath("$.free").isEqualTo(true);
		assertThat(json).hasNoPath("$.bindable");
		assertThat(json).hasNoPath("$.plan_updateable");
		assertThat(json).hasNoPath("$.schemas");

		Plan deserialized = JsonUtils.fromJson(JsonUtils.toJson(plan), Plan.class);
		assertThat(deserialized.getId()).isEqualTo("plan-id-one");
		assertThat(deserialized.getName()).isEqualTo("plan-one");
		assertThat(deserialized.getDescription()).isEqualTo("Plan One");
		assertThat(deserialized.getMetadata()).isEmpty(); // it's ok to not return null
		assertThat(deserialized.isFree()).isEqualTo(true);
		assertThat(deserialized.isBindable()).isNull();
		assertThat(deserialized.isPlanUpdateable()).isNull();
		assertThat(deserialized.getSchemas()).isNull();
	}

	@Test
	void planWithAllFieldsIsSerializedToJson() {
		Map<String, Object> amount = new HashMap<>();
		amount.put("usd", 649.0d);

		Map<String, Object> standardCost = new HashMap<>();
		standardCost.put("unit", "MONTHLY");
		standardCost.put("amount", amount);

		List<Map<String, Object>> costs = new ArrayList<>();
		costs.add(standardCost);

		Map<String, Object> metadata = new HashMap<>();
		metadata.put("field3", "value3");
		metadata.put("field4", "value4");
		metadata.put("bullets", new String[] { "bullet1", "bullet2" });
		metadata.put("costs", costs);
		metadata.put("displayName", "sample display name");

		Plan plan = Plan.builder()
			.id("plan-id-one")
			.name("plan-one")
			.description("Plan One")
			.free(false)
			.bindable(true)
			.planUpdateable(true)
			.metadata("field1", "value1")
			.metadata("field2", "value2")
			.metadata(metadata)
			.schemas(Schemas.builder().build())
			.maximumPollingDuration(210)
			.maintenanceInfo(MaintenanceInfo.builder()
				.version(1, 0, 0, "-alpha+001")
				.description("Description for maintenance info")
				.build())
			.build();

		assertThat(plan.getId()).isEqualTo("plan-id-one");
		assertThat(plan.getName()).isEqualTo("plan-one");
		assertThat(plan.getDescription()).isEqualTo("Plan One");
		assertThat(plan.isFree()).isEqualTo(false);
		assertThat(plan.isBindable()).isEqualTo(true);
		assertThat(plan.isPlanUpdateable()).isEqualTo(true);
		assertThat(plan.getMetadata()).hasSize(7);
		assertThat(plan.getMetadata()).contains(entry("field1", "value1"), entry("field2", "value2"),
				entry("field3", "value3"), entry("field4", "value4"), entry("displayName", "sample display name"),
				entry("bullets", new String[] { "bullet1", "bullet2" }), entry("costs", costs));
		assertThat(plan.getSchemas()).isNotNull();
		assertThat(plan.getMaximumPollingDuration()).isEqualTo(210);
		assertThat(plan.getMaintenanceInfo().getVersion()).isEqualTo("1.0.0-alpha+001");
		assertThat(plan.getMaintenanceInfo().getDescription()).isEqualTo("Description for maintenance info");

		DocumentContext json = JsonUtils.toJsonPath(plan);

		assertThat(json).hasPath("$.id").isEqualTo("plan-id-one");
		assertThat(json).hasPath("$.name").isEqualTo("plan-one");
		assertThat(json).hasPath("$.description").isEqualTo("Plan One");
		assertThat(json).hasPath("$.free").isEqualTo(false);
		assertThat(json).hasPath("$.bindable").isEqualTo(true);
		assertThat(json).hasPath("$.plan_updateable").isEqualTo(true);
		assertThat(json).hasMapAtPath("$.metadata")
			.contains(entry("field1", "value1"), entry("field2", "value2"), entry("field3", "value3"),
					entry("field4", "value4"));
		assertThat(json).hasPath("$.metadata.displayName").isEqualTo("sample display name");
		assertThat(json).hasPath("$.metadata.costs");
		assertThat(json).hasMapAtPath("$.metadata.costs[0]");
		assertThat(json).hasMapAtPath("$.metadata.costs[0].amount").contains(entry("usd", 649.0d));
		assertThat(json).hasPath("$.metadata.costs[0].unit").isEqualTo("MONTHLY");
		assertThat(json).hasListAtPath("$.metadata.bullets").containsOnly("bullet1", "bullet2");
		assertThat(json).hasPath("$.schemas");
		assertThat(json).hasPath("$.maximum_polling_duration").isEqualTo(210);
		assertThat(json).hasPath("$.maintenance_info.version").isEqualTo("1.0.0-alpha+001");
		assertThat(json).hasPath("$.maintenance_info.description").isEqualTo("Description for maintenance info");
	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier.forClass(Plan.class).verify();
	}

}
