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

package org.springframework.cloud.servicebroker.model.binding;

import java.util.HashMap;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;


public class GetServiceInstanceRouteBindingResponseTest {

	@Test
	public void responseWithDefaultsIsBuilt() {
		GetServiceInstanceRouteBindingResponse response = GetServiceInstanceRouteBindingResponse.builder()
				.build();

		assertThat(response.getRouteServiceUrl()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasNoPath("$.parameters");
		assertThat(json).hasNoPath("$.route_service_url");
	}

	@Test
	@SuppressWarnings("serial")
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

		assertThat(response.getParameters()).hasSize(5);
		assertThat(response.getParameters().get("field1")).isEqualTo("value1");
		assertThat(response.getParameters().get("field2")).isEqualTo(2);
		assertThat(response.getParameters().get("field3")).isEqualTo(true);
		assertThat(response.getParameters().get("field4")).isEqualTo("value4");
		assertThat(response.getParameters().get("field5")).isEqualTo("value5");

		assertThat(response.getRouteServiceUrl()).isEqualTo("https://routes.example.com");

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasPath("$.parameters.field1").isEqualTo("value1");
		assertThat(json).hasPath("$.parameters.field2").isEqualTo(2);
		assertThat(json).hasPath("$.parameters.field3").isEqualTo(true);
		assertThat(json).hasPath("$.parameters.field4").isEqualTo("value4");
		assertThat(json).hasPath("$.parameters.field5").isEqualTo("value5");

		assertThat(json).hasPath("$.route_service_url").isEqualTo("https://routes.example.com");
	}

	@Test
	public void responseWithValuesIsDeserialized() {
		GetServiceInstanceRouteBindingResponse response = JsonUtils.readTestDataFile(
				"getRouteBindingResponse.json",
				GetServiceInstanceRouteBindingResponse.class);

		assertThat(response.getRouteServiceUrl()).isEqualTo("https://route.local");
		assertThat(response.getParameters())
				.hasSize(2)
				.containsOnly(entry("param1", "foo"), entry("param2", "bar"));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(GetServiceInstanceRouteBindingResponse.class)
				.withRedefinedSuperclass()
				.verify();
	}
}
