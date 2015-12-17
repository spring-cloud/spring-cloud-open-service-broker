package org.springframework.cloud.servicebroker.model.fixture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.servicebroker.model.Plan;

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
		return new Plan("plan-two-id", "Plan Two", "Description for Plan Two", metadata, false);
	}
	
}
