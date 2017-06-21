package org.springframework.cloud.servicebroker.model.fixture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.servicebroker.model.MethodSchema;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.Schemas;
import org.springframework.cloud.servicebroker.model.ServiceBindingSchema;
import org.springframework.cloud.servicebroker.model.ServiceInstanceSchema;

public class PlanFixture {

	public static List<Plan> getAllPlans() {
		List<Plan> plans = new ArrayList<>();
		plans.add(getPlanOne());
		plans.add(getPlanTwo());
		return plans;
	}

	public static Plan getPlanOne() {
		return new Plan("plan-one-id", "Plan One", "Description for Plan One");
	}

	public static Plan getPlanTwo() {
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("key1", "value1");
		metadata.put("key2", "value2");

		Map<String, Object> createServiceSchema = new HashMap<>();
		createServiceSchema.put("$schema", "http://example.com/service/create/schema");
		createServiceSchema.put("type", "object");

		Map<String, Object> updateServiceSchema = new HashMap<>();
		updateServiceSchema.put("$schema", "http://example.com/service/update/schema");
		updateServiceSchema.put("type", "object");

		Map<String, Object> createBindingSchema = new HashMap<>();
		createBindingSchema.put("$schema", "http://example.com/binding/create/schema");
		createBindingSchema.put("type", "object");

		ServiceInstanceSchema serviceInstanceSchema = new ServiceInstanceSchema(
				new MethodSchema(createServiceSchema),
				new MethodSchema(updateServiceSchema));
		ServiceBindingSchema serviceBindingSchema = new ServiceBindingSchema(
				new MethodSchema(createBindingSchema));
		Schemas schemas = new Schemas(serviceInstanceSchema, serviceBindingSchema);

		return new Plan("plan-two-id", "Plan Two", "Description for Plan Two", metadata,
				false, true, schemas);
	}

}
