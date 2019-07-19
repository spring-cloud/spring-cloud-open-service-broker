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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.servicebroker.annotation.ServiceBrokerRestController;
import org.springframework.cloud.servicebroker.model.error.ErrorMessage;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception handling logic shared by Web MVC Controllers.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 */
@ControllerAdvice(annotations = ServiceBrokerRestController.class)
@ResponseBody
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class ServiceBrokerWebMvcExceptionHandler extends ServiceBrokerExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceBrokerWebMvcExceptionHandler.class);

	@Override
	protected Logger getLog() {
		return LOG;
	}

	/**
	 * Handle a {@link MethodArgumentNotValidException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleException(MethodArgumentNotValidException ex) {
		return handleBindingException(ex, ex.getBindingResult());
	}

	/**
	 * Handle a {@link MissingServletRequestParameterException}
	 *
	 * @param ex the exception
	 * @return an error message
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleException(MissingServletRequestParameterException ex) {
		LOG.error(UNPROCESSABLE_REQUEST, ex);
		return getErrorResponse(ex.getMessage());
	}
}
