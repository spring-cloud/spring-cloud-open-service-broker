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

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.cloud.servicebroker.autoconfigure.web.AbstractServiceInstanceControllerIntegrationTest;
import org.springframework.cloud.servicebroker.controller.ServiceBrokerWebMvcExceptionHandler;
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
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException.ASYNC_REQUIRED_ERROR;
import static org.springframework.cloud.servicebroker.exception.ServiceBrokerMaintenanceInfoConflictException.MAINTENANCE_INFO_CONFLICT_ERROR;
import static org.springframework.cloud.servicebroker.exception.ServiceBrokerMaintenanceInfoConflictException.MAINTENANCE_INFO_CONFLICT_MESSAGE;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.API_INFO_LOCATION_HEADER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ServiceInstanceControllerIntegrationTest extends AbstractServiceInstanceControllerIntegrationTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.controller)
				.setControllerAdvice(ServiceBrokerWebMvcExceptionHandler.class)
				.setMessageConverters(new MappingJackson2HttpMessageConverter())
				.build();
	}

	@Test
	void createServiceInstanceWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.async(true)
				.build());

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl(PLATFORM_INSTANCE_ID, true))
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isAccepted());

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

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl(PLATFORM_INSTANCE_ID, true))
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", is("task_10")))
				.andExpect(jsonPath("$.dashboard_url", is("https://dashboard.app.local")));

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void createServiceInstanceWithAsyncAndHeadersOperationInProgress() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerCreateOperationInProgressException("task_10"));

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl(PLATFORM_INSTANCE_ID, true))
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", is("task_10")));

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void createServiceInstanceResponseShouldNotContainEmptyValuesWhenNull() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.operation(null)
				.dashboardUrl(null)
				.build());

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.dashboard_url").doesNotExist())
				.andExpect(jsonPath("$.operation").doesNotExist());
	}

	@Test
	void createServiceInstanceResponseShouldNotContainEmptyValuesWhenEmpty() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.operation("")
				.dashboardUrl("")
				.build());

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.dashboard_url").doesNotExist())
				.andExpect(jsonPath("$.operation").doesNotExist());
	}

	@Test
	void createServiceInstanceWithEmptyPlatformInstanceIdSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.async(true)
				.build());

		// force a condition where the platformInstanceId segment is present but empty
		// e.g. https://test.app.local//v2/service_instances/[guid]
		String url = "https://test.app.local/" + buildCreateUpdateUrl();
		MvcResult mvcResult = mockMvc.perform(put(url)
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isAccepted());

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createServiceInstanceWithoutAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.build());

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isCreated());

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createServiceInstanceWithExistingInstanceSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.instanceExisted(true)
				.build());

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk());
	}

	@Test
	void createServiceInstanceFiltersPlansSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(CreateServiceInstanceResponse
				.builder()
				.build());

		MvcResult mvcResult = mockMvc
				.perform(put(buildCreateUpdateUrl())
						.content(createRequestBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isCreated());

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getPlan().getId()).isEqualTo(actualRequest.getPlanId());
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createServiceInstanceWithUnknownServiceDefinitionIdFails() throws Exception {
		setupCatalogService(null);

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", containsString(serviceDefinition.getId())));
	}

	@Test
	void createDuplicateServiceInstanceIdFails() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceInstanceExistsException(SERVICE_INSTANCE_ID, serviceDefinition.getId()));

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.description", containsString(SERVICE_INSTANCE_ID)));
	}

	@Test
	void createServiceInstanceWithAsyncRequiredFails() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerAsyncRequiredException("async required description"));

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.error", is(ASYNC_REQUIRED_ERROR)))
				.andExpect(jsonPath("$.description", endsWith("async required description")));
	}

	@Test
	void createServiceInstanceWithMaintenanceInfoConflictFails() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerMaintenanceInfoConflictException());

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.error", is(MAINTENANCE_INFO_CONFLICT_ERROR)))
				.andExpect(jsonPath("$.description", equalTo(MAINTENANCE_INFO_CONFLICT_MESSAGE)));
	}

	@Test
	void createServiceInstanceWithMaintenanceInfoConflictWithCustomMessageFails() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerMaintenanceInfoConflictException("nope old version"));

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.error", is(MAINTENANCE_INFO_CONFLICT_ERROR)))
				.andExpect(jsonPath("$.description", endsWith("nope old version")));
	}

	@Test
	void createServiceInstanceWithInvalidParametersFails() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerInvalidParametersException("invalid parameters description"));

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", endsWith("invalid parameters description")));
	}

	@Test
	void createServiceInstanceWithExceptionFails() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new RuntimeException("unknown error"));

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.description", endsWith("unknown error")));
	}

	@Test
	void createServiceInstanceWithInvalidFieldsFails() throws Exception {
		String body = createRequestBody.replace("service_id", "service-1");

		mockMvc.perform(put(buildCreateUpdateUrl())
				.content(body)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")))
				.andExpect(request().asyncNotStarted())
				.andReturn();
	}

	@Test
	void createServiceInstanceWithMissingFieldsFails() throws Exception {
		String body = "{}";

		mockMvc.perform(put(buildCreateUpdateUrl())
				.content(body)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")))
				.andExpect(jsonPath("$.description", containsString("planId")))
				.andExpect(request().asyncNotStarted())
				.andReturn();
	}

	@Test
	void createServiceInstanceWithEmptyBodyFails() throws Exception {
		final String body = "";

		mockMvc.perform(put(buildCreateUpdateUrl())
						.content(body)
						.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
						.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", containsString("Required request body is missing")))
				.andExpect(request().asyncNotStarted())
				.andReturn();
	}

	@Test
	void createServiceInstanceWithEmptyBodyAndNoContentTypeFails() throws Exception {
		final String body = "";

		mockMvc.perform(put(buildCreateUpdateUrl())
						.content(body)
						.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
						.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", containsString("Required request body is missing")))
				.andExpect(request().asyncNotStarted())
				.andReturn();
	}

	@Test
	void createServiceInstanceWithEmptyBodyAndMismatchedContentTypeFails() throws Exception {
		final String body = "";

		mockMvc.perform(put(buildCreateUpdateUrl())
						.content(body)
						.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
						.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
						.contentType(MediaType.TEXT_PLAIN)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", containsString("Content-Type 'text/plain' is not supported")))
				.andExpect(request().asyncNotStarted())
				.andReturn();
	}

	@Test
	void getServiceInstanceSucceeds() throws Exception {
		setupServiceInstanceService(GetServiceInstanceResponse.builder()
				.build());

		MvcResult mvcResult = mockMvc.perform(get(buildGetUrl(PLATFORM_INSTANCE_ID))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk());

		GetServiceInstanceRequest actualRequest = verifyGetServiceInstance();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void getServiceInstanceWithParamsSucceeds() throws Exception {
		setupServiceInstanceService(GetServiceInstanceResponse.builder()
				.build());

		MvcResult mvcResult = mockMvc.perform(get(buildGetUrl(PLATFORM_INSTANCE_ID, "service-definition-id",
				"plan-id", false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk());

		GetServiceInstanceRequest actualRequest = verifyGetServiceInstance();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void getServiceInstanceWithOperationInProgressFails() throws Exception {
		setupServiceInstanceService(new ServiceBrokerOperationInProgressException("task_10"));

		MvcResult mvcResult = mockMvc.perform(get(buildGetUrl(PLATFORM_INSTANCE_ID))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.description", containsString("task_10")));
	}

	@Test
	void deleteServiceInstanceWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(DeleteServiceInstanceResponse.builder()
				.async(true)
				.operation("working")
				.build());

		MvcResult mvcResult = mockMvc.perform(delete(buildDeleteUrl(PLATFORM_INSTANCE_ID, true))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", equalTo("working")));

		DeleteServiceInstanceRequest actualRequest = verifyDeleteServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void deleteServiceInstanceWithAsyncAndHeadersOperationInProgress() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerDeleteOperationInProgressException("task_10"));

		MvcResult mvcResult = mockMvc.perform(delete(buildDeleteUrl(PLATFORM_INSTANCE_ID, true))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", is("task_10")));

		verifyDeleteServiceInstance();
	}

	@Test
	void deleteServiceInstanceWithoutAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(DeleteServiceInstanceResponse.builder()
				.build());

		MvcResult mvcResult = mockMvc.perform(delete(buildDeleteUrl())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(content().string("{}"));

		DeleteServiceInstanceRequest actualRequest = verifyDeleteServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void deleteServiceInstanceFiltersPlansSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(DeleteServiceInstanceResponse.builder()
				.build());

		MvcResult mvcResult = mockMvc
				.perform(delete(buildDeleteUrl())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(content().string("{}"));

		DeleteServiceInstanceRequest actualRequest = verifyDeleteServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getPlan().getId()).isEqualTo(actualRequest.getPlanId());
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void deleteServiceInstanceWithUnknownIdFails() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceInstanceDoesNotExistException(SERVICE_INSTANCE_ID));

		MvcResult mvcResult = mockMvc.perform(delete(buildDeleteUrl())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isGone());
	}

	@Test
	void deleteServiceInstanceWithUnknownServiceDefinitionIdFails() throws Exception {
		setupCatalogService(null);

		MvcResult mvcResult = mockMvc.perform(delete(buildDeleteUrl())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", containsString(serviceDefinition.getId())));
	}

	@Test
	void deleteServiceInstanceWithMissingQueryParamsFails() throws Exception {
		final String url = buildDeleteUrl(null, false).replace("plan_id", "plan-1");

		mockMvc.perform(delete(url)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", containsString("plan_id")))
				.andReturn();
	}

	@Test
	void updateServiceInstanceWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(UpdateServiceInstanceResponse.builder()
				.async(true)
				.operation("working")
				.dashboardUrl("https://dashboard.app.local")
				.build());

		MvcResult mvcResult = mockMvc.perform(patch(buildCreateUpdateUrl(PLATFORM_INSTANCE_ID, true))
				.content(updateRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", equalTo("working")))
				.andExpect(jsonPath("$.dashboard_url", equalTo("https://dashboard.app.local")));

		UpdateServiceInstanceRequest actualRequest = verifyUpdateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void updateServiceInstanceWithAsyncAndHeadersOperationInProgress() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceBrokerUpdateOperationInProgressException("task_10"));

		MvcResult mvcResult = mockMvc.perform(patch(buildCreateUpdateUrl(PLATFORM_INSTANCE_ID, true))
				.content(updateRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", is("task_10")));

		verifyUpdateServiceInstance();
	}

	@Test
	void updateServiceInstanceWithoutSyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(UpdateServiceInstanceResponse.builder()
				.build());

		MvcResult mvcResult = mockMvc.perform(patch(buildCreateUpdateUrl())
				.content(updateRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(content().string("{}"));

		UpdateServiceInstanceRequest actualRequest = verifyUpdateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getServiceDefinition().getPlans().size()).isEqualTo(3);
		assertThat(actualRequest.getPlan()).isNull();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void updateServiceInstanceFiltersPlansSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(UpdateServiceInstanceResponse
				.builder()
				.build());

		MvcResult mvcResult = mockMvc
				.perform(patch(buildCreateUpdateUrl())
						.content(updateRequestBodyWithPlan)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(content().string("{}"));

		UpdateServiceInstanceRequest actualRequest = verifyUpdateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getPlan().getId()).isEqualTo(actualRequest.getPlanId());
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void updateServiceInstanceResponseShouldNotContainEmptyValuesWhenNull() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(UpdateServiceInstanceResponse.builder()
				.dashboardUrl(null)
				.operation(null)
				.build());

		mockMvc.perform(patch(buildCreateUpdateUrl())
				.content(updateRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.dashboard_url").doesNotExist())
				.andExpect(jsonPath("$.operation").doesNotExist());
	}

	@Test
	void updateServiceInstanceResponseShouldNotContainEmptyValuesWhenEmpty() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(UpdateServiceInstanceResponse.builder()
				.dashboardUrl("")
				.operation("")
				.build());

		mockMvc.perform(patch(buildCreateUpdateUrl())
				.content(updateRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.dashboard_url").doesNotExist())
				.andExpect(jsonPath("$.operation").doesNotExist());
	}

	@Test
	void updateServiceInstanceWithUnknownServiceDefinitionIdFails() throws Exception {
		setupCatalogService(null);

		MvcResult mvcResult = mockMvc.perform(patch(buildCreateUpdateUrl())
				.content(updateRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.description", containsString(serviceDefinition.getId())));
	}

	@Test
	void updateServiceInstanceWithUnsupportedOperationFails() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceInstanceUpdateNotSupportedException("description"));

		MvcResult mvcResult = mockMvc.perform(patch(buildCreateUpdateUrl())
				.content(updateRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.description", containsString("description")));
	}

	@Test
	void lastOperationHasInProgressStatus() throws Exception {
		setupServiceInstanceService(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.IN_PROGRESS)
				.description("working on it")
				.build());

		MvcResult mvcResult = mockMvc.perform(get(buildLastOperationUrl()))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state", is(OperationState.IN_PROGRESS.toString())))
				.andExpect(jsonPath("$.description", is("working on it")));

		GetLastServiceOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void lastOperationHasSucceededStatus() throws Exception {
		setupServiceInstanceService(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.description("all good")
				.build());

		MvcResult mvcResult = mockMvc.perform(get(buildLastOperationUrl(PLATFORM_INSTANCE_ID))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader()))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state", is(OperationState.SUCCEEDED.toString())))
				.andExpect(jsonPath("$.description", is("all good")));

		GetLastServiceOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void lastOperationHasSucceededStatusWithDeletionComplete() throws Exception {
		setupServiceInstanceService(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.description("all gone")
				.deleteOperation(true)
				.build());

		MvcResult mvcResult = mockMvc.perform(get(buildLastOperationUrl()))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isGone())
				.andExpect(jsonPath("$.state", is(OperationState.SUCCEEDED.toString())))
				.andExpect(jsonPath("$.description", is("all gone")));
	}

	@Test
	void lastOperationWithUnknownInstanceBadRequest() throws Exception {
		setupServiceInstanceServiceLastOperation(new ServiceInstanceDoesNotExistException("nonexistent-instance-id"));

		MvcResult mvcResult = mockMvc.perform(get(buildLastOperationUrl()))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.state").doesNotExist())
				.andExpect(jsonPath("$.description",
						containsString("The requested Service Instance does not exist")));
	}

	@Test
	void lastOperationHasFailedStatus() throws Exception {
		setupServiceInstanceService(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.FAILED)
				.description("not so good")
				.build());

		MvcResult mvcResult = mockMvc.perform(get(buildLastOperationUrl()))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state", is(OperationState.FAILED.toString())))
				.andExpect(jsonPath("$.description", is("not so good")));
	}

}
