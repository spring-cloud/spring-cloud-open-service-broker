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
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.AsyncServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
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
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#provisioning">Open Service
 * 		Broker API specification</a>
 */
@ServiceBrokerRestController
public class ServiceInstanceController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceInstanceController.class);

	private static final String PLATFORM_PATH_MAPPING = "/{platformInstanceId}/v2/service_instances/{instanceId}";

	private static final String PATH_MAPPING = "/v2/service_instances/{instanceId}";

	private static final String DEBUG_REQUEST = "request={}";

	private static final String DEBUG_RESPONSE = "serviceInstanceId={}, response={}";

	private final ServiceInstanceService service;

	/**
	 * Construct a new {@link ServiceInstanceController}
	 *
	 * @param catalogService the catalog service
	 * @param serviceInstanceService the service instance service
	 */
	public ServiceInstanceController(CatalogService catalogService, ServiceInstanceService serviceInstanceService) {
		super(catalogService);
		this.service = serviceInstanceService;
	}

	/**
	 * REST controller for creating a service instance
	 *
	 * @param pathVariables the path variables
	 * @param serviceInstanceId the service instance ID
	 * @param acceptsIncomplete indicates an asynchronous request
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentityString identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 * @param request the request body
	 * @return the response
	 */
	@PutMapping({PLATFORM_PATH_MAPPING, PATH_MAPPING})
	public Mono<ResponseEntity<CreateServiceInstanceResponse>> createServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@RequestHeader(value = ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, required = false) String requestIdentity,
			@Valid @RequestBody CreateServiceInstanceRequest request) {
		return getRequiredServiceDefinition(request.getServiceDefinitionId())
				.flatMap(serviceDefinition -> getRequiredServiceDefinitionPlan(serviceDefinition, request.getPlanId())
						.map(plan -> {
							request.setPlan(plan);
							return request;
						})
						.map(req -> {
							req.setServiceInstanceId(serviceInstanceId);
							req.setServiceDefinition(serviceDefinition);
							return req;
						}))
				.flatMap(req -> configureCommonRequestFields(req,
						pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE), apiInfoLocation,
						originatingIdentityString, requestIdentity, acceptsIncomplete))
				.cast(CreateServiceInstanceRequest.class)
				.flatMap(req -> service.createServiceInstance(req)
						.doOnRequest(v -> {
							LOG.info("Creating a service instance");
							LOG.debug(DEBUG_REQUEST, req);
						})
						.doOnSuccess(response -> {
							LOG.info("Creating a service instance succeeded");
							LOG.debug(DEBUG_RESPONSE, serviceInstanceId, response);
						})
						.doOnError(e -> LOG.error("Error creating service instance. error=" + e.getMessage(), e)))
				.map(response -> new ResponseEntity<>(response, getCreateResponseCode(response)))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.CREATED)));
	}

	private HttpStatus getCreateResponseCode(CreateServiceInstanceResponse response) {
		HttpStatus status = HttpStatus.CREATED;
		if (response != null) {
			if (response.isAsync()) {
				status = HttpStatus.ACCEPTED;
			}
			else if (response.isInstanceExisted()) {
				status = HttpStatus.OK;
			}
		}
		return status;
	}

	/**
	 * REST controller for getting a service instance
	 *
	 * @param pathVariables the path variables
	 * @param serviceInstanceId the service instance ID
	 * @param serviceDefinitionId the service definition ID
	 * @param planId the plan ID
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentityString identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 * @return the response
	 */
	@GetMapping({PLATFORM_PATH_MAPPING, PATH_MAPPING})
	public Mono<ResponseEntity<GetServiceInstanceResponse>> getServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(value = ServiceBrokerRequest.SERVICE_ID_PARAMETER, required = false) String serviceDefinitionId,
			@RequestParam(value = ServiceBrokerRequest.PLAN_ID_PARAMETER, required = false) String planId,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@RequestHeader(value = ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, required = false) String requestIdentity) {
		return Mono.just(GetServiceInstanceRequest.builder()
				.serviceInstanceId(serviceInstanceId)
				.serviceDefinitionId(serviceDefinitionId)
				.planId(planId)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.requestIdentity(requestIdentity)
				.build())
				.flatMap(request -> service.getServiceInstance(request)
						.doOnRequest(v -> {
							LOG.info("Getting service instance");
							LOG.debug(DEBUG_REQUEST, request);
						})
						.doOnSuccess(response -> {
							LOG.info("Getting service instance succeeded");
							LOG.debug(DEBUG_RESPONSE, serviceInstanceId, response);
						})
						.doOnError(e -> LOG.error("Error getting service instance. error=" + e.getMessage(), e)))
				.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.OK)))
				.onErrorResume(e -> {
					if (e instanceof ServiceInstanceDoesNotExistException) {
						return Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND));
					}
					else {
						return Mono.error(e);
					}
				});
	}

	/**
	 * REST controller for getting the last operation of a service instance
	 *
	 * @param pathVariables the path variables
	 * @param serviceInstanceId the service instance ID
	 * @param serviceDefinitionId the service definition ID
	 * @param planId the plan ID
	 * @param operation description of the operation being performed
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentityString identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 * @return the response
	 */
	@GetMapping({PLATFORM_PATH_MAPPING + "/last_operation", PATH_MAPPING + "/last_operation"})
	public Mono<ResponseEntity<GetLastServiceOperationResponse>> getServiceInstanceLastOperation(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(value = ServiceBrokerRequest.SERVICE_ID_PARAMETER, required = false) String serviceDefinitionId,
			@RequestParam(value = ServiceBrokerRequest.PLAN_ID_PARAMETER, required = false) String planId,
			@RequestParam(value = "operation", required = false) String operation,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@RequestHeader(value = ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, required = false) String requestIdentity) {
		return Mono.just(GetLastServiceOperationRequest.builder()
				.serviceDefinitionId(serviceDefinitionId)
				.serviceInstanceId(serviceInstanceId)
				.planId(planId)
				.operation(operation)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.requestIdentity(requestIdentity)
				.build())
				.flatMap(request -> service.getLastOperation(request)
						.doOnRequest(v -> {
							LOG.info("Getting service instance last operation");
							LOG.debug(DEBUG_REQUEST, request);
						})
						.doOnSuccess(response -> {
							LOG.info("Getting service instance last operation succeeded");
							LOG.debug(DEBUG_RESPONSE, serviceInstanceId, response);
						})
						.doOnError(e -> LOG.error("Error getting service instance last operation. error=" +
								e.getMessage(), e)))
				.map(response -> {
					boolean isSuccessfulDelete = OperationState.SUCCEEDED.equals(response.getState()) && response
							.isDeleteOperation();
					return new ResponseEntity<>(response, isSuccessfulDelete ? HttpStatus.GONE : HttpStatus.OK);
				})
				.onErrorResume(e -> {
					if (e instanceof ServiceInstanceDoesNotExistException) {
						// TODO: v2.16 of the OSB API spec changes this to an HTTP 404
						return Mono.just(new ResponseEntity<>(GetLastServiceOperationResponse.builder()
								.description("The requested Service Instance does not exist")
								.build(), HttpStatus.BAD_REQUEST));
					}
					else {
						return Mono.error(e);
					}
				});
	}

	/**
	 * REST controller for deleting a service instance
	 *
	 * @param pathVariables the path variables
	 * @param serviceInstanceId the service instance ID
	 * @param serviceDefinitionId the service definition ID
	 * @param planId the plan ID
	 * @param acceptsIncomplete indicates an asynchronous request
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentityString identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 * @return the response
	 */
	@DeleteMapping({PLATFORM_PATH_MAPPING, PATH_MAPPING})
	public Mono<ResponseEntity<DeleteServiceInstanceResponse>> deleteServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(ServiceBrokerRequest.SERVICE_ID_PARAMETER) String serviceDefinitionId,
			@RequestParam(ServiceBrokerRequest.PLAN_ID_PARAMETER) String planId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@RequestHeader(value = ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, required = false) String requestIdentity) {
		return getRequiredServiceDefinition(serviceDefinitionId)
				.flatMap(serviceDefinition -> getRequiredServiceDefinitionPlan(serviceDefinition, planId)
						.map(DeleteServiceInstanceRequest.builder()::plan)
						.map(builder -> builder
								.serviceInstanceId(serviceInstanceId)
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
				.flatMap(request -> service.deleteServiceInstance(request)
						.doOnRequest(v -> {
							LOG.info("Deleting a service instance");
							LOG.debug(DEBUG_REQUEST, request);
						})
						.doOnSuccess(response -> {
							LOG.info("Deleting a service instance succeeded");
							LOG.debug(DEBUG_RESPONSE, serviceInstanceId, response);
						})
						.doOnError(e -> LOG.error("Error deleting a service instance. error=" + e.getMessage(), e)))
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

	/**
	 * REST controller for updating a service instance
	 *
	 * @param pathVariables the path variables
	 * @param serviceInstanceId the service instance ID
	 * @param acceptsIncomplete indicates an asynchronous request
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentityString identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 * @param request the request body
	 * @return the response
	 */
	@PatchMapping({PLATFORM_PATH_MAPPING, PATH_MAPPING})
	public Mono<ResponseEntity<UpdateServiceInstanceResponse>> updateServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@RequestHeader(value = ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, required = false) String requestIdentity,
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
				.flatMap(req -> configureCommonRequestFields(req,
						pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE), apiInfoLocation,
						originatingIdentityString, requestIdentity, acceptsIncomplete))
				.cast(UpdateServiceInstanceRequest.class)
				.flatMap(req -> service.updateServiceInstance(req)
						.doOnRequest(v -> {
							LOG.info("Updating service instance");
							LOG.debug(DEBUG_REQUEST, request);
						})
						.doOnSuccess(response -> {
							LOG.info("Updating service instance succeeded");
							LOG.debug(DEBUG_RESPONSE, serviceInstanceId, response);
						})
						.doOnError(e -> LOG.error("Error updating service instance. error=" + e.getMessage(), e)))
				.map(response -> new ResponseEntity<>(response, getAsyncResponseCode(response)))
				.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
	}

}
