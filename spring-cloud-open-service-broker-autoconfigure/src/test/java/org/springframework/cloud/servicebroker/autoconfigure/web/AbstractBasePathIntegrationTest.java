package org.springframework.cloud.servicebroker.autoconfigure.web;

import static java.lang.String.format;

import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.servicebroker.JsonUtils;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import reactor.core.publisher.Mono;
import wiremock.com.google.common.base.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(
		webEnvironment = WebEnvironment.MOCK,
		classes = AbstractBasePathIntegrationTest.ServiceBrokerApplication.class
)
public abstract class AbstractBasePathIntegrationTest {
	
	protected abstract void assertFound(String baseUri, String expectedPlatform);
	protected abstract void assertNotFound(String baseUri);

	protected final String serviceCreationPayload() {
		return JsonUtils.toJson(CreateServiceInstanceRequest.builder()
				                                            .serviceDefinitionId("default-service")
				                                            .planId("default-plan")
				                                            .build()
	    );
	}



	@SpringBootApplication
	@Configuration
	protected static class ServiceBrokerApplication {
		
		@Bean
		public CatalogService catalogService() {
			return new CatalogService() {
				
				@Override
				public Mono<ServiceDefinition> getServiceDefinition(String serviceId) {
					return this.getCatalog()
							   .flatMapIterable(Catalog::getServiceDefinitions)
							   .filter(service -> Objects.equal(serviceId, service.getId()))
							   .next();
				}
				
				@Override
				public Mono<Catalog> getCatalog() {
					return Mono.just(
							Catalog.builder()
								   .serviceDefinitions(
										   ServiceDefinition.builder()
										                    .id("default-service")
										                    .plans(
										                    		Plan.builder()
										                    		    .id("default-plan")
										                    		    .build()
										                    )
										                    .build()
								   )
							       .build()
					);
				}
			};
		}
		
		@Bean
		public ServiceInstanceService serviceInstanceService() {
			return new ServiceInstanceService() {
				
				@Override
				public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(DeleteServiceInstanceRequest request) {
					return Mono.error(() -> new UnsupportedOperationException());
				}
				
				@Override
				public Mono<CreateServiceInstanceResponse> createServiceInstance(CreateServiceInstanceRequest request) {
					return Mono.just(CreateServiceInstanceResponse.builder()
					                                              .operation(format("platform: %s", request.getPlatformInstanceId()))
					                                              .build()
				    );
				}
			};
		}
		

		public static void main(String[] args) {
			SpringApplication.run(ServiceBrokerApplication.class, args);
		}

	}
}
