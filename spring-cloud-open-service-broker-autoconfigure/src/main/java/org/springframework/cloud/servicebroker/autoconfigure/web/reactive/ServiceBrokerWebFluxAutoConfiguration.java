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

package org.springframework.cloud.servicebroker.autoconfigure.web.reactive;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.cloud.servicebroker.autoconfigure.web.ServiceBrokerAutoConfiguration;
import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.controller.ServiceBrokerExceptionHandler;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceController;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingEventService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceEventService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.cloud.servicebroker.service.events.EventFlowRegistries;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for the service broker REST API
 * endpoints.
 *
 * @author Roy Clarkson
 */
@Configuration
@AutoConfigureAfter({ WebFluxAutoConfiguration.class,
		ServiceBrokerAutoConfiguration.class })
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ServiceBrokerWebFluxAutoConfiguration {

	private final CatalogService catalogService;

	private final ServiceInstanceEventService serviceInstanceEventService;

	private final ServiceInstanceBindingEventService serviceInstanceBindingEventService;

	protected ServiceBrokerWebFluxAutoConfiguration(CatalogService catalogService,
			ServiceInstanceService serviceInstanceService,
			ServiceInstanceBindingService serviceInstanceBindingService,
			EventFlowRegistries eventFlowRegistries) {
		this.catalogService = catalogService;
		this.serviceInstanceEventService = new ServiceInstanceEventService(
				serviceInstanceService, eventFlowRegistries);
		this.serviceInstanceBindingEventService = new ServiceInstanceBindingEventService(
				serviceInstanceBindingService, eventFlowRegistries);
	}

	@Bean
	public CatalogController catalogController() {
		return new CatalogController(this.catalogService);
	}

	@Bean
	public ServiceInstanceController serviceInstanceController() {
		return new ServiceInstanceController(this.catalogService,
				this.serviceInstanceEventService);
	}

	@Bean
	public ServiceInstanceBindingController serviceInstanceBindingController() {
		return new ServiceInstanceBindingController(this.catalogService,
				this.serviceInstanceBindingEventService);
	}

	@Bean
	public ServiceBrokerExceptionHandler serviceBrokerExceptionHandler() {
		return new ServiceBrokerExceptionHandler();
	}

}
