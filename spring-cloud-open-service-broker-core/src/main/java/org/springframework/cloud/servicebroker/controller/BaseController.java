/*
 * Copyright 2002-2019 the original author or authors.
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidOriginatingIdentityException;
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionPlanDoesNotExistException;
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

/**
 * Base functionality shared by controllers.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 */
public class BaseController {

	private static final int ORIGINATING_IDENTITY_HEADER_PARTS = 2;

	protected CatalogService catalogService;

	/**
	 * Construct a new {@link BaseController}
	 * @param catalogService the catalog service
	 */
	public BaseController(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	/**
	 * Sets common headers for the request
	 * @param request the request in which to set the headers
	 * @param platformInstanceId the platform instance ID
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentityString identity of the user that initiated the request from the platform
	 * @return the request with the applied headers
	 */
	protected Mono<ServiceBrokerRequest> configureCommonRequestFields(ServiceBrokerRequest request, String platformInstanceId,
																	  String apiInfoLocation, String originatingIdentityString) {
		request.setPlatformInstanceId(platformInstanceId);
		request.setApiInfoLocation(apiInfoLocation);
		request.setOriginatingIdentity(parseOriginatingIdentity(originatingIdentityString));
		return Mono.just(request);
	}

	/**
	 * Sets common headers for the request
	 * @param request the request in which to set the headers
	 * @param platformInstanceId the platform instance ID
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentityString identity of the user that initiated the request from the platform
	 * @param asyncAccepted does the platform accept asynchronous requests
	 * @return the request with the applied headers
	 */
	protected Mono<AsyncServiceBrokerRequest> configureCommonRequestFields(AsyncServiceBrokerRequest request, String platformInstanceId,
																		   String apiInfoLocation, String originatingIdentityString,
																		   boolean asyncAccepted) {
		request.setAsyncAccepted(asyncAccepted);
		return configureCommonRequestFields(request, platformInstanceId, apiInfoLocation, originatingIdentityString)
				.cast(AsyncServiceBrokerRequest.class);
	}

	/**
	 * Find the Service Definition for the provided ID. Emits an error if not found.
	 * @param serviceDefinitionId the service definition ID
	 * @return the Service Definition
	 */
	protected Mono<ServiceDefinition> getRequiredServiceDefinition(String serviceDefinitionId) {
		return getServiceDefinition(serviceDefinitionId)
				.switchIfEmpty(Mono.error(new ServiceDefinitionDoesNotExistException(serviceDefinitionId)));
	}

	/**
	 * Find the Service Definition for the provided ID, or empty if not found.
	 * @param serviceDefinitionId the service definition ID
	 * @return the Service Definition
	 */
	protected Mono<ServiceDefinition> getServiceDefinition(String serviceDefinitionId) {
		return catalogService.getServiceDefinition(serviceDefinitionId);
	}

	/**
	 * Find the Plan for the Service Definition and Plan ID, or empty if not found.
	 * @param serviceDefinition the Service Definition
	 * @param planId the plan ID
	 * @return the Plan
	 */
	protected Mono<Plan> getServiceDefinitionPlan(ServiceDefinition serviceDefinition, String planId) {
		return Mono.justOrEmpty(serviceDefinition)
				.flatMap(serviceDef -> Mono.justOrEmpty(serviceDef.getPlans())
						.flatMap(plans -> Flux.fromIterable(plans)
								.filter(plan -> plan.getId().equals(planId))
								.singleOrEmpty()));
	}

	/**
	 * Find the Plan for the Service Definition and Plan ID. Emits an error if not found.
	 * @param serviceDefinition the Service Definition
	 * @param planId the plan ID
	 * @return the Plan
	 */
	protected Mono<Plan> getRequiredServiceDefinitionPlan(ServiceDefinition serviceDefinition, String planId) {
		return getServiceDefinitionPlan(serviceDefinition, planId)
				.switchIfEmpty(Mono.error(new ServiceDefinitionPlanDoesNotExistException(planId)));
	}

	/**
	 * Populates a platform specific context from the originating identity
	 *
	 * @param originatingIdentityString identity of the user that initiated the request from the platform
	 * @return the Context
	 */
	protected Context parseOriginatingIdentity(String originatingIdentityString) {
		if (originatingIdentityString == null) {
			return null;
		}

		String[] parts = splitOriginatingIdentityHeaderParts(originatingIdentityString);
		String encodedProperties = decodeOriginatingIdentityHeader(parts[1]);
		Map<String, Object> properties = parseOriginatingIdentityHeader(encodedProperties);
		String platform = parts[0];

		if (CloudFoundryContext.CLOUD_FOUNDRY_PLATFORM.equals(platform)) {
			return CloudFoundryContext.builder()
					.properties(properties)
					.build();
		}
		else if (KubernetesContext.KUBERNETES_PLATFORM.equals(platform)) {
			return KubernetesContext.builder()
					.properties(properties)
					.build();
		}
		else {
			return PlatformContext.builder()
					.platform(platform)
					.properties(properties)
					.build();
		}
	}

	private String[] splitOriginatingIdentityHeaderParts(String header) {
		String[] parts = header.split(" ", ORIGINATING_IDENTITY_HEADER_PARTS);
		if (parts.length != ORIGINATING_IDENTITY_HEADER_PARTS) {
			throw new ServiceBrokerInvalidOriginatingIdentityException("Expected platform and properties values in "
					+ ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER + " header in request");
		}
		return parts;
	}

	private String decodeOriginatingIdentityHeader(String encodedProperties) {
		try {
			return new String(Base64Utils.decode(encodedProperties.getBytes()));
		}
		catch (IllegalArgumentException e) {
			throw new ServiceBrokerInvalidOriginatingIdentityException("Error decoding JSON properties from "
					+ ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER + " header in request", e);
		}
	}

	private Map<String, Object> parseOriginatingIdentityHeader(String encodedProperties) {
		try {
			return readJsonFromString(encodedProperties);
		}
		catch (IOException e) {
			throw new ServiceBrokerInvalidOriginatingIdentityException("Error parsing JSON properties from "
					+ ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER + " header in request", e);
		}
	}

	private Map<String, Object> readJsonFromString(String value) throws IOException {
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
		return objectMapper.readValue(value, new TypeReference<Map<String,Object>>() {});
	}

	/**
	 * If an asynchronous request is received, then return HTTP 202 Accepted, otherwise HTTP 200 OK
	 * @param response the response
	 * @return the HTTP status
	 */
	protected HttpStatus getAsyncResponseCode(AsyncServiceBrokerResponse response) {
		if (response != null && response.isAsync()) {
			return HttpStatus.ACCEPTED;
		}
		return HttpStatus.OK;
	}

}
