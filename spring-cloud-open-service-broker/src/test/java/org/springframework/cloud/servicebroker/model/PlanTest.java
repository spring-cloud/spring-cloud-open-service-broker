package org.springframework.cloud.servicebroker.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlanTest {

	@Test
	public void servicePlanIsFreeByDefault() {
		final Plan plan = Plan.builder().build();
		assertEquals(true, plan.isFree());
	}
}