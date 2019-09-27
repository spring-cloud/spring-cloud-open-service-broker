/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web.fixture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.servicebroker.model.catalog.MaintenanceInfo;
import org.springframework.cloud.servicebroker.model.catalog.MethodSchema;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.Schemas;
import org.springframework.cloud.servicebroker.model.catalog.ServiceBindingSchema;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinitionRequires;
import org.springframework.cloud.servicebroker.model.catalog.ServiceInstanceSchema;

public final class ServiceFixture {

	private ServiceFixture() {
	}

	public static ServiceDefinition getSimpleService() {
		return ServiceDefinition.builder()
				.id("service-one-id")
				.name("Service One")
				.description("Description for Service One")
				.bindable(true)
				.plans(getPlanOne(), getPlanTwo(), getPlanThree())
				.requires(ServiceDefinitionRequires.SERVICE_REQUIRES_SYSLOG_DRAIN.toString(),
						ServiceDefinitionRequires.SERVICE_REQUIRES_ROUTE_FORWARDING.toString())
				.metadata(getMetadata())
				.build();
	}

	private static Map<String, Object> getMetadata() {
		List<String> features = new ArrayList<>();
		features.add("hosting");
		features.add("scaling");

		Map<String, Object> metadata = new HashMap<>();
		metadata.put("features", features);
		return metadata;
	}

	private static Plan getPlanOne() {
		return Plan.builder()
				.id("plan-one-id")
				.name("Plan One")
				.description("Description for Plan One")
				.maintenanceInfo(MaintenanceInfo.builder()
						.version("1.0.0-alpha+001")
						.description("Description for maintenance info")
						.build())
				.build();
	}

	private static Plan getPlanTwo() {
		Schemas schemas = Schemas.builder()
				.serviceInstanceSchema(ServiceInstanceSchema.builder()
						.createMethodSchema(MethodSchema.builder()
								.parameters("$schema", "https://json-schema.org/draft-04/schema#")
								.parameters("type", "object")
								.build())
						.updateMethodSchema(MethodSchema.builder()
								.parameters("$schema", "https://json-schema.org/draft-04/schema#")
								.parameters("type", "object")
								.build())
						.build())
				.serviceBindingSchema(ServiceBindingSchema.builder()
						.createMethodSchema(MethodSchema.builder()
								.parameters("$schema", "https://json-schema.org/draft-04/schema#")
								.parameters("type", "object")
								.build())
						.build())
				.build();

		return Plan.builder()
				.id("plan-two-id")
				.name("Plan Two")
				.description("Description for Plan Two")
				.metadata("key1", "value1")
				.metadata("key2", "value2")
				.bindable(false)
				.free(true)
				.planUpdateable(true)
				.schemas(schemas)
				.maximumPollingDuration(180)
				.build();
	}

	private static Plan getPlanThree() {
		return Plan.builder()
				.id("plan-three-id")
				.name("Plan Three")
				.description("Description for Plan Three")
				.build();
	}
}
