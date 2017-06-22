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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.cloud.servicebroker.interceptor.ServiceBrokerApiVersionInterceptor;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.ServiceBrokerApiVersion;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This auto configuration class configures the version system of the service broker.
 *
 * @author Benjamin Ihrig
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnBean({ Catalog.class, ServiceInstanceService.class })
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class ServiceBrokerApiVersionAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(ServiceBrokerApiVersion.class)
	public ServiceBrokerApiVersion brokerApiVersion() {
		return new ServiceBrokerApiVersion();
	}

	@Bean
	public ServiceBrokerApiVersionInterceptor serviceBrokerApiVersionInterceptor(
			ServiceBrokerApiVersion serviceBrokerApiVersion) {
		return new ServiceBrokerApiVersionInterceptor(serviceBrokerApiVersion);
	}

	@Bean
	public ServiceBrokerWebMvcConfigurerAdapter serviceBrokerWebMvcConfigurerAdapter(
			ServiceBrokerApiVersionInterceptor serviceBrokerApiVersionInterceptor) {
		return new ServiceBrokerWebMvcConfigurerAdapter(
				serviceBrokerApiVersionInterceptor);
	}
}