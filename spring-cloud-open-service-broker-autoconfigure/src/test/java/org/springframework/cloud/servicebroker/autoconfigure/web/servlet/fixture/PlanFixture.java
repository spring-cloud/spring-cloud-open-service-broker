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
