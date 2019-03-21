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

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * {@link HandlerInterceptor} that inspects the service broker API version passed in all request headers
 * and compares it to the API version supported by the broker.
 *
 * @author Scott Frederick
 */
public class ApiVersionInterceptor extends HandlerInterceptorAdapter {

	private final BrokerApiVersion version;

	/**
	 * Construct an interceptor that disables API version validation.
	 */
	public ApiVersionInterceptor() {
		this(null);
	}

	/**
	 * Construct an interceptor that validates the API version passed in request headers to the
	 * configured version.
	 *
	 * @param version the API version supported by the broker.
	 */
	public ApiVersionInterceptor(BrokerApiVersion version) {
		this.version = version;
	}

	/**
	 * Compares the service broker API version header to the supported version.
	 *
	 * @param request {@inheritDoc}
	 * @param response {@inheritDoc}
	 * @param handler {@inheritDoc}
	 * @throws ServiceBrokerApiVersionException if the API version header value does not match the version
	 *         supported by the broker
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws ServiceBrokerApiVersionException {
		if (version != null && !anyVersionAllowed()) {
			String apiVersion = request.getHeader(version.getBrokerApiVersionHeader());
			if (!version.getApiVersion().equals(apiVersion)) {
				throw new ServiceBrokerApiVersionException(version.getApiVersion(), apiVersion);
			}
		}
		return true;
	}

	private boolean anyVersionAllowed() {
		return BrokerApiVersion.API_VERSION_ANY.equals(version.getApiVersion());
	}

}
