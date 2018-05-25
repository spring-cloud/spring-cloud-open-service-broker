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

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerConcurrencyException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerUnavailableException;
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.error.ErrorMessage;
import org.springframework.cloud.servicebroker.model.instance.AsyncServiceInstanceRequest;
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

/**
 * Base functionality shared by controllers.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
public class BaseController {

	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

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

	protected Context parseOriginatingIdentity(String originatingIdentityString) {
		if (originatingIdentityString == null) {
			return null;
		}

		String[] parts = originatingIdentityString.split(" ", 2);

		if (parts.length != 2) {
			throw new HttpMessageNotReadableException("Expected platform and properties values in "
					+ ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER + " header in request");
		}

		String encodedProperties;
		try {
			encodedProperties = new String(Base64Utils.decode(parts[1].getBytes()));
		} catch (IllegalArgumentException e) {
			throw new HttpMessageNotReadableException("Error decoding JSON properties from "
					+ ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER + " header in request", e);
		}

		Map<String, Object> properties;
		try {
			properties = readJsonFromString(encodedProperties);
		} catch (IOException e) {
			throw new HttpMessageNotReadableException("Error parsing JSON properties from "
					+ ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER + " header in request", e);
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
		logger.debug(ex.getMessage(), ex);
		return getErrorResponse(ex.getErrorMessage(), HttpStatus.PRECONDITION_FAILED);
	}

	@ExceptionHandler(ServiceInstanceDoesNotExistException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceDoesNotExistException ex) {
		logger.debug(ex.getMessage(), ex);
		return getErrorResponse(ex.getErrorMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceDefinitionDoesNotExistException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceDefinitionDoesNotExistException ex) {
		logger.debug(ex.getMessage(), ex);
		return getErrorResponse(ex.getErrorMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceBrokerAsyncRequiredException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceBrokerAsyncRequiredException ex) {
		logger.debug(ex.getMessage(), ex);
		return getErrorResponse(ex.getErrorMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceBrokerInvalidParametersException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceBrokerInvalidParametersException ex) {
		logger.debug(ex.getMessage(), ex);
		return getErrorResponse(ex.getErrorMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceBrokerOperationInProgressException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceBrokerOperationInProgressException ex) {
		logger.debug(ex.getMessage(), ex);
		return getErrorResponse(ex.getErrorMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ServiceBrokerUnavailableException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceBrokerUnavailableException ex) {
		logger.debug(ex.getMessage(), ex);
		return getErrorResponse(ex.getErrorMessage(), HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ExceptionHandler(ServiceBrokerConcurrencyException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceBrokerConcurrencyException ex) {
		logger.debug(ex.getMessage(), ex);
		return getErrorResponse(ex.getErrorMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceBrokerException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceBrokerException ex) {
		logger.debug("Service broker exception handled: ", ex);
		return getErrorResponse(ex.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorMessage> handleException(HttpMessageNotReadableException ex) {
		logger.error("Unprocessable request received: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessage> handleException(MethodArgumentNotValidException ex) {
		return handleBindingException(ex, ex.getBindingResult());
	}

	private ResponseEntity<ErrorMessage> handleBindingException(Exception ex, final BindingResult result) {
		logger.error("Unprocessable request received: ", ex);
		StringBuilder message = new StringBuilder("Missing required fields:");
		for (FieldError error : result.getFieldErrors()) {
			message.append(' ').append(error.getField());
		}
		return getErrorResponse(message.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorMessage> handleException(Exception ex) {
		logger.error("Unknown exception handled: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	protected ResponseEntity<ErrorMessage> getErrorResponse(String message, HttpStatus status) {
		return new ResponseEntity<>(new ErrorMessage(message), status);
	}

	protected ResponseEntity<ErrorMessage> getErrorResponse(ErrorMessage message, HttpStatus status) {
		return new ResponseEntity<>(message, status);
	}
}
