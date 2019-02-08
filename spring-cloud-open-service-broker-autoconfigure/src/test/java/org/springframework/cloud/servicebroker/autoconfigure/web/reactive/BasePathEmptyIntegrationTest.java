package org.springframework.cloud.servicebroker.autoconfigure.web.reactive;

import org.junit.Test;


public class BasePathEmptyIntegrationTest extends AbstractBasePathWebApplicationIntegrationTest {
	
	@Test
	public void noPrefix() {
		assertFound("", "null");
	}
	
	@Test
	public void withSinglePrefix() {
		assertFound("/123", "123");
	}
	
	@Test
	public void withDoublePrefix() {
		assertNotFound("/api/broker");
	}
	@Test
	public void withTriplePrefix() {
		assertNotFound("/api/broker/123");
	}

	@Test
	public void withQuadruplePrefix() {
		assertNotFound("/api/broker/123/456");
	}
}
