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

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.controller.ServiceBrokerExceptionHandler;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceController;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for the service broker REST API endpoints.
 *
 * @author Benjamin Ihrig
 * @author Roy Clarkson
 */
@Configuration
@ConditionalOnBean({CatalogService.class, ServiceInstanceService.class, ServiceInstanceBindingService.class})
@AutoConfigureAfter({WebMvcAutoConfiguration.class, ServiceBrokerAutoConfiguration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServiceBrokerWebMvcAutoConfiguration {

	private final CatalogService catalogService;

	private final ServiceInstanceService serviceInstanceService;

	private final ServiceInstanceBindingService serviceInstanceBindingService;

	protected ServiceBrokerWebMvcAutoConfiguration(
			CatalogService catalogService, ServiceInstanceService serviceInstanceService,
			ServiceInstanceBindingService serviceInstanceBindingService) {
		this.catalogService = catalogService;
		this.serviceInstanceService = serviceInstanceService;
		this.serviceInstanceBindingService = serviceInstanceBindingService;
	}

	@Bean
	public CatalogController catalogController() {
		return new CatalogController(this.catalogService);
	}

	@Bean
	public ServiceInstanceController serviceInstanceController() {
		return new ServiceInstanceController(this.catalogService, this.serviceInstanceService);
	}

	@Bean
	public ServiceInstanceBindingController serviceInstanceBindingController() {
		return new ServiceInstanceBindingController(this.catalogService, this.serviceInstanceBindingService);
	}

	@Bean
	public ServiceBrokerExceptionHandler serviceBrokerExceptionHandler() {
		return new ServiceBrokerExceptionHandler();
	}

}
