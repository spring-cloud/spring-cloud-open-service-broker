/*
 * Copyright 2002-2019 the original author or authors.
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
import org.springframework.cloud.servicebroker.service.events.EventFlowRegistries;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractServiceBrokerWebAutoConfigurationTest {

	private static final String ANALYZER_DESCRIPTION = String.format("Service brokers must implement the '%s' and " +
			"provide implementations of the required methods of that interface.", ServiceInstanceService.class);

	@Test
	public void controllersAreNotCreatedWithNonWebConfiguration() {
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

		@SuppressWarnings("deprecation")
		@Bean
		public EventFlowRegistries eventFlowRegistries() {
			return new EventFlowRegistries();
		}

	}

	@TestConfiguration
	public static class MissingServiceInstanceServiceConfiguration {

		@Bean
		public CatalogService catalogService() {
			return new TestCatalogService();
		}

		@Bean
		public ServiceInstanceBindingService serviceInstanceBindingService() {
			return new TestServiceInstanceBindingService();
		}

		@SuppressWarnings("deprecation")
		@Bean
		public EventFlowRegistries eventFlowRegistries() {
			return new EventFlowRegistries();
		}

	}

}
