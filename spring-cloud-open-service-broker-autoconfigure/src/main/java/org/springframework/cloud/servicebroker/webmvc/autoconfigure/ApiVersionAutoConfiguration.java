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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.cloud.servicebroker.webmvc.interceptor.ApiVersionInterceptor;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for the service broker API validation.
 * Configures support for any service broker API version if a version is not specifically
 * configured.
 *
 * @author Benjamin Ihrig
 * @author Scott Frederick
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnBean({ Catalog.class, ServiceInstanceService.class })
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class ApiVersionAutoConfiguration {
	@Bean
	@ConditionalOnMissingBean(BrokerApiVersion.class)
	public BrokerApiVersion serviceBrokerApiVersion() {
		return new BrokerApiVersion();
	}

	@Bean
	public ApiVersionInterceptor serviceBrokerApiVersionInterceptor(BrokerApiVersion brokerApiVersion) {
		return new ApiVersionInterceptor(brokerApiVersion);
	}

	@Bean
	public ApiVersionWebMvcConfigurerAdapter serviceBrokerWebMvcConfigurerAdapter(
			ApiVersionInterceptor apiVersionInterceptor) {
		return new ApiVersionWebMvcConfigurerAdapter(
				apiVersionInterceptor);
	}
}
