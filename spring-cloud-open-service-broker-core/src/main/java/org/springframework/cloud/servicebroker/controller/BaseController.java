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

package org.springframework.cloud.servicebroker.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidOriginatingIdentityException;
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.model.AsyncServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.AsyncServiceBrokerResponse;
import org.springframework.cloud.servicebroker.model.CloudFoundryContext;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.KubernetesContext;
import org.springframework.cloud.servicebroker.model.PlatformContext;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

/**
 * Base functionality shared by controllers.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 */
public class BaseController {

	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

	protected CatalogService catalogService;

	public BaseController(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	protected Mono<ServiceBrokerRequest> setCommonRequestFields(ServiceBrokerRequest request, String platformInstanceId,
																String apiInfoLocation, String originatingIdentityString) {
		request.setPlatformInstanceId(platformInstanceId);
		request.setApiInfoLocation(apiInfoLocation);
		request.setOriginatingIdentity(parseOriginatingIdentity(originatingIdentityString));
		return Mono.just(request);
	}

	protected Mono<AsyncServiceBrokerRequest> setCommonRequestFields(AsyncServiceBrokerRequest request, String platformInstanceId,
																	   String apiInfoLocation, String originatingIdentityString,
																	   boolean asyncAccepted) {
		request.setAsyncAccepted(asyncAccepted);
		return setCommonRequestFields(request, platformInstanceId, apiInfoLocation, originatingIdentityString)
				.cast(AsyncServiceBrokerRequest.class);
	}

	protected Mono<ServiceDefinition> getRequiredServiceDefinition(String serviceDefinitionId) {
		return getServiceDefinition(serviceDefinitionId)
				.switchIfEmpty(Mono.error(new ServiceDefinitionDoesNotExistException(serviceDefinitionId)));
	}

	protected Mono<ServiceDefinition> getServiceDefinition(String serviceDefinitionId) {
		return catalogService.getServiceDefinition(serviceDefinitionId);
	}

	protected Mono<Plan> getServiceDefinitionPlan(ServiceDefinition serviceDefinition, String planId) {
		return Mono.defer(() -> {
			if (serviceDefinition != null) {
				return Mono.just(serviceDefinition.getPlans())
					.flatMap(plans -> Flux.fromIterable(plans)
							  .filter(plan -> plan.getId().equals(planId))
							  .singleOrEmpty());
			}
			return Mono.empty();
		});
	}

	protected Context parseOriginatingIdentity(String originatingIdentityString) {
		if (originatingIdentityString == null) {
			return null;
		}

		String[] parts = originatingIdentityString.split(" ", 2);

		if (parts.length != 2) {
			throw new ServiceBrokerInvalidOriginatingIdentityException("Expected platform and properties values in "
					+ ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER + " header in request");
		}

		String encodedProperties;
		try {
			encodedProperties = new String(Base64Utils.decode(parts[1].getBytes()));
		} catch (IllegalArgumentException e) {
			throw new ServiceBrokerInvalidOriginatingIdentityException("Error decoding JSON properties from "
					+ ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER + " header in request", e);
		}

		Map<String, Object> properties;
		try {
			properties = readJsonFromString(encodedProperties);
		} catch (IOException e) {
			throw new ServiceBrokerInvalidOriginatingIdentityException("Error parsing JSON properties from "
					+ ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER + " header in request", e);
		}

		String platform = parts[0];

		if (CloudFoundryContext.CLOUD_FOUNDRY_PLATFORM.equals(platform)) {
			return CloudFoundryContext.builder()
					.properties(properties)
					.build();
		} else if (KubernetesContext.KUBERNETES_PLATFORM.equals(platform)) {
			return KubernetesContext.builder()
					.properties(properties)
					.build();
		} else {
			return PlatformContext.builder()
					.platform(platform)
					.properties(properties)
					.build();
		}
	}

	private Map<String, Object> readJsonFromString(String value) throws IOException {
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
		return objectMapper.readValue(value, new TypeReference<Map<String,Object>>() {});
	}

	protected HttpStatus getAsyncResponseCode(AsyncServiceBrokerResponse response) {
		if (response != null && response.isAsync()) {
			return HttpStatus.ACCEPTED;
		}
		return HttpStatus.OK;
	}

}
