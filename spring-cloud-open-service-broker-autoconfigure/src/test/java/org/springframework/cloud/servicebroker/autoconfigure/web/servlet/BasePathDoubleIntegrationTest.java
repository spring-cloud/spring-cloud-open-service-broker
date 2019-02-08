package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import org.junit.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "spring.cloud.openservicebroker.base-path=/api/broker")
public class BasePathDoubleIntegrationTest extends AbstractBasePathWebApplicationIntegrationTest {
	
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
		assertFound("/api/broker", "null");
	}

	@Test
	public void withTriplePrefix() {
		assertFound("/api/broker/123", "123");
	}

	@Test
	public void withQuadruplePrefix() {
		assertNotFound("/api/broker/123/456");
	}
}
