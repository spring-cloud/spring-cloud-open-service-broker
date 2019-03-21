/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
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

import org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractServiceInstanceBindingControllerIntegrationTest extends ServiceInstanceBindingIntegrationTest {

	@InjectMocks
	protected ServiceInstanceBindingController controller;

	@Mock
	protected ServiceInstanceBindingService serviceInstanceBindingService;

	protected void setupServiceInstanceBindingService(CreateServiceInstanceBindingResponse createResponse) {
		when(serviceInstanceBindingService.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.thenReturn(createResponse);
	}

	protected void setupServiceInstanceBindingService(GetServiceInstanceBindingResponse getResponse) {
		when(serviceInstanceBindingService.getServiceInstanceBinding(any(GetServiceInstanceBindingRequest.class)))
				.thenReturn(getResponse);
	}

	protected CreateServiceInstanceBindingRequest verifyCreateBinding() {
		ArgumentCaptor<CreateServiceInstanceBindingRequest> argumentCaptor = ArgumentCaptor.forClass(CreateServiceInstanceBindingRequest.class);
		verify(serviceInstanceBindingService).createServiceInstanceBinding(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	protected GetServiceInstanceBindingRequest verifyGetBinding() {
		ArgumentCaptor<GetServiceInstanceBindingRequest> argumentCaptor = ArgumentCaptor.forClass(GetServiceInstanceBindingRequest.class);
		verify(serviceInstanceBindingService).getServiceInstanceBinding(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	protected DeleteServiceInstanceBindingRequest verifyDeleteBinding() {
		ArgumentCaptor<DeleteServiceInstanceBindingRequest> argumentCaptor = ArgumentCaptor.forClass(DeleteServiceInstanceBindingRequest.class);
		verify(serviceInstanceBindingService).deleteServiceInstanceBinding(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}
}
