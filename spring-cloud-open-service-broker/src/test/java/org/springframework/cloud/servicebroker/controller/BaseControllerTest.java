/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.error.AsyncRequiredErrorMessage;
import org.springframework.cloud.servicebroker.model.error.ErrorMessage;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.model.error.AsyncRequiredErrorMessage.ASYNC_REQUIRED_ERROR;

public class BaseControllerTest {
	private BaseController controller;

	@Before
	public void setUp() {
		controller = new BaseController(null);
	}

	@Test(expected = HttpMessageNotReadableException.class)
	public void originatingIdentityWithNoPropertiesThrowsException() {
		ServiceBrokerRequest request = new ServiceBrokerRequest() {
		};

		TestBaseController controller = new TestBaseController(request);

		controller.testOriginatingIdentity("platform");
	}

	@Test(expected = HttpMessageNotReadableException.class)
	public void originatingIdentityWithNonEncodedPropertiesThrowsException() {
		ServiceBrokerRequest request = new ServiceBrokerRequest() {
		};

		TestBaseController controller = new TestBaseController(request);

		controller.testOriginatingIdentity("platform some-properties");
	}

	@Test(expected = HttpMessageNotReadableException.class)
	public void originatingIdentityWithNonJsonPropertiesThrowsException() {
		ServiceBrokerRequest request = new ServiceBrokerRequest() {
		};

		TestBaseController controller = new TestBaseController(request);

		String encodedProperties = Base64.getEncoder().encodeToString("some-properties".getBytes());

		controller.testOriginatingIdentity("platform " + encodedProperties);
	}

	@Test
	public void serviceBrokerApiVersionExceptionGivesExpectedStatus() {
		ServiceBrokerApiVersionException exception =
				new ServiceBrokerApiVersionException("expected-version", "actual-version");

		ResponseEntity<ErrorMessage> responseEntity = controller.handleException(exception);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
		assertThat(responseEntity.getBody().getMessage()).contains("expected version=expected-version");
		assertThat(responseEntity.getBody().getMessage()).contains("provided version=actual-version");
	}

	@Test
	public void serviceInstanceDoesNotExistExceptionGivesExpectedStatus() {
		ServiceInstanceDoesNotExistException exception =
				new ServiceInstanceDoesNotExistException("service-instance-id");

		ResponseEntity<ErrorMessage> responseEntity = controller.handleException(exception);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(responseEntity.getBody().getMessage()).contains("id=service-instance-id");
	}

	@Test
	public void serviceDefinitionDoesNotExistExceptionGivesExpectedStatus() {
		ServiceDefinitionDoesNotExistException exception =
				new ServiceDefinitionDoesNotExistException("service-definition-id");

		ResponseEntity<ErrorMessage> responseEntity = controller.handleException(exception);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(responseEntity.getBody().getMessage()).contains("id=service-definition-id");
	}

	@Test
	public void serviceBrokerAsyncRequiredExceptionGivesExpectedStatus() {
		ServiceBrokerAsyncRequiredException exception =
				new ServiceBrokerAsyncRequiredException("test message");

		ResponseEntity<AsyncRequiredErrorMessage> responseEntity = controller.handleException(exception);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(responseEntity.getBody().getMessage()).contains("test message");
		assertThat(responseEntity.getBody().getError()).isEqualTo(ASYNC_REQUIRED_ERROR);
	}

	@Test
	public void serviceBrokerInvalidParametersExceptionGivesExpectedStatus() {
		ServiceBrokerInvalidParametersException exception =
				new ServiceBrokerInvalidParametersException("test message");

		ResponseEntity<ErrorMessage> responseEntity = controller.handleException(exception);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(responseEntity.getBody().getMessage()).contains("test message");
	}

	@Test
	public void httpMessageNotReadableExceptionGivesExpectedStatus() {
		HttpMessageNotReadableException exception =
				new HttpMessageNotReadableException("test message");

		ResponseEntity<ErrorMessage> responseEntity = controller.handleException(exception);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(responseEntity.getBody().getMessage()).contains("test message");
	}

	@Test
	public void operationInProgressExceptionGivesExpectedStatus() {
		ServiceBrokerOperationInProgressException exception =
				new ServiceBrokerOperationInProgressException("still working");

		ResponseEntity<ErrorMessage> responseEntity = controller.handleException(exception);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseEntity.getBody().getMessage()).contains("still working");
	}

	@Test
	public void unknownExceptionGivesExpectedStatus() {
		Exception exception = new Exception("test message");

		ResponseEntity<ErrorMessage> responseEntity = controller.handleException(exception);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(responseEntity.getBody().getMessage()).contains("test message");
	}

	@Test
	public void methodArgumentNotValidExceptionGivesExpectedStatus() throws NoSuchMethodException {
		BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "objectName");
		bindingResult.addError(new FieldError("objectName", "field1", "message"));
		bindingResult.addError(new FieldError("objectName", "field2", "message"));

		Method method = this.getClass().getMethod("setUp", (Class<?>[]) null);
		MethodParameter parameter = new MethodParameter(method, -1);
		
		MethodArgumentNotValidException exception =
				new MethodArgumentNotValidException(parameter, bindingResult);

		ResponseEntity<ErrorMessage> responseEntity = controller.handleException(exception);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(responseEntity.getBody().getMessage()).contains("field1");
		assertThat(responseEntity.getBody().getMessage()).contains("field2");
	}

	private static class TestBaseController extends BaseController {
		private final ServiceBrokerRequest request;

		public TestBaseController(ServiceBrokerRequest request) {
			super(null);
			this.request = request;
		}

		public void testOriginatingIdentity(String originatingIdentityString) {
			setCommonRequestFields(request, "platform-instance-id", "api-info-location", originatingIdentityString);
		}
	}
}
