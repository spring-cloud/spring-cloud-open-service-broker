package com.example.servicebroker;

import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.stereotype.Service;

@Service
public class ExampleCatalogService implements CatalogService {

	@Override
	public Catalog getCatalog() {
		return Catalog.builder()
		  .serviceDefinitions(getServiceDefinition("example-service"))
		  .build();
	}

	@Override
	public ServiceDefinition getServiceDefinition(String serviceId) {
		return ServiceDefinition.builder()
			.id(serviceId)
			.name("example")
			.description("A simple example")
			.bindable(true)
			.tags("example", "tags")
			.plans(getPlan())
			.build();
	}

	private Plan getPlan() {
		return Plan.builder()
		   .id("simple-plan")
		   .name("standard")
		   .description("A simple plan")
		   .free(true)
		   .build();
	}
}
