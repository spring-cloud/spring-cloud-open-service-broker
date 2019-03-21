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

import org.junit.Before;

import org.springframework.cloud.servicebroker.JsonUtils;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class ServiceInstanceBindingIntegrationTest extends ControllerIntegrationTest {
	public static final String SERVICE_INSTANCE_BINDING_ID = "service-instance-binding-id";
	
	protected static final String SERVICE_INSTANCES_ROOT_PATH = "/v2/service_instances/";

	protected String createRequestBody;

	@Before
	public void setupBase() {
		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder()
				.serviceDefinitionId(serviceDefinition.getId())
				.planId("standard")
				.build();

		createRequestBody = JsonUtils.toJson(request);
	}

	protected String buildCreateUrl() {
		return buildCreateUrl(null);
	}

	protected String buildCreateUrl(String platformInstanceId) {
		return buildBaseUrl(platformInstanceId).toUriString();
	}

	protected String buildDeleteUrl() {
		return buildDeleteUrl(null);
	}

	protected String buildDeleteUrl(String platformInstanceId) {
		return buildBaseUrl(platformInstanceId)
				.queryParam("service_id", serviceDefinition.getId())
				.queryParam("plan_id", serviceDefinition.getPlans().get(0).getId())
				.toUriString();
	}

	private UriComponentsBuilder buildBaseUrl(String platformInstanceId) {
		return UriComponentsBuilder.fromPath("/")
				.pathSegment(platformInstanceId)
				.path(SERVICE_INSTANCES_ROOT_PATH)
				.pathSegment(SERVICE_INSTANCE_ID, "service_bindings", SERVICE_INSTANCE_BINDING_ID);
	}
}
