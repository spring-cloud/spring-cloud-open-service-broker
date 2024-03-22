/*
 * Copyright 2002-2024 the original author or authors.
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

import org.junit.jupiter.api.Test;

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
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionPlanDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.springframework.cloud.servicebroker.model.error.ErrorMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException.ASYNC_REQUIRED_ERROR;
import static org.springframework.cloud.servicebroker.exception.ServiceBrokerBindingRequiresAppException.APP_REQUIRED_ERROR;
import static org.springframework.cloud.servicebroker.exception.ServiceBrokerConcurrencyException.CONCURRENCY_ERROR;

abstract class ServiceBrokerExceptionHandlerTests {

	ServiceBrokerExceptionHandler exceptionHandler;

	@Test
	void serviceBrokerApiVersionException() {
		ServiceBrokerApiVersionException exception = new ServiceBrokerApiVersionException("expected-version",
				"actual-version");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("expected version=expected-version");
		assertThat(errorMessage.getMessage()).contains("provided version=actual-version");
	}

	@Test
	void serviceInstanceDoesNotExistException() {
		ServiceInstanceDoesNotExistException exception = new ServiceInstanceDoesNotExistException(
				"service-instance-id");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("id=service-instance-id");
	}

	@Test
	void serviceDefinitionDoesNotExistException() {
		ServiceDefinitionDoesNotExistException exception = new ServiceDefinitionDoesNotExistException(
				"service-definition-id");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("id=service-definition-id");
	}

	@Test
	void serviceDefinitionPlanDoesNotExistException() {
		ServiceDefinitionPlanDoesNotExistException exception = new ServiceDefinitionPlanDoesNotExistException(
				"service-definition-plan-id");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage())
			.contains("Service Definition Plan does not exist: " + "id=service-definition-plan-id");
	}

	@Test
	void serviceDefinitionPlanDoesNotExistExceptionWithCustomCode() {
		final String errorCode = "error";
		ServiceDefinitionPlanDoesNotExistException exception = new ServiceDefinitionPlanDoesNotExistException(errorCode,
				"service-definition-plan-id");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isEqualTo(errorCode);
		assertThat(errorMessage.getMessage())
			.contains("Service Definition Plan does not exist: " + "id=service-definition-plan-id");
	}

	@Test
	void serviceBrokerAsyncRequiredException() {
		ServiceBrokerAsyncRequiredException exception = new ServiceBrokerAsyncRequiredException("test message");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isEqualTo(ASYNC_REQUIRED_ERROR);
		assertThat(errorMessage.getMessage()).contains("test message");
	}

	@Test
	void serviceBrokerInvalidParametersException() {
		ServiceBrokerInvalidParametersException exception = new ServiceBrokerInvalidParametersException("test message");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("test message");
	}

	@Test
	void serviceBrokerInvalidOriginatingIdentityException() {
		ServiceBrokerInvalidOriginatingIdentityException exception = new ServiceBrokerInvalidOriginatingIdentityException(
				"test message");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("test message");
	}

	@Test
	void operationInProgressException() {
		ServiceBrokerOperationInProgressException exception = new ServiceBrokerOperationInProgressException("task_10");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("task_10");
	}

	@Test
	void serviceBrokerUnavailableException() {
		ServiceBrokerUnavailableException exception = new ServiceBrokerUnavailableException("maintenance in progress");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("maintenance in progress");
	}

	@Test
	void serviceBrokerConcurrencyException() {
		ServiceBrokerConcurrencyException exception = new ServiceBrokerConcurrencyException("operation in progress");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isEqualTo(CONCURRENCY_ERROR);
		assertThat(errorMessage.getMessage()).contains("operation in progress");
	}

	@Test
	void serviceBrokerException() {
		ServiceBrokerException exception = new ServiceBrokerException("test message");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("test message");
	}

	@Test
	void serviceBrokerExceptionWithErrorCode() {
		ServiceBrokerException exception = new ServiceBrokerException("ErrorCode", "test message");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isEqualTo("ErrorCode");
		assertThat(errorMessage.getMessage()).contains("test message");
	}

	@Test
	void unknownException() {
		Exception exception = new Exception("test message");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("test message");
	}

	@Test
	void serviceInstanceExistsException() {
		ServiceInstanceExistsException exception = new ServiceInstanceExistsException("service-instance-id",
				"service-definition-id");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getMessage()).contains("serviceInstanceId=service-instance-id");
		assertThat(errorMessage.getMessage()).contains("serviceDefinitionId=service-definition-id");
	}

	@Test
	void serviceInstanceUpdateNotSupportedException() {
		ServiceInstanceUpdateNotSupportedException exception = new ServiceInstanceUpdateNotSupportedException(
				"test exception");

		ErrorMessage errorMessage = this.exceptionHandler.handleException(exception);

		assertThat(errorMessage.getMessage()).contains("test exception");
	}

	@Test
	void bindingExistsException() {
		ErrorMessage errorMessage = this.exceptionHandler
			.handleException(new ServiceInstanceBindingExistsException("service-instance-id", "binding-id"));

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("serviceInstanceId=service-instance-id")
			.contains("bindingId=binding-id");
	}

	@Test
	void bindingDoesNotExistException() {
		ErrorMessage errorMessage = this.exceptionHandler
			.handleException(new ServiceInstanceBindingDoesNotExistException("binding-id"));

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains("id=binding-id");
	}

	@Test
	void appRequiredException() {
		ErrorMessage errorMessage = this.exceptionHandler
			.handleException(new ServiceBrokerBindingRequiresAppException("app GUID is required"));

		assertThat(errorMessage.getError()).isEqualTo(APP_REQUIRED_ERROR);
		assertThat(errorMessage.getMessage()).contains("app GUID is required");
	}

}
