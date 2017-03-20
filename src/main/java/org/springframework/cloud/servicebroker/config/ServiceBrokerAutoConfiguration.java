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

package org.springframework.cloud.servicebroker.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.service.BeanCatalogService;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.NonBindableServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "org.springframework.cloud.servicebroker" })
@ConditionalOnWebApplication
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class ServiceBrokerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(BrokerApiVersion.class)
	public BrokerApiVersion brokerApiVersion() {
		return new BrokerApiVersion();
	}

	@Bean
	@ConditionalOnMissingBean(CatalogService.class)
	public CatalogService beanCatalogService(Catalog catalog) {
		return new BeanCatalogService(catalog);
	}

	@Bean
	@ConditionalOnMissingBean(ServiceInstanceBindingService.class)
	public ServiceInstanceBindingService nonBindableServiceInstanceBindingService() {
		return new NonBindableServiceInstanceBindingService();
	}
}