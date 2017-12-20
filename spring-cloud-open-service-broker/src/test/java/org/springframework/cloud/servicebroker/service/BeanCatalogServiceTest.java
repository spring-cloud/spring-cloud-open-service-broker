package org.springframework.cloud.servicebroker.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.junit.Before;
import org.junit.Test;

public class BeanCatalogServiceTest {

	private BeanCatalogService service;

	private Catalog catalog;
	private ServiceDefinition serviceDefinition;
	private static final String SVC_DEF_ID = "svc-def-id";

	@Before
	public void setup() {
		serviceDefinition = ServiceDefinition.builder()
				.id(SVC_DEF_ID)
				.name("Name")
				.description("Description")
				.bindable(true)
				.build();
		catalog = Catalog.builder()
				.serviceDefinitions(serviceDefinition)
				.build();
		service = new BeanCatalogService(catalog);
	}

	@Test
	public void catalogIsReturnedSuccessfully() {
		assertEquals(catalog, service.getCatalog());
	}

	@Test
	public void serviceDefinitionIsFound() {
		assertEquals(serviceDefinition, service.getServiceDefinition(SVC_DEF_ID));
	}

	@Test
	public void serviceDefinitionIsNotFound() {
		assertNull(service.getServiceDefinition("NOT_THERE"));
	}

}
