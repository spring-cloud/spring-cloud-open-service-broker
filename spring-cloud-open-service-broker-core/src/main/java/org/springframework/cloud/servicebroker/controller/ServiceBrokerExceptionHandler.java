/*
 * Copyright 2002-2021 the original author or authors.
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

import org.slf4j.Logger;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionMissingException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerBindingRequiresAppException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerConcurrencyException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerCreateOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerDeleteOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidOriginatingIdentityException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerMaintenanceInfoConflictException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerUnavailableException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerUpdateOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionPlanDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.springframework.cloud.servicebroker.model.error.ErrorMessage;
import org.springframework.cloud.servicebroker.model.error.OperationInProgressMessage;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception handling logic shared by all Controllers.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public abstract class ServiceBrokerExceptionHandler {

	protected static final String UNPROCESSABLE_REQUEST = "Unprocessable request received: ";

	/**
	 * Callback to implementing classes to obtain the configured Logger
	 *
	 * @return the Logger
	 */
	protected abstract Logger getLog();

	/**
	 * Handle a {@link ServiceBrokerApiVersionException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceBrokerApiVersionException.class)
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	public ErrorMessage handleException(ServiceBrokerApiVersionException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceBrokerApiVersionMissingException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceBrokerApiVersionMissingException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleException(ServiceBrokerApiVersionMissingException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceInstanceDoesNotExistException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceInstanceDoesNotExistException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceInstanceDoesNotExistException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceDefinitionDoesNotExistException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceDefinitionDoesNotExistException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleException(ServiceDefinitionDoesNotExistException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceDefinitionPlanDoesNotExistException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceDefinitionPlanDoesNotExistException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleException(ServiceDefinitionPlanDoesNotExistException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceBrokerAsyncRequiredException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceBrokerAsyncRequiredException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceBrokerAsyncRequiredException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceBrokerMaintenanceInfoConflictException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceBrokerMaintenanceInfoConflictException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceBrokerMaintenanceInfoConflictException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceBrokerInvalidParametersException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceBrokerInvalidParametersException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleException(ServiceBrokerInvalidParametersException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceBrokerOperationInProgressException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceBrokerOperationInProgressException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException(ServiceBrokerOperationInProgressException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceBrokerCreateOperationInProgressException}
	 *
	 * @param ex the exception
	 * @return an operation in progress message
	 */
	@ExceptionHandler(ServiceBrokerCreateOperationInProgressException.class)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public OperationInProgressMessage handleException(ServiceBrokerCreateOperationInProgressException ex) {
		return getOperationInProgressMessage(ex);
	}

	/**
	 * Handle a {@link ServiceBrokerUpdateOperationInProgressException}
	 *
	 * @param ex the exception
	 * @return an operation in progress message
	 */
	@ExceptionHandler(ServiceBrokerUpdateOperationInProgressException.class)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public OperationInProgressMessage handleException(ServiceBrokerUpdateOperationInProgressException ex) {
		return getOperationInProgressMessage(ex);
	}

	/**
	 * Handle a {@link ServiceBrokerDeleteOperationInProgressException}
	 *
	 * @param ex the exception
	 * @return an operation in progress message
	 */
	@ExceptionHandler(ServiceBrokerDeleteOperationInProgressException.class)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public OperationInProgressMessage handleException(ServiceBrokerDeleteOperationInProgressException ex) {
		return getOperationInProgressMessage(ex);
	}

	/**
	 * Handle a {@link ServiceBrokerUnavailableException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceBrokerUnavailableException.class)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public ErrorMessage handleException(ServiceBrokerUnavailableException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceBrokerConcurrencyException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceBrokerConcurrencyException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceBrokerConcurrencyException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceBrokerException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceBrokerException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorMessage handleException(ServiceBrokerException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceBrokerInvalidOriginatingIdentityException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceBrokerInvalidOriginatingIdentityException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceBrokerInvalidOriginatingIdentityException ex) {
		getLog().error(UNPROCESSABLE_REQUEST, ex);
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link Exception}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorMessage handleException(Exception ex) {
		getLog().error("Unknown exception handled: ", ex);
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceInstanceExistsException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceInstanceExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorMessage handleException(ServiceInstanceExistsException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceInstanceUpdateNotSupportedException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceInstanceUpdateNotSupportedException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceInstanceUpdateNotSupportedException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceInstanceBindingExistsException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceInstanceBindingExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorMessage handleException(ServiceInstanceBindingExistsException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceInstanceBindingDoesNotExistException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceInstanceBindingDoesNotExistException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceInstanceBindingDoesNotExistException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Handle a {@link ServiceBrokerBindingRequiresAppException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(ServiceBrokerBindingRequiresAppException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorMessage handleException(ServiceBrokerBindingRequiresAppException ex) {
		return getErrorResponse(ex);
	}

	/**
	 * Format an error message for the exception
	 *
	 * @param ex the exception
	 * @return the error message
	 */
	protected ErrorMessage getErrorResponse(ServiceBrokerException ex) {
		getLog().debug(ex.getMessage(), ex);
		return ex.getErrorMessage();
	}

	/**
	 * Format an operation in progress message for the exception
	 *
	 * @param ex the exception
	 * @return the message
	 */
	protected OperationInProgressMessage getOperationInProgressMessage(ServiceBrokerOperationInProgressException ex) {
		getLog().debug(ex.getMessage(), ex);
		return ex.getOperationInProgressMessage();
	}

	/**
	 * Format an error message for the exception
	 *
	 * @param ex the exception
	 * @return the error message
	 */
	protected ErrorMessage getErrorResponse(Exception ex) {
		return getErrorResponse(ex.getMessage());
	}

	/**
	 * Create an error message object
	 *
	 * @param message the text of the message
	 * @return the error message
	 */
	protected ErrorMessage getErrorResponse(String message) {
		return new ErrorMessage(message);
	}

	/**
	 * Creates an error message for binding errors
	 *
	 * @param ex the exception
	 * @param result the binding result
	 * @return the error message
	 */
	protected ErrorMessage handleBindingException(Exception ex, final BindingResult result) {
		getLog().error(UNPROCESSABLE_REQUEST, ex);
		StringBuilder message = new StringBuilder("Missing required fields:");
		for (FieldError error : result.getFieldErrors()) {
			message.append(' ').append(error.getField());
		}
		return getErrorResponse(message.toString());
	}

}
