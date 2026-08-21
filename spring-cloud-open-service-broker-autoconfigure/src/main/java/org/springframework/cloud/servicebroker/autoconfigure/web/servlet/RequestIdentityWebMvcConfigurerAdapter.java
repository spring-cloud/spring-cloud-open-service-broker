/*
 * Copyright 2002-2026 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * {@link WebMvcConfigurer} that configures echoing of the service broker API request
 * identity header.
 *
 * @author Roy Clarkson
 */
public class RequestIdentityWebMvcConfigurerAdapter implements WebMvcConfigurer {

	private final RequestIdentityInterceptor requestIdentityInterceptor;

	/**
	 * Auto-wire the expected beans.
	 * @param requestIdentityInterceptor the RequestIdentityInterceptor bean
	 */
	protected RequestIdentityWebMvcConfigurerAdapter(RequestIdentityInterceptor requestIdentityInterceptor) {
		this.requestIdentityInterceptor = requestIdentityInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(this.requestIdentityInterceptor);
	}

}
