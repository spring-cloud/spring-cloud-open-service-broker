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

package org.springframework.cloud.servicebroker.autoconfigure.web.reactive;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.autoconfigure.web.AbstractServiceInstanceBindingControllerIntegrationTest;
import org.springframework.cloud.servicebroker.controller.ServiceBrokerExceptionHandler;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.API_INFO_LOCATION_HEADER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInstanceBindingControllerIntegrationTest extends AbstractServiceInstanceBindingControllerIntegrationTest {

	private WebTestClient client;

	@Before
	public void setUp() {
		this.client = WebTestClient.bindToController(this.controller)
				.controllerAdvice(ServiceBrokerExceptionHandler.class)
				.build();
	}

	@Test
	public void createBindingToAppWithoutAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse.builder()
				.bindingExisted(false)
				.build());

		client.put().uri(buildCreateUrl(PLATFORM_INSTANCE_ID, false))
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(createRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isCreated();

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void createBindingToAppFiltersPlansSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse
				.builder()
				.bindingExisted(false)
				.build());

		client.put().uri(buildCreateUrl(PLATFORM_INSTANCE_ID, false))
			  .contentType(MediaType.APPLICATION_JSON)
			  .syncBody(createRequestBody)
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
	public void createBindingToAppWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse.builder()
				.async(true)
				.operation("working")
				.bindingExisted(false)
				.build());

		client.put().uri(buildCreateUrl(PLATFORM_INSTANCE_ID, true))
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(createRequestBody)
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
	public void createBindingToAppWithExistingSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse.builder()
				.bindingExisted(true)
				.build());

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	public void createBindingToRouteWithoutAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse.builder()
				.bindingExisted(false)
				.build());

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isCreated();

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	public void createBindingToRouteFiltersPlansSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse
				.builder()
				.bindingExisted(false)
				.build());

		client.put().uri(buildCreateUrl())
			  .contentType(MediaType.APPLICATION_JSON)
			  .syncBody(createRequestBody)
			  .accept(MediaType.APPLICATION_JSON)
			  .exchange()
			  .expectStatus().isCreated();

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getPlan().getId()).isEqualTo(actualRequest.getPlanId());
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	public void createBindingToRouteWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse.builder()
				.async(true)
				.operation("working")
				.bindingExisted(false)
				.build());

		client.put().uri(buildCreateUrl(null, true))
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(createRequestBody)
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
	public void createBindingToRouteWithExistingSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse.builder()
				.bindingExisted(true)
				.build());

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void createBindingWithUnknownServiceInstanceIdFails() throws Exception {
		setupCatalogService();

		when(serviceInstanceBindingService.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.thenThrow(new ServiceInstanceDoesNotExistException(SERVICE_INSTANCE_ID));

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, String.format("id=%s", SERVICE_INSTANCE_ID)));
	}

	@Test
	public void createBindingWithUnknownServiceDefinitionIdSucceeds() throws Exception {
		setupCatalogService(null);

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, serviceDefinition.getId()));
	}

	@Test
	public void createBindingWithDuplicateIdFails() throws Exception {
		setupCatalogService();

		when(serviceInstanceBindingService.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.thenThrow(new ServiceInstanceBindingExistsException(SERVICE_INSTANCE_ID, SERVICE_INSTANCE_BINDING_ID));

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.CONFLICT)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, String.format("serviceInstanceId=%s, bindingId=%s", SERVICE_INSTANCE_ID, SERVICE_INSTANCE_BINDING_ID)));
	}

	@Test
	public void createBindingWithInvalidFieldsFails() throws Exception {
		String body = createRequestBody.replace("service_id", "foo");

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(body)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, "serviceDefinitionId"));
	}

	@Test
	public void createBindingWithMissingFieldsFails() throws Exception {
		String body = "{}";

		client.put().uri(buildCreateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(body)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, "serviceDefinitionId"))
				.consumeWith(result -> assertDescriptionContains(result, "planId"));
	}

	@Test
	public void getBindingToAppSucceeds() throws Exception {
		setupServiceInstanceBindingService(GetServiceInstanceAppBindingResponse.builder()
				.build());

		client.get().uri(buildCreateUrl(PLATFORM_INSTANCE_ID, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();

		GetServiceInstanceBindingRequest actualRequest = verifyGetBinding();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void getBindingToRouteSucceeds() throws Exception {
		setupServiceInstanceBindingService(GetServiceInstanceRouteBindingResponse.builder()
				.build());

		client.get().uri(buildCreateUrl(PLATFORM_INSTANCE_ID, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();

		GetServiceInstanceBindingRequest actualRequest = verifyGetBinding();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void getBindingWithOperationInProgressFails() throws Exception {
		when(serviceInstanceBindingService.getServiceInstanceBinding(any(GetServiceInstanceBindingRequest.class)))
				.thenThrow(new ServiceBrokerOperationInProgressException("still working"));

		client.get().uri(buildCreateUrl())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	public void deleteBindingWithoutAsyncAndHeadersSucceeds() throws Exception {
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

		verify(serviceInstanceBindingService).deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		DeleteServiceInstanceBindingRequest actualRequest = verifyDeleteBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void deleteBindingFiltersPlansSucceeds() throws Exception {
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

		verify(serviceInstanceBindingService).deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		DeleteServiceInstanceBindingRequest actualRequest = verifyDeleteBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getPlan().getId()).isEqualTo(actualRequest.getPlanId());
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void deleteBindingWithAsyncAndHeadersSucceeds() throws Exception {
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

		verify(serviceInstanceBindingService).deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		DeleteServiceInstanceBindingRequest actualRequest = verifyDeleteBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void deleteBindingWithUnknownInstanceIdFails() throws Exception {
		setupCatalogService();

		doThrow(new ServiceInstanceDoesNotExistException(SERVICE_INSTANCE_ID))
				.when(serviceInstanceBindingService).deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		client.delete().uri(buildDeleteUrl())
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, SERVICE_INSTANCE_ID));
	}

	@Test
	public void deleteBindingWithUnknownBindingIdFails() throws Exception {
		setupCatalogService();

		doThrow(new ServiceInstanceBindingDoesNotExistException(SERVICE_INSTANCE_BINDING_ID))
				.when(serviceInstanceBindingService).deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		client.delete().uri(buildDeleteUrl())
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.GONE);
	}

	@Test
	public void deleteBindingWithUnknownServiceDefinitionIdSucceeds() throws Exception {
		setupCatalogService(null);

		when(serviceInstanceBindingService.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class)))
				.thenReturn(Mono.just(DeleteServiceInstanceBindingResponse.builder().build()));

		client.delete().uri(buildDeleteUrl())
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void lastOperationHasInProgressStatus() throws Exception {
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

		verify(serviceInstanceBindingService).getLastOperation(any(GetLastServiceBindingOperationRequest.class));

		GetLastServiceBindingOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	public void lastOperationHasSucceededStatus() throws Exception {
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

		verify(serviceInstanceBindingService).getLastOperation(any(GetLastServiceBindingOperationRequest.class));

		GetLastServiceBindingOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void lastOperationHasSucceededStatusWithDeletionComplete() throws Exception {
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

		verify(serviceInstanceBindingService).getLastOperation(any(GetLastServiceBindingOperationRequest.class));

		GetLastServiceBindingOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	public void lastOperationHasFailedStatus() throws Exception {
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

		verify(serviceInstanceBindingService).getLastOperation(any(GetLastServiceBindingOperationRequest.class));

		GetLastServiceBindingOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesNotSet(actualRequest);
	}

}
