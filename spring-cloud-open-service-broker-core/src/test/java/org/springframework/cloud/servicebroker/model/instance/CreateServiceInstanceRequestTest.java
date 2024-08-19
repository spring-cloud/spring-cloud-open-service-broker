/*
 * Copyright 2002-2023 the original author or authors.
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
import org.springframework.cloud.servicebroker.model.catalog.MaintenanceInfo;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class CreateServiceInstanceRequestTest {

	@Test
	void requestWithDefaultsIsBuilt() {
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
		assertThat(request.getRequestIdentity()).isNull();
		assertThat(request.getMaintenanceInfo()).isNull();
	}

	@Test
	void requestWithAllValuesIsBuilt() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("field4", "value4");
		parameters.put("field5", "value5");

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
				.apiInfoLocation("https://api.app.local")
				.originatingIdentity(originatingIdentity)
				.requestIdentity("request-id")
				.maintenanceInfo(
					new MaintenanceInfo("1.1.0", "Patch for CVE-x"))
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
		assertThat(request.getApiInfoLocation()).isEqualTo("https://api.app.local");
		assertThat(request.getOriginatingIdentity()).isEqualTo(originatingIdentity);
		assertThat(request.getRequestIdentity()).isEqualTo("request-id");
		assertThat(request.getMaintenanceInfo()).isEqualTo(
			new MaintenanceInfo("1.1.0", "Patch for CVE-x"));
	}

	@Test
	void serializesAccordingToOsbSpecs() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("field4", "value4");
		parameters.put("field5", "value5");

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
				.apiInfoLocation("https://api.app.local")
				.originatingIdentity(originatingIdentity)
				.requestIdentity("request-id")
				.plan(Plan.builder().build())
				.serviceDefinition(ServiceDefinition.builder().build())
				.maintenanceInfo(
					new MaintenanceInfo("1.1.0", "Patch for CVE-x"))
				.build();

		DocumentContext json = JsonUtils.toJsonPath(request);

		// 6 OSB Fields should be present
		JsonPathAssert.assertThat(json).hasPath("$.plan_id").isEqualTo("plan-id");
		JsonPathAssert.assertThat(json).hasPath("$.service_id").isEqualTo("service-definition-id");
		JsonPathAssert.assertThat(json).hasMapAtPath("$.parameters").hasSize(5);
		JsonPathAssert.assertThat(json).hasPath("$.parameters.field1").isEqualTo("value1");
		JsonPathAssert.assertThat(json).hasMapAtPath("$.context").hasSize(3);
		JsonPathAssert.assertThat(json).hasMapAtPath("$.maintenance_info").hasSize(2);
		JsonPathAssert.assertThat(json).hasPath("$.maintenance_info.version").isEqualTo("1.1.0");
		JsonPathAssert.assertThat(json).hasPath("$.maintenance_info.description").isEqualTo("Patch for CVE-x");
		JsonPathAssert.assertThat(json).hasPath("$.space_guid").isEqualTo("space-guid");
		JsonPathAssert.assertThat(json).hasPath("$.organization_guid").isEqualTo("org-guid");
		JsonPathAssert.assertThat(json).hasNoPath("$.request_identity");


		// fields mapped outside of json body (typically http headers or request paths)
		// should be excluded
		JsonPathAssert.assertThat(json).hasMapAtPath("$").hasSize(7);
	}

	@Test
	void serializesWithoutContextAccordingToOsbSpecs() {
		CreateServiceInstanceRequest request = CreateServiceInstanceRequest.builder()
				.serviceInstanceId("service-instance-id")
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.build();

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
	void requestIsDeserializedFromJson() {
		CreateServiceInstanceRequest request =
				JsonUtils.readTestDataFile("createRequest.json",
						CreateServiceInstanceRequest.class);

		assertThat(request.getServiceDefinitionId()).isEqualTo("test-service-id");
		assertThat(request.getPlanId()).isEqualTo("test-plan-id");
		assertThat(request.getOrganizationGuid()).isEqualTo("test-organization-guid");
		assertThat(request.getSpaceGuid()).isEqualTo("test-space-guid");
	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier
				.forClass(CreateServiceInstanceRequest.class)
				.withRedefinedSuperclass()
				.suppress(Warning.NONFINAL_FIELDS)
				.suppress(Warning.TRANSIENT_FIELDS)
				.suppress(Warning.STRICT_INHERITANCE)
				.verify();
	}

}
