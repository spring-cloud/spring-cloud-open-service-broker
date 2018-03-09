package org.springframework.cloud.servicebroker.example;

import java.util.Map;

import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.OperationState;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceInstanceProvision implements ServiceInstanceService {

	@Override
	public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {

		String spaceGuid = request.getContext().getProperty("space_guid").toString();
		String orgGuid = request.getContext().getProperty("organization_guid").toString();

		String dashboardUrl = deployServiceInstanceToPlaform(
				request.getServiceInstanceId(),
				request.getPlanId(),
				request.getServiceDefinitionId(),
				request.getParameters(),
				orgGuid,
				spaceGuid);

		return CreateServiceInstanceResponse.builder().dashboardUrl(dashboardUrl).build();
	}


	@Override
	public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {

		deleteServiceFromPlatform(request.getServiceInstanceId());
		return DeleteServiceInstanceResponse.builder().build();
	}

	@Override
	public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {

		OperationState status = getServiceInstanceStatuse(request.getServiceInstanceId());
		return GetLastServiceOperationResponse.builder().operationState(status).build();
	}

	/**
	 * Implementation of getServiceInstance() is optional
	 */
	@Override
	public GetServiceInstanceResponse getServiceInstance(GetServiceInstanceRequest request) {

		return getServiceInstanceDetails(request.getServiceInstanceId());
	}

	@Override
	public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request) {

		updateService(request.getServiceInstanceId(), request.getParameters());
		return UpdateServiceInstanceResponse.builder().build();
	}

	private void deleteServiceFromPlatform(String serviceInstanceId) {
		throw new UnsupportedOperationException();
	}

	private String deployServiceInstanceToPlaform(String serviceInstanceId, String planId, String serviceDefinitionId,
												  Map<String, Object> parameters, String orgGuid, String spaceGuid) {
		throw new UnsupportedOperationException();
	}

	private GetServiceInstanceResponse getServiceInstanceDetails(String serviceInstanceId) {
		throw new UnsupportedOperationException();
	}

	private OperationState getServiceInstanceStatuse(String serviceInstanceId) {

		return OperationState.SUCCEEDED;
	}

	private void updateService(String serviceInstanceId, Map<String, Object> parameters) {
		throw new UnsupportedOperationException();
	}
}
