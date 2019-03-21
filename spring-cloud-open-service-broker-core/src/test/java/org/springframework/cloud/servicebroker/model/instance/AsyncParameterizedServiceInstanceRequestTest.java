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

package org.springframework.cloud.servicebroker.model.instance;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.springframework.cloud.servicebroker.model.CloudFoundryContext;
import org.springframework.cloud.servicebroker.model.KubernetesContext;
import org.springframework.cloud.servicebroker.JsonUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.CLOUD_FOUNDRY_PLATFORM;
import static org.springframework.cloud.servicebroker.model.KubernetesContext.KUBERNETES_PLATFORM;

public class AsyncParameterizedServiceInstanceRequestTest {
	@Test
	public void requestWithCloudFoundryContextIsDeserializedFromJson() {
		AsyncParameterizedServiceInstanceRequest request =
				JsonUtils.readTestDataFile("requestWithParametersAndCloudFoundryContext.json",
						CreateServiceInstanceRequest.class);

		assertThat(request.getContext().getPlatform()).isEqualTo(CLOUD_FOUNDRY_PLATFORM);
		assertThat(request.getContext()).isInstanceOf(CloudFoundryContext.class);

		CloudFoundryContext context = (CloudFoundryContext) request.getContext();
		assertThat(context.getOrganizationGuid()).isEqualTo("test-organization-guid");
		assertThat(context.getSpaceGuid()).isEqualTo("test-space-guid");
		assertThat(context.getProperty("field1")).isEqualTo("data");
		assertThat(context.getProperty("field2")).isEqualTo(2);

		Map<String, Object> parameters = request.getParameters();
		assertThat(parameters).hasSize(3);
		assertThat(parameters.get("parameter1")).isEqualTo(1);
		assertThat(parameters.get("parameter2")).isEqualTo("foo");
		assertThat(parameters.get("parameter3")).isEqualTo(true);
	}

	@Test
	public void requestWithKubernetesContextIsDeserializedFromJson() {
		AsyncParameterizedServiceInstanceRequest request =
				JsonUtils.readTestDataFile("requestWithEmptyParametersAndKubernetesContext.json",
						CreateServiceInstanceRequest.class);

		assertThat(request.getContext().getPlatform()).isEqualTo(KUBERNETES_PLATFORM);
		assertThat(request.getContext()).isInstanceOf(KubernetesContext.class);

		KubernetesContext context = (KubernetesContext) request.getContext();
		assertThat(context.getNamespace()).isEqualTo("test-namespace");
		assertThat(context.getProperty("field1")).isEqualTo("data");
		assertThat(context.getProperty("field2")).isEqualTo(2);

		assertThat(request.getParameters()).hasSize(0);
	}

	@Test
	public void requestWithUnknownContextIsDeserializedFromJson() {
		AsyncParameterizedServiceInstanceRequest request =
				JsonUtils.readTestDataFile("requestWithCustomContext.json",
						CreateServiceInstanceRequest.class);

		assertThat(request.getContext().getPlatform()).isEqualTo("test-platform");

		assertThat(request.getContext().getProperty("field1")).isEqualTo("data");
		assertThat(request.getContext().getProperty("field2")).isEqualTo(2);

		assertThat(request.getParameters()).isNull();
	}

	@Test
	public void equalsAndHashCode() {
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
