/*
 * Copyright 2002-2018 the original author or authors.
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
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.error.ErrorMessage;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.API_INFO_LOCATION_HEADER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.BINDING_ID_PATH_VARIABLE;
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
public class ServiceInstanceBindingController extends BaseController {
	private static final Logger LOGGER = getLogger(ServiceInstanceBindingController.class);

	private final ServiceInstanceBindingService serviceInstanceBindingService;

	@Autowired
	public ServiceInstanceBindingController(CatalogService catalogService,
											ServiceInstanceBindingService serviceInstanceBindingService) {
		super(catalogService);
		this.serviceInstanceBindingService = serviceInstanceBindingService;
	}

	@PutMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}/service_bindings/{bindingId}",
			"/v2/service_instances/{instanceId}/service_bindings/{bindingId}"
	})
	public ResponseEntity<CreateServiceInstanceBindingResponse> createServiceInstanceBinding(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestHeader(value = API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
			@Valid @RequestBody CreateServiceInstanceBindingRequest request) {
		request.setServiceInstanceId(serviceInstanceId);
		request.setBindingId(bindingId);
		request.setServiceDefinition(getRequiredServiceDefinition(request.getServiceDefinitionId()));
		setCommonRequestFields(request, pathVariables.get(PLATFORM_INSTANCE_ID_VARIABLE), apiInfoLocation, originatingIdentityString);

		LOGGER.debug("Creating a service instance binding: request={}", request);

		CreateServiceInstanceBindingResponse response = serviceInstanceBindingService.createServiceInstanceBinding(request);

		LOGGER.debug("Creating a service instance binding succeeded: serviceInstanceId={}, bindingId={}, response={}",
				serviceInstanceId, bindingId, response);

		return new ResponseEntity<>(response, getCreateResponseCode(response));
	}

	private HttpStatus getCreateResponseCode(CreateServiceInstanceBindingResponse response) {
		if (response != null && response.isBindingExisted()) {
			return  HttpStatus.OK;
		}
		return HttpStatus.CREATED;
	}

	@GetMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}/service_bindings/{bindingId}",
			"/v2/service_instances/{instanceId}/service_bindings/{bindingId}"
	})
	public ResponseEntity<GetServiceInstanceBindingResponse> getServiceInstanceBinding(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestHeader(value = API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		GetServiceInstanceBindingRequest request = GetServiceInstanceBindingRequest.builder()
				.serviceInstanceId(serviceInstanceId)
				.bindingId(bindingId)
				.platformInstanceId(pathVariables.get(PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.build();

		LOGGER.debug("Getting a service instance binding: request={}", request);

		GetServiceInstanceBindingResponse response = serviceInstanceBindingService.getServiceInstanceBinding(request);

		LOGGER.debug("Getting a service instance binding succeeded: bindingId={}", bindingId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping(value = {
			"/{platformInstanceId}/v2/service_instances/{instanceId}/service_bindings/{bindingId}",
			"/v2/service_instances/{instanceId}/service_bindings/{bindingId}"
	})
	public ResponseEntity<String> deleteServiceInstanceBinding(
			@PathVariable Map<String, String> pathVariables,
			@PathVariable(INSTANCE_ID_PATH_VARIABLE) String serviceInstanceId,
			@PathVariable(BINDING_ID_PATH_VARIABLE) String bindingId,
			@RequestParam(SERVICE_ID_PARAMETER) String serviceDefinitionId,
			@RequestParam(PLAN_ID_PARAMETER) String planId,
			@RequestHeader(value = API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
			@RequestHeader(value = ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		DeleteServiceInstanceBindingRequest request = DeleteServiceInstanceBindingRequest.builder()
				.serviceInstanceId(serviceInstanceId)
				.bindingId(bindingId)
				.serviceDefinitionId(serviceDefinitionId)
				.planId(planId)
				.serviceDefinition(getServiceDefinition(serviceDefinitionId))
				.platformInstanceId(pathVariables.get(PLATFORM_INSTANCE_ID_VARIABLE))
				.apiInfoLocation(apiInfoLocation)
				.originatingIdentity(parseOriginatingIdentity(originatingIdentityString))
				.build();

		LOGGER.debug("Deleting a service instance binding: request={}", request);

		try {
			serviceInstanceBindingService.deleteServiceInstanceBinding(request);
		} catch (ServiceInstanceBindingDoesNotExistException e) {
			LOGGER.debug("Service instance binding does not exist: ", e);
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}

		LOGGER.debug("Deleting a service instance binding succeeded: bindingId={}", bindingId);

		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	@ExceptionHandler(ServiceInstanceBindingExistsException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceBindingExistsException ex) {
		LOGGER.debug(ex.getMessage(), ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
}
