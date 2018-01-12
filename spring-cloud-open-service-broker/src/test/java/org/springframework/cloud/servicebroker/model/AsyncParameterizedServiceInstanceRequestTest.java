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

package org.springframework.cloud.servicebroker.model;

import org.junit.Test;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import java.util.Map;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.CLOUD_FOUNDRY_PLATFORM;
import static org.springframework.cloud.servicebroker.model.KubernetesContext.KUBERNETES_PLATFORM;

public class AsyncParameterizedServiceInstanceRequestTest {
	@Test
	public void requestWithCloudFoundryContextIsDeserializedFromJson() {
		AsyncParameterizedServiceInstanceRequest request =
				DataFixture.readTestDataFile("requestWithParametersAndCloudFoundryContext.json",
						CreateServiceInstanceRequest.class);

		assertEquals(CLOUD_FOUNDRY_PLATFORM, request.getContext().getPlatform());
		assertThat(request.getContext(), instanceOf(CloudFoundryContext.class));

		CloudFoundryContext context = (CloudFoundryContext) request.getContext();
		assertThat(context.getOrganizationGuid(), equalTo("test-organization-guid"));
		assertThat(context.getSpaceGuid(), equalTo("test-space-guid"));
		assertThat(context.getProperty("field1"), equalTo("data"));
		assertThat(context.getProperty("field2"), equalTo(2));

		Map<String, Object> parameters = request.getParameters();
		assertThat(parameters, aMapWithSize(3));
		assertThat(parameters.get("parameter1"), equalTo(1));
		assertThat(parameters.get("parameter2"), equalTo("foo"));
		assertThat(parameters.get("parameter3"), equalTo(true));
	}

	@Test
	public void requestWithKubernetesContextIsDeserializedFromJson() {
		AsyncParameterizedServiceInstanceRequest request =
				DataFixture.readTestDataFile("requestWithEmptyParametersAndKubernetesContext.json",
						CreateServiceInstanceRequest.class);

		assertEquals(KUBERNETES_PLATFORM, request.getContext().getPlatform());
		assertThat(request.getContext(), instanceOf(KubernetesContext.class));

		KubernetesContext context = (KubernetesContext) request.getContext();
		assertThat(context.getNamespace(), equalTo("test-namespace"));
		assertThat(context.getProperty("field1"), equalTo("data"));
		assertThat(context.getProperty("field2"), equalTo(2));

		assertThat(request.getParameters(), aMapWithSize(0));
	}

	@Test
	public void requestWithUnknownContextIsDeserializedFromJson() {
		AsyncParameterizedServiceInstanceRequest request =
				DataFixture.readTestDataFile("requestWithCustomContext.json",
						CreateServiceInstanceRequest.class);

		assertThat(request.getContext().getPlatform(), equalTo("test-platform"));

		assertThat(request.getContext().getProperty("field1"), equalTo("data"));
		assertThat(request.getContext().getProperty("field2"), equalTo(2));

		assertThat(request.getParameters(), nullValue());
	}
}