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

package org.springframework.cloud.servicebroker.controller;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.PlatformContext;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.JsonUtils;
import org.springframework.cloud.servicebroker.service.CatalogService;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public abstract class ControllerRequestTest {
	@Mock
	protected CatalogService catalogService;

	protected ServiceDefinition serviceDefinition;

	protected Context identityContext;
	protected Context requestContext;
	
	protected Map<String, String> pathVariables = Collections.singletonMap("platformInstanceId", "platform-instance-id");

	@Before
	public void setUpControllerRequestTest() {
		initMocks(this);

		serviceDefinition = ServiceDefinition.builder()
				.id("service-definition-id")
				.plans(Plan.builder()
						.id("plan-id")
						.build())
				.build();

		when(catalogService.getServiceDefinition("service-definition-id"))
				.thenReturn(serviceDefinition);

		identityContext = PlatformContext.builder()
				.platform("test-platform")
				.property("user", "user-id")
				.build();

		requestContext = PlatformContext.builder()
				.platform("test-platform")
				.property("request-property", "value")
				.build();
	}

	protected String encodeOriginatingIdentity(Context context) {
		Map<String, Object> properties = context.getProperties();
		String propertiesJson = JsonUtils.toJson(properties);

		return context.getPlatform() +
				" " +
				Base64.getEncoder().encodeToString(propertiesJson.getBytes());
	}
}
