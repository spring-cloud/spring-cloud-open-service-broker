/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.controller;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.springframework.cloud.servicebroker.model.AsyncServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.ErrorMessage;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.cloud.servicebroker.model.AsyncServiceInstanceRequest.ASYNC_REQUEST_PARAMETER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.API_INFO_LOCATION_HEADER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.INSTANCE_ID_PATH_VARIABLE;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.PLAN_ID_PARAMETER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.PLATFORM_INSTANCE_ID_VARIABLE;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.SERVICE_ID_PARAMETER;

/**
 * See: http://docs.cloudfoundry.org/services/api.html
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@RestController
public class ServiceInstanceController extends BaseController {
	private static final Logger LOGGER = getLogger(ServiceInstanceController.class);

	private final ServiceInstanceService service;

	@Autowired
	public ServiceInstanceController(CatalogService catalogService, ServiceInstanceService serviceInstanceService) {
		super(catalogService);
		this.service = serviceInstanceService;
	}

	@PutMapping(value = {
			"/{cfInstanceId}/v2/service_instances/{instanceId}",
			"/v2/service_instances/{instanceId}"
	})
	public ResponseEntity<CreateServiceInstanceResponse> createServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(value = ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@Valid @RequestBody CreateServiceInstanceRequest request) {
		ServiceDefinition serviceDefinition = getRequiredServiceDefinition(request.getServiceDefinitionId());

		request.setServiceInstanceId(serviceInstanceId);
		request.setServiceDefinition(serviceDefinition);
		setCommonRequestFields(request, pathVariables.get(PLATFORM_INSTANCE_ID_VARIABLE), apiInfoLocation,
				originatingIdentityString, acceptsIncomplete);

		LOGGER.debug("Creating a service instance: request={}", request);

		CreateServiceInstanceResponse response = service.createServiceInstance(request);

		LOGGER.debug("Creating a service instance succeeded: serviceInstanceId={}, response={}",
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
			"/{cfInstanceId}/v2/service_instances/{instanceId}/last_operation",
			"/v2/service_instances/{instanceId}/last_operation"
	})
	public ResponseEntity<GetLastServiceOperationResponse> getServiceInstanceLastOperation(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(SERVICE_ID_PARAMETER) String serviceDefinitionId,
			@RequestParam(PLAN_ID_PARAMETER) String planId,
			@RequestParam(value = "operation", required = false) String operation,
			@RequestHeader(value = API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		GetLastServiceOperationRequest request = new GetLastServiceOperationRequest();
		request.setServiceDefinitionId(serviceDefinitionId);
		request.setServiceInstanceId(serviceInstanceId);
		request.setPlanId(planId);
		request.setOperation(operation);
		setCommonRequestFields(request, pathVariables.get(PLATFORM_INSTANCE_ID_VARIABLE), apiInfoLocation, originatingIdentityString);

		LOGGER.debug("Getting service instance status: request={}", request);

		GetLastServiceOperationResponse response = service.getLastOperation(request);

		LOGGER.debug("Getting service instance status succeeded: serviceInstanceId={}, response={}",
				serviceInstanceId, response);

		boolean isSuccessfulDelete = response.getState().equals(OperationState.SUCCEEDED) && response.isDeleteOperation();

		return new ResponseEntity<>(response, isSuccessfulDelete ? HttpStatus.GONE : HttpStatus.OK);
	}

	@DeleteMapping(value = {
			"/{cfInstanceId}/v2/service_instances/{instanceId}",
			"/v2/service_instances/{instanceId}"
	})
	public ResponseEntity<DeleteServiceInstanceResponse> deleteServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(SERVICE_ID_PARAMETER) String serviceDefinitionId,
			@RequestParam(PLAN_ID_PARAMETER) String planId,
			@RequestParam(value = ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		ServiceDefinition serviceDefinition = getRequiredServiceDefinition(serviceDefinitionId);

		DeleteServiceInstanceRequest request = new DeleteServiceInstanceRequest();
		request.setServiceInstanceId(serviceInstanceId);
		request.setServiceDefinitionId(serviceDefinitionId);
		request.setPlanId(planId);
		request.setServiceDefinition(serviceDefinition);
		setCommonRequestFields(request, pathVariables.get(PLATFORM_INSTANCE_ID_VARIABLE), apiInfoLocation,
				originatingIdentityString, acceptsIncomplete);

		LOGGER.debug("Deleting a service instance: request={}", request);

		try {
			DeleteServiceInstanceResponse response = service.deleteServiceInstance(request);

			LOGGER.debug("Deleting a service instance succeeded: serviceInstanceId={}, response={}",
					serviceInstanceId, response);

			return new ResponseEntity<>(response, getAsyncResponseCode(response));
		} catch (ServiceInstanceDoesNotExistException e) {
			LOGGER.debug("Service instance does not exist: ", e);
			return new ResponseEntity<>(DeleteServiceInstanceResponse.builder().build(), HttpStatus.GONE);
		}
	}

	@PatchMapping(value = {
			"/{cfInstanceId}/v2/service_instances/{instanceId}",
			"/v2/service_instances/{instanceId}"
	})
	public ResponseEntity<UpdateServiceInstanceResponse> updateServiceInstance(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@RequestParam(value = ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
			@RequestHeader(value = API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@Valid @RequestBody UpdateServiceInstanceRequest request) {
		ServiceDefinition serviceDefinition = getRequiredServiceDefinition(request.getServiceDefinitionId());

		request.setServiceInstanceId(serviceInstanceId);
		request.setServiceDefinition(serviceDefinition);
		setCommonRequestFields(request, pathVariables.get(PLATFORM_INSTANCE_ID_VARIABLE), apiInfoLocation,
				originatingIdentityString, acceptsIncomplete);

		LOGGER.debug("Updating a service instance: request={}", request);

		UpdateServiceInstanceResponse response = service.updateServiceInstance(request);

		LOGGER.debug("Updating a service instance succeeded: serviceInstanceId={}, response={}",
				serviceInstanceId, response);

		return new ResponseEntity<>(response, getAsyncResponseCode(response));
	}

	private HttpStatus getAsyncResponseCode(AsyncServiceInstanceResponse response) {
		if (response != null && response.isAsync()) {
			return HttpStatus.ACCEPTED;
		}
		return HttpStatus.OK;
	}

	@ExceptionHandler(ServiceInstanceExistsException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceExistsException ex) {
		LOGGER.debug("Service instance already exists: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ServiceInstanceUpdateNotSupportedException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceUpdateNotSupportedException ex) {
		LOGGER.debug("Service instance update not supported: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
