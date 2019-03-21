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

import java.lang.reflect.Method;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

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
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException.ASYNC_REQUIRED_ERROR;
import static org.springframework.cloud.servicebroker.exception.ServiceBrokerBindingRequiresAppException.APP_REQUIRED_ERROR;
import static org.springframework.cloud.servicebroker.exception.ServiceBrokerConcurrencyException.CONCURRENCY_ERROR;

public class ServiceBrokerExceptionHandlerTest {

	private ServiceBrokerExceptionHandler exceptionHandler;

	@Before
	public void setUp() {
		exceptionHandler = new ServiceBrokerExceptionHandler();
	}

	@Test
	public void serviceBrokerApiVersionException() {
		ServiceBrokerApiVersionException exception =
				new ServiceBrokerApiVersionException("expected-version", "actual-version");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("expected version=expected-version");
		assertThat(errorMessage.getMessage()).contains("provided version=actual-version");
	}

	@Test
	public void serviceInstanceDoesNotExistException() {
		ServiceInstanceDoesNotExistException exception =
				new ServiceInstanceDoesNotExistException("service-instance-id");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("id=service-instance-id");
	}

	@Test
	public void serviceDefinitionDoesNotExistException() {
		ServiceDefinitionDoesNotExistException exception =
				new ServiceDefinitionDoesNotExistException("service-definition-id");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("id=service-definition-id");
	}

	@Test
	public void serviceBrokerAsyncRequiredException() {
		ServiceBrokerAsyncRequiredException exception =
				new ServiceBrokerAsyncRequiredException("test message");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isEqualTo(ASYNC_REQUIRED_ERROR);
		assertThat(errorMessage.getMessage()).contains("test message");
	}

	@Test
	public void serviceBrokerInvalidParametersException() {
		ServiceBrokerInvalidParametersException exception =
				new ServiceBrokerInvalidParametersException("test message");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("test message");
	}

	@Test
	public void serviceBrokerInvalidOriginatingIdentityException() {
		ServiceBrokerInvalidOriginatingIdentityException exception =
				new ServiceBrokerInvalidOriginatingIdentityException("test message");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("test message");
	}

	@Test
	public void operationInProgressException() {
		ServiceBrokerOperationInProgressException exception =
				new ServiceBrokerOperationInProgressException("still working");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("still working");
	}

	@Test
	public void serviceBrokerUnavailableException() {
		ServiceBrokerUnavailableException exception = new ServiceBrokerUnavailableException("maintenance in progress");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("maintenance in progress");
	}

	@Test
	public void serviceBrokerConcurrencyException() {
		ServiceBrokerConcurrencyException exception = new ServiceBrokerConcurrencyException("operation in progress");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isEqualTo(CONCURRENCY_ERROR);
		assertThat(errorMessage.getMessage()).contains("operation in progress");
	}

	@Test
	public void serviceBrokerException() {
		ServiceBrokerException exception = new ServiceBrokerException("test message");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("test message");
	}

	@Test
	public void serviceBrokerExceptionWithErrorCode() {
		ServiceBrokerException exception = new ServiceBrokerException("ErrorCode", "test message");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isEqualTo("ErrorCode");
		assertThat(errorMessage.getMessage()).contains("test message");
	}

	@Test
	public void unknownException() {
		Exception exception = new Exception("test message");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("test message");
	}

	@Test
	public void methodArgumentNotValidException() throws NoSuchMethodException {
		BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "objectName");
		bindingResult.addError(new FieldError("objectName", "field1", "message"));
		bindingResult.addError(new FieldError("objectName", "field2", "message"));

		Method method = this.getClass().getMethod("setUp", (Class<?>[]) null);
		MethodParameter parameter = new MethodParameter(method, -1);

		MethodArgumentNotValidException exception =
				new MethodArgumentNotValidException(parameter, bindingResult);

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("field1");
		assertThat(errorMessage.getMessage()).contains("field2");
	}

	@Test
	public void webExchangeBindExceptionException() throws NoSuchMethodException {
		BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "objectName");
		bindingResult.addError(new FieldError("objectName", "field1", "message"));
		bindingResult.addError(new FieldError("objectName", "field2", "message"));

		Method method = this.getClass().getMethod("setUp", (Class<?>[]) null);
		MethodParameter parameter = new MethodParameter(method, -1);

		WebExchangeBindException exception =
				new WebExchangeBindException(parameter, bindingResult);

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("field1");
		assertThat(errorMessage.getMessage()).contains("field2");
	}

	@Test
	public void serviceInstanceExistsException() {
		ServiceInstanceExistsException exception =
				new ServiceInstanceExistsException("service-instance-id", "service-definition-id");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getMessage()).contains("serviceInstanceId=service-instance-id");
		assertThat(errorMessage.getMessage()).contains("serviceDefinitionId=service-definition-id");
	}

	@Test
	public void serviceInstanceUpdateNotSupportedException() {
		ServiceInstanceUpdateNotSupportedException exception = new
				ServiceInstanceUpdateNotSupportedException("test exception");

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getMessage()).contains("test exception");
	}

	@Test
	public void bindingExistsException() {
		ErrorMessage errorMessage = exceptionHandler
				.handleException(new ServiceInstanceBindingExistsException("service-instance-id", "binding-id"));

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage())
				.contains("serviceInstanceId=service-instance-id")
				.contains("bindingId=binding-id");
	}

	@Test
	public void appRequiredException() {
		ErrorMessage errorMessage = exceptionHandler
				.handleException(new ServiceBrokerBindingRequiresAppException("app GUID is required"));

		assertThat(errorMessage.getError()).isEqualTo(APP_REQUIRED_ERROR);
		assertThat(errorMessage.getMessage()).contains("app GUID is required");
	}

}