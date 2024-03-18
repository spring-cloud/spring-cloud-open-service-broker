/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonUtils;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.PlatformContext;
import org.springframework.cloud.servicebroker.model.catalog.MaintenanceInfo;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest.PreviousValues;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateServiceInstanceRequestTest {

	@Test
	void requestWithDefaultsIsBuilt() {
		UpdateServiceInstanceRequest request = UpdateServiceInstanceRequest.builder()
				.build();

		assertThat(request.getServiceDefinitionId()).isNull();
		assertThat(request.getServiceInstanceId()).isNull();
		assertThat(request.getPlanId()).isNull();
		assertThat(request.getServiceDefinition()).isNull();
		assertThat(request.getContext()).isNull();
		assertThat(request.getParameters()).hasSize(0);
		assertThat(request.isAsyncAccepted()).isEqualTo(false);
		assertThat(request.getPreviousValues()).isNull();
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

		UpdateServiceInstanceRequest request = UpdateServiceInstanceRequest.builder()
				.serviceInstanceId("service-instance-id")
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.previousValues(PreviousValues.builder()
						.serviceDefinitionId("previous-service-definition-id")
						.planId("previous-plan-id")
						.organizationId("previous-organization-id")
						.spaceId("previous-space-id")
						.maintenanceInfo(MaintenanceInfo.builder()
								.version("1.1.0")
								.description("Patch for CVE-x")
								.build())
						.build())
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
				.maintenanceInfo(new MaintenanceInfo("2.0.0", null))
				.build();

		assertThat(request.getServiceInstanceId()).isEqualTo("service-instance-id");
		assertThat(request.getServiceDefinitionId()).isEqualTo("service-definition-id");
		assertThat(request.getPlanId()).isEqualTo("plan-id");

		assertThat(request.getPreviousValues().getServiceDefinitionId()).isEqualTo("previous-service-definition-id");
		assertThat(request.getPreviousValues().getPlanId()).isEqualTo("previous-plan-id");
		assertThat(request.getPreviousValues().getOrganizationId()).isEqualTo("previous-organization-id");
		assertThat(request.getPreviousValues().getSpaceId()).isEqualTo("previous-space-id");
		assertThat(request.getPreviousValues().getMaintenanceInfo()).isEqualTo(
			new MaintenanceInfo("1.1.0", "Patch for CVE-x"));

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
		assertThat(request.getMaintenanceInfo().getVersion()).isEqualTo("2.0.0");
		assertThat(request.getMaintenanceInfo().getDescription()).isNull();
	}

	@Test
	void requestIsDeserializedFromJson() {
		UpdateServiceInstanceRequest request =
				JsonUtils.readTestDataFile("updateRequest.json",
						UpdateServiceInstanceRequest.class);

		assertThat(request.getServiceDefinitionId()).isEqualTo("test-service-id");
		assertThat(request.getPlanId()).isEqualTo("test-plan-id");
		assertThat(request.getPreviousValues().getServiceDefinitionId()).isEqualTo("previous-service-definition-id");
		assertThat(request.getPreviousValues().getPlanId()).isEqualTo("previous-plan-id");
		assertThat(request.getPreviousValues().getOrganizationId()).isEqualTo("previous-organization-id");
		assertThat(request.getPreviousValues().getSpaceId()).isEqualTo("previous-space-id");
		assertThat(request.getPreviousValues().getMaintenanceInfo()).isEqualTo(new MaintenanceInfo("1.1.0", "Patch for CVE-x"));
		assertThat(request.getMaintenanceInfo().getVersion()).isEqualTo("2.0.0");
		assertThat(request.getMaintenanceInfo().getDescription()).isNull();
	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier
				.forClass(UpdateServiceInstanceRequest.class)
				.withRedefinedSuperclass()
				.suppress(Warning.NONFINAL_FIELDS)
				.suppress(Warning.TRANSIENT_FIELDS)
				.verify();
	}

}
