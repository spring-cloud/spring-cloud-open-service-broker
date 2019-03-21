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

package org.springframework.cloud.servicebroker.model.catalog;

import java.util.HashMap;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;
import static org.springframework.cloud.servicebroker.model.catalog.ServiceDefinitionRequires.SERVICE_REQUIRES_ROUTE_FORWARDING;
import static org.springframework.cloud.servicebroker.model.catalog.ServiceDefinitionRequires.SERVICE_REQUIRES_SYSLOG_DRAIN;
import static org.springframework.cloud.servicebroker.model.catalog.ServiceDefinitionRequires.SERVICE_REQUIRES_VOLUME_MOUNT;

public class ServiceDefinitionTest {
	@Test
	public void serviceDefinitionWithDefaultsIsSerializedToJson() {
		ServiceDefinition serviceDefinition = ServiceDefinition.builder()
				.id("service-definition-id-one")
				.name("service-definition-one")
				.description("Service Definition One")
				.plans(Plan.builder().build())
				.build();

		assertThat(serviceDefinition.getId()).isEqualTo("service-definition-id-one");
		assertThat(serviceDefinition.getName()).isEqualTo("service-definition-one");
		assertThat(serviceDefinition.getDescription()).isEqualTo("Service Definition One");
		assertThat(serviceDefinition.getPlans()).hasSize(1);
		assertThat(serviceDefinition.isBindable()).isEqualTo(false);
		assertThat(serviceDefinition.isPlanUpdateable()).isNull();
		assertThat(serviceDefinition.isInstancesRetrievable()).isNull();
		assertThat(serviceDefinition.isBindingsRetrievable()).isNull();
		assertThat(serviceDefinition.getTags()).isNull();
		assertThat(serviceDefinition.getRequires()).isNull();
		assertThat(serviceDefinition.getMetadata()).isNull();
		assertThat(serviceDefinition.getDashboardClient()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(serviceDefinition);

		assertThat(json).hasPath("$.id").isEqualTo("service-definition-id-one");
		assertThat(json).hasPath("$.name").isEqualTo("service-definition-one");
		assertThat(json).hasPath("$.description").isEqualTo("Service Definition One");
		assertThat(json).hasListAtPath("$.plans").hasSize(1);
		assertThat(json).hasPath("$.bindable").isEqualTo(false);
		assertThat(json).hasNoPath("$.plan_updateable");
		assertThat(json).hasNoPath("$.tags");
		assertThat(json).hasNoPath("$.requires");
		assertThat(json).hasNoPath("$.metadata");
		assertThat(json).hasNoPath("$.dashboard_client");
		assertThat(json).hasNoPath("$.instances_retrievable");
		assertThat(json).hasNoPath("$.bindings_retrievable");
	}

	@Test
	@SuppressWarnings("serial")
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
				.instancesRetrievable(true)
				.bindingsRetrievable(true)
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

		assertThat(serviceDefinition.getId()).isEqualTo("service-definition-id-one");
		assertThat(serviceDefinition.getName()).isEqualTo("service-definition-one");
		assertThat(serviceDefinition.getDescription()).isEqualTo("Service Definition One");
		assertThat(serviceDefinition.getPlans()).hasSize(1);
		assertThat(serviceDefinition.isBindable()).isEqualTo(true);
		assertThat(serviceDefinition.isPlanUpdateable()).isEqualTo(true);
		assertThat(serviceDefinition.isInstancesRetrievable()).isEqualTo(true);
		assertThat(serviceDefinition.isBindingsRetrievable()).isEqualTo(true);
		assertThat(serviceDefinition.getTags()).contains("tag1", "tag2");
		assertThat(serviceDefinition.getRequires()).contains(
							SERVICE_REQUIRES_ROUTE_FORWARDING.toString(),
							SERVICE_REQUIRES_SYSLOG_DRAIN.toString(),
							SERVICE_REQUIRES_VOLUME_MOUNT.toString(),
							"another_requires");
		assertThat(serviceDefinition.getMetadata()).contains(
							entry("field1", "value1"),
							entry("field2", "value2"),
							entry("field3", "value3"),
							entry("field4", "value4"));
		assertThat(serviceDefinition.getDashboardClient()).isNotNull();

		DocumentContext json = JsonUtils.toJsonPath(serviceDefinition);

		assertThat(json).hasPath("$.id").isEqualTo("service-definition-id-one");
		assertThat(json).hasPath("$.name").isEqualTo("service-definition-one");
		assertThat(json).hasPath("$.description").isEqualTo("Service Definition One");
		assertThat(json).hasListAtPath("$.plans").hasSize(1);
		assertThat(json).hasPath("$.bindable").isEqualTo(true);
		assertThat(json).hasPath("$.plan_updateable").isEqualTo(true);
		assertThat(json).hasPath("$.plan_updateable").isEqualTo(true);

		assertThat(json).hasPath("$.instances_retrievable").isEqualTo(true);
		assertThat(json).hasPath("$.bindings_retrievable").isEqualTo(true);
		assertThat(json).hasListAtPath("$.tags[*]").contains("tag1", "tag2");
		assertThat(json).hasListAtPath("$.requires[*]").contains(
								SERVICE_REQUIRES_ROUTE_FORWARDING.toString(),
								SERVICE_REQUIRES_SYSLOG_DRAIN.toString(),
								SERVICE_REQUIRES_VOLUME_MOUNT.toString(),
								"another_requires");
		assertThat(json).hasMapAtPath("$.metadata").contains(
								entry("field1", "value1"),
								entry("field2", "value2"),
								entry("field3", "value3"),
								entry("field4", "value4"));
		assertThat(json).hasPath("$.dashboard_client");
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(ServiceDefinition.class)
				.verify();
	}

}
