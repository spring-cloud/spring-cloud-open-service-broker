/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.cloud.servicebroker.controller.ServiceBrokerExceptionHandler;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceRouteBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceRouteBindingResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInstanceBindingControllerIntegrationTest extends AbstractServiceInstanceBindingControllerIntegrationTest {

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setControllerAdvice(ServiceBrokerExceptionHandler.class)
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}

	@Test
	public void createBindingToAppSucceeds() throws Exception {
		setupCatalogService();

		setupServiceInstanceBindingService(CreateServiceInstanceAppBindingResponse.builder()
				.bindingExisted(false)
				.build());

		mockMvc.perform(put(buildCreateUrl(PLATFORM_INSTANCE_ID))
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

		mockMvc.perform(put(buildCreateUrl())
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

		mockMvc.perform(put(buildCreateUrl())
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

		mockMvc.perform(put(buildCreateUrl())
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void createBindingWithUnknownServiceInstanceIdFails() throws Exception {
		setupCatalogService();

		when(serviceInstanceBindingService.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.thenThrow(new ServiceInstanceDoesNotExistException(SERVICE_INSTANCE_ID));

		mockMvc.perform(put(buildCreateUrl())
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(SERVICE_INSTANCE_ID)));
	}

	@Test
	public void createBindingWithUnknownServiceDefinitionIdSucceeds() throws Exception {
		setupCatalogService(null);

		mockMvc.perform(put(buildCreateUrl())
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(serviceDefinition.getId())));
	}

	@Test
	public void createBindingWithDuplicateIdFails() throws Exception {
		setupCatalogService();

		when(serviceInstanceBindingService.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.thenThrow(new ServiceInstanceBindingExistsException(SERVICE_INSTANCE_ID, SERVICE_INSTANCE_BINDING_ID));

		mockMvc.perform(put(buildCreateUrl())
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

		mockMvc.perform(put(buildCreateUrl())
				.content(body)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")));
	}

	@Test
	public void createBindingWithMissingFieldsFails() throws Exception {
		String body = "{}";

		mockMvc.perform(put(buildCreateUrl())
				.content(body)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")))
				.andExpect(jsonPath("$.description", containsString("planId")));
	}

	@Test
	public void getBindingToAppSucceeds() throws Exception {
		setupServiceInstanceBindingService(GetServiceInstanceAppBindingResponse.builder()
				.build());

		mockMvc.perform(get(buildCreateUrl(PLATFORM_INSTANCE_ID))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		GetServiceInstanceBindingRequest actualRequest = verifyGetBinding();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void getBindingToRouteSucceeds() throws Exception {
		setupServiceInstanceBindingService(GetServiceInstanceRouteBindingResponse.builder()
				.build());

		mockMvc.perform(get(buildCreateUrl(PLATFORM_INSTANCE_ID))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		GetServiceInstanceBindingRequest actualRequest = verifyGetBinding();
		assertHeaderValuesSet(actualRequest);
	}

	@Test
	public void getBindingWithOperationInProgressFails() throws Exception {
		when(serviceInstanceBindingService.getServiceInstanceBinding(any(GetServiceInstanceBindingRequest.class)))
				.thenThrow(new ServiceBrokerOperationInProgressException("still working"));

		mockMvc.perform(get(buildCreateUrl())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void deleteBindingSucceeds() throws Exception {
		setupCatalogService();

		mockMvc.perform(delete(buildDeleteUrl(PLATFORM_INSTANCE_ID))
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

		doThrow(new ServiceInstanceDoesNotExistException(SERVICE_INSTANCE_ID))
				.when(serviceInstanceBindingService).deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		mockMvc.perform(delete(buildDeleteUrl())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(SERVICE_INSTANCE_ID)));
	}

	@Test
	public void deleteBindingWithUnknownBindingIdFails() throws Exception {
		setupCatalogService();

		doThrow(new ServiceInstanceBindingDoesNotExistException(SERVICE_INSTANCE_BINDING_ID))
				.when(serviceInstanceBindingService).deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		mockMvc.perform(delete(buildDeleteUrl())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isGone())
				.andExpect(jsonPath("$", is("{}")));
	}

	@Test
	public void deleteBindingWithUnknownServiceDefinitionIdSucceeds() throws Exception {
		setupCatalogService(null);

		mockMvc.perform(delete(buildDeleteUrl())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
