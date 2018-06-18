package com.example.servicebroker;

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.stereotype.Service;

@Service
public class ExampleCatalogService implements CatalogService {

	@Override
	public Mono<Catalog> getCatalog() {
		return getServiceDefinition("example-service")
				.map(serviceDefinition -> Catalog.builder()
						.serviceDefinitions(serviceDefinition)
						.build());
	}

	@Override
	public Mono<ServiceDefinition> getServiceDefinition(String serviceId) {
		ServiceDefinition serviceDefinition = ServiceDefinition.builder()
			.id(serviceId)
			.name("example")
			.description("A simple example")
			.bindable(true)
			.tags("example", "tags")
			.plans(getPlan())
			.build();

		return Mono.just(serviceDefinition);
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
