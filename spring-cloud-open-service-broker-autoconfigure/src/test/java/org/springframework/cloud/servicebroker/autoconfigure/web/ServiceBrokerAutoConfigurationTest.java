/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.service.BeanCatalogService;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.NonBindableServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.cloud.servicebroker.service.events.EventFlowRegistries;
import org.springframework.context.annotation.Bean;

public class ServiceBrokerAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(ServiceBrokerAutoConfiguration.class));

	@Test
	public void servicesAreCreatedWithMinimalConfiguration() {
		this.contextRunner
				.withUserConfiguration(MinimalWithCatalogConfiguration.class)
				.run((context) -> {
					assertThat(context)
							.getBean(CatalogService.class)
							.isExactlyInstanceOf(BeanCatalogService.class);

					assertThat(context)
							.getBean(ServiceInstanceBindingService.class)
							.isExactlyInstanceOf(NonBindableServiceInstanceBindingService.class);

					assertThat(context)
							.getBean(ServiceInstanceService.class)
							.isExactlyInstanceOf(TestServiceInstanceService.class);

					assertThat(context)
							.hasSingleBean(EventFlowRegistries.class);
				});
	}

	@Test
	public void servicesAreCreatedWithCatalogAndFullConfiguration() {
		this.contextRunner
				.withUserConfiguration(FullServicesWithCatalogConfiguration.class)
				.run((context) -> {
					assertThat(context)
							.getBean(CatalogService.class)
							.isExactlyInstanceOf(BeanCatalogService.class);

					assertThat(context)
							.getBean(ServiceInstanceBindingService.class)
							.isExactlyInstanceOf(TestServiceInstanceBindingService.class);

					assertThat(context)
							.getBean(ServiceInstanceService.class)
							.isExactlyInstanceOf(TestServiceInstanceService.class);

					assertThat(context)
							.hasSingleBean(EventFlowRegistries.class);
				});
	}

	@Test
	public void servicesAreCreatedWithFullConfiguration() {
		this.contextRunner
				.withUserConfiguration(FullServicesConfiguration.class)
				.run((context) -> {
					assertThat(context)
							.getBean(CatalogService.class)
							.isExactlyInstanceOf(TestCatalogService.class);

					assertThat(context)
							.getBean(ServiceInstanceBindingService.class)
							.isExactlyInstanceOf(TestServiceInstanceBindingService.class);

					assertThat(context)
							.getBean(ServiceInstanceService.class)
							.isExactlyInstanceOf(TestServiceInstanceService.class);

					assertThat(context)
							.hasSingleBean(EventFlowRegistries.class);
				});
	}

	@Test
	public void servicesAreCreatedWithCatalogAndCatalogServiceConfiguration() {
		this.contextRunner
				.withUserConfiguration(CatalogAndCatalogServiceConfiguration.class)
				.run((context) -> {
					assertThat(context)
							.getBean(CatalogService.class)
							.isExactlyInstanceOf(TestCatalogService.class);

					assertThat(context)
							.getBean(ServiceInstanceBindingService.class)
							.isExactlyInstanceOf(NonBindableServiceInstanceBindingService.class);

					assertThat(context)
							.getBean(ServiceInstanceService.class)
							.isExactlyInstanceOf(TestServiceInstanceService.class);

					assertThat(context)
							.hasSingleBean(EventFlowRegistries.class);
				});
	}

	@Test
	public void servicesAreNotCreatedWithoutInstanceService() {
		this.contextRunner
				.withUserConfiguration(MissingInstanceServiceConfiguration.class)
				.run(context -> assertThat(context.getStartupFailure())
							.isExactlyInstanceOf(UnsatisfiedDependencyException.class));
	}

	@Test
	public void servicesAreNotCreatedWhenMissingCatalogAndCatalogServiceConfiguration() {
		this.contextRunner
				.withUserConfiguration(MissingCatalogAndCatalogServiceConfiguration.class)
				.run((context) -> assertThat(context.getStartupFailure())
						.isExactlyInstanceOf(UnsatisfiedDependencyException.class));
	}

	@Test
	public void servicesAreNotCreatedWhenMissingAllConfiguration() {
		this.contextRunner
				.withUserConfiguration(MissingAllConfiguration.class)
				.run((context) -> assertThat(context.getStartupFailure())
						.isExactlyInstanceOf(UnsatisfiedDependencyException.class));
	}

	@Test
	public void servicesAreCreatedFromCatalogProperties() {
		this.contextRunner
				.withUserConfiguration(MissingCatalogAndCatalogServiceConfiguration.class)
				.withPropertyValues(
						"spring.cloud.openservicebroker.catalog.services[0].id=service-one-id",
						"spring.cloud.openservicebroker.catalog.services[0].name=Service One",
						"spring.cloud.openservicebroker.catalog.services[0].description=Description for Service One",
						"spring.cloud.openservicebroker.catalog.services[0].plans[0].id=plan-one-id",
						"spring.cloud.openservicebroker.catalog.services[0].plans[0].name=Plan One",
						"spring.cloud.openservicebroker.catalog.services[0].plans[0].description=Description for Plan One")
				.run((context) -> {
					assertThat(context).hasSingleBean(Catalog.class);
					Catalog catalog = context.getBean(Catalog.class);
					assertThat(catalog.getServiceDefinitions()).hasSize(1);
					assertThat(catalog.getServiceDefinitions().get(0).getId()).isEqualTo("service-one-id");
					assertThat(catalog.getServiceDefinitions().get(0).getName()).isEqualTo("Service One");
					assertThat(catalog.getServiceDefinitions().get(0).getDescription()).isEqualTo("Description for Service One");
					assertThat(catalog.getServiceDefinitions().get(0).getPlans()).hasSize(1);
					assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(0).getId()).isEqualTo("plan-one-id");
					assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(0).getName()).isEqualTo("Plan One");
					assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(0).getDescription()).isEqualTo("Description for Plan One");
					assertThat(context)
							.getBean(CatalogService.class)
							.isExactlyInstanceOf(BeanCatalogService.class);

					assertThat(context)
							.getBean(ServiceInstanceBindingService.class)
							.isExactlyInstanceOf(NonBindableServiceInstanceBindingService.class);

					assertThat(context)
							.getBean(ServiceInstanceService.class)
							.isExactlyInstanceOf(TestServiceInstanceService.class);

					assertThat(context)
							.hasSingleBean(EventFlowRegistries.class);
				});
	}

	@TestConfiguration
	public static class MinimalWithCatalogConfiguration {
		@Bean
		public Catalog catalog() {
			return Catalog.builder().build();
		}

		@Bean
		public ServiceInstanceService serviceInstanceService() {
			return new TestServiceInstanceService();
		}
	}

	@TestConfiguration
	public static class FullServicesWithCatalogConfiguration {
		@Bean
		public Catalog catalog() {
			return Catalog.builder().build();
		}

		@Bean
		public ServiceInstanceService serviceInstanceService() {
			return new TestServiceInstanceService();
		}

		@Bean
		public ServiceInstanceBindingService serviceInstanceBindingService() {
			return new TestServiceInstanceBindingService();
		}
	}

	@TestConfiguration
	public static class FullServicesConfiguration {
		@Bean
		public CatalogService catalogService() {
			return new TestCatalogService();
		}

		@Bean
		public ServiceInstanceService serviceInstanceService() {
			return new TestServiceInstanceService();
		}

		@Bean
		public ServiceInstanceBindingService serviceInstanceBindingService() {
			return new TestServiceInstanceBindingService();
		}
	}

	@TestConfiguration
	public static class CatalogAndCatalogServiceConfiguration {
		@Bean
		public Catalog catalog() {
			return Catalog.builder().build();
		}

		@Bean
		public CatalogService catalogService() {
			return new TestCatalogService();
		}

		@Bean
		public ServiceInstanceService serviceInstanceService() {
			return new TestServiceInstanceService();
		}
	}

	@TestConfiguration
	public static class MissingInstanceServiceConfiguration {
		public MissingInstanceServiceConfiguration(ServiceInstanceService serviceInstanceService) {
		}
		@Bean
		public Catalog catalog() {
			return Catalog.builder().build();
		}
	}

	@TestConfiguration
	public static class MissingCatalogAndCatalogServiceConfiguration {
		public MissingCatalogAndCatalogServiceConfiguration(Catalog catalog, CatalogService catalogService) {
		}
		@Bean
		public ServiceInstanceService serviceInstanceService() {
			return new TestServiceInstanceService();
		}
	}

	@TestConfiguration
	public static class MissingAllConfiguration {
		public MissingAllConfiguration(Catalog catalog, CatalogService catalogService, ServiceInstanceService serviceInstanceService) {
		}
	}

}