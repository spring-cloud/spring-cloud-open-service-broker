/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApiVersionInterceptorTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private BrokerApiVersion brokerApiVersion;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void noBrokerApiVersionConfigured() throws IOException, ServletException, ServiceBrokerApiVersionException {
		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(null);
		assertTrue(interceptor.preHandle(request, response, null));
	}

	@Test
	public void anyVersionAccepted() throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = BrokerApiVersion.API_VERSION_ANY;
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersion()).thenReturn(version);
		when(request.getHeader(header)).thenReturn("version");

		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		assertTrue(interceptor.preHandle(request, response, null));
		verify(brokerApiVersion, atLeastOnce()).getApiVersion();
	}

	@Test
	public void versionsMatch() throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = "version";
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersion()).thenReturn(version);
		when(request.getHeader(header)).thenReturn(version);

		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		assertTrue(interceptor.preHandle(request, response, null));
		verify(brokerApiVersion, atLeastOnce()).getApiVersion();
	}

	@Test(expected = ServiceBrokerApiVersionException.class)
	public void versionMismatch() throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = "version";
		String notVersion = "not_version";
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersion()).thenReturn(version);
		when(request.getHeader(header)).thenReturn(notVersion);

		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		interceptor.preHandle(request, response, null);
		verify(brokerApiVersion).getBrokerApiVersionHeader();
		verify(brokerApiVersion).getApiVersion();
	}

}
