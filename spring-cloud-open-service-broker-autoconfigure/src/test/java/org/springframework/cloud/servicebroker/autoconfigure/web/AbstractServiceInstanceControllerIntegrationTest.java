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

import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.JsonUtils;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceController;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.web.util.UriComponentsBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class AbstractServiceInstanceControllerIntegrationTest extends ControllerIntegrationTest {

	protected static final String SERVICE_INSTANCES_ROOT_PATH = "/v2/service_instances/";

	@InjectMocks
	protected ServiceInstanceController controller;

	@Mock
	protected ServiceInstanceService serviceInstanceService;

	protected String createRequestBody;

	protected String updateRequestBody;

	protected String updateRequestBodyWithPlan;

	@Before
	public void setUpCommonFixtures() {
		this.createRequestBody = JsonUtils.toJson(CreateServiceInstanceRequest
				.builder()
				.serviceDefinitionId(serviceDefinition.getId())
				.planId("plan-one-id")
				.build());

		this.updateRequestBody = JsonUtils.toJson(UpdateServiceInstanceRequest
				.builder()
				.serviceDefinitionId(serviceDefinition.getId())
				.build());

		this.updateRequestBodyWithPlan = JsonUtils.toJson(UpdateServiceInstanceRequest
				.builder()
				.serviceDefinitionId(serviceDefinition.getId())
				.planId("plan-three-id")
				.build());
	}

	protected void setupServiceInstanceService(CreateServiceInstanceResponse response) {
		when(serviceInstanceService.createServiceInstance(any(CreateServiceInstanceRequest.class)))
				.thenReturn(Mono.just(response));
	}

	protected void setupServiceInstanceService(Exception exception) {
		when(serviceInstanceService.createServiceInstance(any(CreateServiceInstanceRequest.class)))
				.thenReturn(Mono.error(exception));
	}

	protected void setupServiceInstanceService(GetServiceInstanceResponse response) {
		when(serviceInstanceService.getServiceInstance(any(GetServiceInstanceRequest.class)))
				.thenReturn(Mono.just(response));
	}

	protected void setupServiceInstanceService(ServiceBrokerOperationInProgressException exception) {
		when(serviceInstanceService.getServiceInstance(any(GetServiceInstanceRequest.class)))
				.thenReturn(Mono.error(exception));
	}
	protected void setupServiceInstanceService(DeleteServiceInstanceResponse response) {
		when(serviceInstanceService.deleteServiceInstance(any(DeleteServiceInstanceRequest.class)))
				.thenReturn(Mono.just(response));
	}

	protected void setupServiceInstanceService(ServiceInstanceDoesNotExistException exception) {
		when(serviceInstanceService.deleteServiceInstance(any(DeleteServiceInstanceRequest.class)))
				.thenReturn(Mono.error(exception));
	}

	protected void setupServiceInstanceService(UpdateServiceInstanceResponse response) {
		when(serviceInstanceService.updateServiceInstance(any(UpdateServiceInstanceRequest.class)))
				.thenReturn(Mono.just(response));
	}

	protected void setupServiceInstanceService(ServiceInstanceUpdateNotSupportedException exception) {
		when(serviceInstanceService.updateServiceInstance(any(UpdateServiceInstanceRequest.class)))
				.thenReturn(Mono.error(exception));
	}

	protected void setupServiceInstanceService(GetLastServiceOperationResponse response) {
		when(serviceInstanceService.getLastOperation(any(GetLastServiceOperationRequest.class)))
				.thenReturn(Mono.just(response));
	}

	protected String buildCreateUpdateUrl() {
		return buildCreateUpdateUrl(null, false);
	}

	protected String buildCreateUpdateUrl(String platformInstanceId, boolean asyncAccepted) {
		return buildBaseUrl(platformInstanceId)
				.path(SERVICE_INSTANCE_ID)
				.queryParam("accepts_incomplete", asyncAccepted)
				.toUriString();
	}

	protected String buildDeleteUrl() {
		return buildDeleteUrl(null, false);
	}

	protected String buildDeleteUrl(String platformInstanceId, boolean asyncAccepted) {
		return buildBaseUrl(platformInstanceId)
				.path(SERVICE_INSTANCE_ID)
				.queryParam("service_id", serviceDefinition.getId())
				.queryParam("plan_id", "plan-three-id")
				.queryParam("accepts_incomplete", asyncAccepted)
				.toUriString();
	}

	protected String buildLastOperationUrl() {
		return buildLastOperationUrl(null);
	}

	protected String buildLastOperationUrl(String platformInstanceId) {
		return buildBaseUrl(platformInstanceId)
				.pathSegment(SERVICE_INSTANCE_ID, "last_operation")
				.queryParam("operation", "working")
				.toUriString();
	}

	protected UriComponentsBuilder buildBaseUrl(String platformInstanceId) {
		return UriComponentsBuilder.fromPath("//")
				.path(platformInstanceId)
				.path(SERVICE_INSTANCES_ROOT_PATH);
	}

	protected CreateServiceInstanceRequest verifyCreateServiceInstance() {
		ArgumentCaptor<CreateServiceInstanceRequest> argumentCaptor = ArgumentCaptor.forClass(CreateServiceInstanceRequest.class);
		Mockito.verify(serviceInstanceService).createServiceInstance(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	protected GetServiceInstanceRequest verifyGetServiceInstance() {
		ArgumentCaptor<GetServiceInstanceRequest> argumentCaptor = ArgumentCaptor.forClass(GetServiceInstanceRequest.class);
		Mockito.verify(serviceInstanceService).getServiceInstance(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	protected DeleteServiceInstanceRequest verifyDeleteServiceInstance() {
		ArgumentCaptor<DeleteServiceInstanceRequest> argumentCaptor = ArgumentCaptor.forClass(DeleteServiceInstanceRequest.class);
		Mockito.verify(serviceInstanceService).deleteServiceInstance(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	protected UpdateServiceInstanceRequest verifyUpdateServiceInstance() {
		ArgumentCaptor<UpdateServiceInstanceRequest> argumentCaptor = ArgumentCaptor.forClass(UpdateServiceInstanceRequest.class);
		Mockito.verify(serviceInstanceService).updateServiceInstance(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	protected GetLastServiceOperationRequest verifyLastOperation() {
		ArgumentCaptor<GetLastServiceOperationRequest> argumentCaptor = ArgumentCaptor.forClass(GetLastServiceOperationRequest.class);
		Mockito.verify(serviceInstanceService).getLastOperation(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}
}
