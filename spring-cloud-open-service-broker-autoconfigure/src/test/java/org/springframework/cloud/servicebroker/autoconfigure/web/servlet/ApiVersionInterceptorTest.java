/*
 * Copyright 2002-2022 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionMissingException;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

class ApiVersionInterceptorTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@BeforeEach
	void setUp() {
		openMocks(this);
	}

	@Test
	void noBrokerApiVersionConfigured() {
		ApiVersionInterceptor interceptor = new ApiVersionInterceptor();
		assertThat(interceptor.preHandle(request, response, null)).isTrue();
	}

	@Test
	void anyVersionAccepted() {
		BrokerApiVersion brokerApiVersion = new BrokerApiVersion("header", BrokerApiVersion.API_VERSION_ANY);
		given(request.getHeader("header")).willReturn("9.9");

		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		assertThat(interceptor.preHandle(request, response, null)).isTrue();
	}

	@Test
	void versionsMatch() {
		BrokerApiVersion brokerApiVersion = new BrokerApiVersion("header", "9.9");
		given(request.getHeader("header")).willReturn("9.9");

		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		assertThat(interceptor.preHandle(request, response, null)).isTrue();
	}

	@Test
	void versionMismatch() {
		BrokerApiVersion brokerApiVersion = new BrokerApiVersion("header", "9.9");
		given(request.getHeader("header")).willReturn("8.8");
		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		assertThrows(ServiceBrokerApiVersionException.class, () ->
				interceptor.preHandle(request, response, null));
	}

	@Test
	void versionHeaderIsMissing() {
		BrokerApiVersion brokerApiVersion = new BrokerApiVersion("header", "9.9");
		given(request.getHeader("header")).willReturn(null);
		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		assertThrows(ServiceBrokerApiVersionMissingException.class, () ->
				interceptor.preHandle(request, response, null));
	}

	@Test
	void versionHeaderIsMissingAnyVersionAccepted() {
		BrokerApiVersion brokerApiVersion = new BrokerApiVersion("header", BrokerApiVersion.API_VERSION_ANY);
		given(request.getHeader("header")).willReturn(null);
		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		assertThat(interceptor.preHandle(request, response, null)).isTrue();
	}

}
