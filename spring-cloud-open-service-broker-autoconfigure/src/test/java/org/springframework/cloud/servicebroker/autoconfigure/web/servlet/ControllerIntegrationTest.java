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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.mockito.Mock;

import org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Base64Utils;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public abstract class ControllerIntegrationTest {
	protected static final String API_INFO_LOCATION = "https://api.cf.example.com";
	protected static final String ORIGINATING_IDENTITY_PLATFORM = "test-platform";
	protected static final String ORIGINATING_USER_KEY = "user_id";
	protected static final String ORIGINATING_USER_VALUE = "user_id";
	protected static final String ORIGINATING_EMAIL_KEY = "email";
	protected static final String ORIGINATING_EMAIL_VALUE = "user@example.com";
	protected static final String CF_INSTANCE_ID = "cf-abc";
	protected static final String SERVICE_INSTANCE_ID = "service-instance-one-id";

	@Mock
	protected CatalogService catalogService;

	protected ServiceDefinition serviceDefinition;

	@Before
	public void setUpControllerIntegrationTest() {
		serviceDefinition = ServiceFixture.getSimpleService();
	}

	protected void setupCatalogService() {
		when(catalogService.getServiceDefinition(eq(serviceDefinition.getId())))
				.thenReturn(serviceDefinition);
	}

	protected void setupCatalogService(ServiceDefinition serviceDefinition) {
		when(catalogService.getServiceDefinition(eq(this.serviceDefinition.getId())))
				.thenReturn(serviceDefinition);
	}

	protected String buildOriginatingIdentityHeader() throws JsonProcessingException {
		Map<String, Object> propMap = new HashMap<>();
		propMap.put(ORIGINATING_USER_KEY, ORIGINATING_USER_VALUE);
		propMap.put(ORIGINATING_EMAIL_KEY, ORIGINATING_EMAIL_VALUE);
		ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
		String properties = mapper.writeValueAsString(propMap);
		String encodedProperties = new String(Base64Utils.encode(properties.getBytes()));
		return ORIGINATING_IDENTITY_PLATFORM + " " + encodedProperties;
	}

	protected void assertHeaderValuesSet(ServiceBrokerRequest actualRequest) {
		assertThat(actualRequest.getCfInstanceId(), equalTo(CF_INSTANCE_ID));
		assertThat(actualRequest.getApiInfoLocation(), equalTo(API_INFO_LOCATION));

		assertThat(actualRequest.getOriginatingIdentity(), notNullValue());
		Context identity = actualRequest.getOriginatingIdentity();
		assertThat(identity.getPlatform(), equalTo(ORIGINATING_IDENTITY_PLATFORM));
		assertThat(identity.getProperty(ORIGINATING_USER_KEY), equalTo(ORIGINATING_USER_VALUE));
		assertThat(identity.getProperty(ORIGINATING_EMAIL_KEY), equalTo(ORIGINATING_EMAIL_VALUE));
	}

	protected void assertHeaderValuesNotSet(ServiceBrokerRequest actualRequest) {
		assertNull(actualRequest.getApiInfoLocation());
		assertNull(actualRequest.getCfInstanceId());
		assertNull(actualRequest.getOriginatingIdentity());
	}
}
