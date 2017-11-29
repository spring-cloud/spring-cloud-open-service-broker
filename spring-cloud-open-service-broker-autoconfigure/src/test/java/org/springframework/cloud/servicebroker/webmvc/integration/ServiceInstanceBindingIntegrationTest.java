package org.springframework.cloud.servicebroker.webmvc.integration;

import org.junit.Before;

import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.fixture.ServiceInstanceBindingFixture;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class ServiceInstanceBindingIntegrationTest extends ControllerIntegrationTest {
	protected static final String SERVICE_INSTANCES_ROOT_PATH = "/v2/service_instances/";

	protected UriComponentsBuilder uriBuilder;
	protected UriComponentsBuilder cfInstanceIdUriBuilder;

	protected CreateServiceInstanceBindingRequest createRequest;
	protected DeleteServiceInstanceBindingRequest deleteRequest;

	@Before
	public void setupBase() {
		uriBuilder = UriComponentsBuilder.fromPath(SERVICE_INSTANCES_ROOT_PATH)
				.pathSegment("service-instance-one-id", "service_bindings");
		cfInstanceIdUriBuilder = UriComponentsBuilder.fromPath("/").path(CF_INSTANCE_ID).path(SERVICE_INSTANCES_ROOT_PATH)
				.pathSegment("service-instance-one-id", "service_bindings");

		createRequest = ServiceInstanceBindingFixture.buildCreateAppBindingRequest();

		deleteRequest = ServiceInstanceBindingFixture.buildDeleteServiceInstanceBindingRequest();
	}

	protected String buildUrl(CreateServiceInstanceBindingRequest request, Boolean withCfInstanceId) {
		UriComponentsBuilder builder = withCfInstanceId ? cfInstanceIdUriBuilder : uriBuilder;
		return builder.path(request.getBindingId()).toUriString();
	}

	protected String buildUrl(DeleteServiceInstanceBindingRequest request, Boolean withCfInstanceId) {
		UriComponentsBuilder builder = withCfInstanceId ? cfInstanceIdUriBuilder : uriBuilder;
		return builder.path(request.getBindingId())
				.queryParam("service_id", request.getServiceDefinitionId())
				.queryParam("plan_id", request.getPlanId())
				.toUriString();
	}
}
