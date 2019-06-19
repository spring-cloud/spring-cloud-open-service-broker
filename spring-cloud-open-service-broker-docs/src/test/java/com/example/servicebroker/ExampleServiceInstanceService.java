package com.example.servicebroker;

import java.util.Map;

import reactor.core.publisher.Mono;

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
public class ExampleServiceInstanceService implements ServiceInstanceService {

	@Override
	public Mono<CreateServiceInstanceResponse> createServiceInstance(CreateServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		String planId = request.getPlanId();
		Map<String, Object> parameters = request.getParameters();

		//
		// perform the steps necessary to initiate the asynchronous
		// provisioning of all necessary resources
		//

		String dashboardUrl = ""; /* construct a dashboard URL */

		return Mono.just(CreateServiceInstanceResponse.builder()
				.dashboardUrl(dashboardUrl)
				.async(true)
				.build());
	}

	@Override
	public Mono<UpdateServiceInstanceResponse> updateServiceInstance(UpdateServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		String planId = request.getPlanId();
		String previousPlan = request.getPreviousValues().getPlanId();
		Map<String, Object> parameters = request.getParameters();

		//
		// perform the steps necessary to initiate the asynchronous
		// updating of all necessary resources
		//

		return Mono.just(UpdateServiceInstanceResponse.builder()
				.async(true)
				.build());
	}

	@Override
	public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(DeleteServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		String planId = request.getPlanId();

		//
		// perform the steps necessary to initiate the asynchronous
		// deletion of all provisioned resources
		//

		return Mono.just(DeleteServiceInstanceResponse.builder()
				.async(true)
				.build());
	}

	@Override
	public Mono<GetServiceInstanceResponse> getServiceInstance(GetServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();

		//
		// retrieve the details of the specified service instance
		//

		String dashboardUrl = ""; /* retrieve dashboard URL */

		return Mono.just(GetServiceInstanceResponse.builder()
				.dashboardUrl(dashboardUrl)
				.build());
	}

	@Override
	public Mono<GetLastServiceOperationResponse> getLastOperation(GetLastServiceOperationRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();

		//
		// determine the status of the operation in progress
		//

		return Mono.just(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.build());
	}
}
