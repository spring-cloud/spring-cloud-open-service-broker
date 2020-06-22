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

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.cloud.servicebroker.autoconfigure.web.AbstractServiceInstanceBindingControllerIntegrationTest;
import org.springframework.cloud.servicebroker.controller.ServiceBrokerWebMvcExceptionHandler;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerCreateOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerDeleteOperationInProgressException;
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
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.API_INFO_LOCATION_HEADER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ServiceInstanceBindingControllerIntegrationTest extends AbstractServiceInstanceBindingControllerIntegrationTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setControllerAdvice(ServiceBrokerWebMvcExceptionHandler.class)
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}

	@Test
	void createBindingToAppWithoutAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse.builder()
				.bindingExisted(false)
				.build());

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUrl(PLATFORM_INSTANCE_ID, false))
				.content(createRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isCreated());

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void createBindingToAppFiltersPlansSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse
				.builder()
				.bindingExisted(false)
				.build());

		MvcResult mvcResult = mockMvc
				.perform(put(buildCreateUrl(PLATFORM_INSTANCE_ID, false))
						.content(createRequestBody)
						.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
						.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isCreated());

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getPlan().getId()).isEqualTo(actualRequest.getPlanId());
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void createBindingToAppWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse.builder()
				.async(true)
				.operation("working")
				.bindingExisted(false)
				.build());

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUrl(PLATFORM_INSTANCE_ID, true))
				.content(createRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", equalTo("working")));

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void createBindingToAppWithAsyncAndHeadersOperationInProgress() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(new ServiceBrokerCreateOperationInProgressException("task_10"));

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUrl(PLATFORM_INSTANCE_ID, true))
				.content(createRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", is("task_10")));

		verifyCreateBinding();
	}

	@Test
	void createBindingToAppWithExistingSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse.builder()
				.bindingExisted(true)
				.build());

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUrl())
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk());

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createBindingToRouteWithoutAsyncHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse.builder()
				.bindingExisted(false)
				.build());

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUrl())
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isCreated());

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createBindingToRouteWithAsyncHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse.builder()
				.async(true)
				.operation("working")
				.bindingExisted(false)
				.build());

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUrl(null, true))
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", equalTo("working")));

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void createBindingToRouteWithExistingSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse.builder()
				.bindingExisted(true)
				.build());

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUrl())
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk());
	}

	@Test
	void createBindingWithUnknownServiceInstanceIdFails() throws Exception {
		setupCatalogService();

		given(serviceInstanceBindingService
				.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.willThrow(new ServiceInstanceDoesNotExistException(SERVICE_INSTANCE_ID));

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUrl())
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(SERVICE_INSTANCE_ID)));
	}

	@Test
	void createBindingWithUnknownServiceDefinitionIdSucceeds() throws Exception {
		setupCatalogService(null);

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUrl())
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", containsString(serviceDefinition.getId())));
	}

	@Test
	void createBindingWithDuplicateIdFails() throws Exception {
		setupCatalogService();

		given(serviceInstanceBindingService
				.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.willThrow(new ServiceInstanceBindingExistsException(SERVICE_INSTANCE_ID, SERVICE_INSTANCE_BINDING_ID));

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUrl())
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.description", containsString(SERVICE_INSTANCE_ID)))
				.andExpect(jsonPath("$.description", containsString(SERVICE_INSTANCE_BINDING_ID)));
	}

	@Test
	void createBindingWithInvalidFieldsFails() throws Exception {
		String body = createRequestBody.replace("service_id", "service-1");

		mockMvc.perform(put(buildCreateUrl())
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncNotStarted())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")));
	}

	@Test
	void createBindingWithMissingFieldsFails() throws Exception {
		String body = "{}";

		mockMvc.perform(put(buildCreateUrl())
				.content(body)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncNotStarted())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")))
				.andExpect(jsonPath("$.description", containsString("planId")));
	}

	@Test
	void getBindingToAppSucceeds() throws Exception {
		setupServiceInstanceBindingService(GetServiceInstanceAppBindingResponse.builder()
				.build());

		MvcResult mvcResult = mockMvc.perform(get(buildCreateUrl(PLATFORM_INSTANCE_ID, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk());

		GetServiceInstanceBindingRequest actualRequest = verifyGetBinding();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void getBindingToRouteSucceeds() throws Exception {
		setupServiceInstanceBindingService(GetServiceInstanceRouteBindingResponse.builder()
				.build());

		MvcResult mvcResult = mockMvc.perform(get(buildCreateUrl(PLATFORM_INSTANCE_ID, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk());

		GetServiceInstanceBindingRequest actualRequest = verifyGetBinding();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void getBindingWithOperationInProgressFails() throws Exception {
		given(serviceInstanceBindingService.getServiceInstanceBinding(any(GetServiceInstanceBindingRequest.class)))
				.willThrow(new ServiceBrokerOperationInProgressException("task_10"));

		MvcResult mvcResult = mockMvc.perform(get(buildCreateUrl())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteBindingWithoutAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(DeleteServiceInstanceBindingResponse.builder()
				.build());

		MvcResult mvcResult = mockMvc.perform(delete(buildDeleteUrl(PLATFORM_INSTANCE_ID, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(content().string("{}"));

		then(serviceInstanceBindingService)
				.should()
				.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		DeleteServiceInstanceBindingRequest actualRequest = verifyDeleteBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void deleteBindingWithoutAsyncAndHeadersOperationInProgress() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(new ServiceBrokerDeleteOperationInProgressException("task_10"));

		MvcResult mvcResult = mockMvc.perform(delete(buildDeleteUrl(PLATFORM_INSTANCE_ID, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", is("task_10")));

		then(serviceInstanceBindingService)
				.should()
				.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		verifyDeleteBinding();
	}

	@Test
	void deleteBindingWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(DeleteServiceInstanceBindingResponse.builder()
				.async(true)
				.operation("working")
				.build());

		MvcResult mvcResult = mockMvc.perform(delete(buildDeleteUrl(PLATFORM_INSTANCE_ID, true))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", equalTo("working")));

		then(serviceInstanceBindingService)
				.should()
				.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		DeleteServiceInstanceBindingRequest actualRequest = verifyDeleteBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(true);
		assertHeaderValuesSet(actualRequest);
	}


	@Test
	void deleteBindingFiltersPlansSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(DeleteServiceInstanceBindingResponse
				.builder()
				.build());

		MvcResult mvcResult = mockMvc
				.perform(delete(buildDeleteUrl(PLATFORM_INSTANCE_ID, false))
						.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
						.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(content().string("{}"));

		then(serviceInstanceBindingService)
				.should()
				.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		DeleteServiceInstanceBindingRequest actualRequest = verifyDeleteBinding();
		assertThat(actualRequest.isAsyncAccepted()).isEqualTo(false);
		assertThat(actualRequest.getPlan().getId()).isEqualTo(actualRequest.getPlanId());
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void deleteBindingWithUnknownInstanceIdFails() throws Exception {
		setupCatalogService();

		doThrow(new ServiceInstanceDoesNotExistException(SERVICE_INSTANCE_ID))
				.when(serviceInstanceBindingService)
				.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		MvcResult mvcResult = mockMvc.perform(delete(buildDeleteUrl())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(SERVICE_INSTANCE_ID)));
	}

	@Test
	void deleteBindingWithUnknownBindingIdFails() throws Exception {
		setupCatalogService();

		doThrow(new ServiceInstanceBindingDoesNotExistException(SERVICE_INSTANCE_BINDING_ID))
				.when(serviceInstanceBindingService)
				.deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		MvcResult mvcResult = mockMvc.perform(delete(buildDeleteUrl())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isGone());
	}

	@Test
	void deleteBindingWithUnknownServiceDefinitionIdFails() throws Exception {
		setupCatalogService(null);

		MvcResult mvcResult = mockMvc.perform(delete(buildDeleteUrl())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deleteBindingWithMissingQueryParamsFails() throws Exception {
		final String url = buildDeleteUrl(null, false).replace("plan_id", "plan-1");

		mockMvc.perform(delete(url)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.description", containsString("plan_id")))
				.andReturn();
	}

	@Test
	void lastOperationHasInProgressStatus() throws Exception {
		setupServiceInstanceBindingService(GetLastServiceBindingOperationResponse.builder()
				.operationState(OperationState.IN_PROGRESS)
				.description("working on it")
				.build());

		MvcResult mvcResult = mockMvc.perform(get(buildLastOperationUrl())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state", is(OperationState.IN_PROGRESS.toString())))
				.andExpect(jsonPath("$.description", is("working on it")));

		GetLastServiceBindingOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	void lastOperationHasSucceededStatus() throws Exception {
		setupServiceInstanceBindingService(GetLastServiceBindingOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.description("all good")
				.build());

		MvcResult mvcResult = mockMvc.perform(get(buildLastOperationUrl(PLATFORM_INSTANCE_ID))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader()))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state", is(OperationState.SUCCEEDED.toString())))
				.andExpect(jsonPath("$.description", is("all good")));

		GetLastServiceBindingOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	void lastOperationHasSucceededStatusWithDeletionComplete() throws Exception {
		setupServiceInstanceBindingService(GetLastServiceBindingOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.description("all gone")
				.deleteOperation(true)
				.build());

		MvcResult mvcResult = mockMvc.perform(get(buildLastOperationUrl()))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isGone())
				.andExpect(jsonPath("$.state", is(OperationState.SUCCEEDED.toString())))
				.andExpect(jsonPath("$.description", is("all gone")));
	}

	@Test
	void lastOperationHasFailedStatus() throws Exception {
		setupServiceInstanceBindingService(GetLastServiceBindingOperationResponse.builder()
				.operationState(OperationState.FAILED)
				.description("not so good")
				.build());

		MvcResult mvcResult = mockMvc.perform(get(buildLastOperationUrl()))
				.andExpect(request().asyncStarted())
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state", is(OperationState.FAILED.toString())))
				.andExpect(jsonPath("$.description", is("not so good")));
	}

}
