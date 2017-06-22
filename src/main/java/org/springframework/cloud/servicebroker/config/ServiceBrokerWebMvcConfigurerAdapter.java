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

import org.springframework.cloud.servicebroker.interceptor.ServiceBrokerApiVersionInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

public class ServiceBrokerWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {
	private static final String V2_API_PATH_PATTERN = "/v2/**";

	private final ServiceBrokerApiVersionInterceptor serviceBrokerApiVersionInterceptor;

	public ServiceBrokerWebMvcConfigurerAdapter(
			ServiceBrokerApiVersionInterceptor serviceBrokerApiVersionInterceptor) {
		this.serviceBrokerApiVersionInterceptor = serviceBrokerApiVersionInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(serviceBrokerApiVersionInterceptor)
				.addPathPatterns(V2_API_PATH_PATTERN);
	}
}