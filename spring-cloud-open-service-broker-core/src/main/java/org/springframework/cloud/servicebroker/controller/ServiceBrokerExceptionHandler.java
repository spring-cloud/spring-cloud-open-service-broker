/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.servicebroker.annotation.ServiceBrokerRestController;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerBindingRequiresAppException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerConcurrencyException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidOriginatingIdentityException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerUnavailableException;
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.springframework.cloud.servicebroker.model.error.ErrorMessage;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception handling logic shared by all Controllers.
 *
 * @author Scott Frederick
 */
@ControllerAdvice(annotations = ServiceBrokerRestController.class)
@ResponseBody
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class ServiceBrokerExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(ServiceBrokerExceptionHandler.class);

	@ExceptionHandler(ServiceBrokerApiVersionException.class)
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	public ErrorMessage handleException(ServiceBrokerApiVersionException ex) {
		return getErrorResponse(ex);
	}

	@ExceptionHandler(ServiceInstanceDoesNotExistException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceInstanceDoesNotExistException ex) {
		return getErrorResponse(ex);
	}

	@ExceptionHandler(ServiceDefinitionDoesNotExistException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceDefinitionDoesNotExistException ex) {
		return getErrorResponse(ex);
	}

	@ExceptionHandler(ServiceBrokerAsyncRequiredException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceBrokerAsyncRequiredException ex) {
		return getErrorResponse(ex);
	}

	@ExceptionHandler(ServiceBrokerInvalidParametersException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceBrokerInvalidParametersException ex) {
		return getErrorResponse(ex);
	}

	@ExceptionHandler(ServiceBrokerOperationInProgressException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException(ServiceBrokerOperationInProgressException ex) {
		return getErrorResponse(ex);
	}

	@ExceptionHandler(ServiceBrokerUnavailableException.class)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public ErrorMessage handleException(ServiceBrokerUnavailableException ex) {
		return getErrorResponse(ex);
	}

	@ExceptionHandler(ServiceBrokerConcurrencyException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceBrokerConcurrencyException ex) {
		return getErrorResponse(ex);
	}

	@ExceptionHandler(ServiceBrokerException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorMessage handleException(ServiceBrokerException ex) {
		return getErrorResponse(ex);
	}

	@ExceptionHandler(ServiceBrokerInvalidOriginatingIdentityException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceBrokerInvalidOriginatingIdentityException ex) {
		logger.error("Unprocessable request received: ", ex);
		return getErrorResponse(ex);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(MethodArgumentNotValidException ex) {
		return handleBindingException(ex, ex.getBindingResult());
	}

	private ErrorMessage handleBindingException(Exception ex, final BindingResult result) {
		logger.error("Unprocessable request received: ", ex);
		StringBuilder message = new StringBuilder("Missing required fields:");
		for (FieldError error : result.getFieldErrors()) {
			message.append(' ').append(error.getField());
		}
		return getErrorResponse(message.toString());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorMessage handleException(Exception ex) {
		logger.error("Unknown exception handled: ", ex);
		return getErrorResponse(ex);
	}

	@ExceptionHandler(ServiceInstanceExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorMessage handleException(ServiceInstanceExistsException ex) {
		return getErrorResponse(ex);
	}

	@ExceptionHandler(ServiceInstanceUpdateNotSupportedException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceInstanceUpdateNotSupportedException ex) {
		return getErrorResponse(ex);
	}

	@ExceptionHandler(ServiceInstanceBindingExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorMessage handleException(ServiceInstanceBindingExistsException ex) {
		return getErrorResponse(ex);
	}

	@ExceptionHandler(ServiceBrokerBindingRequiresAppException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceBrokerBindingRequiresAppException ex) {
		return getErrorResponse(ex);
	}

	protected ErrorMessage getErrorResponse(ServiceBrokerException ex) {
		logger.debug(ex.getMessage(), ex);
		return ex.getErrorMessage();
	}

	protected ErrorMessage getErrorResponse(Exception ex) {
		return getErrorResponse(ex.getMessage());
	}

	protected ErrorMessage getErrorResponse(String message) {
		return new ErrorMessage(message);
	}
}
