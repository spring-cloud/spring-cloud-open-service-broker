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

package org.springframework.cloud.servicebroker.model.instance;

import java.util.HashMap;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

public class GetServiceInstanceResponseTest {
	@Test
	public void responseWithDefaultsIsBuilt() {
		GetServiceInstanceResponse response = GetServiceInstanceResponse.builder()
				.build();

		assertThat(response.getServiceDefinitionId()).isNull();
		assertThat(response.getPlanId()).isNull();
		assertThat(response.getDashboardUrl()).isNull();
		assertThat(response.getParameters()).hasSize(0);

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasNoPath("$.service_id");
		assertThat(json).hasNoPath("$.plan_id");
		assertThat(json).hasNoPath("$.dashboard_url");
		assertThat(json).hasMapAtPath("$.parameters").hasSize(0);
	}

	@Test
	@SuppressWarnings("serial")
	public void responseWithAllValuesIsBuilt() {
		Map<String, Object> parameters = new HashMap<String, Object>() {{
			put("field4", "value4");
			put("field5", "value5");
		}};

		GetServiceInstanceResponse response = GetServiceInstanceResponse.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.dashboardUrl("https://dashboard.example.com")
				.parameters("field1", "value1")
				.parameters("field2", 2)
				.parameters("field3", true)
				.parameters(parameters)
				.build();

		assertThat(response.getServiceDefinitionId()).isEqualTo("service-definition-id");
		assertThat(response.getPlanId()).isEqualTo("plan-id");
		assertThat(response.getDashboardUrl()).isEqualTo("https://dashboard.example.com");

		assertThat(response.getParameters()).hasSize(5);
		assertThat(response.getParameters().get("field1")).isEqualTo("value1");
		assertThat(response.getParameters().get("field2")).isEqualTo(2);
		assertThat(response.getParameters().get("field3")).isEqualTo(true);
		assertThat(response.getParameters().get("field4")).isEqualTo("value4");
		assertThat(response.getParameters().get("field5")).isEqualTo("value5");

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasPath("$.service_id").isEqualTo("service-definition-id");
		assertThat(json).hasPath("$.plan_id").isEqualTo("plan-id");
		assertThat(json).hasPath("$.dashboard_url").isEqualTo("https://dashboard.example.com");
		assertThat(json).hasPath("$.parameters.field1").isEqualTo("value1");
		assertThat(json).hasPath("$.parameters.field2").isEqualTo(2);
		assertThat(json).hasPath("$.parameters.field3").isEqualTo(true);
		assertThat(json).hasPath("$.parameters.field4").isEqualTo("value4");
		assertThat(json).hasPath("$.parameters.field5").isEqualTo("value5");
	}

	@Test
	public void responseWithAllValuesIsDeserialized() {
		GetServiceInstanceResponse response = JsonUtils.readTestDataFile(
				"getResponse.json", GetServiceInstanceResponse.class);

		assertThat(response.getServiceDefinitionId()).isEqualTo("service-definition-id");
		assertThat(response.getPlanId()).isEqualTo("plan-id");
		assertThat(response.getDashboardUrl()).isEqualTo("https://dashboard.local");
		assertThat(response.getParameters()).containsOnly(entry("field1", "foo"), entry("field2", "bar"));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(GetServiceInstanceResponse.class)
				.verify();
	}
}
