package org.springframework.cloud.servicebroker.model.fixture;

import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;

public class ServiceInstanceFixture {

	public static CreateServiceInstanceRequest buildCreateServiceInstanceRequest(boolean acceptsIncomplete) {
		ServiceDefinition service = ServiceFixture.getSimpleService();
		return new CreateServiceInstanceRequest(
				service.getId(),
				service.getPlans().get(0).getId(),
				DataFixture.getOrgOneGuid(),
				DataFixture.getSpaceOneGuid(),
				ParametersFixture.getParameters())
				.withServiceInstanceId("service-instance-id")
				.withAsyncAccepted(acceptsIncomplete);
	}

	public static CreateServiceInstanceResponse buildCreateServiceInstanceResponse(boolean async) {
		return new CreateServiceInstanceResponse("https://dashboard_url.example.com", async);
	}

	public static DeleteServiceInstanceRequest buildDeleteServiceInstanceRequest(boolean acceptsIncomplete) {
		ServiceDefinition service = ServiceFixture.getSimpleService();
		return new DeleteServiceInstanceRequest("service-instance-id",
				service.getId(),
				service.getPlans().get(0).getId(),
				service,
				acceptsIncomplete);
	}

	public static DeleteServiceInstanceResponse buildDeleteServiceInstanceResponse(boolean async) {
		return new DeleteServiceInstanceResponse(async);
	}

	public static UpdateServiceInstanceRequest buildUpdateServiceInstanceRequest(boolean acceptsIncomplete) {
		ServiceDefinition service = ServiceFixture.getSimpleService();
		return new UpdateServiceInstanceRequest(
				service.getId(),
				service.getPlans().get(0).getId(),
				ParametersFixture.getParameters())
				.withServiceInstanceId("service-instance-id")
				.withAsyncAccepted(acceptsIncomplete);
	}

	public static UpdateServiceInstanceResponse buildUpdateServiceInstanceResponse(boolean async) {
		return new UpdateServiceInstanceResponse(async);
	}

	public static GetLastServiceOperationRequest buildGetLastOperationRequest() {
		return new GetLastServiceOperationRequest("service-instance-id");
	}
}
