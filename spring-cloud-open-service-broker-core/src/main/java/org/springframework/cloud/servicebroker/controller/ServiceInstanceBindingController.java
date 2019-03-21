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

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	public ServiceInstanceBindingController(CatalogService catalogService,
											ServiceInstanceBindingService serviceInstanceBindingService) {
		super(catalogService);
		this.service = serviceInstanceBindingService;
	}

	@PutMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}/service_bindings/{bindingId}",
			"/v2/service_instances/{instanceId}/service_bindings/{bindingId}"
	})
	public ResponseEntity<CreateServiceInstanceBindingResponse> createServiceInstanceBinding(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(ServiceBrokerRequest.BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@Valid @RequestBody CreateServiceInstanceBindingRequest request) {
		ServiceDefinition serviceDefinition = getRequiredServiceDefinition(request.getServiceDefinitionId());
		Plan plan = getServiceDefinitionPlan(serviceDefinition, request.getPlanId());
		request.setServiceInstanceId(serviceInstanceId);
		request.setBindingId(bindingId);
		request.setServiceDefinition(serviceDefinition);
		request.setPlan(plan);
		setCommonRequestFields(request, pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE), apiInfoLocation,
				originatingIdentityString, acceptsIncomplete);

		logger.debug("Creating a service instance binding: request={}", request);

		CreateServiceInstanceBindingResponse response = service.createServiceInstanceBinding(request);

		logger.debug("Creating a service instance binding succeeded: serviceInstanceId={}, bindingId={}, response={}",
				serviceInstanceId, bindingId, response);

		return new ResponseEntity<>(response, getCreateResponseCode(response));
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
	public ResponseEntity<GetServiceInstanceBindingResponse> getServiceInstanceBinding(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(ServiceBrokerRequest.BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		GetServiceInstanceBindingRequest request = GetServiceInstanceBindingRequest.builder()
				.serviceInstanceId(serviceInstanceId)
				.bindingId(bindingId)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.build();

		logger.debug("Getting a service instance binding: request={}", request);

		GetServiceInstanceBindingResponse response = service.getServiceInstanceBinding(request);

		logger.debug("Getting a service instance binding succeeded: bindingId={}", bindingId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}/service_bindings/{bindingId}/last_operation",
			"/v2/service_instances/{instanceId}/service_bindings/{bindingId}/last_operation"
	})
	public ResponseEntity<GetLastServiceBindingOperationResponse> getServiceInstanceBindingLastOperation(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(ServiceBrokerRequest.BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestParam(value = ServiceBrokerRequest.SERVICE_ID_PARAMETER, required = false) String serviceDefinitionId,
			@RequestParam(value = ServiceBrokerRequest.PLAN_ID_PARAMETER, required = false) String planId,
			@RequestParam(value = "operation", required = false) String operation,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		GetLastServiceBindingOperationRequest request = GetLastServiceBindingOperationRequest.builder()
				.serviceDefinitionId(serviceDefinitionId)
				.serviceInstanceId(serviceInstanceId)
				.bindingId(bindingId)
				.planId(planId)
				.operation(operation)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.build();

		logger.debug("Getting service instance binding last operation: request={}", request);

		GetLastServiceBindingOperationResponse response = service.getLastOperation(request);

		logger.debug("Getting service instance binding last operation succeeded: serviceInstanceId={}, bindingId={}, response={}",
				serviceInstanceId, bindingId, response);

		boolean isSuccessfulDelete = response.getState().equals(OperationState.SUCCEEDED) && response.isDeleteOperation();

		return new ResponseEntity<>(response, isSuccessfulDelete ? HttpStatus.GONE : HttpStatus.OK);
	}

	@DeleteMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}/service_bindings/{bindingId}",
			"/v2/service_instances/{instanceId}/service_bindings/{bindingId}"
	})
	public ResponseEntity<DeleteServiceInstanceBindingResponse> deleteServiceInstanceBinding(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(ServiceBrokerRequest.BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestParam(ServiceBrokerRequest.SERVICE_ID_PARAMETER) String serviceDefinitionId,
			@RequestParam(ServiceBrokerRequest.PLAN_ID_PARAMETER) String planId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		ServiceDefinition serviceDefinition = getServiceDefinition(serviceDefinitionId);
		Plan plan = getServiceDefinitionPlan(serviceDefinition, planId);
		DeleteServiceInstanceBindingRequest request = DeleteServiceInstanceBindingRequest
				.builder()
				.serviceInstanceId(serviceInstanceId)
				.bindingId(bindingId)
				.serviceDefinitionId(serviceDefinitionId)
				.planId(planId)
				.serviceDefinition(serviceDefinition)
				.plan(plan)
				.asyncAccepted(acceptsIncomplete)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.build();

		logger.debug("Deleting a service instance binding: request={}", request);

		try {
			DeleteServiceInstanceBindingResponse response = service.deleteServiceInstanceBinding(request);

			logger.debug("Deleting a service instance binding succeeded: bindingId={}, response={}",
					bindingId, response);

			return new ResponseEntity<>(response, getAsyncResponseCode(response));
		} catch (ServiceInstanceBindingDoesNotExistException e) {
			logger.debug("Service instance binding does not exist: ", e);
			return new ResponseEntity<>(DeleteServiceInstanceBindingResponse.builder().build(), HttpStatus.GONE);
		}
	}
}
