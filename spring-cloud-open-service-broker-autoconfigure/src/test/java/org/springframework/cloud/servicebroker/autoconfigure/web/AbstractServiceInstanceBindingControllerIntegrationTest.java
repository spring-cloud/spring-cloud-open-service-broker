/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerCreateOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerDeleteOperationInProgressException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationRequest;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

public abstract class AbstractServiceInstanceBindingControllerIntegrationTest
		extends ServiceInstanceBindingIntegrationTest {

	@InjectMocks
	protected ServiceInstanceBindingController controller;

	@Mock
	protected ServiceInstanceBindingService serviceInstanceBindingService;

	protected void setupServiceInstanceBindingService(CreateServiceInstanceBindingResponse createResponse) {
		given(serviceInstanceBindingService
				.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.willReturn(Mono.just(createResponse));
	}

	protected void setupServiceInstanceBindingService(GetServiceInstanceBindingResponse getResponse) {
		given(serviceInstanceBindingService.getServiceInstanceBinding(any(GetServiceInstanceBindingRequest.class)))
				.willReturn(Mono.just(getResponse));
	}

	protected void setupServiceInstanceBindingService(DeleteServiceInstanceBindingResponse deleteResponse) {
		given(serviceInstanceBindingService
				.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class)))
				.willReturn(Mono.just(deleteResponse));
	}

	protected void setupServiceInstanceBindingService(GetLastServiceBindingOperationResponse response) {
		given(serviceInstanceBindingService.getLastOperation(any(GetLastServiceBindingOperationRequest.class)))
				.willReturn(Mono.just(response));
	}

	protected void setupServiceInstanceBindingService(ServiceBrokerCreateOperationInProgressException exception) {
		given(serviceInstanceBindingService
				.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.willReturn(Mono.error(exception));
	}

	protected void setupServiceInstanceBindingService(ServiceBrokerDeleteOperationInProgressException exception) {
		given(serviceInstanceBindingService
				.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class)))
				.willReturn(Mono.error(exception));
	}

	protected CreateServiceInstanceBindingRequest verifyCreateBinding() {
		ArgumentCaptor<CreateServiceInstanceBindingRequest> argumentCaptor = ArgumentCaptor
				.forClass(CreateServiceInstanceBindingRequest.class);
		then(serviceInstanceBindingService)
				.should()
				.createServiceInstanceBinding(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	protected GetServiceInstanceBindingRequest verifyGetBinding() {
		ArgumentCaptor<GetServiceInstanceBindingRequest> argumentCaptor = ArgumentCaptor
				.forClass(GetServiceInstanceBindingRequest.class);
		then(serviceInstanceBindingService)
				.should()
				.getServiceInstanceBinding(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	protected DeleteServiceInstanceBindingRequest verifyDeleteBinding() {
		ArgumentCaptor<DeleteServiceInstanceBindingRequest> argumentCaptor = ArgumentCaptor
				.forClass(DeleteServiceInstanceBindingRequest.class);
		then(serviceInstanceBindingService)
				.should()
				.deleteServiceInstanceBinding(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	protected GetLastServiceBindingOperationRequest verifyLastOperation() {
		ArgumentCaptor<GetLastServiceBindingOperationRequest> argumentCaptor = ArgumentCaptor
				.forClass(GetLastServiceBindingOperationRequest.class);
		then(serviceInstanceBindingService)
				.should()
				.getLastOperation(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

}
