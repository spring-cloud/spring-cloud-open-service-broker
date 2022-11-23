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

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionErrorMessage;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.cloud.servicebroker.model.error.ErrorMessage;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * {@link WebFilter} that configures checking for an appropriate service broker API version.
 *
 * @author Roy Clarkson
 */
public class ApiVersionWebFilter implements WebFilter {

	private static final String V2_API_PATH_PATTERN = "/v2/**";

	private final BrokerApiVersion version;

	/**
	 * Construct a filter that disables API version validation.
	 */
	public ApiVersionWebFilter() {
		this(null);
	}

	/**
	 * Construct a filter that validates the API version passed in request headers to the configured version.
	 *
	 * @param version the API version supported by the broker.
	 */
	public ApiVersionWebFilter(BrokerApiVersion version) {
		this.version = version;
	}

	/**
	 * Process the web request and validate the API version in the header. If the API version does not match, then set
	 * an HTTP 412 status and write the error message to the response.
	 *
	 * @param exchange {@inheritDoc}
	 * @param chain {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		PathPattern pathPattern = new PathPatternParser().parse(V2_API_PATH_PATTERN);
		if (pathPattern.matches(exchange.getRequest().getPath()) && version != null && !anyVersionAllowed()) {
			String requestedApiVersion = exchange.getRequest().getHeaders()
					.getFirst(version.getBrokerApiVersionHeader());
			ServerHttpResponse response = exchange.getResponse();
			if (requestedApiVersion == null) {
				response.setStatusCode(HttpStatus.BAD_REQUEST);
				return writeResponse(response, requestedApiVersion);
			}
			else if (!version.getApiVersion().equals(requestedApiVersion)) {
				response.setStatusCode(HttpStatus.PRECONDITION_FAILED);
				return writeResponse(response, requestedApiVersion);
			}
		}
		return chain.filter(exchange);
	}

	private boolean anyVersionAllowed() {
		return BrokerApiVersion.API_VERSION_ANY.equals(version.getApiVersion());
	}

	private Mono<Void> writeResponse(ServerHttpResponse response, String requestedApiVersion) {
		String message = ServiceBrokerApiVersionErrorMessage.from(version.getApiVersion(), requestedApiVersion)
				.toString();
		return response.writeWith(Flux.just(response.bufferFactory().allocateBuffer(DefaultDataBufferFactory.DEFAULT_INITIAL_CAPACITY)
				.write(toJson(ErrorMessage.builder().message(message).build()), StandardCharsets.UTF_8)));
	}

	private String toJson(ErrorMessage message) {
		String json;
		try {
			json = new ObjectMapper().writeValueAsString(message);
		}
		catch (JsonProcessingException e) {
			json = "{}";
		}
		return json;
	}

}
