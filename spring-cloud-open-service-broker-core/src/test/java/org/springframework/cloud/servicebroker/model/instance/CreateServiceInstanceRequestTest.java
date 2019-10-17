/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.cloud.servicebroker.model.instance;

import java.util.HashMap;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonPathAssert;
import org.springframework.cloud.servicebroker.JsonUtils;
import org.springframework.cloud.servicebroker.model.CloudFoundryContext;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.PlatformContext;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateServiceInstanceRequestTest {

	@Test
	public void requestWithDefaultsIsBuilt() {
		CreateServiceInstanceRequest request = CreateServiceInstanceRequest.builder()
				.build();

		assertThat(request.getServiceDefinitionId()).isNull();
		assertThat(request.getServiceInstanceId()).isNull();
		assertThat(request.getPlanId()).isNull();
		assertThat(request.getServiceDefinition()).isNull();
		assertThat(request.getContext()).isNull();
		assertThat(request.getParameters()).hasSize(0);
		assertThat(request.isAsyncAccepted()).isEqualTo(false);
		assertThat(request.getApiInfoLocation()).isNull();
		assertThat(request.getPlatformInstanceId()).isNull();
		assertThat(request.getOriginatingIdentity()).isNull();
	}

	@Test
	@SuppressWarnings("serial")
	public void requestWithAllValuesIsBuilt() {
		Map<String, Object> parameters = new HashMap<String, Object>() {{
			put("field4", "value4");
			put("field5", "value5");
		}};
		Context context = PlatformContext.builder().build();

		Context originatingIdentity = PlatformContext.builder()
				.platform("test-platform")
				.build();

		CreateServiceInstanceRequest request = CreateServiceInstanceRequest.builder()
				.serviceInstanceId("service-instance-id")
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.context(context)
				.parameters("field1", "value1")
				.parameters("field2", 2)
				.parameters("field3", true)
				.parameters(parameters)
				.asyncAccepted(true)
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("https://api.example.com")
				.originatingIdentity(originatingIdentity)
				.build();

		assertThat(request.getServiceInstanceId()).isEqualTo("service-instance-id");
		assertThat(request.getServiceDefinitionId()).isEqualTo("service-definition-id");
		assertThat(request.getPlanId()).isEqualTo("plan-id");

		assertThat(request.getParameters()).hasSize(5);
		assertThat(request.getParameters().get("field1")).isEqualTo("value1");
		assertThat(request.getParameters().get("field2")).isEqualTo(2);
		assertThat(request.getParameters().get("field3")).isEqualTo(true);
		assertThat(request.getParameters().get("field4")).isEqualTo("value4");
		assertThat(request.getParameters().get("field5")).isEqualTo("value5");

		assertThat(request.getContext()).isEqualTo(context);
		assertThat(request.isAsyncAccepted()).isEqualTo(true);

		assertThat(request.getPlatformInstanceId()).isEqualTo("platform-instance-id");
		assertThat(request.getApiInfoLocation()).isEqualTo("https://api.example.com");
		assertThat(request.getOriginatingIdentity()).isEqualTo(originatingIdentity);
	}

	@Test
	@SuppressWarnings("serial")
	public void serializesAccordingToOsbSpecs() {
		Map<String, Object> parameters = new HashMap<String, Object>() {
			{
				put("field4", "value4");
				put("field5", "value5");
			}
		};
		Context context = CloudFoundryContext.builder()
				.organizationGuid("org-guid")
				.spaceGuid("space-guid").build();

		Context originatingIdentity = CloudFoundryContext.builder()
				.property("user_id", "user-id").build();

		CreateServiceInstanceRequest request = CreateServiceInstanceRequest.builder()
				.serviceInstanceId("service-instance-id")
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.context(context)
					.parameters("field1", "value1")
					.parameters("field2", 2)
					.parameters("field3", true)
				    .parameters(parameters)
				.asyncAccepted(true)
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("https://api.example.com")
				.originatingIdentity(originatingIdentity)
				.plan(Plan.builder().build())
				.serviceDefinition(ServiceDefinition.builder().build())
				.build();

		DocumentContext json = JsonUtils.toJsonPath(request);

		// 6 OSB Fields should be present
		JsonPathAssert.assertThat(json).hasPath("$.plan_id").isEqualTo("plan-id");
		JsonPathAssert.assertThat(json).hasPath("$.service_id").isEqualTo("service-definition-id");
		JsonPathAssert.assertThat(json).hasMapAtPath("$.parameters").hasSize(5);
		JsonPathAssert.assertThat(json).hasPath("$.parameters.field1").isEqualTo("value1");
		JsonPathAssert.assertThat(json).hasMapAtPath("$.context").hasSize(3);
		JsonPathAssert.assertThat(json).hasPath("$.space_guid").isEqualTo("space-guid");
		JsonPathAssert.assertThat(json).hasPath("$.organization_guid").isEqualTo("org-guid");


		// fields mapped outside of json body (typically http headers or request paths)
		// should be excluded
		JsonPathAssert.assertThat(json).hasMapAtPath("$").hasSize(6);
	}

	@Test
	@SuppressWarnings("serial")
	public void serializesWithoutContextAccordingToOsbSpecs() {
		CreateServiceInstanceRequest request = CreateServiceInstanceRequest.builder()
				.serviceInstanceId("service-instance-id")
				.serviceDefinitionId("service-definition-id").planId("plan-id").build();

		DocumentContext json = JsonUtils.toJsonPath(request);

		// 4 OSB Fields should be present
		JsonPathAssert.assertThat(json).hasPath("$.plan_id").isEqualTo("plan-id");
		JsonPathAssert.assertThat(json).hasPath("$.service_id").isEqualTo("service-definition-id");
		JsonPathAssert.assertThat(json).hasPath("$.space_guid").isEqualTo("default-undefined-value");
		JsonPathAssert.assertThat(json).hasPath("$.organization_guid").isEqualTo("default-undefined-value");


		// fields mapped outside of json body (typically http headers or request paths)
		// should be excluded
		JsonPathAssert.assertThat(json).hasMapAtPath("$").hasSize(4);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void requestIsDeserializedFromJson() {
		CreateServiceInstanceRequest request =
				JsonUtils.readTestDataFile("createRequest.json",
						CreateServiceInstanceRequest.class);

		assertThat(request.getServiceDefinitionId()).isEqualTo("test-service-id");
		assertThat(request.getPlanId()).isEqualTo("test-plan-id");
		assertThat(request.getOrganizationGuid()).isEqualTo("test-organization-guid");
		assertThat(request.getSpaceGuid()).isEqualTo("test-space-guid");
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(CreateServiceInstanceRequest.class)
				.withRedefinedSuperclass()
				.suppress(Warning.NONFINAL_FIELDS)
				.suppress(Warning.TRANSIENT_FIELDS)
				.verify();
	}
}
