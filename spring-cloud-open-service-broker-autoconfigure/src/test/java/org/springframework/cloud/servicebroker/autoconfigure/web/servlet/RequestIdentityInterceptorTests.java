/*
 * Copyright 2002-2023 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class RequestIdentityInterceptorTests {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@BeforeEach
	void setUp() {
		openMocks(this);
	}

	@Test
	void defaultBehaviorReturnsTrue() {
		RequestIdentityInterceptor interceptor = new RequestIdentityInterceptor();
		assertThat(interceptor.preHandle(this.request, this.response, null)).isTrue();
	}

	@Test
	void requestIdentityHeader() {
		given(this.request.getHeader(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER)).willReturn("request-id");
		RequestIdentityInterceptor interceptor = new RequestIdentityInterceptor();
		assertThat(interceptor.preHandle(this.request, this.response, null)).isTrue();
		verify(this.response, times(1)).addHeader(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER, "request-id");
	}

	@Test
	void requestIdentityHeaderIsMissing() {
		given(this.request.getHeader(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER)).willReturn(null);
		RequestIdentityInterceptor interceptor = new RequestIdentityInterceptor();
		assertThat(interceptor.preHandle(this.request, this.response, null)).isTrue();
		assertThat(this.response.getHeader(ServiceBrokerRequest.REQUEST_IDENTITY_HEADER)).isNull();
	}

}
