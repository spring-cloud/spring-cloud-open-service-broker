/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web.reactive;

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

/**
 * {@link WebFilter} that inspects the request for the presence of the {@literal X-Broker-API-Request-Identity} header
 * and sets the corresponding value in the same response header
 *
 * @author Roy Clarkson
 */
public class RequestIdentityWebFilter implements WebFilter {

	/**
	 * Sets the {@literal X-Broker-API-Request-Identity} header in the response if a value is received in the request
	 * from the platform
	 *
	 * @param exchange {@inheritDoc}
	 * @param chain {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String requestIdentity = exchange.getRequest().getHeaders()
				.getFirst(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER);
		if (StringUtils.hasLength(requestIdentity)) {
			exchange.getResponse().getHeaders().add(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, requestIdentity);
		}
		return chain.filter(exchange);
	}

}
