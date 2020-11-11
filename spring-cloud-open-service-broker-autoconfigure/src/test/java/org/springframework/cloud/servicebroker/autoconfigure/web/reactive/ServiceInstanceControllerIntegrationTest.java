/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web.reactive;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.cloud.servicebroker.autoconfigure.web.AbstractServiceInstanceControllerIntegrationTest;
import org.springframework.cloud.servicebroker.controller.ServiceBrokerWebFluxExceptionHandler;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerCreateOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerDeleteOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerMaintenanceInfoConflictException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerUpdateOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException.ASYNC_REQUIRED_ERROR;
import static org.springframework.cloud.servicebroker.exception.ServiceBrokerMaintenanceInfoConflictException.MAINTENANCE_INFO_CONFLICT_ERROR;
import static org.springframework.cloud.servicebroker.exception.ServiceBrokerMaintenanceInfoConflictException.MAINTENANCE_INFO_CONFLICT_MESSAGE;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.API_INFO_LOCATION_HEADER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER;

@ExtendWith(MockitoExtension.class)
class ServiceInstanceControllerIntegrationTest extends AbstractServiceInstanceControllerIntegrationTest {

	private WebTestClient client;

	@BeforeEach
	void setUp() {
		this.client = WebTestClient.bindToController(this.controller)
				.controllerAdvice(ServiceBrokerWebFluxExceptionHandler.class)
				.build();
	}

