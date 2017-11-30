/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.webmvc.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.servicebroker.controller.CatalogController;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceController;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for the service broker REST API endpoints.
 *
 * @author Benjamin Ihrig
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnBean({ Catalog.class, ServiceInstanceService.class })
@AutoConfigureAfter(ServiceBrokerAutoConfiguration.class)
public class ServiceBrokerWebMvcAutoConfiguration {

	@Bean
	public CatalogController catalogController(CatalogService catalogService) {
		return new CatalogController(catalogService);
	}

	@Bean
	public ServiceInstanceController serviceInstanceController(
			CatalogService catalogService,
			ServiceInstanceService serviceInstanceService) {
		return new ServiceInstanceController(catalogService, serviceInstanceService);
	}

	@Bean
	public ServiceInstanceBindingController serviceInstanceBindingController(
			CatalogService catalogService,
			ServiceInstanceBindingService serviceInstanceBindingService) {
		return new ServiceInstanceBindingController(catalogService, serviceInstanceBindingService);
	}
}
