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

package org.springframework.cloud.servicebroker.autoconfigure.web;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.mockito.Mock;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.autoconfigure.web.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.util.Base64Utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

public abstract class ControllerIntegrationTest {

	protected static final String API_INFO_LOCATION = "https://api.platform.example.com";

	protected static final String ORIGINATING_IDENTITY_PLATFORM = "test-platform";

	protected static final String ORIGINATING_USER_KEY = "user_id";

	protected static final String ORIGINATING_USER_VALUE = "user_id";

	protected static final String ORIGINATING_EMAIL_KEY = "email";

	protected static final String ORIGINATING_EMAIL_VALUE = "user@example.com";

	protected static final String PLATFORM_INSTANCE_ID = "platform-abc";

	protected static final String SERVICE_INSTANCE_ID = "service-instance-one-id";

	@Mock
	protected CatalogService catalogService;

	protected ServiceDefinition serviceDefinition;

	@Before
	public void setUpControllerIntegrationTest() {
		serviceDefinition = ServiceFixture.getSimpleService();
	}

	protected void setupCatalogService() {
		when(catalogService.getServiceDefinition(isNull()))
				.thenReturn(Mono.empty());
		when(catalogService.getServiceDefinition(eq(serviceDefinition.getId())))
				.thenReturn(Mono.just(serviceDefinition));
	}

	protected void setupCatalogService(ServiceDefinition serviceDefinition) {
		Mono<ServiceDefinition> serviceDefinitionMono;
		if (serviceDefinition == null) {
			serviceDefinitionMono = Mono.empty();
		}
		else {
			serviceDefinitionMono = Mono.just(serviceDefinition);
		}
		when(catalogService.getServiceDefinition(isNull()))
				.thenReturn(Mono.empty());
		when(catalogService.getServiceDefinition(eq(this.serviceDefinition.getId())))
				.thenReturn(serviceDefinitionMono);
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
		assertThat(actualRequest.getPlatformInstanceId()).isEqualTo(PLATFORM_INSTANCE_ID);
		assertThat(actualRequest.getApiInfoLocation()).isEqualTo(API_INFO_LOCATION);

		assertThat(actualRequest.getOriginatingIdentity()).isNotNull();

		Context identity = actualRequest.getOriginatingIdentity();
		assertThat(identity.getPlatform()).isEqualTo(ORIGINATING_IDENTITY_PLATFORM);
		assertThat(identity.getProperty(ORIGINATING_USER_KEY)).isEqualTo(ORIGINATING_USER_VALUE);
		assertThat(identity.getProperty(ORIGINATING_EMAIL_KEY)).isEqualTo(ORIGINATING_EMAIL_VALUE);
	}

	protected void assertHeaderValuesNotSet(ServiceBrokerRequest actualRequest) {
		assertThat(actualRequest.getApiInfoLocation()).isNull();
		assertThat(actualRequest.getPlatformInstanceId()).isNull();
		assertThat(actualRequest.getOriginatingIdentity()).isNull();
	}

	protected void assertDescriptionContains(EntityExchangeResult<byte[]> result, String value) {
		String responseBody = new String(result.getResponseBody(), Charset.forName("UTF-8"));
		String description = JsonPath.read(responseBody, "$.description");
		assertThat(description).contains(value);
	}

}
