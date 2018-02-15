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

import org.junit.Test;

import org.springframework.cloud.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest.PreviousValues;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ServiceInstanceControllerRequestTest extends ControllerRequestTest {

	@Test
	public void createServiceInstanceParametersAreMappedToRequest() {
		CreateServiceInstanceRequest parsedRequest = buildCreateRequest();

		CreateServiceInstanceRequest expectedRequest = buildCreateRequest();
		expectedRequest.setAsyncAccepted(true);
		expectedRequest.setServiceInstanceId("service-instance-id");
		expectedRequest.setPlatformInstanceId("platform-instance-id");
		expectedRequest.setApiInfoLocation("api-info-location");
		expectedRequest.setOriginatingIdentity(identityContext);
		expectedRequest.setServiceDefinition(serviceDefinition);

		ServiceInstanceController controller = createControllerUnderTest(expectedRequest);

		controller.createServiceInstance(pathVariables, "service-instance-id", true,
				"api-info-location", encodeOriginatingIdentity(identityContext), parsedRequest);
	}

	private CreateServiceInstanceRequest buildCreateRequest() {
		return CreateServiceInstanceRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.parameters("create-param-1", "value1")
				.parameters("create-param-2", "value2")
				.context(requestContext)
				.build();
	}

	@Test(expected = ServiceDefinitionDoesNotExistException.class)
	public void createServiceInstanceWithInvalidServiceDefinitionIdThrowsException() {
		CreateServiceInstanceRequest createRequest = CreateServiceInstanceRequest.builder()
				.serviceDefinitionId("unknown-service-definition-id")
				.build();

		ServiceInstanceController controller = createControllerUnderTest();

		controller.createServiceInstance(pathVariables, null, false,
				null, null, createRequest);
	}

	@Test
	public void getServiceInstanceLastOperationParametersAreMappedToRequest() {
		GetLastServiceOperationRequest expectedRequest = new GetLastServiceOperationRequest();
		expectedRequest.setServiceInstanceId("service-instance-id");
		expectedRequest.setServiceDefinitionId("service-definition-id");
		expectedRequest.setPlanId("plan-id");
		expectedRequest.setOperation("operation");
		expectedRequest.setPlatformInstanceId("platform-instance-id");
		expectedRequest.setApiInfoLocation("api-info-location");
		expectedRequest.setOriginatingIdentity(identityContext);

		ServiceInstanceController controller = createControllerUnderTest(expectedRequest);

		controller.getServiceInstanceLastOperation(pathVariables, "service-instance-id",
				"service-definition-id", "plan-id", "operation",
				"api-info-location", encodeOriginatingIdentity(identityContext));
	}

	@Test
	public void deleteServiceInstanceParametersAreMappedToRequest() {
		DeleteServiceInstanceRequest expectedRequest = new DeleteServiceInstanceRequest();
		expectedRequest.setAsyncAccepted(true);
		expectedRequest.setServiceInstanceId("service-instance-id");
		expectedRequest.setServiceDefinitionId("service-definition-id");
		expectedRequest.setPlanId("plan-id");
		expectedRequest.setPlatformInstanceId("platform-instance-id");
		expectedRequest.setApiInfoLocation("api-info-location");
		expectedRequest.setOriginatingIdentity(identityContext);
		expectedRequest.setServiceDefinition(serviceDefinition);

		ServiceInstanceController controller = createControllerUnderTest(expectedRequest);

		controller.deleteServiceInstance(pathVariables, "service-instance-id", "service-definition-id",
				"plan-id", true, "api-info-location", encodeOriginatingIdentity(identityContext));
	}

	@Test(expected = ServiceDefinitionDoesNotExistException.class)
	public void deleteServiceInstanceWithInvalidServiceDefinitionIdThrowsException() {
		ServiceInstanceController controller = createControllerUnderTest();
		controller.deleteServiceInstance(pathVariables, null, "unknown-service-definition-id",
				null, false, null, null);
	}

	@Test
	public void updateServiceInstanceParametersAreMappedToRequest() {
		UpdateServiceInstanceRequest parsedRequest = buildUpdateRequest();

		UpdateServiceInstanceRequest expectedRequest = buildUpdateRequest();
		expectedRequest.setAsyncAccepted(true);
		expectedRequest.setServiceInstanceId("service-instance-id");
		expectedRequest.setPlatformInstanceId("platform-instance-id");
		expectedRequest.setApiInfoLocation("api-info-location");
		expectedRequest.setOriginatingIdentity(identityContext);
		expectedRequest.setServiceDefinition(serviceDefinition);

		ServiceInstanceController controller = createControllerUnderTest(expectedRequest);

		controller.updateServiceInstance(pathVariables, "service-instance-id", true,
				"api-info-location", encodeOriginatingIdentity(identityContext), parsedRequest);
	}

	private UpdateServiceInstanceRequest buildUpdateRequest() {
		return UpdateServiceInstanceRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.previousValues(new PreviousValues("previous-plan-id"))
				.parameters("create-param-1", "value1")
				.parameters("create-param-2", "value2")
				.context(requestContext)
				.build();
	}

	@Test(expected = ServiceDefinitionDoesNotExistException.class)
	public void updateServiceInstanceWithInvalidServiceDefinitionIdThrowsException() {
		UpdateServiceInstanceRequest updateRequest = UpdateServiceInstanceRequest.builder()
				.serviceDefinitionId("unknown-service-definition-id")
				.build();

		ServiceInstanceController controller = createControllerUnderTest();

		controller.updateServiceInstance(pathVariables, null, false,
				null, null, updateRequest);
	}

	private ServiceInstanceController createControllerUnderTest(ServiceBrokerRequest expectedRequest) {
		return new ServiceInstanceController(catalogService, new VerifyingService(expectedRequest));
	}

	private ServiceInstanceController createControllerUnderTest() {
		return createControllerUnderTest(null);
	}

	private static class VerifyingService implements ServiceInstanceService {
		private final ServiceBrokerRequest expectedRequest;

		public VerifyingService(ServiceBrokerRequest expectedRequest) {
			this.expectedRequest = expectedRequest;
		}

		@Override
		public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
			assertThat(request, equalTo(expectedRequest));
			return null;
		}

		@Override
		public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {
			assertThat(request, equalTo(expectedRequest));
			return GetLastServiceOperationResponse.builder()
					.operationState(OperationState.SUCCEEDED)
					.build();
		}

		@Override
		public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {
			assertThat(request, equalTo(expectedRequest));
			return null;
		}

		@Override
		public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request) {
			assertThat(request, equalTo(expectedRequest));
			return null;
		}
	}
}