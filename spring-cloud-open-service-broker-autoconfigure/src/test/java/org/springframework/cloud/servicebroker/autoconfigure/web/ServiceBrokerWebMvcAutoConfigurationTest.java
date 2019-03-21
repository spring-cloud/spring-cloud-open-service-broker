/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web;

import org.junit.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceController;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceBrokerWebMvcAutoConfigurationTest {
	@Test
	public void controllersAreNotCreatedWithNonWebConfiguration() {
		nonWebApplicationContextRunner()
				.withUserConfiguration(FullServicesConfiguration.class)
				.run((context) -> {
					assertThat(context).doesNotHaveBean(CatalogController.class);
					assertThat(context).doesNotHaveBean(ServiceInstanceController.class);
					assertThat(context).doesNotHaveBean(ServiceInstanceBindingController.class);
				});
	}

	@Test
	public void controllersAreNotCreatedWithoutRequiredServices() {
		webApplicationContextRunner()
				.run((context) -> {
					assertThat(context).doesNotHaveBean(CatalogController.class);
					assertThat(context).doesNotHaveBean(ServiceInstanceController.class);
					assertThat(context).doesNotHaveBean(ServiceInstanceBindingController.class);
				});
	}

	@Test
	public void controllersAreCreated() {
		webApplicationContextRunner()
				.withUserConfiguration(FullServicesConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(CatalogController.class);
					assertThat(context).hasSingleBean(ServiceInstanceController.class);
					assertThat(context).hasSingleBean(ServiceInstanceBindingController.class);
				});
	}

	private WebApplicationContextRunner webApplicationContextRunner() {
		return new WebApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(ServiceBrokerWebMvcAutoConfiguration.class));
	}

	private ApplicationContextRunner nonWebApplicationContextRunner() {
		return new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(ServiceBrokerWebMvcAutoConfiguration.class));
	}

	@Configuration
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
}