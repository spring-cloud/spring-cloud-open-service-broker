package org.springframework.cloud.servicebroker.model.fixture;

import org.springframework.cloud.servicebroker.model.CloudFoundryContext;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest.PreviousValues;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;

public class ServiceInstanceFixture {

	public static CreateServiceInstanceRequest buildCreateServiceInstanceRequest(boolean acceptsIncomplete) {
		ServiceDefinition service = ServiceFixture.getSimpleService();
		CloudFoundryContext context = ContextFixture.getCloudFoundryContext();
		return new CreateServiceInstanceRequest(
				service.getId(),
				service.getPlans().get(0).getId(),
				context.getOrganizationGuid(),
				context.getSpaceGuid(),
				context,
				ParametersFixture.getParameters())
				.withServiceInstanceId("service-instance-id")
				.withAsyncAccepted(acceptsIncomplete);
	}

	public static CreateServiceInstanceResponse buildCreateServiceInstanceResponse(boolean async) {
		final CreateServiceInstanceResponse response = new CreateServiceInstanceResponse()
				.withDashboardUrl("https://dashboard_url.example.com")
				.withAsync(async);
		return response.isAsync() ? response.withOperation("task_10") : response;
	}

	public static DeleteServiceInstanceRequest buildDeleteServiceInstanceRequest(boolean acceptsIncomplete) {
		ServiceDefinition service = ServiceFixture.getSimpleService();
		return new DeleteServiceInstanceRequest("service-instance-id",
				service.getId(),
				service.getPlans().get(0).getId(),
				service)
				.withAsyncAccepted(acceptsIncomplete);
	}

	public static DeleteServiceInstanceResponse buildDeleteServiceInstanceResponse(boolean async) {
		final DeleteServiceInstanceResponse response = new DeleteServiceInstanceResponse().withAsync(async);
        return response.isAsync() ? response.withOperation("task_10") : response;
	}

	public static UpdateServiceInstanceRequest buildUpdateServiceInstanceRequest(boolean acceptsIncomplete) {
		ServiceDefinition service = ServiceFixture.getSimpleService();
		return new UpdateServiceInstanceRequest(
				service.getId(),
				service.getPlans().get(1).getId(),
				ParametersFixture.getParameters(),
				new PreviousValues(service.getPlans().get(0).getId()),
				ContextFixture.getCloudFoundryContext())
				.withServiceInstanceId("service-instance-id")
				.withAsyncAccepted(acceptsIncomplete);
	}

	public static UpdateServiceInstanceResponse buildUpdateServiceInstanceResponse(boolean async) {
		final UpdateServiceInstanceResponse response = new UpdateServiceInstanceResponse().withAsync(async);
        return response.isAsync() ? response.withOperation("task_10") : response;
	}

	public static GetLastServiceOperationRequest buildGetLastOperationRequest() {
		ServiceDefinition service = ServiceFixture.getSimpleService();
		return new GetLastServiceOperationRequest(
				"service-instance-id",
				service.getId(),
				service.getPlans().get(0).getId(),
				"task_10");
	}
}