	@Test
	void createServiceInstanceWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.async(true)
				.build());

		client.put().uri(buildCreateUpdateUrl(PLATFORM_INSTANCE_ID, true))
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isAccepted();

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void createServiceInstanceWithAsyncOperationAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		// alternatively throw a ServiceBrokerCreateOperationInProgressException when a request is received for
		// an operation in progress. See test: createServiceInstanceWithAsyncAndHeadersOperationInProgress
		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.async(true)
				.operation("task_10")
				.dashboardUrl("https://dashboard.app.local")
				.build());

		client.put().uri(buildCreateUpdateUrl(PLATFORM_INSTANCE_ID, true))
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isAccepted()
				.expectBody()
				.jsonPath("$.operation").isEqualTo("task_10")
				.jsonPath("$.dashboard_url").isEqualTo("https://dashboard.app.local");

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void createServiceInstanceWithAsyncAndHeadersOperationInProgress() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerCreateOperationInProgressException("task_10"));

		client.put().uri(buildCreateUpdateUrl(PLATFORM_INSTANCE_ID, true))
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isAccepted()
				.expectBody()
				.jsonPath("$.operation").isEqualTo("task_10");

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void createServiceInstanceResponseShouldNotContainEmptyValuesWhenNull() {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.operation(null)
				.dashboardUrl(null)
				.build());

		client.put().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
				.jsonPath("$.dashboard_url").doesNotExist()
				.jsonPath("$.operation").doesNotExist();
	}

	@Test
	void createServiceInstanceResponseShouldNotContainEmptyValuesWhenEmpty() {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.operation("")
				.dashboardUrl("")
				.build());

		client.put().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
				.jsonPath("$.dashboard_url").doesNotExist()
				.jsonPath("$.operation").doesNotExist();
	}

	@Test
	void createServiceInstanceWithEmptyPlatformInstanceIdSucceeds() {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.async(true)
				.build());

		// force a condition where the platformInstanceId segment is present but empty
		// e.g. https://test.app.local//v2/service_instances/[guid]
		String url = "https://test.app.local/" + buildCreateUpdateUrl();
		client.put().uri(url)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isAccepted();

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createServiceInstanceWithoutAsyncAndHeadersSucceeds() {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.build());

		client.put().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isCreated();

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createServiceInstanceWithExistingInstanceSucceeds() {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.instanceExisted(true)
				.build());

		client.put().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	void createServiceInstanceFiltersPlanSucceeds() {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse
				.builder()
				.build());

		client.put().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isCreated();

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getPlan().getId()).isEqualTo("plan-one-id");
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createServiceInstanceWithUnknownServiceDefinitionIdFails() {
		setupCatalogService(null);

		client.put().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(
						result -> assertDescriptionContains(result, String.format("id=%s", serviceDefinition.getId())));
	}

	@Test
	void createDuplicateServiceInstanceIdFails() {
		setupCatalogService();

		setupServiceInstanceService(new ServiceInstanceExistsException(SERVICE_INSTANCE_ID, serviceDefinition.getId()));

		client.put().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.CONFLICT)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result,
						String.format("serviceInstanceId=%s, serviceDefinitionId=%s", SERVICE_INSTANCE_ID,
								serviceDefinition.getId())));
	}

	@Test
	void createServiceInstanceWithAsyncRequiredFails() {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerAsyncRequiredException("async required description"));

		client.put().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectBody()
				.jsonPath("$.error").isEqualTo(ASYNC_REQUIRED_ERROR)
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, "async required description"));
	}

	@Test
	void createServiceInstanceWithMaintenanceInfoConflictFails() {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerMaintenanceInfoConflictException());

		client.put().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectBody()
				.jsonPath("$.error").isEqualTo(MAINTENANCE_INFO_CONFLICT_ERROR)
				.jsonPath("$.description").isEqualTo(MAINTENANCE_INFO_CONFLICT_MESSAGE);
	}

	@Test
	void createServiceInstanceWithMaintenanceInfoConflictWithCustomMessageFails() {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerMaintenanceInfoConflictException("nope old version"));

		client.put().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectBody()
				.jsonPath("$.error").isEqualTo(MAINTENANCE_INFO_CONFLICT_ERROR)
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, "nope old version"));
	}

	@Test
	void createServiceInstanceWithInvalidParametersFails() {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerInvalidParametersException("invalid parameters description"));

		client.put().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, "invalid parameters description"));
	}

	@Test
	void createServiceInstanceWithInvalidFieldsFails() throws Exception {
		String body = createRequestBody.replace("service_id", "service-1");

		client.put().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(body)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, "serviceDefinitionId"));
	}

	@Test
	void createServiceInstanceWithMissingFieldsFails() throws Exception {
		String body = "{}";

		client.put().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(body)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> {
					String responseBody = new String(Objects.requireNonNull(result.getResponseBody()),
							StandardCharsets.UTF_8);
					String description = JsonPath.read(responseBody, "$.description");
					assertThat(description).contains("planId", "serviceDefinitionId");
				});
	}

	@Test
	void getServiceInstanceSucceeds() throws Exception {
		setupServiceInstanceService(GetServiceInstanceResponse.builder()
				.build());

		client.get().uri(buildCreateUpdateUrl(PLATFORM_INSTANCE_ID, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();

		GetServiceInstanceRequest actualRequest = verifyGetServiceInstance();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void getServiceInstanceWithOperationInProgressFails() throws Exception {
		setupServiceInstanceService(new ServiceBrokerOperationInProgressException("task_10"));

		client.get().uri(buildCreateUpdateUrl(PLATFORM_INSTANCE_ID, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.consumeWith(result -> assertDescriptionContains(result, "operation=task_10"));
	}

	@Test
	void deleteServiceInstanceWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(DeleteServiceInstanceResponse.builder()
				.async(true)
				.operation("working")
				.build());

		client.delete().uri(buildDeleteUrl(PLATFORM_INSTANCE_ID, true))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isAccepted()
				.expectBody()
				.jsonPath("$.operation").isEqualTo("working");

		DeleteServiceInstanceRequest actualRequest = verifyDeleteServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void deleteServiceInstanceWithAsyncAndHeadersOperationInProgress() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerDeleteOperationInProgressException("task_10"));

		client.delete().uri(buildDeleteUrl(PLATFORM_INSTANCE_ID, true))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isAccepted()
				.expectBody()
				.jsonPath("$.operation").isEqualTo("task_10");

		verifyDeleteServiceInstance();
	}

	@Test
	void deleteServiceInstanceWithoutAsyncAndHeadersSucceeds() {
		setupCatalogService();

		setupServiceInstanceService(DeleteServiceInstanceResponse.builder()
				.build());

		client.delete().uri(buildDeleteUrl())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.json("{}");

		DeleteServiceInstanceRequest actualRequest = verifyDeleteServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void deleteServiceInstanceFiltersPlansSucceeds() {
		setupCatalogService();

		setupServiceInstanceService(DeleteServiceInstanceResponse
				.builder()
				.build());

		client.delete().uri(buildDeleteUrl())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.json("{}");

		DeleteServiceInstanceRequest actualRequest = verifyDeleteServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getPlan().getId()).isEqualTo("plan-three-id");
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void deleteServiceInstanceWithUnknownIdFails() {
		setupCatalogService();

		setupServiceInstanceService(new ServiceInstanceDoesNotExistException(SERVICE_INSTANCE_ID));

		client.delete().uri(buildDeleteUrl())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.GONE);
	}

	@Test
	void deleteServiceInstanceWithUnknownServiceDefinitionIdFails() {
		setupCatalogService(null);

		client.delete().uri(buildDeleteUrl())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(
						result -> assertDescriptionContains(result, String.format("id=%s", serviceDefinition.getId())));
	}

	@Test
	void deleteServiceInstanceWithMissingQueryParamsFails() {
		final String url = buildDeleteUrl(null, false).replace("plan_id", "plan-1");

		client.delete().uri(url)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath(".description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, "plan_id"));
	}

	@Test
	void updateServiceInstanceWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(UpdateServiceInstanceResponse.builder()
				.async(true)
				.operation("working")
				.dashboardUrl("https://dashboard.app.local")
				.build());

		client.patch().uri(buildCreateUpdateUrl(PLATFORM_INSTANCE_ID, true))
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(updateRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isAccepted()
				.expectBody()
				.jsonPath("$.operation").isEqualTo("working")
				.jsonPath("$.dashboard_url").isEqualTo("https://dashboard.app.local");

		UpdateServiceInstanceRequest actualRequest = verifyUpdateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void updateServiceInstanceWithAsyncAndHeadersOperationInProgress() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerUpdateOperationInProgressException("task_10"));

		client.patch().uri(buildCreateUpdateUrl(PLATFORM_INSTANCE_ID, true))
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(updateRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isAccepted()
				.expectBody()
				.jsonPath("$.operation").isEqualTo("task_10");

		verifyUpdateServiceInstance();
	}

	@Test
	void updateServiceInstanceWithoutAsyncAndHeadersSucceeds() {
		setupCatalogService();

		setupServiceInstanceService(UpdateServiceInstanceResponse.builder()
				.build());

		client.patch().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(updateRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.json("{}");

		UpdateServiceInstanceRequest actualRequest = verifyUpdateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getPlan()).isNull();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void updateServiceInstanceFiltersPlansSucceeds() {
		setupCatalogService();

		setupServiceInstanceService(UpdateServiceInstanceResponse.builder()
				.build());

		client.patch().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(updateRequestBodyWithPlan)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.json("{}");

		UpdateServiceInstanceRequest actualRequest = verifyUpdateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getPlan().getId()).isEqualTo("plan-three-id");
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void updateServiceInstanceResponseShouldNotContainEmptyValuesWhenNull() {
		setupCatalogService();

		setupServiceInstanceService(UpdateServiceInstanceResponse.builder()
				.dashboardUrl(null)
				.operation(null)
				.build());

		client.patch().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(updateRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.dashboard_url").doesNotExist()
				.jsonPath("$.operation").doesNotExist();
	}

	@Test
	void updateServiceInstanceResponseShouldNotContainEmptyValuesWhenEmpty() {
		setupCatalogService();

		setupServiceInstanceService(UpdateServiceInstanceResponse.builder()
				.dashboardUrl("")
				.operation("")
				.build());

		client.patch().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(updateRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.dashboard_url").doesNotExist()
				.jsonPath("$.operation").doesNotExist();
	}

	@Test
	void updateServiceInstanceWithUnknownServiceDefinitionIdFails() {
		setupCatalogService(null);

		client.patch().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(updateRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
				.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(
						result -> assertDescriptionContains(result, String.format("id=%s", serviceDefinition.getId())));
	}

	@Test
	void updateServiceInstanceWithUnsupportedOperationFails() {
		setupCatalogService();

		setupServiceInstanceService(new ServiceInstanceUpdateNotSupportedException("description"));

		client.patch().uri(buildCreateUpdateUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(updateRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.description").isNotEmpty()
				.consumeWith(result -> assertDescriptionContains(result, "description"));
	}

	@Test
	void lastOperationHasInProgressStatus() {
		setupServiceInstanceService(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.IN_PROGRESS)
				.description("working on it")
				.build());

		client.get().uri(buildLastOperationUrl())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.state").isEqualTo(OperationState.IN_PROGRESS.toString())
				.jsonPath("$.description").isEqualTo("working on it");

		GetLastServiceOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void lastOperationHasSucceededStatus() throws Exception {
		setupServiceInstanceService(GetLastServiceOperationResponse.builder()
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
				.jsonPath("$.description").isEqualTo("all good");

		GetLastServiceOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void lastOperationHasSucceededStatusWithDeletionComplete() {
		setupServiceInstanceService(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.description("all gone")
				.deleteOperation(true)
				.build());

		client.get().uri(buildLastOperationUrl())
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.GONE)
				.expectBody()
				.jsonPath("$.state").isEqualTo(OperationState.SUCCEEDED.toString())
				.jsonPath("$.description").isEqualTo("all gone");
	}

	@Test
	void lastOperationWithUnknownInstanceBadRequest() {
		setupServiceInstanceServiceLastOperation(new ServiceInstanceDoesNotExistException("nonexistent-instance-id"));

		client.get().uri(buildLastOperationUrl())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
				.expectBody()
				.jsonPath("$.state").doesNotExist()
				.jsonPath("$.description").value(containsString("The requested Service Instance does not exist"));
	}

	@Test
	void lastOperationHasFailedStatus() {
		setupServiceInstanceService(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.FAILED)
				.description("not so good")
				.build());

		client.get().uri(buildLastOperationUrl())
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.state").isEqualTo(OperationState.FAILED.toString())
				.jsonPath("$.description").isEqualTo("not so good");
	}

}
