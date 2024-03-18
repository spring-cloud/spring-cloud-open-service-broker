/*
 * Copyright 2002-2023 the original author or authors.
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
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationRequest;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.instance.OperationState;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;

public class BasicServiceInstanceBindingService implements ServiceInstanceBindingService {

	private static final String IN_PROGRESS_SERVICE_INSTANCE_ID = "service-instance-two-id";

	private static final String EXISTING_SERVICE_INSTANCE_ID = "service-instance-three-id";

	private static final String UNKNOWN_SERVICE_INSTANCE_ID = "service-instance-four-id";

	private static final String UNKNOWN_BINDING_ID = "service-binding-two-id";

	@Override
	public Mono<CreateServiceInstanceBindingResponse> createServiceInstanceBinding(
			CreateServiceInstanceBindingRequest request) {
		if (IN_PROGRESS_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.error(new ServiceBrokerCreateOperationInProgressException("task_10"));
		}
		if (EXISTING_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.just(CreateServiceInstanceAppBindingResponse.builder().bindingExisted(true).build());
		}
		if (UNKNOWN_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.error(new ServiceInstanceDoesNotExistException(request.getServiceInstanceId()));
		}
		if (request.isAsyncAccepted()) {
			return Mono.just(CreateServiceInstanceAppBindingResponse.builder()
				.async(true)
				.operation("working")
				.bindingExisted(false)
				.build());
		}
		else {
			return Mono.just(CreateServiceInstanceAppBindingResponse.builder().bindingExisted(false).build());
		}
	}

	@Override
	public Mono<DeleteServiceInstanceBindingResponse> deleteServiceInstanceBinding(
			DeleteServiceInstanceBindingRequest request) {
		if (IN_PROGRESS_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.error(new ServiceBrokerDeleteOperationInProgressException("task_10"));
		}
		if (UNKNOWN_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.error(new ServiceInstanceDoesNotExistException(request.getServiceInstanceId()));
		}
		if (UNKNOWN_BINDING_ID.equals(request.getBindingId())) {
			return Mono.error(new ServiceInstanceBindingDoesNotExistException(request.getBindingId()));
		}
		if (request.isAsyncAccepted()) {
			return Mono.just(DeleteServiceInstanceBindingResponse.builder().async(true).operation("working").build());
		}
		else {
			return Mono.just(DeleteServiceInstanceBindingResponse.builder().build());
		}
	}

	@Override
	public Mono<GetServiceInstanceBindingResponse> getServiceInstanceBinding(GetServiceInstanceBindingRequest request) {
		if (IN_PROGRESS_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.error(new ServiceBrokerOperationInProgressException("task_10"));
		}
		return Mono.just(GetServiceInstanceAppBindingResponse.builder().build());
	}

	@Override
	public Mono<GetLastServiceBindingOperationResponse> getLastOperation(
			GetLastServiceBindingOperationRequest request) {
		if (IN_PROGRESS_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.just(GetLastServiceBindingOperationResponse.builder()
				.operationState(OperationState.IN_PROGRESS)
				.description("working on it")
				.build());
		}
		// deleted service binding status
		if (EXISTING_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.just(GetLastServiceBindingOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.description("all good")
				.deleteOperation(true)
				.build());
		}
		// failed service binding status
		if (UNKNOWN_SERVICE_INSTANCE_ID.equals(request.getServiceInstanceId())) {
			return Mono.just(GetLastServiceBindingOperationResponse.builder()
				.operationState(OperationState.FAILED)
				.description("not so good")
				.build());
		}
		return Mono.just(GetLastServiceBindingOperationResponse.builder()
			.operationState(OperationState.SUCCEEDED)
			.description("all good")
			.build());
	}

}
