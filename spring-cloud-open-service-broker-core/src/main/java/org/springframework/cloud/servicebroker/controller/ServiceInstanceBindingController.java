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

import java.util.Map;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.annotation.ServiceBrokerRestController;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.model.AsyncServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationRequest;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.instance.OperationState;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Provide endpoints for the service bindings API.
 * 
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#binding">Open Service Broker API specification</a>
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 */
@ServiceBrokerRestController
public class ServiceInstanceBindingController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceBindingController.class);

	private final ServiceInstanceBindingService service;

	public ServiceInstanceBindingController(CatalogService catalogService,
			ServiceInstanceBindingService serviceInstanceBindingService) {
		super(catalogService);
		this.service = serviceInstanceBindingService;
	}

	@PutMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}/service_bindings/{bindingId}",
			"/v2/service_instances/{instanceId}/service_bindings/{bindingId}"
	})
	public Mono<ResponseEntity<CreateServiceInstanceBindingResponse>> createServiceInstanceBinding(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(ServiceBrokerRequest.BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@Valid @RequestBody CreateServiceInstanceBindingRequest request) {
		return getRequiredServiceDefinition(request.getServiceDefinitionId())
				.flatMap(serviceDefinition -> getServiceDefinitionPlan(serviceDefinition, request.getPlanId())
						.map(plan -> {
							request.setPlan(plan);
							return request;
						})
						.switchIfEmpty(Mono.just(request))
						.map(req -> {
							request.setServiceInstanceId(serviceInstanceId);
							request.setBindingId(bindingId);
							request.setServiceDefinition(serviceDefinition);
							return request;
						}))
				.cast(AsyncServiceBrokerRequest.class)
				.flatMap(req -> setCommonRequestFields(req, pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE),
						apiInfoLocation, originatingIdentityString, acceptsIncomplete))
				.cast(CreateServiceInstanceBindingRequest.class)
				.flatMap(req -> service.createServiceInstanceBinding(req)
						.doOnRequest(v -> logger.debug("Creating a service instance binding: request={}", req))
						.doOnSuccess(response -> logger.debug("Creating a service instance binding succeeded: serviceInstanceId={}, bindingId={}, response={}",
								serviceInstanceId, bindingId, response)))
				.map(response -> new ResponseEntity<>(response, getCreateResponseCode(response)))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.CREATED)));
	}

	private HttpStatus getCreateResponseCode(CreateServiceInstanceBindingResponse response) {
		if (response != null) {
			if (response.isAsync()) {
				return HttpStatus.ACCEPTED;
			} else if (response.isBindingExisted()) {
				return HttpStatus.OK;
			}
		}
		return HttpStatus.CREATED;
	}

	@GetMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}/service_bindings/{bindingId}",
			"/v2/service_instances/{instanceId}/service_bindings/{bindingId}"
	})
	public Mono<ResponseEntity<GetServiceInstanceBindingResponse>> getServiceInstanceBinding(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(ServiceBrokerRequest.BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		return Mono.just(GetServiceInstanceBindingRequest.builder()
				.serviceInstanceId(serviceInstanceId)
				.bindingId(bindingId)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.build())
				.flatMap(req -> service.getServiceInstanceBinding(req)
						.doOnRequest(v -> logger.debug("Getting a service instance binding: request={}", req))
						.doOnSuccess(response -> logger.debug("Getting a service instance binding succeeded: bindingId={}", bindingId)))
				.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
	}

	@GetMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}/service_bindings/{bindingId}/last_operation",
			"/v2/service_instances/{instanceId}/service_bindings/{bindingId}/last_operation"
	})
	public Mono<ResponseEntity<GetLastServiceBindingOperationResponse>> getServiceInstanceBindingLastOperation(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(ServiceBrokerRequest.BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestParam(value = ServiceBrokerRequest.SERVICE_ID_PARAMETER, required = false) String serviceDefinitionId,
			@RequestParam(value = ServiceBrokerRequest.PLAN_ID_PARAMETER, required = false) String planId,
			@RequestParam(value = "operation", required = false) String operation,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		return Mono.just(GetLastServiceBindingOperationRequest.builder()
				.serviceDefinitionId(serviceDefinitionId)
				.serviceInstanceId(serviceInstanceId)
				.bindingId(bindingId)
				.planId(planId)
				.operation(operation)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.build())
				.flatMap(request -> service.getLastOperation(request)
						.doOnRequest(v -> logger.debug("Getting service instance binding last operation: request={}", request))
						.doOnSuccess(aVoid -> logger.debug("Getting service instance binding last operation succeeded: serviceInstanceId={}, bindingId={}",
								serviceInstanceId, bindingId))
						.doOnError(e -> logger.debug(e.getMessage(), e)))
				.flatMap(response -> Mono.just(response.getState().equals(OperationState.SUCCEEDED) && response.isDeleteOperation())
						.flatMap(isSuccessfulDelete ->
								Mono.just(new ResponseEntity<>(response, isSuccessfulDelete ? HttpStatus.GONE : HttpStatus.OK))));
	}

	@DeleteMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}/service_bindings/{bindingId}",
			"/v2/service_instances/{instanceId}/service_bindings/{bindingId}"
	})
	public Mono<ResponseEntity<DeleteServiceInstanceBindingResponse>> deleteServiceInstanceBinding(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(ServiceBrokerRequest.BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestParam(ServiceBrokerRequest.SERVICE_ID_PARAMETER) String serviceDefinitionId,
			@RequestParam(ServiceBrokerRequest.PLAN_ID_PARAMETER) String planId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		return getServiceDefinition(serviceDefinitionId)
				.switchIfEmpty(Mono.just(ServiceDefinition.builder().build()))
				.flatMap(serviceDefinition -> getServiceDefinitionPlan(serviceDefinition, planId)
						.map(DeleteServiceInstanceBindingRequest.builder()::plan)
						.switchIfEmpty(Mono.just(DeleteServiceInstanceBindingRequest.builder()))
						.map(builder -> builder
								.serviceInstanceId(serviceInstanceId)
								.bindingId(bindingId)
								.serviceDefinitionId(serviceDefinitionId)
								.planId(planId)
								.serviceDefinition(serviceDefinition)
								.asyncAccepted(acceptsIncomplete)
								.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
								.apiInfoLocation(apiInfoLocation)
								.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
								.build()))
				.flatMap(req -> service.deleteServiceInstanceBinding(req)
						.doOnRequest(v -> logger.debug("Deleting a service instance binding: request={}", req))
						.doOnSuccess(aVoid -> logger.debug("Deleting a service instance binding succeeded: bindingId={}", bindingId))
						.doOnError(e -> logger.debug(e.getMessage(), e)))
				.map(response -> new ResponseEntity<>(response, getAsyncResponseCode(response)))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.OK)))
				.onErrorResume(e -> {
					if (e instanceof ServiceInstanceBindingDoesNotExistException) {
						return Mono.just(new ResponseEntity<>(HttpStatus.GONE));
					}
					else {
						return Mono.error(e);
					}
				});
	}

}
