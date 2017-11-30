package org.springframework.cloud.servicebroker.controller;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.AsyncRequiredErrorMessage;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.ErrorMessage;
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

import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER;

/**
 * Base controller.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@Slf4j
public class BaseController {

	protected CatalogService catalogService;

	public BaseController(CatalogService catalogService) {
		this.catalogService = catalogService;
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
					+ ORIGINATING_IDENTITY_HEADER + " header in request");
		}

		String platform = parts[0];

		String encodedProperties;
		try {
			encodedProperties = new String(Base64Utils.decode(parts[1].getBytes()));
		} catch (Exception e) {
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

		return new Context(platform, properties);
	}

	private Map<String, Object> readJsonFromString(String value) throws java.io.IOException {
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
		return objectMapper.readValue(value, new TypeReference<Map<String,Object>>() {});
	}

	@ExceptionHandler(ServiceBrokerApiVersionException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceBrokerApiVersionException ex) {
		log.debug("Unsupported service broker API version: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}

	@ExceptionHandler(ServiceInstanceDoesNotExistException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceDoesNotExistException ex) {
		log.debug("Service instance does not exist: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceDefinitionDoesNotExistException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceDefinitionDoesNotExistException ex) {
		log.debug("Service definition does not exist: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorMessage> handleException(HttpMessageNotReadableException ex) {
		log.debug("Unprocessable request received: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessage> handleException(MethodArgumentNotValidException ex) {
		log.debug("Unprocessable request received: ", ex);
		BindingResult result = ex.getBindingResult();
		String message = "Missing required fields:";
		for (FieldError error : result.getFieldErrors()) {
			message += " " + error.getField();
		}
		return getErrorResponse(message, HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceBrokerAsyncRequiredException.class)
	public ResponseEntity<AsyncRequiredErrorMessage> handleException(ServiceBrokerAsyncRequiredException ex) {
		log.debug("Broker requires async support: ", ex);
		return new ResponseEntity<>(
				new AsyncRequiredErrorMessage(ex.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceBrokerInvalidParametersException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceBrokerInvalidParametersException ex) {
		log.debug("Invalid parameters received: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorMessage> handleException(Exception ex) {
		log.debug("Unknown exception handled: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<ErrorMessage> getErrorResponse(String message, HttpStatus status) {
		return new ResponseEntity<>(new ErrorMessage(message), status);
	}
}
