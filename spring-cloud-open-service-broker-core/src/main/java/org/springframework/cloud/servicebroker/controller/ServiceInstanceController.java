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

	@Autowired
	public ServiceInstanceController(CatalogService catalogService, ServiceInstanceService serviceInstanceService) {
		super(catalogService);
		this.service = serviceInstanceService;
	}

	@PutMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}",
			"/v2/service_instances/{instanceId}"
	})
	public ResponseEntity<CreateServiceInstanceResponse> createServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@Valid @RequestBody CreateServiceInstanceRequest request) {
		ServiceDefinition serviceDefinition = getRequiredServiceDefinition(request.getServiceDefinitionId());
		Plan plan = getServiceDefinitionPlan(serviceDefinition, request.getPlanId());

		request.setServiceInstanceId(serviceInstanceId);
		request.setServiceDefinition(serviceDefinition);
		request.setPlan(plan);
		setCommonRequestFields(request, pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE), apiInfoLocation,
				originatingIdentityString, acceptsIncomplete);

		logger.debug("Creating a service instance: request={}", request);

		CreateServiceInstanceResponse response = service.createServiceInstance(request);

		logger.debug("Creating a service instance succeeded: serviceInstanceId={}, response={}",
				serviceInstanceId, response);

		return new ResponseEntity<>(response, getCreateResponseCode(response));
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
	public ResponseEntity<GetServiceInstanceResponse> getServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		GetServiceInstanceRequest request = GetServiceInstanceRequest.builder()
				.serviceInstanceId(serviceInstanceId)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.build();

		logger.debug("Getting service instance: request={}", request);

		GetServiceInstanceResponse response = service.getServiceInstance(request);

		logger.debug("Getting service instance succeeded: serviceInstanceId={}, response={}",
				serviceInstanceId, response);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}/last_operation",
			"/v2/service_instances/{instanceId}/last_operation"
	})
	public ResponseEntity<GetLastServiceOperationResponse> getServiceInstanceLastOperation(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(value = ServiceBrokerRequest.SERVICE_ID_PARAMETER, required = false) String serviceDefinitionId,
			@RequestParam(value = ServiceBrokerRequest.PLAN_ID_PARAMETER, required = false) String planId,
			@RequestParam(value = "operation", required = false) String operation,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		GetLastServiceOperationRequest request = GetLastServiceOperationRequest.builder()
				.serviceDefinitionId(serviceDefinitionId)
				.serviceInstanceId(serviceInstanceId)
				.planId(planId)
				.operation(operation)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.build();

		logger.debug("Getting service instance last operation: request={}", request);

		GetLastServiceOperationResponse response = service.getLastOperation(request);

		logger.debug("Getting service instance last operation succeeded: serviceInstanceId={}, response={}",
				serviceInstanceId, response);

		boolean isSuccessfulDelete = response.getState().equals(OperationState.SUCCEEDED) && response.isDeleteOperation();

		return new ResponseEntity<>(response, isSuccessfulDelete ? HttpStatus.GONE : HttpStatus.OK);
	}

	@DeleteMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}",
			"/v2/service_instances/{instanceId}"
	})
	public ResponseEntity<DeleteServiceInstanceResponse> deleteServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(ServiceBrokerRequest.SERVICE_ID_PARAMETER) String serviceDefinitionId,
			@RequestParam(ServiceBrokerRequest.PLAN_ID_PARAMETER) String planId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		ServiceDefinition serviceDefinition = getRequiredServiceDefinition(serviceDefinitionId);
		Plan plan = getServiceDefinitionPlan(serviceDefinition, planId);

		DeleteServiceInstanceRequest request = DeleteServiceInstanceRequest
				.builder()
				.serviceInstanceId(serviceInstanceId)
				.serviceDefinitionId(serviceDefinitionId)
				.planId(planId)
				.serviceDefinition(serviceDefinition)
				.plan(plan)
				.asyncAccepted(acceptsIncomplete)
				.platformInstanceId(pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.build();

		logger.debug("Deleting a service instance: request={}", request);

		try {
			DeleteServiceInstanceResponse response = service.deleteServiceInstance(request);

			logger.debug("Deleting a service instance succeeded: serviceInstanceId={}, response={}",
					serviceInstanceId, response);

			return new ResponseEntity<>(response, getAsyncResponseCode(response));
		} catch (ServiceInstanceDoesNotExistException e) {
			logger.debug("Service instance does not exist: ", e);
			return new ResponseEntity<>(DeleteServiceInstanceResponse.builder().build(), HttpStatus.GONE);
		}
	}

	@PatchMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}",
			"/v2/service_instances/{instanceId}"
	})
	public ResponseEntity<UpdateServiceInstanceResponse> updateServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(value = AsyncServiceBrokerRequest.ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = ServiceBrokerRequest.API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@Valid @RequestBody UpdateServiceInstanceRequest request) {
		ServiceDefinition serviceDefinition = getRequiredServiceDefinition(request.getServiceDefinitionId());
		Plan plan = getServiceDefinitionPlan(serviceDefinition, request.getPlanId());

		request.setServiceInstanceId(serviceInstanceId);
		request.setServiceDefinition(serviceDefinition);
		request.setPlan(plan);
		setCommonRequestFields(request, pathVariables.get(ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE), apiInfoLocation,
				originatingIdentityString, acceptsIncomplete);

		logger.debug("Updating a service instance: request={}", request);

		UpdateServiceInstanceResponse response = service.updateServiceInstance(request);

		logger.debug("Updating a service instance succeeded: serviceInstanceId={}, response={}",
				serviceInstanceId, response);

		return new ResponseEntity<>(response, getAsyncResponseCode(response));
	}

}
