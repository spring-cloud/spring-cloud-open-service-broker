/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.cloud.servicebroker.autoconfigure.web.ApiVersionInterceptor;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ApiVersionInterceptorTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void noBrokerApiVersionConfigured() {
		ApiVersionInterceptor interceptor = new ApiVersionInterceptor();
		assertTrue(interceptor.preHandle(request, response, null));
	}

	@Test
	public void anyVersionAccepted() {
		BrokerApiVersion brokerApiVersion = new BrokerApiVersion("header", BrokerApiVersion.API_VERSION_ANY);
		when(request.getHeader("header")).thenReturn("9.9");

		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		assertTrue(interceptor.preHandle(request, response, null));
	}

	@Test
	public void versionsMatch() {
		BrokerApiVersion brokerApiVersion = new BrokerApiVersion("header", "9.9");
		when(request.getHeader("header")).thenReturn("9.9");

		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		assertTrue(interceptor.preHandle(request, response, null));
	}

	@Test(expected = ServiceBrokerApiVersionException.class)
	public void versionMismatch() {
		BrokerApiVersion brokerApiVersion = new BrokerApiVersion("header", "9.9");
		when(request.getHeader("header")).thenReturn("8.8");

		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		interceptor.preHandle(request, response, null);
	}

}
