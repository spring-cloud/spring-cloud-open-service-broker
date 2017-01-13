package org.springframework.cloud.servicebroker.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlanTest {

	@Test
	public void servicePlanIsFreeByDefault() throws Exception {
		final Plan plan = new Plan();
		assertEquals(Boolean.TRUE, plan.isFree());
	}
}