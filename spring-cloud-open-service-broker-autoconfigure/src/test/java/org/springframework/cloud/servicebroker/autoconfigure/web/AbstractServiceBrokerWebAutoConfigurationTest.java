/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web;


import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.servicebroker.autoconfigure.web.reactive.ServiceBrokerWebFluxAutoConfiguration;
import org.springframework.cloud.servicebroker.autoconfigure.web.servlet.ServiceBrokerWebMvcAutoConfiguration;
import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceController;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.cloud.servicebroker.service.events.AsyncOperationServiceInstanceBindingEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.AsyncOperationServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.CreateServiceInstanceBindingEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.CreateServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.DeleteServiceInstanceBindingEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.DeleteServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.EventFlowRegistries;
import org.springframework.cloud.servicebroker.service.events.UpdateServiceInstanceEventFlowRegistry;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractServiceBrokerWebAutoConfigurationTest {

	private static final String ANALYZER_DESCRIPTION = String.format("Service brokers must implement the '%s' and " +
			"provide implementations of the required methods of that interface.", ServiceInstanceService.class);

	private static EventFlowRegistries eventFlowRegistries;

	protected static EventFlowRegistries getEventFlowRegistries() {
		return eventFlowRegistries;
	}

	@BeforeEach
	void setUp() {
		eventFlowRegistries = new EventFlowRegistries(
				new CreateServiceInstanceEventFlowRegistry(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),
				new UpdateServiceInstanceEventFlowRegistry(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),
				new DeleteServiceInstanceEventFlowRegistry(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),
				new AsyncOperationServiceInstanceEventFlowRegistry(
						new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),
				new CreateServiceInstanceBindingEventFlowRegistry(
						new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),
				new DeleteServiceInstanceBindingEventFlowRegistry(
						new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),
				new AsyncOperationServiceInstanceBindingEventFlowRegistry(
						new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
	}

	@Test
	void controllersAreNotCreatedWithNonWebConfiguration() {
		nonWebApplicationContextRunner()
				.withUserConfiguration(FullServicesConfiguration.class)
				.run(context -> assertThat(context).doesNotHaveBean(CatalogController.class)
						.doesNotHaveBean(ServiceInstanceController.class)
						.doesNotHaveBean(ServiceInstanceBindingController.class));
	}

	protected void assertFailureAnalysis(Throwable t) {
		FailureAnalyzer analyzer = new RequiredServiceInstanceServiceBeanFailureAnalyzer();
		FailureAnalysis analysis = analyzer.analyze(t);
		assertThat(analysis).isNotNull();
		assertThat(analysis.getDescription()).isEqualTo(ANALYZER_DESCRIPTION);
	}

	protected AutoConfigurations autoConfigurations() {
		return AutoConfigurations.of(ServiceBrokerWebFluxAutoConfiguration.class,
				ServiceBrokerWebMvcAutoConfiguration.class);
	}

	protected ApplicationContextRunner nonWebApplicationContextRunner() {
		return new ApplicationContextRunner().withConfiguration(autoConfigurations());
	}

	@TestConfiguration
	protected static class FullServicesConfiguration {

		@Bean
		protected CatalogService catalogService() {
			return new TestCatalogService();
		}

		@Bean
		protected ServiceInstanceService serviceInstanceService() {
			return new TestServiceInstanceService();
		}

		@Bean
		protected ServiceInstanceBindingService serviceInstanceBindingService() {
			return new TestServiceInstanceBindingService();
		}

		@Bean
		protected EventFlowRegistries eventFlowRegistries() {
			return getEventFlowRegistries();
		}

	}

	@TestConfiguration
	protected static class MissingServiceInstanceServiceConfiguration {

		@Bean
		protected CatalogService catalogService() {
			return new TestCatalogService();
		}

		@Bean
		protected ServiceInstanceBindingService serviceInstanceBindingService() {
			return new TestServiceInstanceBindingService();
		}

		@Bean
		protected EventFlowRegistries eventFlowRegistries() {
			return getEventFlowRegistries();
		}

	}

}
