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
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.AsyncServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.OperationState;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Provide endpoints for the service instances API.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#provisioning">Open Service Broker API specification</a>
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 */
@ServiceBrokerRestController
public class ServiceInstanceController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceController.class);

	private final ServiceInstanceService service;

	public ServiceInstanceController(CatalogService catalogService, ServiceInstanceService serviceInstanceService) {
		super(catalogService);
		this.service = serviceInstanceService;
	}

	@PutMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}",
			"/v2/service_instances/{instanceId}"
	})
	public Mono<ResponseEntity<CreateServiceInstanceResponse>> createServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@Valid @RequestBody CreateServiceInstanceRequest request) {
		return getRequiredServiceDefinition(request.getServiceDefinitionId())
				.flatMap(serviceDefinition -> getServiceDefinitionPlan(serviceDefinition, request.getPlanId())
						.map(plan -> {
							request.setPlan(plan);
							return request;
						})
						.switchIfEmpty(Mono.just(request))
						.map(req -> {
							req.setServiceInstanceId(serviceInstanceId);
							req.setServiceDefinition(serviceDefinition);
							return req;
						}))
				.flatMap(req -> setCommonRequestFields(req, pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE), apiInfoLocation,
						originatingIdentityString, acceptsIncomplete))
				.cast(CreateServiceInstanceRequest.class)
				.flatMap(req -> service.createServiceInstance(req)
						.doOnRequest(v -> logger.debug("Creating a service instance: request={}", req))
						.doOnSuccess(response ->
								logger.debug("Creating a service instance succeeded: serviceInstanceId={}, response={}",
										serviceInstanceId, response)))
				.map(response -> new ResponseEntity<>(response, getCreateResponseCode(response)))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.CREATED)));
	}

	private HttpStatus getCreateResponseCode(CreateServiceInstanceResponse response) {
		if (response != null) {
			if (response.isAsync()) {
				return HttpStatus.ACCEPTED;
			} else if (response.isInstanceExisted()) {
				return HttpStatus.OK;
			}
		}
		return HttpStatus.CREATED;
	}

	@GetMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}",
			"/v2/service_instances/{instanceId}"
	})
	public Mono<ResponseEntity<GetServiceInstanceResponse>> getServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		return Mono.just(GetServiceInstanceRequest.builder()
				.serviceInstanceId(serviceInstanceId)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.build())
				.flatMap(request -> service.getServiceInstance(request)
						.doOnRequest(v -> logger.debug("Getting service instance: request={}", request))
						.doOnSuccess(response -> logger.debug("Getting service instance succeeded: serviceInstanceId={}, response={}",
								serviceInstanceId, response)))
				.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
	}

	@GetMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}/last_operation",
			"/v2/service_instances/{instanceId}/last_operation"
	})
	public Mono<ResponseEntity<GetLastServiceOperationResponse>> getServiceInstanceLastOperation(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(value = ServiceBrokerRequest.SERVICE_ID_PARAMETER, required = false) String serviceDefinitionId,
			@RequestParam(value = ServiceBrokerRequest.PLAN_ID_PARAMETER, required = false) String planId,
			@RequestParam(value = "operation", required = false) String operation,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		return Mono.just(GetLastServiceOperationRequest.builder()
				.serviceDefinitionId(serviceDefinitionId)
				.serviceInstanceId(serviceInstanceId)
				.planId(planId)
				.operation(operation)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.build())
				.flatMap(request -> service.getLastOperation(request)
						.doOnRequest(v -> logger.debug("Getting service instance last operation: request={}", request))
						.doOnSuccess(response -> logger.debug("Getting service instance last operation succeeded: serviceInstanceId={}, response={}",
								serviceInstanceId, response))
				)
				.map(response -> {
					boolean isSuccessfulDelete = response.getState().equals(OperationState.SUCCEEDED) && response.isDeleteOperation();
					return new ResponseEntity<>(response, isSuccessfulDelete ? HttpStatus.GONE : HttpStatus.OK);
				});
	}

	@DeleteMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}",
			"/v2/service_instances/{instanceId}"
	})
	public Mono<ResponseEntity<DeleteServiceInstanceResponse>> deleteServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(ServiceBrokerRequest.SERVICE_ID_PARAMETER) String serviceDefinitionId,
			@RequestParam(ServiceBrokerRequest.PLAN_ID_PARAMETER) String planId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		return getRequiredServiceDefinition(serviceDefinitionId)
				.flatMap(serviceDefinition -> getServiceDefinitionPlan(serviceDefinition, planId)
						.map(DeleteServiceInstanceRequest.builder()::plan)
						.switchIfEmpty(Mono.just(DeleteServiceInstanceRequest.builder()))
						.map(builder -> builder
								.serviceInstanceId(serviceInstanceId)
								.serviceDefinitionId(serviceDefinitionId)
								.planId(planId)
								.serviceDefinition(serviceDefinition)
								.asyncAccepted(acceptsIncomplete)
								.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
								.apiInfoLocation(apiInfoLocation)
								.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
								.build()))
				.flatMap(request -> service.deleteServiceInstance(request)
						.doOnRequest(v -> logger.debug("Deleting a service instance: request={}", request))
						.doOnSuccess(response -> logger.debug("Deleting a service instance succeeded: serviceInstanceId={}, response={}",
								serviceInstanceId, response))
						.doOnError(e -> logger.debug("Service instance does not exist: ", e)))
				.map(response -> new ResponseEntity<>(response, getAsyncResponseCode(response)))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.OK)))
				.onErrorResume(e -> {
					if (e instanceof ServiceInstanceDoesNotExistException) {
						return Mono.just(new ResponseEntity<>(HttpStatus.GONE));
					}
					else {
						return Mono.error(e);
					}
				});
	}

	@PatchMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}",
			"/v2/service_instances/{instanceId}"
	})
	public Mono<ResponseEntity<UpdateServiceInstanceResponse>> updateServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@Valid @RequestBody UpdateServiceInstanceRequest request) {
		return getRequiredServiceDefinition(request.getServiceDefinitionId())
				.flatMap(serviceDefinition -> getServiceDefinitionPlan(serviceDefinition, request.getPlanId())
						.map(plan -> {
							request.setPlan(plan);
							return request;
						})
						.switchIfEmpty(Mono.just(request))
						.map(req -> {
							req.setServiceInstanceId(serviceInstanceId);
							req.setServiceDefinition(serviceDefinition);
							return req;
						}))
				.flatMap(req -> setCommonRequestFields(req, pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE), apiInfoLocation,
						originatingIdentityString, acceptsIncomplete))
				.cast(UpdateServiceInstanceRequest.class)
				.flatMap(req -> service.updateServiceInstance(req)
						.doOnRequest(v -> logger.debug("Updating a service instance: request={}", request))
						.doOnSuccess(response -> logger.debug("Updating a service instance succeeded: serviceInstanceId={}, response={}",
								serviceInstanceId, response)))
				.map(response -> new ResponseEntity<>(response, getAsyncResponseCode(response)))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
	}

}
