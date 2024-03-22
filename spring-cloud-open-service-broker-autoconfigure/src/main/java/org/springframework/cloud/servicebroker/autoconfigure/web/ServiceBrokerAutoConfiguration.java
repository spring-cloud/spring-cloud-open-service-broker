/*
 * Copyright 2002-2024 the original author or authors.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.servicebroker.autoconfigure.web.exception.CatalogDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.service.BeanCatalogService;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.NonBindableServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for the service broker
 * implementation beans.
 * <p>
 * Provides a default {@link CatalogService} bean if a {@link Catalog} bean is provided. A
 * catalog may be defined in external configuration, or via a Spring bean.
 * <p>
 * Provides a {@link NonBindableServiceInstanceBindingService} if a
 * {@link ServiceInstanceBindingService} is not provided, indicating that the service
 * broker provides no bindable services.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see ServiceBrokerProperties
 */
@Configuration
public class ServiceBrokerAutoConfiguration {

	/**
	 * Conditionally provides a {@link CatalogService} bean.
	 * @param catalog the catalog
	 * @return the bean
	 */
	@Bean
	@ConditionalOnMissingBean(CatalogService.class)
	public CatalogService beanCatalogService(@Autowired(required = false) Catalog catalog) {
		if (catalog == null) {
			throw new CatalogDefinitionDoesNotExistException();
		}
		return new BeanCatalogService(catalog);
	}

	/**
	 * Conditionally provides a {@link ServiceInstanceBindingService} bean.
	 * @return the bean
	 */
	@Bean
	@ConditionalOnMissingBean(ServiceInstanceBindingService.class)
	public ServiceInstanceBindingService nonBindableServiceInstanceBindingService() {
		return new NonBindableServiceInstanceBindingService();
	}

	/**
	 * Provides a {@link Catalog} bean when catalog properties are available in external.
	 * configuration
	 */
	@Configuration
	@ConditionalOnMissingBean({ Catalog.class, CatalogService.class })
	@EnableConfigurationProperties(ServiceBrokerProperties.class)
	@ConditionalOnProperty(prefix = "spring.cloud.openservicebroker.catalog.services[0]", name = "id")
	protected static class CatalogPropertiesMinimalConfiguration {

		private final ServiceBrokerProperties serviceBrokerProperties;

		/**
		 * Construct a new {@link CatalogPropertiesMinimalConfiguration}.
		 * @param serviceBrokerProperties the service broker properties
		 */
		public CatalogPropertiesMinimalConfiguration(ServiceBrokerProperties serviceBrokerProperties) {
			this.serviceBrokerProperties = serviceBrokerProperties;
		}

		/**
		 * Privide a {@link Catalog} bean.
		 * @return the bean
		 */
		@Bean
		public Catalog catalog() {
			return this.serviceBrokerProperties.getCatalog().toModel();
		}

	}

}
