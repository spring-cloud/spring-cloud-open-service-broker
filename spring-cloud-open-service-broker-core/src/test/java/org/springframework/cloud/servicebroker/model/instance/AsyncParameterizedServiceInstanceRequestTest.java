/*
 * Copyright 2002-2022 the original author or authors.
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

import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonPathAssert;
import org.springframework.cloud.servicebroker.JsonUtils;
import org.springframework.cloud.servicebroker.model.CloudFoundryContext;
import org.springframework.cloud.servicebroker.model.KubernetesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.CLOUD_FOUNDRY_PLATFORM;
import static org.springframework.cloud.servicebroker.model.KubernetesContext.KUBERNETES_PLATFORM;

class AsyncParameterizedServiceInstanceRequestTest {

	@Test
	void requestWithCloudFoundryContextIsDeserializedFromJson() {
		AsyncParameterizedServiceInstanceRequest request =
				JsonUtils.readTestDataFile("requestWithParametersAndCloudFoundryContext.json",
						CreateServiceInstanceRequest.class);

		assertThat(request.getContext().getPlatform()).isEqualTo(CLOUD_FOUNDRY_PLATFORM);
		assertThat(request.getContext()).isInstanceOf(CloudFoundryContext.class);

		CloudFoundryContext context = (CloudFoundryContext) request.getContext();
		assertThat(context.getOrganizationGuid()).isEqualTo("test-organization-guid");
		assertThat(context.getOrganizationName()).isEqualTo("test-organization-name");

		Map<String, Object> organizationAnnotations = context.getOrganizationAnnotations();
		assertThat(organizationAnnotations).hasSize(3);
		assertThat(organizationAnnotations.get("org_annotation1")).isEqualTo(2);
		assertThat(organizationAnnotations.get("org_annotation2")).isEqualTo("param-b");
		assertThat(organizationAnnotations.get("org_annotation3")).isEqualTo(true);

		assertThat(context.getSpaceGuid()).isEqualTo("test-space-guid");
		assertThat(context.getSpaceName()).isEqualTo("test-space-name");

		Map<String, Object> spaceAnnotations = context.getSpaceAnnotations();
		assertThat(spaceAnnotations).hasSize(3);
		assertThat(spaceAnnotations.get("space_annotation1")).isEqualTo(3);
		assertThat(spaceAnnotations.get("space_annotation2")).isEqualTo("param-c");
		assertThat(spaceAnnotations.get("space_annotation3")).isEqualTo(true);

		assertThat(context.getProperty("field1")).isEqualTo("data");
		assertThat(context.getProperty("field2")).isEqualTo(2);

		Map<String, Object> parameters = request.getParameters();
		assertThat(parameters).hasSize(3);
		assertThat(parameters.get("parameter1")).isEqualTo(1);
		assertThat(parameters.get("parameter2")).isEqualTo("param-a");
		assertThat(parameters.get("parameter3")).isEqualTo(true);
	}

	@Test
	void requestWithKubernetesContextIsDeserializedFromJson() {
		AsyncParameterizedServiceInstanceRequest request =
				JsonUtils.readTestDataFile("requestWithEmptyParametersAndKubernetesContext.json",
						CreateServiceInstanceRequest.class);

		assertThat(request.getContext().getPlatform()).isEqualTo(KUBERNETES_PLATFORM);
		assertThat(request.getContext()).isInstanceOf(KubernetesContext.class);

		KubernetesContext context = (KubernetesContext) request.getContext();
		assertThat(context.getNamespace()).isEqualTo("test-namespace");

		Map<String, Object> namespaceAnnotations = context.getNamespaceAnnotations();
		assertThat(namespaceAnnotations).hasSize(3);
		assertThat(namespaceAnnotations.get("namespace_annotation1")).isEqualTo(4);
		assertThat(namespaceAnnotations.get("namespace_annotation2")).isEqualTo("param-d");
		assertThat(namespaceAnnotations.get("namespace_annotation3")).isEqualTo(true);

		Map<String, Object> instanceAnnotations = context.getInstanceAnnotations();
		assertThat(instanceAnnotations).hasSize(3);
		assertThat(instanceAnnotations.get("instance_annotation1")).isEqualTo(5);
		assertThat(instanceAnnotations.get("instance_annotation2")).isEqualTo("param-e");
		assertThat(instanceAnnotations.get("instance_annotation3")).isEqualTo(true);

		assertThat(context.getInstanceName()).isEqualTo("test-instance-name");
		assertThat(context.getClusterid()).isEqualTo("test-clusterid");

		assertThat(context.getProperty("field1")).isEqualTo("data");
		assertThat(context.getProperty("field2")).isEqualTo(2);

		assertThat(request.getParameters()).hasSize(0);
	}

	@Test
	void requestWithUnknownContextIsDeserializedFromJson() {
		AsyncParameterizedServiceInstanceRequest request =
				JsonUtils.readTestDataFile("requestWithCustomContext.json",
						CreateServiceInstanceRequest.class);

		assertThat(request.getContext().getPlatform()).isEqualTo("test-platform");

		assertThat(request.getContext().getProperty("field1")).isEqualTo("data");
		assertThat(request.getContext().getProperty("field2")).isEqualTo(2);

		// missing parameters should result into empty response, same as in builder
		assertThat(request.getParameters()).isEmpty();
	}

	@Test
	void requestWithNoParametersIsSerializedWithoutParametersField() {
		CreateServiceInstanceRequest request = CreateServiceInstanceRequest.builder().build();
		DocumentContext json = JsonUtils.toJsonPath(request);
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters");
	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier
				.forClass(AsyncParameterizedServiceInstanceRequest.class)
				.withRedefinedSuperclass()
				.withRedefinedSubclass(CreateServiceInstanceRequest.class)
				.withRedefinedSubclass(UpdateServiceInstanceRequest.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.suppress(Warning.TRANSIENT_FIELDS)
				.verify();
	}

}
