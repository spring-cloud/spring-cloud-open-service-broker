/*
 * Copyright 2002-2022 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web.reactive;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.cloud.servicebroker.autoconfigure.web.AbstractServiceBrokerWebAutoConfigurationTest;
import org.springframework.cloud.servicebroker.autoconfigure.web.exception.ServiceInstanceServiceBeanDoesNotExistException;
import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.controller.ServiceBrokerWebFluxExceptionHandler;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceController;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceBrokerWebFluxAutoConfigurationTest extends AbstractServiceBrokerWebAutoConfigurationTest {

	@Test
	void controllersAreNotCreatedWithoutRequiredServices() {
		webApplicationContextRunner()
				.run(context -> assertThat(context.getStartupFailure())
						.isExactlyInstanceOf(UnsatisfiedDependencyException.class));
	}

	@Test
	void controllersAreCreated() {
		webApplicationContextRunner()
				.withUserConfiguration(FullServicesConfiguration.class)
				.run(context -> assertThat(context).hasSingleBean(CatalogController.class)
						.hasSingleBean(ServiceInstanceController.class)
						.hasSingleBean(ServiceInstanceBindingController.class)
						.hasSingleBean(ServiceBrokerWebFluxExceptionHandler.class));
	}

	@Test
	void controllersAreNotCreatedWithMissingInstanceService() {
		webApplicationContextRunner()
				.withUserConfiguration(MissingServiceInstanceServiceConfiguration.class)
				.run(context -> {
					Throwable t = context.getStartupFailure();
					assertThat(t).isExactlyInstanceOf(BeanCreationException.class)
							.hasRootCauseExactlyInstanceOf(ServiceInstanceServiceBeanDoesNotExistException.class);
					assertFailureAnalysis(t);
				});
	}

	private ReactiveWebApplicationContextRunner webApplicationContextRunner() {
		return new ReactiveWebApplicationContextRunner().withConfiguration(autoConfigurations());
	}

}
