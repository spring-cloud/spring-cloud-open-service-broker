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

package org.springframework.cloud.servicebroker.autoconfigure.web.reactive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.cloud.servicebroker.autoconfigure.web.AbstractServiceInstanceBindingControllerIntegrationTest;
import org.springframework.cloud.servicebroker.controller.ServiceBrokerWebFluxExceptionHandler;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerCreateOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerDeleteOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.BindingStatus;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceRouteBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationRequest;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceRouteBindingResponse;
import org.springframework.cloud.servicebroker.model.instance.OperationState;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.API_INFO_LOCATION_HEADER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER;

@ExtendWith(MockitoExtension.class)
class ServiceInstanceBindingControllerIntegrationTest extends AbstractServiceInstanceBindingControllerIntegrationTest {

	private WebTestClient client;

	@BeforeEach
	void setUp() {
		this.client = WebTestClient.bindToController(this.controller)
				.controllerAdvice(ServiceBrokerWebFluxExceptionHandler.class)
				.build();
	}

	@Test
	void createBindingToAppWithoutAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse.builder()
				.build());

		client.put().uri(buildCreateUrl(PLATFORM_INSTANCE_ID, false))
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isCreated();

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void createBindingToAppFiltersPlansSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse
				.builder()
				.build());

		client.put().uri(buildCreateUrl(PLATFORM_INSTANCE_ID, false))
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isCreated();

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertThat(actualRequest.getPlan().getId()).isEqualTo(actualRequest.getPlanId());
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void createBindingToAppWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse.builder()
				.async(true)
				.operation("working")
				.build());

		client.put().uri(buildCreateUrl(PLATFORM_INSTANCE_ID, true))
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isAccepted()
				.expectBody()
				.jsonPath("$.operation").isEqualTo("working");

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void createBindingToAppWithAsyncAndHeadersOperationInProgress() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(new ServiceBrokerCreateOperationInProgressException("task_10"));

		client.put().uri(buildCreateUrl(PLATFORM_INSTANCE_ID, true))
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isAccepted()
				.expectBody().jsonPath("$.operation").isEqualTo("task_10");

		verifyCreateBinding();
	}

	@Test
	void createBindingToAppWithIdenticalExistingSucceeds() {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse.builder()
				.bindingStatus(BindingStatus.EXISTS_WITH_IDENTICAL_PARAMETERS)
				.build());

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createBindingToAppWithDifferentExistingSucceeds() {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse.builder()
				.bindingStatus(BindingStatus.EXISTS_WITH_DIFFERENT_PARAMETERS)
				.build());

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.CONFLICT);

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createBindingToRouteWithoutAsyncAndHeadersSucceeds() {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse.builder()
				.build());

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isCreated();

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createBindingToRouteFiltersPlansSucceeds() {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse
				.builder()
				.build());

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isCreated();

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getPlan().getId()).isEqualTo(actualRequest.getPlanId());
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createBindingToRouteWithAsyncAndHeadersSucceeds() {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse.builder()
				.async(true)
				.operation("working")
				.build());

		client.put().uri(buildCreateUrl(null, true))
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isAccepted()
				.expectBody()
				.jsonPath("$.operation").isEqualTo("working");

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createBindingToRouteWithIdenticalExistingSucceeds() {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse.builder()
				.bindingStatus(BindingStatus.EXISTS_WITH_IDENTICAL_PARAMETERS)
				.build());

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	void createBindingToRouteWithDifferentExistingSucceeds() {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse.builder()
				.bindingStatus(BindingStatus.EXISTS_WITH_DIFFERENT_PARAMETERS)
				.build());

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	void createBindingWithUnknownServiceInstanceIdFails() {
		setupCatalogService();

		given(serviceInstanceBindingService
				.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.willThrow(new ServiceInstanceDoesNotExistException(SERVICE_INSTANCE_ID));

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, String.format("id=%s", SERVICE_INSTANCE_ID)));
	}

	@Test
	void createBindingWithUnknownServiceDefinitionIdFails() {
		setupCatalogService(null);

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, serviceDefinition.getId()));
	}

	@Test
	void createBindingWithDuplicateIdFails() {
		setupCatalogService();

		given(serviceInstanceBindingService
				.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.willThrow(new ServiceInstanceBindingExistsException(SERVICE_INSTANCE_ID, SERVICE_INSTANCE_BINDING_ID));

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.CONFLICT)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result,
						String.format("serviceInstanceId=%s, bindingId=%s", SERVICE_INSTANCE_ID,
								SERVICE_INSTANCE_BINDING_ID)));
	}

	@Test
	void createBindingWithInvalidFieldsFails() {
		String body = createRequestBody.replace("service_id", "service-1");

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(body)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, "serviceDefinitionId"));
	}

	@Test
	void createBindingWithMissingFieldsFails() {
		String body = "{}";

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(body)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, "serviceDefinitionId"))
				.consumeWith(result -> assertDescriptionContains(result, "planId"));
	}

	@Test
	void getBindingToAppSucceeds() throws Exception {
		setupServiceInstanceBindingService(GetServiceInstanceAppBindingResponse.builder()
				.build());

		client.get().uri(buildGetUrl(PLATFORM_INSTANCE_ID))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();

		GetServiceInstanceBindingRequest actualRequest = verifyGetBinding();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void getBindingToAppWithParamsSucceeds() throws Exception {
		setupServiceInstanceBindingService(GetServiceInstanceAppBindingResponse.builder()
				.build());

		client.get().uri(buildGetUrl(PLATFORM_INSTANCE_ID, "service-definition-id", "plan-id", false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();

		GetServiceInstanceBindingRequest actualRequest = verifyGetBinding();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void getBindingToRouteSucceeds() throws Exception {
		setupServiceInstanceBindingService(GetServiceInstanceRouteBindingResponse.builder()
				.build());

		client.get().uri(buildGetUrl(PLATFORM_INSTANCE_ID))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();

		GetServiceInstanceBindingRequest actualRequest = verifyGetBinding();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void getBindingWithOperationInProgressFails() {
		given(serviceInstanceBindingService.getServiceInstanceBinding(any(GetServiceInstanceBindingRequest.class)))
				.willThrow(new ServiceBrokerOperationInProgressException("task_10"));

		client.get().uri(buildCreateUrl())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void deleteBindingWithoutAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(DeleteServiceInstanceBindingResponse.builder()
				.build());

		client.delete().uri(buildDeleteUrl(PLATFORM_INSTANCE_ID, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.json("{}");


		then(serviceInstanceBindingService)
				.should()
				.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		DeleteServiceInstanceBindingRequest actualRequest = verifyDeleteBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void deleteBindingFiltersPlansSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(DeleteServiceInstanceBindingResponse.builder()
				.build());

		client.delete().uri(buildDeleteUrl(PLATFORM_INSTANCE_ID, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.json("{}");

		then(serviceInstanceBindingService)
				.should()
				.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		DeleteServiceInstanceBindingRequest actualRequest = verifyDeleteBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getPlan().getId()).isEqualTo(actualRequest.getPlanId());
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void deleteBindingWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(DeleteServiceInstanceBindingResponse.builder()
				.async(true)
				.operation("working")
				.build());

		client.delete().uri(buildDeleteUrl(PLATFORM_INSTANCE_ID, true))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.exchange()
				.expectStatus().isAccepted()
				.expectBody()
				.jsonPath("$.operation").isEqualTo("working");

		then(serviceInstanceBindingService)
				.should()
				.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		DeleteServiceInstanceBindingRequest actualRequest = verifyDeleteBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void deleteBindingWithAsyncAndHeadersOperationInProgress() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(new ServiceBrokerDeleteOperationInProgressException("task_10"));

		client.delete().uri(buildDeleteUrl(PLATFORM_INSTANCE_ID, true))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.exchange()
				.expectStatus().isAccepted()
				.expectBody()
				.jsonPath("$.operation").isEqualTo("task_10");

		verifyDeleteBinding();
	}

	@Test
	void deleteBindingWithUnknownInstanceIdFails() {
		setupCatalogService();

		given(serviceInstanceBindingService
				.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class)))
				.willThrow(new ServiceInstanceDoesNotExistException(SERVICE_INSTANCE_ID));

		client.delete().uri(buildDeleteUrl())
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, SERVICE_INSTANCE_ID));
	}

	@Test
	void deleteBindingWithUnknownBindingIdFails() {
		setupCatalogService();

		given(serviceInstanceBindingService
				.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class)))
				.willThrow(new ServiceInstanceBindingDoesNotExistException(SERVICE_INSTANCE_BINDING_ID));

		client.delete().uri(buildDeleteUrl())
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.GONE);
	}

	@Test
	void deleteBindingWithUnknownServiceDefinitionIdFails() {
		setupCatalogService(null);

		client.delete().uri(buildDeleteUrl())
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void deleteBindingWithMissingQueryParamsFails() {
		final String url = buildDeleteUrl(null, false).replace("plan_id", "plan-1");

		client.delete().uri(url)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, "plan_id"));
	}

	@Test
	void lastOperationHasInProgressStatus() {
		setupServiceInstanceBindingService(GetLastServiceBindingOperationResponse.builder()
				.operationState(OperationState.IN_PROGRESS)
				.description("working on it")
				.build());

		client.get().uri(buildLastOperationUrl())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.state").isEqualTo(OperationState.IN_PROGRESS.toString())
				.jsonPath("$.description", is("working on it"));

		then(serviceInstanceBindingService)
				.should()
				.getLastOperation(any(GetLastServiceBindingOperationRequest.class));

		GetLastServiceBindingOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void lastOperationHasSucceededStatus() throws Exception {
		setupServiceInstanceBindingService(GetLastServiceBindingOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.description("all good")
				.build());

		client.get().uri(buildLastOperationUrl(PLATFORM_INSTANCE_ID))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.state").isEqualTo(OperationState.SUCCEEDED.toString())
				.jsonPath("$.description", is("all good"));

		then(serviceInstanceBindingService)
				.should()
				.getLastOperation(any(GetLastServiceBindingOperationRequest.class));

		GetLastServiceBindingOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void lastOperationHasSucceededStatusWithDeletionComplete() {
		setupServiceInstanceBindingService(GetLastServiceBindingOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.description("all good")
				.deleteOperation(true)
				.build());

		client.get().uri(buildLastOperationUrl())
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.GONE)
				.expectBody()
				.jsonPath("$.state").isEqualTo(OperationState.SUCCEEDED.toString())
				.jsonPath("$.description", is("all good"));

		then(serviceInstanceBindingService)
				.should()
				.getLastOperation(any(GetLastServiceBindingOperationRequest.class));

		GetLastServiceBindingOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void lastOperationHasFailedStatus() {
		setupServiceInstanceBindingService(GetLastServiceBindingOperationResponse.builder()
				.operationState(OperationState.FAILED)
				.description("not so good")
				.build());

		client.get().uri(buildLastOperationUrl())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.state").isEqualTo(OperationState.FAILED.toString())
				.jsonPath("$.description").isEqualTo("not so good");

		then(serviceInstanceBindingService)
				.should()
				.getLastOperation(any(GetLastServiceBindingOperationRequest.class));

		GetLastServiceBindingOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesNotSet(actualRequest);
	}

}
