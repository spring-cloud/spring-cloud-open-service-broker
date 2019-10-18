/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.model.error.ErrorMessage;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceBrokerWebMvcExceptionHandlerTest extends ServiceBrokerExceptionHandlerTest {

	@BeforeEach
	public void setUp() {
		exceptionHandler = new ServiceBrokerWebMvcExceptionHandler();
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
	public void stringParameterIsNotPresent() {
		final String parameterType = "String";
		final String parameterName = "plan_id";

		MissingServletRequestParameterException exception =
				new MissingServletRequestParameterException(parameterName, parameterType);

		ErrorMessage errorMessage = exceptionHandler.handleException(exception);

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).contains(parameterType);
		assertThat(errorMessage.getMessage()).contains(parameterName);
	}

}
