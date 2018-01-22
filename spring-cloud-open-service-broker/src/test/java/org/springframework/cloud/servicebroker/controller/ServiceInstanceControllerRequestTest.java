/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.cloud.servicebroker.controller;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest.PreviousValues;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInstanceControllerRequestTest {

	@Mock
	private CatalogService catalogService;

	private ServiceDefinition serviceDefinition;

	private Map<String, String> pathVariables = Collections.singletonMap("cfInstanceId", "platform-instance-id");

	private Context identityContext;
	private Context requestContext;

	@Before
	public void setUp() {
		initMocks(this);

		serviceDefinition = ServiceDefinition.builder()
				.id("service-definition-id")
				.plans(Plan.builder()
						.id("plan-id")
						.build())
				.build();

		when(catalogService.getServiceDefinition("service-definition-id"))
				.thenReturn(serviceDefinition);

		identityContext = Context.builder()
				.platform("test-platform")
				.property("user", "user-id")
				.build();

		requestContext = Context.builder()
				.platform("test-platform")
				.property("request-property", "value")
				.build();
	}

	@Test
	public void createServiceInstanceParametersAreMappedToRequest() throws Exception {
		CreateServiceInstanceRequest expectedRequest = CreateServiceInstanceRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.parameters("create-param-1", "value1")
				.parameters("create-param-2", "value2")
				.context(requestContext)
				.build();

		expectedRequest.setAsyncAccepted(true);
		expectedRequest.setServiceInstanceId("service-instance-id");
		expectedRequest.setCfInstanceId("platform-instance-id");
		expectedRequest.setApiInfoLocation("api-info-location");
		expectedRequest.setOriginatingIdentity(identityContext);
		expectedRequest.setServiceDefinition(serviceDefinition);

		CreateServiceInstanceRequest parsedRequest = CreateServiceInstanceRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.parameters("create-param-1", "value1")
				.parameters("create-param-2", "value2")
				.context(requestContext)
				.build();

		ServiceInstanceController controller =
				new ServiceInstanceController(catalogService, new VerifyingServiceInstanceService(expectedRequest));

		controller.createServiceInstance(pathVariables, "service-instance-id", true,
				"api-info-location", encodeOriginatingIdentity(identityContext), parsedRequest);
	}

	@Test(expected = ServiceDefinitionDoesNotExistException.class)
	public void createServiceInstanceWithInvalidServiceDefinitionIdThrowsException() {
		CreateServiceInstanceRequest createRequest = CreateServiceInstanceRequest.builder()
				.serviceDefinitionId("unknown-service-definition-id")
				.build();

		ServiceInstanceController controller = new ServiceInstanceController(catalogService, null);

		controller.createServiceInstance(pathVariables, null, false,
				null, null, createRequest);
	}

	@Test
	public void getServiceInstanceLastOperationParametersAreMappedToRequest() throws Exception {
		GetLastServiceOperationRequest expectedRequest = new GetLastServiceOperationRequest();
		expectedRequest.setServiceInstanceId("service-instance-id");
		expectedRequest.setServiceDefinitionId("service-definition-id");
		expectedRequest.setPlanId("plan-id");
		expectedRequest.setOperation("operation");
		expectedRequest.setCfInstanceId("platform-instance-id");
		expectedRequest.setApiInfoLocation("api-info-location");
		expectedRequest.setOriginatingIdentity(identityContext);

		ServiceInstanceController controller =
				new ServiceInstanceController(catalogService, new VerifyingServiceInstanceService(expectedRequest));

		controller.getServiceInstanceLastOperation(pathVariables, "service-instance-id",
				"service-definition-id", "plan-id", "operation",
				"api-info-location", encodeOriginatingIdentity(identityContext));
	}

	@Test
	public void deleteServiceInstanceParametersAreMappedToRequest() throws Exception {
		DeleteServiceInstanceRequest expectedRequest = new DeleteServiceInstanceRequest();
		expectedRequest.setAsyncAccepted(true);
		expectedRequest.setServiceInstanceId("service-instance-id");
		expectedRequest.setServiceDefinitionId("service-definition-id");
		expectedRequest.setPlanId("plan-id");
		expectedRequest.setCfInstanceId("platform-instance-id");
		expectedRequest.setApiInfoLocation("api-info-location");
		expectedRequest.setOriginatingIdentity(identityContext);
		expectedRequest.setServiceDefinition(serviceDefinition);

		ServiceInstanceController controller =
				new ServiceInstanceController(catalogService, new VerifyingServiceInstanceService(expectedRequest));

		controller.deleteServiceInstance(pathVariables, "service-instance-id", "service-definition-id",
				"plan-id", true, "api-info-location", encodeOriginatingIdentity(identityContext));
	}

	@Test(expected = ServiceDefinitionDoesNotExistException.class)
	public void deleteServiceInstanceWithInvalidServiceDefinitionIdThrowsException() {
		ServiceInstanceController controller = new ServiceInstanceController(catalogService, null);
		controller.deleteServiceInstance(pathVariables, null, "unknown-service-definition-id",
				null, false, null, null);
	}

	@Test
	public void updateServiceInstanceParametersAreMappedToRequest() throws Exception {
		UpdateServiceInstanceRequest expectedRequest = UpdateServiceInstanceRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.previousValues(new PreviousValues("previous-plan-id"))
				.parameters("create-param-1", "value1")
				.parameters("create-param-2", "value2")
				.context(requestContext)
				.build();

		expectedRequest.setAsyncAccepted(true);
		expectedRequest.setServiceInstanceId("service-instance-id");
		expectedRequest.setCfInstanceId("platform-instance-id");
		expectedRequest.setApiInfoLocation("api-info-location");
		expectedRequest.setOriginatingIdentity(identityContext);
		expectedRequest.setServiceDefinition(serviceDefinition);

		UpdateServiceInstanceRequest parsedRequest = UpdateServiceInstanceRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.previousValues(new PreviousValues("previous-plan-id"))
				.parameters("create-param-1", "value1")
				.parameters("create-param-2", "value2")
				.context(requestContext)
				.build();

		ServiceInstanceController controller =
				new ServiceInstanceController(catalogService, new VerifyingServiceInstanceService(expectedRequest));

		controller.updateServiceInstance(pathVariables, "service-instance-id", true,
				"api-info-location", encodeOriginatingIdentity(identityContext), parsedRequest);
	}

	@Test(expected = ServiceDefinitionDoesNotExistException.class)
	public void updateServiceInstanceWithInvalidServiceDefinitionIdThrowsException() {
		UpdateServiceInstanceRequest updateRequest = UpdateServiceInstanceRequest.builder()
				.serviceDefinitionId("unknown-service-definition-id")
				.build();

		ServiceInstanceController controller = new ServiceInstanceController(catalogService, null);

		controller.updateServiceInstance(pathVariables, null, false,
				null, null, updateRequest);
	}

	private String encodeOriginatingIdentity(Context context) throws Exception {
		Map<String, Object> properties = context.getProperties();
		String propertiesJson = DataFixture.toJson(properties);

		return context.getPlatform() +
				" " +
				Base64.getEncoder().encodeToString(propertiesJson.getBytes());
	}

	private static class VerifyingServiceInstanceService implements ServiceInstanceService {
		private CreateServiceInstanceRequest expectedCreateRequest;
		private GetLastServiceOperationRequest expectedLastOperationRequest;
		private DeleteServiceInstanceRequest expectedDeleteRequest;
		private UpdateServiceInstanceRequest expectedUpdateRequest;

		public VerifyingServiceInstanceService(CreateServiceInstanceRequest expectedCreateRequest) {
			this.expectedCreateRequest = expectedCreateRequest;
		}

		public VerifyingServiceInstanceService(GetLastServiceOperationRequest expectedLastOperationRequest) {
			this.expectedLastOperationRequest = expectedLastOperationRequest;
		}

		public VerifyingServiceInstanceService(DeleteServiceInstanceRequest expectedDeleteRequest) {
			this.expectedDeleteRequest = expectedDeleteRequest;
		}

		public VerifyingServiceInstanceService(UpdateServiceInstanceRequest expectedUpdateRequest) {
			this.expectedUpdateRequest = expectedUpdateRequest;
		}

		@Override
		public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
			assertThat(request, equalTo(expectedCreateRequest));
			return null;
		}

		@Override
		public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {
			assertThat(request, equalTo(expectedLastOperationRequest));
			return GetLastServiceOperationResponse.builder()
					.operationState(OperationState.SUCCEEDED)
					.build();
		}

		@Override
		public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {
			assertThat(request, equalTo(expectedDeleteRequest));
			return null;
		}

		@Override
		public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request) {
			assertThat(request, equalTo(expectedUpdateRequest));
			return null;
		}
	}
}