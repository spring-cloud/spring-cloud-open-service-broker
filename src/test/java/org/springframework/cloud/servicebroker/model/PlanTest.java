package org.springframework.cloud.servicebroker.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PlanTest {

	@Test
	public void servicePlanIsFreeByDefault() throws Exception {
		final Plan plan = new Plan();
		assertEquals(Boolean.TRUE, plan.isFree());
	}
}