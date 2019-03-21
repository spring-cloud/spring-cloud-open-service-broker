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

package org.springframework.cloud.servicebroker.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(Theories.class)
public class ServiceInstanceBindingControllerResponseCodeTest {
	private final CatalogService catalogService = mock(CatalogService.class);
	private final ServiceInstanceBindingService bindingService = mock(ServiceInstanceBindingService.class);

	private final Map<String, String> pathVariables = Collections.emptyMap();

	private ServiceInstanceBindingController controller;

	@DataPoints("createResponsesWithExpectedStatus")
	public static List<CreateResponseAndExpectedStatus> createDataPoints() {
		return Arrays.asList(
				new CreateResponseAndExpectedStatus(
						null,
						HttpStatus.CREATED
				),
				new CreateResponseAndExpectedStatus(
						CreateServiceInstanceAppBindingResponse.builder()
								.bindingExisted(false)
								.build(),
						HttpStatus.CREATED
				),
				new CreateResponseAndExpectedStatus(
						CreateServiceInstanceAppBindingResponse.builder()
								.bindingExisted(true)
								.build(),
						HttpStatus.OK
				)
		);
	}

	@DataPoints("getResponsesWithExpectedStatus")
	public static List<GetResponseAndExpectedStatus> getDataPoints() {
		return Arrays.asList(
				new GetResponseAndExpectedStatus(
						null,
						HttpStatus.OK
				),
				new GetResponseAndExpectedStatus(
						GetServiceInstanceAppBindingResponse.builder()
								.build(),
						HttpStatus.OK
				)
		);
	}

	@Before
	public void setUp() {
		controller = new ServiceInstanceBindingController(catalogService, bindingService);

		when(catalogService.getServiceDefinition(anyString()))
				.thenReturn(ServiceDefinition.builder().build());
	}

	@Theory
	public void createServiceBindingWithResponseGivesExpectedStatus(CreateResponseAndExpectedStatus data) {
		when(bindingService.createServiceInstanceBinding(any(CreateServiceInstanceBindingRequest.class)))
				.thenReturn(data.response);

		CreateServiceInstanceBindingRequest createRequest = CreateServiceInstanceBindingRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.build();

		ResponseEntity<CreateServiceInstanceBindingResponse> responseEntity = controller
				.createServiceInstanceBinding(pathVariables, null, null, null, null,
						createRequest);

		assertThat(responseEntity.getStatusCode()).isEqualTo(data.expectedStatus);
		assertThat(responseEntity.getBody()).isEqualTo(data.response);
	}

	@Theory
	public void getServiceBindingWithResponseGivesExpectedStatus(GetResponseAndExpectedStatus data) {
		when(bindingService.getServiceInstanceBinding(any(GetServiceInstanceBindingRequest.class)))
				.thenReturn(data.response);

		ResponseEntity<GetServiceInstanceBindingResponse> responseEntity = controller
				.getServiceInstanceBinding(pathVariables, null, null, null, null);

		assertThat(responseEntity.getStatusCode()).isEqualTo(data.expectedStatus);
		assertThat(responseEntity.getBody()).isEqualTo(data.response);
	}

	@Test
	public void deleteServiceBindingWithSuccessGivesExpectedStatus() {
		ResponseEntity<String> responseEntity = controller
				.deleteServiceInstanceBinding(pathVariables, null, null, null, null, null, null);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isEqualTo("{}");

		verify(bindingService).deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));
	}

	@Test
	public void deleteServiceBindingWithMissingBindingGivesExpectedStatus() {
		doThrow(new ServiceInstanceBindingDoesNotExistException("binding-id"))
				.when(bindingService).deleteServiceInstanceBinding(any(DeleteServiceInstanceBindingRequest.class));

		ResponseEntity<String> responseEntity = controller
				.deleteServiceInstanceBinding(pathVariables, null, null, null, null, null, null);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.GONE);
		assertThat(responseEntity.getBody()).isEqualTo("{}");
	}

	public static class CreateResponseAndExpectedStatus {
		protected final CreateServiceInstanceBindingResponse response;
		protected final HttpStatus expectedStatus;

		public CreateResponseAndExpectedStatus(CreateServiceInstanceBindingResponse response, HttpStatus expectedStatus) {
			this.response = response;
			this.expectedStatus = expectedStatus;
		}

		@Override
		public String toString() {
			String responseValue = response == null ? "null" :
					"{" +
						"bindingExisted=" + response.isBindingExisted() +
					"}";

			return "response=" + responseValue +
					", expectedStatus=" + expectedStatus;
		}
	}

	public static class GetResponseAndExpectedStatus {
		protected final GetServiceInstanceBindingResponse response;
		protected final HttpStatus expectedStatus;

		public GetResponseAndExpectedStatus(GetServiceInstanceBindingResponse response, HttpStatus expectedStatus) {
			this.response = response;
			this.expectedStatus = expectedStatus;
		}
	}
}
