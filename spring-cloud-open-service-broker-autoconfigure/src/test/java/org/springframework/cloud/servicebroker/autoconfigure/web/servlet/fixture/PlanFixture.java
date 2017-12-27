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

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture;

import org.springframework.cloud.servicebroker.model.MethodSchema;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.Schemas;
import org.springframework.cloud.servicebroker.model.ServiceBindingSchema;
import org.springframework.cloud.servicebroker.model.ServiceInstanceSchema;

public class PlanFixture {

	public static Plan[] getAllPlans() {
		return new Plan[] {
				getPlanOne(), getPlanTwo()
		};
	}

	public static Plan getPlanOne() {
		return Plan.builder()
				.id("plan-one-id")
				.name("Plan One")
				.description("Description for Plan One")
				.build();
	}

	public static Plan getPlanTwo() {
		Schemas schemas = Schemas.builder()
				.serviceInstanceSchema(ServiceInstanceSchema.builder()
						.createMethodSchema(MethodSchema.builder()
								.configParametersSchema("$schema", "http://example.com/service/create/schema")
								.configParametersSchema("type", "object").build())
						.updateMethodSchema(MethodSchema.builder()
								.configParametersSchema("$schema", "http://example.com/service/update/schema")
								.configParametersSchema("type", "object").build())
						.build())
				.serviceBindingSchema(ServiceBindingSchema.builder()
						.createMethodSchema(MethodSchema.builder()
								.configParametersSchema("$schema", "http://example.com/binding/create/schema")
								.configParametersSchema("type", "object").build())
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
				.schemas(schemas)
				.build();
	}

}
