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

package org.springframework.cloud.servicebroker.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class BrokerApiVersionInterceptor extends HandlerInterceptorAdapter {

	private final BrokerApiVersion version;

	public BrokerApiVersionInterceptor() {
		this(null);
	}

	public BrokerApiVersionInterceptor(BrokerApiVersion version) {
		this.version = version;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws ServiceBrokerApiVersionException {
		if (version != null && !anyVersionAllowed()) {
			String apiVersion = request.getHeader(version.getBrokerApiVersionHeader());
			if (!version.getApiVersion().equals(apiVersion)) {
				throw new ServiceBrokerApiVersionException(version.getApiVersion(),
						apiVersion);
			}
		}
		return true;
	}

	private boolean anyVersionAllowed() {
		return BrokerApiVersion.API_VERSION_ANY.equals(version.getApiVersion());
	}

}
