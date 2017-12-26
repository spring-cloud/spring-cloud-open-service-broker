package org.springframework.cloud.servicebroker.model;

import org.junit.Test;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import java.io.IOException;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.CLOUD_FOUNDRY_PLATFORM;
import static org.springframework.cloud.servicebroker.model.KubernetesContext.KUBERNETES_PLATFORM;
import static org.springframework.cloud.servicebroker.model.fixture.DataFixture.fromJson;
import static org.springframework.cloud.servicebroker.model.fixture.DataFixture.toJson;

@SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"})
public class CreateServiceInstanceBindingRequestTest {
	@Test
	public void requestWithAppGuidBindingIsRead() {
		CreateServiceInstanceBindingRequest request =
				DataFixture.readTestDataFile("bindRequestWithAppGuid.json", CreateServiceInstanceBindingRequest.class);

		assertEquals("test-service-id", request.getServiceDefinitionId());
		assertEquals("test-plan-id", request.getPlanId());
		assertEquals("test-app-guid", request.getAppGuid());
		assertEquals("test-app-guid", request.getBindResource().getAppGuid());

		Context context = request.getContext();
		assertEquals("sample-platform", context.getPlatform());
		assertThat(context, instanceOf(Context.class));
		assertEquals("data", context.getProperty("field1"));
		assertEquals(2, context.getProperty("field2"));

		assertNull(request.getBindResource().getRoute());
		assertEquals("data", request.getBindResource().getProperty("field1"));
		assertEquals(2, request.getBindResource().getProperty("field2"));

		assertEquals(1, request.getParameters().get("parameter1"));
		assertEquals("foo", request.getParameters().get("parameter2"));
		assertEquals(true, request.getParameters().get("parameter3"));
	}

	@Test
	public void requestWithRouteBindingIsRead() {
		CreateServiceInstanceBindingRequest request =
				DataFixture.readTestDataFile("bindRequestWithRoute.json", CreateServiceInstanceBindingRequest.class);

		assertEquals("test-service-id", request.getServiceDefinitionId());
		assertEquals("test-plan-id", request.getPlanId());

		assertNull(request.getAppGuid());
		assertNull(request.getBindResource().getAppGuid());
		assertEquals("http://test.example.com", request.getBindResource().getRoute());
		assertEquals("data", request.getBindResource().getProperty("field1"));
		assertEquals(2, request.getBindResource().getProperty("field2"));

		assertEquals(KUBERNETES_PLATFORM, request.getContext().getPlatform());
		assertThat(request.getContext(), instanceOf(KubernetesContext.class));

		KubernetesContext context = (KubernetesContext) request.getContext();
		assertEquals("test-namespace", context.getNamespace());
		assertEquals("data", context.getProperty("field1"));
		assertEquals(2, context.getProperty("field2"));

		assertEquals(1, request.getParameters().get("parameter1"));
		assertEquals("foo", request.getParameters().get("parameter2"));
		assertEquals(true, request.getParameters().get("parameter3"));
	}

	@Test
	public void requestWithCloudFoundryContextIsRead() {
		CreateServiceInstanceBindingRequest request =
				DataFixture.readTestDataFile("bindRequestWithCloudFoundryContext.json", CreateServiceInstanceBindingRequest.class);

		assertEquals("test-service-id", request.getServiceDefinitionId());
		assertEquals("test-plan-id", request.getPlanId());
		assertEquals("test-app-guid", request.getAppGuid());
		assertEquals("test-app-guid", request.getBindResource().getAppGuid());

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
		CreateServiceInstanceBindingRequest request =
				DataFixture.readTestDataFile("bindRequestWithKubernetesContext.json", CreateServiceInstanceBindingRequest.class);

		assertEquals("test-service-id", request.getServiceDefinitionId());
		assertEquals("test-plan-id", request.getPlanId());
		assertEquals("test-app-guid", request.getAppGuid());
		assertEquals("test-app-guid", request.getBindResource().getAppGuid());

		assertEquals(KUBERNETES_PLATFORM, request.getContext().getPlatform());
		assertThat(request.getContext(), instanceOf(KubernetesContext.class));

		KubernetesContext context = (KubernetesContext) request.getContext();
		assertEquals("test-namespace", context.getNamespace());
		assertEquals("data", context.getProperty("field1"));
		assertEquals(2, context.getProperty("field2"));
	}

	@Test
	public void requestMatchesWithJsonRoundTrip() throws IOException {
		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder()
				.serviceDefinitionId("definition-id")
				.planId("plan-id")
				.parameters("param1", "value1")
				.parameters("param2", "value2")
				.bindResource(BindResource.builder()
						.appGuid("app-guid")
						.route("route")
						.parameters("resource-param1", "value1")
						.parameters("resource-param2", "value2")
						.build())
				.context(Context.builder()
						.platform("sample-platform")
						.property("context-property1", "value1")
						.property("context-property2", "value2")
						.build())
				.build();

		CreateServiceInstanceBindingRequest fromJson =
				fromJson(toJson(request), CreateServiceInstanceBindingRequest.class);
		
		assertEquals(request, fromJson);
	}
}