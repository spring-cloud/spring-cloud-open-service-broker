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

import org.junit.Test;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withoutJsonPath;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

public class PlanTest {

	@Test
	@SuppressWarnings("unchecked")
	public void planWithDefaultsIsSerializedToJson() throws Exception {
		Plan plan = Plan.builder()
				.id("plan-id-one")
				.name("plan-one")
				.description("Plan One")
				.build();

		String json = DataFixture.toJson(plan);

		assertThat(json, isJson(allOf(
				withJsonPath("$.id", equalTo("plan-id-one")),
				withJsonPath("$.name", equalTo("plan-one")),
				withJsonPath("$.description", equalTo("Plan One")),
				withJsonPath("$.free", equalTo(true)),
				withoutJsonPath("$.bindable"),
				withoutJsonPath("$.metadata"),
				withoutJsonPath("$.schemas")
		)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void planWithAllFieldsIsSerializedToJson() throws Exception {
		Map<String, Object> metadata = new HashMap<String, Object>() {{
			put("field3", "value3");
			put("field4", "value4");
		}};

		Plan plan = Plan.builder()
				.id("plan-id-one")
				.name("plan-one")
				.description("Plan One")
				.free(false)
				.bindable(true)
				.metadata("field1", "value1")
				.metadata("field2", "value2")
				.metadata(metadata)
				.schemas(Schemas.builder().build())
				.build();

		String json = DataFixture.toJson(plan);

		assertThat(json, isJson(allOf(
				withJsonPath("$.id", equalTo("plan-id-one")),
				withJsonPath("$.name", equalTo("plan-one")),
				withJsonPath("$.description", equalTo("Plan One")),
				withJsonPath("$.free", equalTo(false)),
				withJsonPath("$.metadata", aMapWithSize(4)),
				withJsonPath("$.metadata",
						allOf(hasEntry("field1", "value1"),
								hasEntry("field2", "value2"),
								hasEntry("field3", "value3"),
								hasEntry("field4", "value4"))),
				withJsonPath("$.bindable", equalTo(true)),
				withJsonPath("$.schemas")
		)));
	}
}