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

package org.springframework.cloud.servicebroker.model.instance;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.Matchers;
import org.junit.Test;

import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class GetServiceInstanceResponseTest {
	@Test
	public void responseWithDefaultsIsBuilt() {
		GetServiceInstanceResponse response = GetServiceInstanceResponse.builder()
				.build();

		assertThat(response.getServiceDefinitionId(), nullValue());
		assertThat(response.getPlanId(), nullValue());
		assertThat(response.getDashboardUrl(), nullValue());
		assertThat(response.getParameters(), aMapWithSize(0));
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

		assertThat(response.getServiceDefinitionId(), equalTo("service-definition-id"));
		assertThat(response.getPlanId(), equalTo("plan-id"));
		assertThat(response.getDashboardUrl(), equalTo("https://dashboard.example.com"));

		assertThat(response.getParameters(), aMapWithSize(5));
		assertThat(response.getParameters().get("field1"), equalTo("value1"));
		assertThat(response.getParameters().get("field2"), equalTo(2));
		assertThat(response.getParameters().get("field3"), equalTo(true));
		assertThat(response.getParameters().get("field4"), equalTo("value4"));
		assertThat(response.getParameters().get("field5"), equalTo("value5"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void responseIsSerializedToJson() {
		GetServiceInstanceResponse response = GetServiceInstanceResponse.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.dashboardUrl("http://dashboard.example.com")
				.parameters("field1", "value1")
				.parameters("field2", 2)
				.parameters("field3", true)
				.build();

		String json = DataFixture.toJson(response);

		assertThat(json, isJson(Matchers.allOf(
				withJsonPath("$.service_id", equalTo("service-definition-id")),
				withJsonPath("$.plan_id", equalTo("plan-id")),
				withJsonPath("$.dashboard_url", equalTo("http://dashboard.example.com")),
				withJsonPath("$.parameters", aMapWithSize(3))
		)));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(GetServiceInstanceResponse.class)
				.verify();
	}
}