package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import org.junit.Before;

import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture.ServiceInstanceBindingFixture;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture.ServiceInstanceBindingFixture.SERVICE_INSTANCE_BINDING_ID;
import static org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture.ServiceInstanceBindingFixture.SERVICE_INSTANCE_ID;

public abstract class ServiceInstanceBindingIntegrationTest extends ControllerIntegrationTest {
	protected static final String SERVICE_INSTANCES_ROOT_PATH = "/v2/service_instances/";

	protected UriComponentsBuilder uriBuilder;
	protected UriComponentsBuilder cfInstanceIdUriBuilder;

	protected CreateServiceInstanceBindingRequest createRequest;
	protected DeleteServiceInstanceBindingRequest deleteRequest;
	protected ServiceDefinition serviceDefinition;

	@Before
	public void setupBase() {
		uriBuilder = UriComponentsBuilder.fromPath(SERVICE_INSTANCES_ROOT_PATH)
				.pathSegment(SERVICE_INSTANCE_ID, "service_bindings", SERVICE_INSTANCE_BINDING_ID);
		cfInstanceIdUriBuilder = UriComponentsBuilder.fromPath("/").path(CF_INSTANCE_ID).path(SERVICE_INSTANCES_ROOT_PATH)
				.pathSegment(SERVICE_INSTANCE_ID, "service_bindings", SERVICE_INSTANCE_BINDING_ID);

		serviceDefinition = ServiceFixture.getSimpleService();

		createRequest = ServiceInstanceBindingFixture.buildCreateAppBindingRequest();

		deleteRequest = ServiceInstanceBindingFixture.buildDeleteServiceInstanceBindingRequest();
	}

	protected String buildCreateUrl(Boolean withCfInstanceId) {
		UriComponentsBuilder builder = withCfInstanceId ? cfInstanceIdUriBuilder : uriBuilder;
		return builder.toUriString();
	}

	protected String buildDeleteUrl(Boolean withCfInstanceId) {
		UriComponentsBuilder builder = withCfInstanceId ? cfInstanceIdUriBuilder : uriBuilder;
		return builder
				.queryParam("service_id", serviceDefinition.getId())
				.queryParam("plan_id", serviceDefinition.getPlans().get(0).getId())
				.toUriString();
	}
}
