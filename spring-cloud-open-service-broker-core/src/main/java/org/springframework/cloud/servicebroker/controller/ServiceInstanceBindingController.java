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

package org.springframework.cloud.servicebroker.controller;

import jakarta.validation.Valid;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.annotation.ServiceBrokerRestController;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
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
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#binding">Open Service Broker
 * 		API specification</a>
 */
@ServiceBrokerRestController
public class ServiceInstanceBindingController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceInstanceBindingController.class);

	private static final String PLATFORM_PATH_MAPPING = "/{platformInstanceId}/v2/service_instances/{instanceId}/service_bindings/{bindingId}";

	private static final String PATH_MAPPING = "/v2/service_instances/{instanceId}/service_bindings/{bindingId}";

	private static final String DEBUG_REQUEST = "request={}";

	private final ServiceInstanceBindingService service;

	/**
	 * Construct a new {@link ServiceInstanceBindingController}
	 *
	 * @param catalogService the catalog service
	 * @param serviceInstanceBindingService the service instance binding service
	 */
	public ServiceInstanceBindingController(CatalogService catalogService,
			ServiceInstanceBindingService serviceInstanceBindingService) {
		super(catalogService);
		this.service = serviceInstanceBindingService;
	}

	/**
	 * REST controller for creating a service instance binding
	 *
	 * @param pathVariables the path variables
	 * @param serviceInstanceId the service instance ID
	 * @param bindingId the service binding ID
	 * @param acceptsIncomplete indicates an asynchronous request
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentityString identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 * @param request the request body
	 * @return the response
	 */
	@PutMapping({PLATFORM_PATH_MAPPING, PATH_MAPPING})
	public Mono<ResponseEntity<CreateServiceInstanceBindingResponse>> createServiceInstanceBinding(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(ServiceBrokerRequest.BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@RequestHeader(value = ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, required = false) String requestIdentity,
			@Valid @RequestBody CreateServiceInstanceBindingRequest request) {
		return getRequiredServiceDefinition(request.getServiceDefinitionId())
				.flatMap(serviceDefinition -> getRequiredServiceDefinitionPlan(serviceDefinition, request.getPlanId())
						.map(plan -> {
							request.setPlan(plan);
							return request;
						})
						.map(req -> {
							request.setServiceInstanceId(serviceInstanceId);
							request.setBindingId(bindingId);
							request.setServiceDefinition(serviceDefinition);
							return request;
						}))
				.cast(AsyncServiceBrokerRequest.class)
				.flatMap(req -> configureCommonRequestFields(req,
						pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE),
						apiInfoLocation, originatingIdentityString, requestIdentity, acceptsIncomplete))
				.cast(CreateServiceInstanceBindingRequest.class)
				.flatMap(req -> service.createServiceInstanceBinding(req)
						.doOnRequest(v -> {
							LOG.info("Creating a service instance binding");
							LOG.debug(DEBUG_REQUEST, req);
						})
						.doOnSuccess(response -> {
							LOG.info("Creating a service instance binding succeeded");
							LOG.debug("serviceInstanceId={}, bindingId={}, response={}", serviceInstanceId, bindingId,
									response);
						})
						.doOnError(e -> LOG.error("Error creating service instance binding. error=" +
								e.getMessage(), e)))
				.map(response -> new ResponseEntity<>(response, getCreateResponseCode(response)))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.CREATED)));
	}

	private HttpStatus getCreateResponseCode(CreateServiceInstanceBindingResponse response) {
		HttpStatus status = HttpStatus.CREATED;
		if (response != null) {
			if (response.isAsync()) {
				status = HttpStatus.ACCEPTED;
			}
			else if (response.isBindingExisted()) {
				status = HttpStatus.OK;
			}
		}
		return status;
	}

	/**
	 * REST controller for getting a service instance binding
	 *
	 * @param pathVariables the path variables
	 * @param serviceInstanceId the service instance ID
	 * @param bindingId the service binding ID
	 * @param serviceDefinitionId the service definition ID
	 * @param planId the plan ID
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentityString identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 * @return the response
	 */
	@GetMapping({PLATFORM_PATH_MAPPING, PATH_MAPPING})
	public Mono<ResponseEntity<GetServiceInstanceBindingResponse>> getServiceInstanceBinding(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(ServiceBrokerRequest.BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestParam(value = ServiceBrokerRequest.SERVICE_ID_PARAMETER, required = false) String serviceDefinitionId,
			@RequestParam(value = ServiceBrokerRequest.PLAN_ID_PARAMETER, required = false) String planId,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@RequestHeader(value = ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, required = false) String requestIdentity) {
		return Mono.just(GetServiceInstanceBindingRequest.builder()
				.serviceInstanceId(serviceInstanceId)
				.bindingId(bindingId)
				.serviceDefinitionId(serviceDefinitionId)
				.planId(planId)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.requestIdentity(requestIdentity)
				.build())
				.flatMap(req -> service.getServiceInstanceBinding(req)
						.doOnRequest(v -> {
							LOG.info("Getting a service instance binding");
							LOG.debug(DEBUG_REQUEST, req);
						})
						.doOnSuccess(response -> {
							LOG.info("Getting a service instance binding succeeded");
							LOG.debug("bindingId={}", bindingId);
						})
						.doOnError(e -> LOG.error("Error getting service instance binding. error=" +
								e.getMessage(), e)))
				.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.OK)))
				.onErrorResume(e -> {
					if (e instanceof ServiceInstanceBindingDoesNotExistException ||
							e instanceof ServiceInstanceDoesNotExistException) {
						return Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND));
					}
					else {
						return Mono.error(e);
					}
				});
	}

	/**
	 * REST Controller for getting the last operation of a service instance binding
	 *
	 * @param pathVariables the path variables
	 * @param serviceInstanceId the service instance ID
	 * @param bindingId the service binding ID
	 * @param serviceDefinitionId the service definition ID
	 * @param planId the plan ID
	 * @param operation description of the operation being performed
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentityString identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 * @return the response
	 */
	@GetMapping({PLATFORM_PATH_MAPPING + "/last_operation", PATH_MAPPING + "/last_operation"})
	public Mono<ResponseEntity<GetLastServiceBindingOperationResponse>> getServiceInstanceBindingLastOperation(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(ServiceBrokerRequest.BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestParam(value = ServiceBrokerRequest.SERVICE_ID_PARAMETER, required = false) String serviceDefinitionId,
			@RequestParam(value = ServiceBrokerRequest.PLAN_ID_PARAMETER, required = false) String planId,
			@RequestParam(value = "operation", required = false) String operation,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@RequestHeader(value = ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, required = false) String requestIdentity) {
		return Mono.just(GetLastServiceBindingOperationRequest.builder()
				.serviceDefinitionId(serviceDefinitionId)
				.serviceInstanceId(serviceInstanceId)
				.bindingId(bindingId)
				.planId(planId)
				.operation(operation)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.requestIdentity(requestIdentity)
				.build())
				.flatMap(request -> service.getLastOperation(request)
						.doOnRequest(v -> {
							LOG.info("Getting service instance binding last operation");
							LOG.debug(DEBUG_REQUEST, request);
						})
						.doOnSuccess(aVoid -> {
							LOG.info("Getting service instance binding last operation succeeded");
							LOG.debug("serviceInstanceId={}, bindingId={}", serviceInstanceId, bindingId);
						})
						.doOnError(e -> LOG.error("Error getting service instance binding last operation. error=" +
								e.getMessage(), e)))
				.flatMap(response -> Mono
						.just(response.getState().equals(OperationState.SUCCEEDED) && response.isDeleteOperation())
						.flatMap(isSuccessfulDelete ->
								Mono.just(new ResponseEntity<>(response,
										isSuccessfulDelete ? HttpStatus.GONE : HttpStatus.OK))));
	}

	/**
	 * REST controller for deleting a service instance binding
	 *
	 * @param pathVariables the path variables
	 * @param serviceInstanceId the service instance ID
	 * @param bindingId the service binding ID
	 * @param serviceDefinitionId the service definition ID
	 * @param planId the plan ID
	 * @param acceptsIncomplete indicates an asynchronous request
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentityString identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 * @return the response
	 */
	@DeleteMapping({PLATFORM_PATH_MAPPING, PATH_MAPPING})
	public Mono<ResponseEntity<DeleteServiceInstanceBindingResponse>> deleteServiceInstanceBinding(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(ServiceBrokerRequest.BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestParam(ServiceBrokerRequest.SERVICE_ID_PARAMETER) String serviceDefinitionId,
			@RequestParam(ServiceBrokerRequest.PLAN_ID_PARAMETER) String planId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@RequestHeader(value = ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, required = false) String requestIdentity) {
		return getRequiredServiceDefinition(serviceDefinitionId)
				.switchIfEmpty(Mono.just(ServiceDefinition.builder().build()))
				.flatMap(serviceDefinition -> getRequiredServiceDefinitionPlan(serviceDefinition, planId)
						.map(DeleteServiceInstanceBindingRequest.builder()::plan)
						.switchIfEmpty(Mono.just(DeleteServiceInstanceBindingRequest.builder()))
						.map(builder -> builder
								.serviceInstanceId(serviceInstanceId)
								.bindingId(bindingId)
								.serviceDefinitionId(serviceDefinitionId)
								.planId(planId)
								.serviceDefinition(serviceDefinition)
								.asyncAccepted(acceptsIncomplete)
								.platformInstanceId(
										pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
								.apiInfoLocation(apiInfoLocation)
								.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
								.requestIdentity(requestIdentity)
								.build()))
				.flatMap(req -> service.deleteServiceInstanceBinding(req)
						.doOnRequest(v -> {
							LOG.info("Deleting a service instance binding");
							LOG.debug(DEBUG_REQUEST, req);
						})
						.doOnSuccess(aVoid -> {
							LOG.info("Deleting a service instance binding succeeded");
							LOG.debug("bindingId={}", bindingId);
						})
						.doOnError(e -> LOG.error("Error deleting a service instance binding. error=" +
								e.getMessage(), e)))
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
