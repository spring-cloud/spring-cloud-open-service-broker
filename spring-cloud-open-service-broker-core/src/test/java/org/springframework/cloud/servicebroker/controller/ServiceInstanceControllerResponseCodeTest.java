/*
 * Copyright 2002-2022 the original author or authors.
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

package org.springframework.cloud.servicebroker.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
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
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class ServiceInstanceControllerResponseCodeTest {

	private final CatalogService catalogService = mock(CatalogService.class);

	private final ServiceInstanceService serviceInstanceService = mock(ServiceInstanceService.class);

	private final Map<String, String> pathVariables = Collections.emptyMap();

	private ServiceInstanceController controller;

	@BeforeEach
	void setUp() {
		controller = new ServiceInstanceController(catalogService, serviceInstanceService);
		ServiceDefinition serviceDefinition = mock(ServiceDefinition.class);
		List<Plan> plans = new ArrayList<>();
		plans.add(Plan.builder().id("service-definition-plan-id").build());
		given(serviceDefinition.getPlans()).willReturn(plans);
		given(serviceDefinition.getId()).willReturn("service-definition-id");
		given(catalogService.getServiceDefinition(any())).willReturn(Mono.just(serviceDefinition));
	}

	@Test
	void createServiceInstanceWithNullResponseGivesExpectedStatus() {
		validateCreateServiceInstanceWithResponseStatus(null, HttpStatus.CREATED);
	}

	@Test
	void createServiceInstanceWithResponseGivesExpectedStatus() {
		validateCreateServiceInstanceWithResponseStatus(CreateServiceInstanceResponse.builder()
				.async(false)
				.instanceExisted(false)
				.dashboardUrl("https://dashboard.app.local")
				.build(), HttpStatus.CREATED);
	}

	@Test
	void createServiceInstanceWithInstanceExistResponseGivesExpectedStatus() {
		validateCreateServiceInstanceWithResponseStatus(CreateServiceInstanceResponse.builder()
				.async(false)
				.instanceExisted(true)
				.build(), HttpStatus.OK);
	}

	@Test
	void createServiceInstanceWithAsyncResponseGivesExpectedStatus() {
		validateCreateServiceInstanceWithResponseStatus(CreateServiceInstanceResponse.builder()
				.async(true)
				.instanceExisted(false)
				.operation("creating")
				.build(), HttpStatus.ACCEPTED);
	}

	@Test
	void createServiceInstanceWithResponseAsyncInstanceExistGivesExpectedStatus() {
		validateCreateServiceInstanceWithResponseStatus(CreateServiceInstanceResponse.builder()
				.async(true)
				.instanceExisted(true)
				.build(), HttpStatus.ACCEPTED);
	}

	private void validateCreateServiceInstanceWithResponseStatus(CreateServiceInstanceResponse response,
			HttpStatus expectedStatus) {
		Mono<CreateServiceInstanceResponse> responseMono;
		if (response == null) {
			responseMono = Mono.empty();
		}
		else {
			responseMono = Mono.just(response);
		}
		given(serviceInstanceService.createServiceInstance(any(CreateServiceInstanceRequest.class)))
				.willReturn(responseMono);

		CreateServiceInstanceRequest createRequest = CreateServiceInstanceRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("service-definition-plan-id")
				.build();

		ResponseEntity<CreateServiceInstanceResponse> responseEntity = controller
				.createServiceInstance(pathVariables, null, false, null, null, null,
						createRequest)
				.block();

		then(serviceInstanceService)
				.should()
				.createServiceInstance(any(CreateServiceInstanceRequest.class));
		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(expectedStatus);
		assertThat(responseEntity.getBody()).isEqualTo(response);
	}

	@Test
	void getServiceInstanceWithNullResponseGivesExpectedStatus() {
		validateGetServiceInstanceWithResponseStatus(null, HttpStatus.OK);
	}

	@Test
	void getServiceInstanceWithResponseGivesExpectedStatus() {
		validateGetServiceInstanceWithResponseStatus(GetServiceInstanceResponse.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.build(), HttpStatus.OK);
	}

	private void validateGetServiceInstanceWithResponseStatus(GetServiceInstanceResponse response,
			HttpStatus expectedStatus) {
		Mono<GetServiceInstanceResponse> responseMono;
		if (response == null) {
			responseMono = Mono.empty();
		}
		else {
			responseMono = Mono.just(response);
		}
		given(serviceInstanceService.getServiceInstance(any(GetServiceInstanceRequest.class)))
				.willReturn(responseMono);

		ResponseEntity<GetServiceInstanceResponse> responseEntity = controller
				.getServiceInstance(pathVariables, null, null, null, null, null, null)
				.block();

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(expectedStatus);
		assertThat(responseEntity.getBody()).isEqualTo(response);
	}

	@Test
	void getServiceInstanceWithMissingInstanceGivesExpectedStatus() {
		given(serviceInstanceService.getServiceInstance(any(GetServiceInstanceRequest.class)))
				.willReturn(Mono.error(new ServiceInstanceDoesNotExistException("instance does not exist")));

		ResponseEntity<GetServiceInstanceResponse> responseEntity = controller
				.getServiceInstance(pathVariables, null, null, null, null, null, null)
				.block();

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void deleteServiceInstanceWithNullResponseGivesExpectedStatus() {
		validateDeleteServiceInstanceWithResponseStatus(null, HttpStatus.OK);
	}

	@Test
	void deleteServiceInstanceWithResponseGivesExpectedStatus() {
		validateDeleteServiceInstanceWithResponseStatus(DeleteServiceInstanceResponse.builder()
				.async(false)
				.build(), HttpStatus.OK);
	}

	@Test
	void deleteServiceInstanceWithAsyncResponseGivesExpectedStatus() {
		validateDeleteServiceInstanceWithResponseStatus(DeleteServiceInstanceResponse.builder()
				.async(true)
				.operation("deleting")
				.build(), HttpStatus.ACCEPTED);
	}

	private void validateDeleteServiceInstanceWithResponseStatus(DeleteServiceInstanceResponse response,
			HttpStatus expectedStatus) {
		Mono<DeleteServiceInstanceResponse> responseMono;
		if (response == null) {
			responseMono = Mono.empty();
		}
		else {
			responseMono = Mono.just(response);
		}
		given(serviceInstanceService.deleteServiceInstance(any(DeleteServiceInstanceRequest.class)))
				.willReturn(responseMono);

		ResponseEntity<DeleteServiceInstanceResponse> responseEntity = controller
				.deleteServiceInstance(pathVariables, null, "service-definition-id", "service-definition-plan-id",
						false, null, null, null)
				.block();

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(expectedStatus);
		assertThat(responseEntity.getBody()).isEqualTo(response);
	}

	@Test
	void deleteServiceInstanceWithMissingInstanceGivesExpectedStatus() {
		given(serviceInstanceService.deleteServiceInstance(any(DeleteServiceInstanceRequest.class)))
				.willReturn(Mono.error(new ServiceInstanceDoesNotExistException("instance does not exist")));

		ResponseEntity<DeleteServiceInstanceResponse> responseEntity = controller
				.deleteServiceInstance(pathVariables, null, "service-definition-id", "service-definition-plan-id",
						false, null, null, null)
				.block();

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.GONE);
	}

	@Test
	void updateServiceInstanceWithNullResponseGivesExpectedStatus() {
		validateUpdateServiceInstanceWithResponseStatus(null, HttpStatus.OK);
	}

	@Test
	void updateServiceInstanceWithResponseGivesExpectedStatus() {
		validateUpdateServiceInstanceWithResponseStatus(UpdateServiceInstanceResponse.builder()
				.async(false)
				.build(), HttpStatus.OK);
	}

	@Test
	void updateServiceInstanceWithAsyncResponseGivesExpectedStatus() {
		validateUpdateServiceInstanceWithResponseStatus(UpdateServiceInstanceResponse.builder()
				.async(true)
				.operation("updating")
				.build(), HttpStatus.ACCEPTED);
	}

	private void validateUpdateServiceInstanceWithResponseStatus(UpdateServiceInstanceResponse response,
			HttpStatus expectedStatus) {
		Mono<UpdateServiceInstanceResponse> responseMono;
		if (response == null) {
			responseMono = Mono.empty();
		}
		else {
			responseMono = Mono.just(response);
		}
		given(serviceInstanceService.updateServiceInstance(any(UpdateServiceInstanceRequest.class)))
				.willReturn(responseMono);

		UpdateServiceInstanceRequest updateRequest = UpdateServiceInstanceRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.build();

		ResponseEntity<UpdateServiceInstanceResponse> responseEntity = controller
				.updateServiceInstance(pathVariables, null, false, null, null, null,
						updateRequest)
				.block();

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(expectedStatus);
		assertThat(responseEntity.getBody()).isEqualTo(response);
	}

	@Test
	void getLastOperationWithInProgressResponseGivesExpectedStatus() {
		validateGetLastOperationWithResponseStatus(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.IN_PROGRESS)
				.description("in progress")
				.build(), HttpStatus.OK);
	}

	@Test
	void getLastOperationWithSucceededResponseGivesExpectedStatus() {
		validateGetLastOperationWithResponseStatus(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.deleteOperation(false)
				.build(), HttpStatus.OK);
	}

	@Test
	void getLastOperationWithDeleteSucceededResponseGivesExpectedStatus() {
		validateGetLastOperationWithResponseStatus(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.deleteOperation(true)
				.build(), HttpStatus.GONE);
	}

	@Test
	void getLastOperationWithUnknownInstanceBadRequest() {
		given(serviceInstanceService.getLastOperation(any(GetLastServiceOperationRequest.class)))
				.willReturn(Mono.error(new ServiceInstanceDoesNotExistException("instance never existed")));

		ResponseEntity<GetLastServiceOperationResponse> responseEntity = controller
				.getServiceInstanceLastOperation(pathVariables, null, "service-definition-id",
						"service-definition-plan-id", null, null, null, null)
				.block();

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void getLastOperationWithFailedResponseGivesExpectedStatus() {
		validateGetLastOperationWithResponseStatus(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.FAILED)
				.build(), HttpStatus.OK);
	}

	@Test
	void getLastOperationWithFailedResponseInstanceUnusableGivesExpectedStatus() {
		validateGetLastOperationWithResponseStatus(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.FAILED)
				.instanceUsable(false)
				.updateRepeatable(true)
				.build(), HttpStatus.OK);
	}

	private void validateGetLastOperationWithResponseStatus(GetLastServiceOperationResponse response,
			HttpStatus expectedStatus) {
		given(serviceInstanceService.getLastOperation(any(GetLastServiceOperationRequest.class)))
				.willReturn(Mono.just(response));

		ResponseEntity<GetLastServiceOperationResponse> responseEntity = controller
				.getServiceInstanceLastOperation(pathVariables, null, null, null, null,
						null, null, null)
				.block();

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(expectedStatus);
		assertThat(responseEntity.getBody()).isEqualTo(response);
	}

}
