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
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRouteBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.API_INFO_LOCATION_HEADER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInstanceBindingControllerIntegrationTest extends ServiceInstanceBindingIntegrationTest {

	private MockMvc mockMvc;

	@InjectMocks
	private ServiceInstanceBindingController controller;

	@Mock
	private ServiceInstanceBindingService serviceInstanceBindingService;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}

	@Test
	public void createBindingToAppSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse.builder()
				.bindingExisted(false)
				.build());

		mockMvc.perform(put(buildCreateUrl(true))
				.content(createRequestBody)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void createBindingToAppWithExistingSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse.builder()
				.bindingExisted(true)
				.build());

		mockMvc.perform(put(buildCreateUrl(false))
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		CreateServiceInstanceBindingRequest actualRequest = verifyCreateBinding();
		assertHeaderValuesNotSet(actualRequest);
	}

	@Test
	public void createBindingToRouteSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse.builder()
				.bindingExisted(false)
				.build());

		mockMvc.perform(put(buildCreateUrl(false))
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	public void createBindingToRouteWithExistingSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceRouteBindingResponse.builder()
				.bindingExisted(true)
				.build());

		mockMvc.perform(put(buildCreateUrl(false))
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void createBindingWithUnknownServiceInstanceIdFails() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(
				new ServiceInstanceDoesNotExistException(SERVICE_INSTANCE_ID));

		mockMvc.perform(put(buildCreateUrl(false))
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(SERVICE_INSTANCE_ID)));
	}

	@Test
	public void createBindingWithUnknownServiceDefinitionIdSucceeds() throws Exception {
		setupCatalogService(null);

		mockMvc.perform(put(buildCreateUrl(false))
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(serviceDefinition.getId())));
	}

	@Test
	public void createBindingWithDuplicateIdFails() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(
				new ServiceInstanceBindingExistsException(SERVICE_INSTANCE_ID, SERVICE_INSTANCE_BINDING_ID));

		mockMvc.perform(put(buildCreateUrl(false))
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.description", containsString(SERVICE_INSTANCE_ID)))
				.andExpect(jsonPath("$.description", containsString(SERVICE_INSTANCE_BINDING_ID)));
	}

	@Test
	public void createBindingWithInvalidFieldsFails() throws Exception {
		String body = createRequestBody.replace("service_id", "foo");

		mockMvc.perform(put(buildCreateUrl(false))
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")));
	}

	@Test
	public void createBindingWithMissingFieldsFails() throws Exception {
		String body = "{}";

		mockMvc.perform(put(buildCreateUrl(false))
				.content(body)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")))
				.andExpect(jsonPath("$.description", containsString("planId")));
	}

	@Test
	public void deleteBindingSucceeds() throws Exception {
		setupCatalogService();

		mockMvc.perform(delete(buildDeleteUrl(true))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", is("{}")));

		verify(serviceInstanceBindingService).deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		DeleteServiceInstanceBindingRequest actualRequest = verifyDeleteBinding();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void deleteBindingWithUnknownInstanceIdFails() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceInstanceDoesNotExistException(SERVICE_INSTANCE_ID));

		mockMvc.perform(delete(buildDeleteUrl(false))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(SERVICE_INSTANCE_ID)));
	}

	@Test
	public void deleteBindingWithUnknownBindingIdFails() throws Exception {
		setupCatalogService();

		setupServiceInstanceService(new ServiceInstanceBindingDoesNotExistException(SERVICE_INSTANCE_BINDING_ID));

		mockMvc.perform(delete(buildDeleteUrl(false))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isGone())
				.andExpect(jsonPath("$", is("{}")));
	}

	@Test
	public void deleteBindingWithUnknownServiceDefinitionIdSucceeds() throws Exception {
		setupCatalogService(null);

		mockMvc.perform(delete(buildDeleteUrl(false))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	private void setupServiceInstanceBindingService(CreateServiceInstanceBindingResponse createResponse) {
		when(serviceInstanceBindingService.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.thenReturn(createResponse);
	}

	private void setupServiceInstanceBindingService(Exception exception) {
		when(serviceInstanceBindingService.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.thenThrow(exception);
	}

	private void setupServiceInstanceService(Exception exception) {
		doThrow(exception)
				.when(serviceInstanceBindingService).deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));
	}

	private CreateServiceInstanceBindingRequest verifyCreateBinding() {
		ArgumentCaptor<CreateServiceInstanceBindingRequest> argumentCaptor = ArgumentCaptor.forClass(CreateServiceInstanceBindingRequest.class);
		verify(serviceInstanceBindingService).createServiceInstanceBinding(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	private DeleteServiceInstanceBindingRequest verifyDeleteBinding() {
		ArgumentCaptor<DeleteServiceInstanceBindingRequest> argumentCaptor = ArgumentCaptor.forClass(DeleteServiceInstanceBindingRequest.class);
		verify(serviceInstanceBindingService).deleteServiceInstanceBinding(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}
}
