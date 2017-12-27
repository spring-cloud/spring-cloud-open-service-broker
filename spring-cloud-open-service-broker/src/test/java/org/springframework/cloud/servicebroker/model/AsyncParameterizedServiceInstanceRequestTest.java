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

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.CLOUD_FOUNDRY_PLATFORM;
import static org.springframework.cloud.servicebroker.model.KubernetesContext.KUBERNETES_PLATFORM;

public class AsyncParameterizedServiceInstanceRequestTest {
	@Test
	public void requestWithCloudFoundryContextIsRead() {
		AsyncParameterizedServiceInstanceRequest request =
				DataFixture.readTestDataFile("createRequestWithCloudFoundryContext.json",
						CreateServiceInstanceRequest.class);

		assertEquals(CLOUD_FOUNDRY_PLATFORM, request.getContext().getPlatform());
		assertThat(request.getContext(), instanceOf(CloudFoundryContext.class));

		CloudFoundryContext context = (CloudFoundryContext) request.getContext();
		assertEquals("test-organization-guid", context.getOrganizationGuid());
		assertEquals("test-space-guid", context.getSpaceGuid());
		assertEquals("data", context.getProperty("field1"));
		assertEquals(2, context.getProperty("field2"));
	}

	@Test
	public void requestWithKubernetesContextIsRead() {
		AsyncParameterizedServiceInstanceRequest request =
				DataFixture.readTestDataFile("createRequestWithKubernetesContext.json",
						CreateServiceInstanceRequest.class);

		assertEquals(KUBERNETES_PLATFORM, request.getContext().getPlatform());
		assertThat(request.getContext(), instanceOf(KubernetesContext.class));

		KubernetesContext context = (KubernetesContext) request.getContext();
		assertEquals("test-namespace", context.getNamespace());
		assertEquals("data", context.getProperty("field1"));
		assertEquals(2, context.getProperty("field2"));
	}

	@Test
	public void requestWithUnknownContextIsRead() {
		AsyncParameterizedServiceInstanceRequest request =
				DataFixture.readTestDataFile("createRequestWithCustomContext.json",
						CreateServiceInstanceRequest.class);

		assertEquals("test-platform", request.getContext().getPlatform());

		assertEquals("data", request.getContext().getProperty("field1"));
		assertEquals(2, request.getContext().getProperty("field2"));
	}
}