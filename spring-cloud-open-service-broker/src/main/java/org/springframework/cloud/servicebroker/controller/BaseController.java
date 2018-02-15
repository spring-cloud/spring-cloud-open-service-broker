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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.AsyncRequiredErrorMessage;
import org.springframework.cloud.servicebroker.model.AsyncServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.ErrorMessage;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER;

/**
 * Base controller.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
public class BaseController {
	private static final Logger LOGGER = getLogger(BaseController.class);

	protected CatalogService catalogService;

	public BaseController(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	protected void setCommonRequestFields(ServiceBrokerRequest request, String platformInstanceId,
										  String apiInfoLocation, String originatingIdentityString) {
		request.setPlatformInstanceId(platformInstanceId);
		request.setApiInfoLocation(apiInfoLocation);
		request.setOriginatingIdentity(parseOriginatingIdentity(originatingIdentityString));
	}

	protected void setCommonRequestFields(AsyncServiceInstanceRequest request, String platformInstanceId,
										  String apiInfoLocation, String originatingIdentityString,
										  boolean asyncAccepted) {
		setCommonRequestFields(request, platformInstanceId, apiInfoLocation, originatingIdentityString);
		request.setAsyncAccepted(asyncAccepted);
	}

	protected ServiceDefinition getRequiredServiceDefinition(String serviceDefinitionId) {
		ServiceDefinition serviceDefinition = getServiceDefinition(serviceDefinitionId);
		if (serviceDefinition == null) {
			throw new ServiceDefinitionDoesNotExistException(serviceDefinitionId);
		}
		return serviceDefinition;
	}

	protected ServiceDefinition getServiceDefinition(String serviceDefinitionId) {
		return catalogService.getServiceDefinition(serviceDefinitionId);
	}

	private Context parseOriginatingIdentity(String originatingIdentityString) {
		if (originatingIdentityString == null) {
			return null;
		}

		String[] parts = originatingIdentityString.split(" ", 2);

		if (parts.length != 2) {
			throw new HttpMessageNotReadableException("Expected platform and properties values in "
					+ ORIGINATING_IDENTITY_HEADER + " header in request");
		}

		String encodedProperties;
		try {
			encodedProperties = new String(Base64Utils.decode(parts[1].getBytes()));
		} catch (IllegalArgumentException e) {
			throw new HttpMessageNotReadableException("Error decoding JSON properties from "
					+ ORIGINATING_IDENTITY_HEADER + " header in request", e);
		}

		Map<String, Object> properties;
		try {
			properties = readJsonFromString(encodedProperties);
		} catch (IOException e) {
			throw new HttpMessageNotReadableException("Error parsing JSON properties from "
					+ ORIGINATING_IDENTITY_HEADER + " header in request", e);
		}

		String platform = parts[0];

		return Context.builder()
				.platform(platform)
				.properties(properties)
				.build();
	}

	private Map<String, Object> readJsonFromString(String value) throws IOException {
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
		return objectMapper.readValue(value, new TypeReference<Map<String,Object>>() {});
	}

	@ExceptionHandler(ServiceBrokerApiVersionException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceBrokerApiVersionException ex) {
		LOGGER.debug("Unsupported service broker API version: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}

	@ExceptionHandler(ServiceInstanceDoesNotExistException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceDoesNotExistException ex) {
		LOGGER.debug("Service instance does not exist: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceDefinitionDoesNotExistException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceDefinitionDoesNotExistException ex) {
		LOGGER.debug("Service definition does not exist: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorMessage> handleException(HttpMessageNotReadableException ex) {
		LOGGER.debug("Unprocessable request received: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessage> handleException(MethodArgumentNotValidException ex) {
		LOGGER.debug("Unprocessable request received: ", ex);
		BindingResult result = ex.getBindingResult();
		StringBuilder message = new StringBuilder("Missing required fields:");
		for (FieldError error : result.getFieldErrors()) {
			message.append(' ').append(error.getField());
		}
		return getErrorResponse(message.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceBrokerAsyncRequiredException.class)
	public ResponseEntity<AsyncRequiredErrorMessage> handleException(ServiceBrokerAsyncRequiredException ex) {
		LOGGER.debug("Broker requires async support: ", ex);
		return new ResponseEntity<>(
				new AsyncRequiredErrorMessage(ex.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceBrokerInvalidParametersException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceBrokerInvalidParametersException ex) {
		LOGGER.debug("Invalid parameters received: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorMessage> handleException(Exception ex) {
		LOGGER.debug("Unknown exception handled: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	protected ResponseEntity<ErrorMessage> getErrorResponse(String message, HttpStatus status) {
		return new ResponseEntity<>(new ErrorMessage(message), status);
	}
}
