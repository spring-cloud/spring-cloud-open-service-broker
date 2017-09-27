package org.springframework.cloud.servicebroker.model;

import org.junit.Test;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CreateServiceInstanceBindingRequestTest {
	@Test
	public void requestWithAppGuidBindingIsRead() {
		CreateServiceInstanceBindingRequest request =
				DataFixture.readTestDataFile("bindRequestWithAppGuid.json", CreateServiceInstanceBindingRequest.class);

		assertEquals("test-service-id", request.getServiceDefinitionId());
		assertEquals("test-plan-id", request.getPlanId());
		assertEquals("test-app-guid", request.getAppGuid());
		assertEquals("test-app-guid", request.getBindResource().getAppGuid());
		assertNull(request.getBindResource().getRoute());
		assertEquals("data", request.getBindResource().getField("field1"));
		assertEquals(2, request.getBindResource().getField("field2"));
		assertEquals(1, request.getParameters().get("parameter1"));
		assertEquals("foo", request.getParameters().get("parameter2"));
		assertEquals(true, request.getParameters().get("parameter3"));
	}

	@Test
	public void requestWithRouteBindingIsRead() {
		CreateServiceInstanceBindingRequest request =
				DataFixture.readTestDataFile("bindRequestWithRoute.json", CreateServiceInstanceBindingRequest.class);

		assertEquals("test-service-id", request.getServiceDefinitionId());
		assertEquals("test-plan-id", request.getPlanId());
		assertNull(request.getAppGuid());
		assertNull(request.getBindResource().getAppGuid());
		assertEquals("http://test.example.com", request.getBindResource().getRoute());
		assertEquals("data", request.getBindResource().getField("field1"));
		assertEquals(2, request.getBindResource().getField("field2"));
		assertEquals(1, request.getParameters().get("parameter1"));
		assertEquals("foo", request.getParameters().get("parameter2"));
		assertEquals(true, request.getParameters().get("parameter3"));
	}
}