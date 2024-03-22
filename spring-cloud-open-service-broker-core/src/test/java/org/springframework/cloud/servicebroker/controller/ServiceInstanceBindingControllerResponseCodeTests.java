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

package org.springframework.cloud.servicebroker.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class ServiceInstanceBindingControllerResponseCodeTests {

	private final CatalogService catalogService = mock(CatalogService.class);

	private final ServiceInstanceBindingService bindingService = mock(ServiceInstanceBindingService.class);

	private final Map<String, String> pathVariables = Collections.emptyMap();

	private ServiceInstanceBindingController controller;

	@BeforeEach
	void setUp() {
		this.controller = new ServiceInstanceBindingController(this.catalogService, this.bindingService);
		ServiceDefinition serviceDefinition = mock(ServiceDefinition.class);
		List<Plan> plans = new ArrayList<>();
		plans.add(Plan.builder().id("service-definition-plan-id").build());
		given(serviceDefinition.getPlans()).willReturn(plans);
		given(serviceDefinition.getId()).willReturn("service-definition-id");
		given(this.catalogService.getServiceDefinition(any())).willReturn(Mono.just(serviceDefinition));
	}

	@Test
	void createServiceBindingWithNullResponseGivesExpectedStatus() {
		validateCreateServiceBindingResponseStatus(null, HttpStatus.CREATED);
	}

	@Test
	void createServiceBindingWithNoExistingBindingResponseGivesExpectedStatus() {
		CreateServiceInstanceBindingResponse response = CreateServiceInstanceAppBindingResponse.builder()
			.bindingExisted(false)
			.build();
		validateCreateServiceBindingResponseStatus(response, HttpStatus.CREATED);
	}

	@Test
	void createServiceBindingWithExistingBindingResponseGivesExpectedStatus() {
		CreateServiceInstanceBindingResponse response = CreateServiceInstanceAppBindingResponse.builder()
			.bindingExisted(true)
			.build();
		validateCreateServiceBindingResponseStatus(response, HttpStatus.OK);
	}

	private void validateCreateServiceBindingResponseStatus(CreateServiceInstanceBindingResponse response,
			HttpStatus httpStatus) {
		Mono<CreateServiceInstanceBindingResponse> responseMono;
		if (response == null) {
			responseMono = Mono.empty();
		}
		else {
			responseMono = Mono.just(response);
		}
		given(this.bindingService.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
			.willReturn(responseMono);

		CreateServiceInstanceBindingRequest createRequest = CreateServiceInstanceBindingRequest.builder()
			.serviceDefinitionId("service-definition-id")
			.planId("service-definition-plan-id")
			.build();

		ResponseEntity<CreateServiceInstanceBindingResponse> responseEntity = this.controller
			.createServiceInstanceBinding(this.pathVariables, null, null, false, null, null, null, createRequest)
			.block();

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(httpStatus);
		assertThat(responseEntity.getBody()).isEqualTo(response);
	}

	@Test
	void getServiceBindingWithResponseGivesExpectedStatus() {
		validateGetServiceBindingResponseStatus(null, HttpStatus.OK);
	}

	@Test
	void getServiceBindingWithResponseGivesExpectedStatus2() {
		GetServiceInstanceBindingResponse response = GetServiceInstanceAppBindingResponse.builder().build();
		validateGetServiceBindingResponseStatus(response, HttpStatus.OK);
	}

	private void validateGetServiceBindingResponseStatus(GetServiceInstanceBindingResponse response,
			HttpStatus httpStatus) {
		Mono<GetServiceInstanceBindingResponse> responseMono;
		if (response == null) {
			responseMono = Mono.empty();
		}
		else {
			responseMono = Mono.just(response);
		}
		given(this.bindingService.getServiceInstanceBinding(any(GetServiceInstanceBindingRequest.class)))
			.willReturn(responseMono);

		ResponseEntity<GetServiceInstanceBindingResponse> responseEntity = this.controller
			.getServiceInstanceBinding(this.pathVariables, null, null, null, null, null, null, null)
			.block();

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(httpStatus);
		assertThat(responseEntity.getBody()).isEqualTo(response);
	}

	@Test
	void getServiceBindingWithMissingBindingGivesExpectedStatus() {
		given(this.bindingService.getServiceInstanceBinding(any(GetServiceInstanceBindingRequest.class)))
			.willThrow(new ServiceInstanceBindingDoesNotExistException("binding-id"));

		ResponseEntity<GetServiceInstanceBindingResponse> responseEntity = this.controller
			.getServiceInstanceBinding(this.pathVariables, null, null, null, null, null, null, null)
			.block();

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void getServiceBindingWithMissingServiceInstanceGivesExpectedStatus() {
		given(this.bindingService.getServiceInstanceBinding(any(GetServiceInstanceBindingRequest.class)))
			.willThrow(new ServiceInstanceDoesNotExistException("nonexistent-service-id"));

		ResponseEntity<GetServiceInstanceBindingResponse> responseEntity = this.controller
			.getServiceInstanceBinding(this.pathVariables, "nonexistent-service-id", "nonexistent-binding-id", null,
					null, null, null, null)
			.block();

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void deleteServiceBindingWithNullResponseGivesExpectedStatus() {
		validateDeleteServiceBindingWithResponseStatus(null, HttpStatus.OK);
	}

	@Test
	void deleteServiceBindingWithResponseGivesExpectedStatus() {
		validateDeleteServiceBindingWithResponseStatus(
				DeleteServiceInstanceBindingResponse.builder().async(false).build(), HttpStatus.OK);
	}

	@Test
	void deleteServiceBindingWithResponseGivesExpectedStatus2() {
		validateDeleteServiceBindingWithResponseStatus(
				DeleteServiceInstanceBindingResponse.builder().async(true).build(), HttpStatus.ACCEPTED);
	}

	private void validateDeleteServiceBindingWithResponseStatus(DeleteServiceInstanceBindingResponse response,
			HttpStatus expectedStatus) {
		Mono<DeleteServiceInstanceBindingResponse> responseMono;
		if (response == null) {
			responseMono = Mono.empty();
		}
		else {
			responseMono = Mono.just(response);
		}
		given(this.bindingService.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class)))
			.willReturn(responseMono);

		ResponseEntity<DeleteServiceInstanceBindingResponse> responseEntity = this.controller
			.deleteServiceInstanceBinding(this.pathVariables, null, null, "service-definition-id",
					"service-definition-plan-id", false, null, null, null)
			.block();

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(expectedStatus);
		assertThat(responseEntity.getBody()).isEqualTo(response);

		then(this.bindingService).should().deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));
	}

	@Test
	void deleteServiceBindingWithMissingBindingGivesExpectedStatus() {
		given(this.bindingService.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class)))
			.willThrow(new ServiceInstanceBindingDoesNotExistException("binding-id"));

		ResponseEntity<DeleteServiceInstanceBindingResponse> responseEntity = this.controller
			.deleteServiceInstanceBinding(this.pathVariables, null, null, "service-definition-id",
					"service-definition-plan-id", false, null, null, null)
			.block();

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.GONE);
	}

}
