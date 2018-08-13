package com.example.servicebroker;

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

import java.util.Map;

@Service
public class ExampleServiceInstanceService implements ServiceInstanceService {

	@Override
	public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		String planId = request.getPlanId();
		Map<String, Object> parameters = request.getParameters();

		//
		// perform the steps necessary to initiate the asynchronous
		// provisioning of all necessary resources
		//
		
		String dashboardUrl = new String(/* construct a dashboard URL */);

		return CreateServiceInstanceResponse.builder()
				.dashboardUrl(dashboardUrl)
				.async(true)
				.build();
	}

	@Override
	public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		String planId = request.getPlanId();
		String previousPlan = request.getPreviousValues().getPlanId();
		Map<String, Object> parameters = request.getParameters();

		//
		// perform the steps necessary to initiate the asynchronous
		// updating of all necessary resources
		//

		return UpdateServiceInstanceResponse.builder()
				.async(true)
				.build();
	}

	@Override
	public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		String planId = request.getPlanId();

		//
		// perform the steps necessary to initiate the asynchronous
		// deletion of all provisioned resources
		//

		return DeleteServiceInstanceResponse.builder()
				.async(true)
				.build();
	}

	@Override
	public GetServiceInstanceResponse getServiceInstance(GetServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();

		//
		// retrieve the details of the specified service instance
		//

		String dashboardUrl = new String(/* retrieve dashboard URL */);

		return GetServiceInstanceResponse.builder()
				.dashboardUrl(dashboardUrl)
				.build();
	}

	@Override
	public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();

		//
		// determine the status of the operation in progress
		//

		OperationState state = OperationState.SUCCEEDED;

		return GetLastServiceOperationResponse.builder()
				.operationState(state)
				.build();
	}
}
