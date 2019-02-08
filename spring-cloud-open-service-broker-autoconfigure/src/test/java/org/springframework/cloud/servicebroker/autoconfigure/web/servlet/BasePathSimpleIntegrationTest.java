package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import org.junit.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "spring.cloud.openservicebroker.base-path=/broker")
public class BasePathSimpleIntegrationTest extends AbstractBasePathWebApplicationIntegrationTest {
	
	@Test
	public void noPrefix() {
		assertNotFound("");
	}
	
	@Test
	public void withSinglePrefix() {
		assertNotFound("/123");
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
