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

package org.springframework.cloud.servicebroker.model.binding;

import nl.jqno.equalsverifier.EqualsVerifier;
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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class GetServiceInstanceRouteBindingResponseTest {

	@Test
	@SuppressWarnings("unchecked")
	public void responseWithDefaultsIsBuilt() {
		GetServiceInstanceRouteBindingResponse response = GetServiceInstanceRouteBindingResponse.builder()
				.build();

		assertThat(response.getRouteServiceUrl(), nullValue());

		String json = DataFixture.toJson(response);

		assertThat(json, isJson(allOf(
				withoutJsonPath("$.parameters"),
				withoutJsonPath("$.route_service_url")
		)));
	}

	@Test
	@SuppressWarnings({"unchecked", "serial"})
	public void responseWithValuesIsBuilt() {
		Map<String, Object> parameters = new HashMap<String, Object>() {{
			put("field4", "value4");
			put("field5", "value5");
		}};

		GetServiceInstanceRouteBindingResponse response = GetServiceInstanceRouteBindingResponse.builder()
				.parameters("field1", "value1")
				.parameters("field2", 2)
				.parameters("field3", true)
				.parameters(parameters)
				.routeServiceUrl("https://routes.example.com")
				.build();

		assertThat(response.getParameters(), aMapWithSize(5));
		assertThat(response.getParameters().get("field1"), equalTo("value1"));
		assertThat(response.getParameters().get("field2"), equalTo(2));
		assertThat(response.getParameters().get("field3"), equalTo(true));
		assertThat(response.getParameters().get("field4"), equalTo("value4"));
		assertThat(response.getParameters().get("field5"), equalTo("value5"));

		assertThat(response.getRouteServiceUrl(), equalTo("https://routes.example.com"));

		String json = DataFixture.toJson(response);

		assertThat(json, isJson(allOf(
				withJsonPath("$.parameters", aMapWithSize(5)),
				withJsonPath("$.parameters.field1", equalTo("value1")),
				withJsonPath("$.parameters.field2", equalTo(2)),
				withJsonPath("$.parameters.field3", equalTo(true)),
				withJsonPath("$.parameters.field4", equalTo("value4")),
				withJsonPath("$.parameters.field5", equalTo("value5")),
				withJsonPath("$.route_service_url", equalTo("https://routes.example.com"))
		)));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(GetServiceInstanceRouteBindingResponse.class)
				.withRedefinedSuperclass()
				.verify();
	}
}