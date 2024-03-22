/*
 * Copyright 2002-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web;

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerCreateOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerDeleteOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerUpdateOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
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

public class BasicServiceInstanceService implements ServiceInstanceService {

	private static final String IN_PROGRESS_SERVICE_INSTANCE_ID = "service-instance-two-id";

	private static final String EXISTING_SERVICE_INSTANCE_ID = "service-instance-three-id";

	private static final String UNKNOWN_SERVICE_INSTANCE_ID = "service-instance-four-id";

	@Override
	public Mono<CreateServiceInstanceResponse> createServiceInstance(CreateServiceInstanceRequest request) {
		if (IN_PROGRESS_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.error(new ServiceBrokerCreateOperationInProgressException("task_10"));
		}
		if (EXISTING_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.just(CreateServiceInstanceResponse.builder().instanceExisted(true).build());
		}
		if (request.isAsyncAccepted()) {
			return Mono.just(CreateServiceInstanceResponse.builder().async(true).operation("working").build());
		}
		else {
			return Mono.just(CreateServiceInstanceResponse.builder().build());
		}
	}

	@Override
	public Mono<GetServiceInstanceResponse> getServiceInstance(GetServiceInstanceRequest request) {
		if (IN_PROGRESS_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.error(new ServiceBrokerOperationInProgressException("task_10"));
		}
		return Mono.just(GetServiceInstanceResponse.builder().build());
	}

	@Override
	public Mono<GetLastServiceOperationResponse> getLastOperation(GetLastServiceOperationRequest request) {
		if (IN_PROGRESS_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.just(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.IN_PROGRESS)
				.description("working on it")
				.build());
		}
		// deleted service instance status
		if (EXISTING_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.just(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.description("all gone")
				.deleteOperation(true)
				.build());
		}
		// failed service instance status
		if (UNKNOWN_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.just(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.FAILED)
				.description("not so good")
				.build());
		}
		return Mono.just(GetLastServiceOperationResponse.builder()
			.operationState(OperationState.SUCCEEDED)
			.description("all good")
			.build());
	}

	@Override
	public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(DeleteServiceInstanceRequest request) {
		if (IN_PROGRESS_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.error(new ServiceBrokerDeleteOperationInProgressException("task_10"));
		}
		if (UNKNOWN_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.error(new ServiceInstanceDoesNotExistException(UNKNOWN_SERVICE_INSTANCE_ID));
		}
		if (request.isAsyncAccepted()) {
			return Mono.just(DeleteServiceInstanceResponse.builder().async(true).operation("working").build());
		}
		else {
			return Mono.just(DeleteServiceInstanceResponse.builder().build());
		}
	}

	@Override
	public Mono<UpdateServiceInstanceResponse> updateServiceInstance(UpdateServiceInstanceRequest request) {
		if (IN_PROGRESS_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.error(new ServiceBrokerUpdateOperationInProgressException("task_10"));
		}
		if (UNKNOWN_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.error(new ServiceInstanceDoesNotExistException(UNKNOWN_SERVICE_INSTANCE_ID));
		}
		if (request.isAsyncAccepted()) {
			return Mono.just(UpdateServiceInstanceResponse.builder()
				.async(true)
				.operation("working")
				.dashboardUrl("https://dashboard.example.local")
				.build());
		}
		else {
			return Mono
				.just(UpdateServiceInstanceResponse.builder().dashboardUrl("https://dashboard.example.local").build());
		}
	}

}
