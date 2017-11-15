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