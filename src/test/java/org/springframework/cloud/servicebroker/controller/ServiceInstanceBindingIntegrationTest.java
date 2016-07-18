package org.springframework.cloud.servicebroker.controller;

import org.junit.Before;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.fixture.ServiceInstanceBindingFixture;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class ServiceInstanceBindingIntegrationTest extends ControllerIntegrationTest {
	protected UriComponentsBuilder uriBuilder;
	protected UriComponentsBuilder foundationIdUriBuilder;

	protected CreateServiceInstanceBindingRequest createRequest;
	protected DeleteServiceInstanceBindingRequest deleteRequest;

	@Before
	public void setupBase() {
		uriBuilder = UriComponentsBuilder.fromPath("/v2/service_instances/")
				.pathSegment("service-instance-one-id", "service_bindings");
		foundationIdUriBuilder = UriComponentsBuilder.fromPath("/123/v2/service_instances/")
				.pathSegment("service-instance-one-id", "service_bindings");

		createRequest = ServiceInstanceBindingFixture.buildCreateAppBindingRequest();

		deleteRequest = ServiceInstanceBindingFixture.buildDeleteServiceInstanceBindingRequest();
	}

	protected String buildUrl(CreateServiceInstanceBindingRequest request, Boolean withFoundationId) {
		UriComponentsBuilder builder = withFoundationId ? foundationIdUriBuilder : uriBuilder;
		return builder.path(request.getBindingId()).toUriString();
	}

	protected String buildUrl(DeleteServiceInstanceBindingRequest request, Boolean withFoundationId) {
		UriComponentsBuilder builder = withFoundationId ? foundationIdUriBuilder : uriBuilder;
		return builder.path(request.getBindingId())
				.queryParam("service_id", request.getServiceDefinitionId())
				.queryParam("plan_id", request.getPlanId())
				.toUriString();
	}
}
