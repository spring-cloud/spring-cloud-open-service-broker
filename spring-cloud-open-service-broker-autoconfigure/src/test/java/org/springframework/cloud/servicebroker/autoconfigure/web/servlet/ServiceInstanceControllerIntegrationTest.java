/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceController;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.springframework.cloud.servicebroker.model.AsyncRequiredErrorMessage;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.API_INFO_LOCATION_HEADER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInstanceControllerIntegrationTest extends ControllerIntegrationTest {

	private static final String SERVICE_INSTANCES_ROOT_PATH = "/v2/service_instances/";

	private static final String SERVICE_INSTANCE_ID = "service-instance-id";

	private MockMvc mockMvc;

	@InjectMocks
	private ServiceInstanceController controller;

	@Mock
	private ServiceInstanceService serviceInstanceService;

	private ServiceDefinition serviceDefinition;

	private UriComponentsBuilder uriBuilder;
	private UriComponentsBuilder cfInstanceIdUriBuilder;

	private String createRequestBody;
	private String updateRequestBody;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setMessageConverters(new MappingJackson2HttpMessageConverter())
				.build();

		uriBuilder = UriComponentsBuilder.fromPath(SERVICE_INSTANCES_ROOT_PATH);
		cfInstanceIdUriBuilder = UriComponentsBuilder.fromPath("/").path(CF_INSTANCE_ID).path(SERVICE_INSTANCES_ROOT_PATH);

		serviceDefinition = ServiceFixture.getSimpleService();

		createRequestBody = DataFixture.toJson(CreateServiceInstanceRequest.builder()
				.serviceDefinitionId(serviceDefinition.getId())
				.planId("standard")
				.build());

		updateRequestBody = DataFixture.toJson(UpdateServiceInstanceRequest.builder()
				.serviceDefinitionId(serviceDefinition.getId())
				.planId("standard")
				.build());
	}

	@Test
	public void createServiceInstanceWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService(serviceDefinition.getId(), serviceDefinition);

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.async(true)
				.build());

		mockMvc.perform(put(buildCreateUpdateUrl(true, true))
				.content(createRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted());

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted(), equalTo(true));
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void createServiceInstanceWithoutAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService(serviceDefinition.getId(), serviceDefinition);

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.build());

		mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted(), equalTo(false));
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	public void createServiceInstanceWithExistingInstanceSucceeds() throws Exception {
		setupCatalogService(serviceDefinition.getId(), serviceDefinition);

		setupServiceInstanceService(CreateServiceInstanceResponse.builder()
				.instanceExisted(true)
				.build());

		mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void createServiceInstanceWithUnknownServiceDefinitionIdFails() throws Exception {
		setupCatalogService(serviceDefinition.getId(), null);

		mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(serviceDefinition.getId())));
	}

	@Test
	public void createDuplicateServiceInstanceIdFails() throws Exception {
		setupCatalogService(serviceDefinition.getId(), serviceDefinition);

		setupServiceInstanceService(new ServiceInstanceExistsException(SERVICE_INSTANCE_ID, serviceDefinition.getId()));

		mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.description", containsString(SERVICE_INSTANCE_ID)));
	}

	@Test
	public void createServiceInstanceWithAsyncRequiredFails() throws Exception {
		setupCatalogService(serviceDefinition.getId(), serviceDefinition);

		setupServiceInstanceService(new ServiceBrokerAsyncRequiredException("async required description"));

		mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.error", is(AsyncRequiredErrorMessage.ASYNC_REQUIRED_ERROR)))
				.andExpect(jsonPath("$.description", is("async required description")));
	}

	@Test
	public void createServiceInstanceWithInvalidParametersFails() throws Exception {
		setupCatalogService(serviceDefinition.getId(), serviceDefinition);

		setupServiceInstanceService(new ServiceBrokerInvalidParametersException("invalid parameters description"));

		mockMvc.perform(put(buildCreateUpdateUrl())
				.content(createRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", is("invalid parameters description")));
	}

	@Test
	public void createServiceInstanceWithInvalidFieldsFails() throws Exception {
		String body = createRequestBody.replace("service_id", "foo");

		mockMvc.perform(put(buildCreateUpdateUrl())
				.content(body)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")));
	}

	@Test
	public void createServiceInstanceWithMissingFieldsFails() throws Exception {
		String body = "{}";

		mockMvc.perform(put(buildCreateUpdateUrl())
				.content(body)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")))
				.andExpect(jsonPath("$.description", containsString("planId")));
	}

	@Test
	public void deleteServiceInstanceWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService(serviceDefinition.getId(), serviceDefinition);

		setupServiceInstanceService(DeleteServiceInstanceResponse.builder()
				.async(true)
				.operation("working")
				.build());

		mockMvc.perform(delete(buildDeleteUrl(true, true))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", equalTo("working")));

		DeleteServiceInstanceRequest actualRequest = verifyDeleteServiceInstance();
		assertThat(actualRequest.isAsyncAccepted(), equalTo(true));
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void deleteServiceInstanceWithoutAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService(serviceDefinition.getId(), serviceDefinition);

		setupServiceInstanceService(DeleteServiceInstanceResponse.builder()
				.build());

		mockMvc.perform(delete(buildDeleteUrl())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("{}"));

		DeleteServiceInstanceRequest actualRequest = verifyDeleteServiceInstance();
		assertThat(actualRequest.isAsyncAccepted(), equalTo(false));
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	public void deleteServiceInstanceWithUnknownIdFails() throws Exception {
		setupCatalogService(serviceDefinition.getId(), serviceDefinition);

		setupServiceInstanceService(new ServiceInstanceDoesNotExistException(SERVICE_INSTANCE_ID));

		mockMvc.perform(delete(buildDeleteUrl())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isGone())
				.andExpect(jsonPath("$", aMapWithSize(0)));
	}

	@Test
	public void deleteServiceInstanceWithUnknownServiceDefinitionIdFails() throws Exception {
		setupCatalogService(serviceDefinition.getId(), null);

		mockMvc.perform(delete(buildDeleteUrl())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(serviceDefinition.getId())));
	}

	@Test
	public void updateServiceInstanceWithAsyncAndHeadersSucceeds() throws Exception {
		setupCatalogService(serviceDefinition.getId(), serviceDefinition);

		setupServiceInstanceService(UpdateServiceInstanceResponse.builder()
				.async(true)
				.operation("working")
				.build());

		mockMvc.perform(patch(buildCreateUpdateUrl(true, true))
				.content(updateRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", equalTo("working")));

		UpdateServiceInstanceRequest actualRequest = verifyUpdateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted(), equalTo(true));
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void updateServiceInstanceWithoutSyncAndHeadersSucceeds() throws Exception {
		setupCatalogService(serviceDefinition.getId(), serviceDefinition);

		setupServiceInstanceService(UpdateServiceInstanceResponse.builder()
				.build());

		mockMvc.perform(patch(buildCreateUpdateUrl())
				.content(updateRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("{}"));

		UpdateServiceInstanceRequest actualRequest = verifyUpdateServiceInstance();
		assertThat(actualRequest.isAsyncAccepted(), equalTo(false));
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	public void updateServiceInstanceWithUnknownServiceDefinitionIdFails() throws Exception {
		setupCatalogService(serviceDefinition.getId(), null);

		mockMvc.perform(patch(buildCreateUpdateUrl())
				.content(updateRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.description", containsString(serviceDefinition.getId())));
	}

	@Test
	public void updateServiceInstanceWithUnsupportedOperationFails() throws Exception {
		setupCatalogService(serviceDefinition.getId(), serviceDefinition);

		setupServiceInstanceService(new ServiceInstanceUpdateNotSupportedException("description"));

		mockMvc.perform(patch(buildCreateUpdateUrl())
				.content(updateRequestBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.description", containsString("description")));
	}

	@Test
	public void lastOperationHasInProgressStatus() throws Exception {
		setupServiceInstanceService(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.IN_PROGRESS)
				.description("working on it")
				.build());

		mockMvc.perform(get(buildLastOperationUrl(false)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state", is(OperationState.IN_PROGRESS.toString())))
				.andExpect(jsonPath("$.description", is("working on it")));

		GetLastServiceOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	public void lastOperationHasSucceededStatus() throws Exception {
		setupServiceInstanceService(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.description("all good")
				.build());

		mockMvc.perform(get(buildLastOperationUrl(true))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state", is(OperationState.SUCCEEDED.toString())))
				.andExpect(jsonPath("$.description", is("all good")));

		GetLastServiceOperationRequest actualRequest = verifyLastOperation();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void lastOperationHasSucceededStatusWithDeletionComplete() throws Exception {
		setupServiceInstanceService(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.description("all gone")
				.deleteOperation(true)
				.build());

		mockMvc.perform(get(buildLastOperationUrl(false)))
				.andExpect(status().isGone())
				.andExpect(jsonPath("$.state", is(OperationState.SUCCEEDED.toString())))
				.andExpect(jsonPath("$.description", is("all gone")));
	}

	@Test
	public void lastOperationHasFailedStatus() throws Exception {
		setupServiceInstanceService(GetLastServiceOperationResponse.builder()
				.operationState(OperationState.FAILED)
				.description("not so good")
				.build());

		mockMvc.perform(get(buildLastOperationUrl(false)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state", is(OperationState.FAILED.toString())))
				.andExpect(jsonPath("$.description", is("not so good")));
	}

	private void setupServiceInstanceService(CreateServiceInstanceResponse response) {
		when(serviceInstanceService.createServiceInstance(any(CreateServiceInstanceRequest.class)))
				.thenReturn(response);
	}

	private void setupServiceInstanceService(Exception exception) {
		when(serviceInstanceService.createServiceInstance(any(CreateServiceInstanceRequest.class)))
				.thenThrow(exception);
	}

	private void setupServiceInstanceService(DeleteServiceInstanceResponse response) {
		when(serviceInstanceService.deleteServiceInstance(any(DeleteServiceInstanceRequest.class)))
				.thenReturn(response);
	}

	private void setupServiceInstanceService(ServiceInstanceDoesNotExistException exception) {
		when(serviceInstanceService.deleteServiceInstance(any(DeleteServiceInstanceRequest.class)))
				.thenThrow(exception);
	}

	private void setupServiceInstanceService(UpdateServiceInstanceResponse response) {
		when(serviceInstanceService.updateServiceInstance(any(UpdateServiceInstanceRequest.class)))
				.thenReturn(response);
	}

	private void setupServiceInstanceService(ServiceInstanceUpdateNotSupportedException exception) {
		when(serviceInstanceService.updateServiceInstance(any(UpdateServiceInstanceRequest.class)))
				.thenThrow(exception);
	}

	private void setupServiceInstanceService(GetLastServiceOperationResponse response) {
		when(serviceInstanceService.getLastOperation(any(GetLastServiceOperationRequest.class)))
				.thenReturn(response);
	}

	private String buildCreateUpdateUrl() {
		return buildCreateUpdateUrl(false, false);
	}

	private String buildCreateUpdateUrl(Boolean withCfInstanceId, boolean asyncAccepted) {
		UriComponentsBuilder builder = withCfInstanceId ? cfInstanceIdUriBuilder : uriBuilder;
		return builder.path(SERVICE_INSTANCE_ID)
				.queryParam("accepts_incomplete", asyncAccepted)
				.toUriString();
	}

	private String buildDeleteUrl() {
		return buildDeleteUrl(false, false);
	}

	private String buildDeleteUrl(boolean withCfInstanceId, boolean asyncAccepted) {
		UriComponentsBuilder builder = withCfInstanceId ? cfInstanceIdUriBuilder : uriBuilder;
		return builder.path(SERVICE_INSTANCE_ID)
				.queryParam("service_id", serviceDefinition.getId())
				.queryParam("plan_id", "standard")
				.queryParam("accepts_incomplete", asyncAccepted)
				.toUriString();
	}

	private String buildLastOperationUrl(Boolean withCfInstanceId) {
		UriComponentsBuilder builder = withCfInstanceId ? cfInstanceIdUriBuilder : uriBuilder;
		return builder.pathSegment(SERVICE_INSTANCE_ID, "last_operation")
				.queryParam("service_id", serviceDefinition.getId())
				.queryParam("plan_id", "standard")
				.queryParam("operation", "working")
				.toUriString();
	}

	private CreateServiceInstanceRequest verifyCreateServiceInstance() {
		ArgumentCaptor<CreateServiceInstanceRequest> argumentCaptor = ArgumentCaptor.forClass(CreateServiceInstanceRequest.class);
		Mockito.verify(serviceInstanceService).createServiceInstance(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	private DeleteServiceInstanceRequest verifyDeleteServiceInstance() {
		ArgumentCaptor<DeleteServiceInstanceRequest> argumentCaptor = ArgumentCaptor.forClass(DeleteServiceInstanceRequest.class);
		Mockito.verify(serviceInstanceService).deleteServiceInstance(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	private UpdateServiceInstanceRequest verifyUpdateServiceInstance() {
		ArgumentCaptor<UpdateServiceInstanceRequest> argumentCaptor = ArgumentCaptor.forClass(UpdateServiceInstanceRequest.class);
		Mockito.verify(serviceInstanceService).updateServiceInstance(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	private GetLastServiceOperationRequest verifyLastOperation() {
		ArgumentCaptor<GetLastServiceOperationRequest> argumentCaptor = ArgumentCaptor.forClass(GetLastServiceOperationRequest.class);
		Mockito.verify(serviceInstanceService).getLastOperation(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	private void assertHeaderValuesSet(ServiceBrokerRequest actualRequest) {
		assertThat(actualRequest.getCfInstanceId(), equalTo(CF_INSTANCE_ID));
		assertThat(actualRequest.getApiInfoLocation(), equalTo(API_INFO_LOCATION));

		assertThat(actualRequest.getOriginatingIdentity(), notNullValue());
		Context identity = actualRequest.getOriginatingIdentity();
		assertThat(identity.getPlatform(), equalTo(ORIGINATING_IDENTITY_PLATFORM));
		assertThat(identity.getProperty(ORIGINATING_USER_KEY), equalTo(ORIGINATING_USER_VALUE));
		assertThat(identity.getProperty(ORIGINATING_EMAIL_KEY), equalTo(ORIGINATING_EMAIL_VALUE));
	}

	private void assertHeaderValuesNotSet(ServiceBrokerRequest actualRequest) {
		assertNull(actualRequest.getApiInfoLocation());
		assertNull(actualRequest.getCfInstanceId());
		assertNull(actualRequest.getOriginatingIdentity());
	}
}
