package com.example.servicebroker;

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
		return Mono.just(ServiceDefinition.builder()
				.id(serviceId)
				.name("example")
				.description("A simple example")
				.bindable(true)
				.tags("example", "tags")
				.plans(getPlan())
				.build());
	}

	@Override
	public Mono<ResponseEntity<Catalog>> getResponseEntityCatalog(HttpHeaders httpHeaders) {
		// Use this method to handle catalog caching and ETag support.
		// The example below is a basic ETag comparison and response.
		if ("useful-etag-value".equals(httpHeaders.getIfNoneMatch())) {
			return Mono.just(ResponseEntity
					.status(304)
					.eTag("useful-etag-value")
					.build());
		}
		else {
			return getCatalog()
					.map(catalog -> ResponseEntity
							.status(200)
							.eTag("different-etag-value")
							.body(catalog));
		}
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
