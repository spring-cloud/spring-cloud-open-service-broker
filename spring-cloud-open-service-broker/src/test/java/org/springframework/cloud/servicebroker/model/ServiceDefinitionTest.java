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

import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withoutJsonPath;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.servicebroker.model.ServiceDefinitionRequires.SERVICE_REQUIRES_ROUTE_FORWARDING;
import static org.springframework.cloud.servicebroker.model.ServiceDefinitionRequires.SERVICE_REQUIRES_SYSLOG_DRAIN;
import static org.springframework.cloud.servicebroker.model.ServiceDefinitionRequires.SERVICE_REQUIRES_VOLUME_MOUNT;

public class ServiceDefinitionTest {
	@Test
	@SuppressWarnings("unchecked")
	public void serviceDefinitionWithDefaultsIsSerializedToJson() {
		ServiceDefinition serviceDefinition = ServiceDefinition.builder()
				.id("service-definition-id-one")
				.name("service-definition-one")
				.description("Service Definition One")
				.plans(Plan.builder().build())
				.build();
		String json = DataFixture.toJson(serviceDefinition);

		assertThat(json, isJson(allOf(
				withJsonPath("$.id", equalTo("service-definition-id-one")),
				withJsonPath("$.name", equalTo("service-definition-one")),
				withJsonPath("$.description", equalTo("Service Definition One")),
				withJsonPath("$.plans", hasSize(1)),
				withJsonPath("$.bindable", equalTo(false)),
				withJsonPath("$.plan_updateable", equalTo(false)),
				withoutJsonPath("$.tags"),
				withoutJsonPath("$.requires"),
				withoutJsonPath("$.metadata"),
				withoutJsonPath("$.dashboard_client")
		)));
	}

	@Test
	@SuppressWarnings({"unchecked", "serial"})
	public void serviceDefinitionWithAllFieldsIsSerializedToJson() {
		Map<String, Object> metadata = new HashMap<String, Object>() {{
			put("field3", "value3");
			put("field4", "value4");
		}};

		ServiceDefinition serviceDefinition = ServiceDefinition.builder()
				.id("service-definition-id-one")
				.name("service-definition-one")
				.description("Service Definition One")
				.plans(Plan.builder().build())
				.bindable(true)
				.tags("tag1", "tag2")
				.metadata("field1", "value1")
				.metadata("field2", "value2")
				.metadata(metadata)
				.requires(SERVICE_REQUIRES_ROUTE_FORWARDING,
						SERVICE_REQUIRES_SYSLOG_DRAIN,
						SERVICE_REQUIRES_VOLUME_MOUNT)
				.requires("another_requires")
				.planUpdateable(true)
				.dashboardClient(DashboardClient.builder().build())
				.build();
		String json = DataFixture.toJson(serviceDefinition);

		assertThat(json, isJson(allOf(
				withJsonPath("$.id", equalTo("service-definition-id-one")),
				withJsonPath("$.name", equalTo("service-definition-one")),
				withJsonPath("$.description", equalTo("Service Definition One")),
				withJsonPath("$.plans", hasSize(1)),
				withJsonPath("$.bindable", equalTo(true)),
				withJsonPath("$.plan_updateable", equalTo(true)),
				withJsonPath("$.tags[*]", contains("tag1", "tag2")),
				withJsonPath("$.requires[*]",
						contains(SERVICE_REQUIRES_ROUTE_FORWARDING.toString(),
								SERVICE_REQUIRES_SYSLOG_DRAIN.toString(),
								SERVICE_REQUIRES_VOLUME_MOUNT.toString(),
								"another_requires")),
				withJsonPath("$.metadata", aMapWithSize(4)),
				withJsonPath("$.metadata",
						allOf(hasEntry("field1", "value1"),
								hasEntry("field2", "value2"),
								hasEntry("field3", "value3"),
								hasEntry("field4", "value4"))),
				withJsonPath("$.dashboard_client", notNullValue())
		)));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(ServiceDefinition.class)
				.verify();
	}

}
