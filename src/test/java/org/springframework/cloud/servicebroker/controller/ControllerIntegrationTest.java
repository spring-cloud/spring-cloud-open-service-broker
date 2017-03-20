package org.springframework.cloud.servicebroker.controller;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.springframework.cloud.servicebroker.model.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.service.CatalogService;

public abstract class ControllerIntegrationTest {
	protected static final String API_INFO_LOCATION = "https://api.cf.example.com";
	protected static final String CF_INSTANCE_ID = "cf-abc";

	@Mock
	protected CatalogService catalogService;

	protected void setupCatalogService(String serviceDefinitionId) {
		when(catalogService.getServiceDefinition(eq(serviceDefinitionId)))
				.thenReturn(ServiceFixture.getSimpleService());
	}
}
