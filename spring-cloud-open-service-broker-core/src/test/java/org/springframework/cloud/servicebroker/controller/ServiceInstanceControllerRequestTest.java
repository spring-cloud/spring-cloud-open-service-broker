/*
 * Copyright 2002-2021 the original author or authors.
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

package org.springframework.cloud.servicebroker.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceDefinitionPlanDoesNotExistException;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest.CreateServiceInstanceRequestBuilder;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.OperationState;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest.PreviousValues;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest.UpdateServiceInstanceRequestBuilder;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceInstanceControllerRequestTest extends ControllerRequestTest {

	@Test
	void createServiceInstanceParametersAreMappedToRequest() {
		CreateServiceInstanceRequest parsedRequest = buildCreateRequest().build();

		CreateServiceInstanceRequest expectedRequest = buildCreateRequest()
				.asyncAccepted(true)
				.serviceInstanceId("service-instance-id")
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("api-info-location")
				.originatingIdentity(identityContext)
				.requestIdentity("request-id")
				.serviceDefinition(serviceDefinition)
				.plan(plan)
				.build();

		ServiceInstanceController controller = createControllerUnderTest(expectedRequest);

		controller.createServiceInstance(pathVariables, "service-instance-id", true,
				"api-info-location", encodeOriginatingIdentity(identityContext), "request-id", parsedRequest)
				.block();
	}

	private CreateServiceInstanceRequestBuilder buildCreateRequest() {
		return CreateServiceInstanceRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.parameters("create-param-1", "value1")
				.parameters("create-param-2", "value2")
				.context(requestContext);
	}

	@Test
	void createServiceInstanceWithInvalidServiceDefinitionIdThrowsException() {
		CreateServiceInstanceRequest createRequest = CreateServiceInstanceRequest.builder()
				.serviceDefinitionId("unknown-service-definition-id")
				.build();

		ServiceInstanceController controller = createControllerUnderTest();

		assertThrows(ServiceDefinitionDoesNotExistException.class, () ->
				controller.createServiceInstance(pathVariables, null, false,
						null, null, null, createRequest)
						.block());
	}

	@Test
	void createServiceInstanceWithInvalidPlanIdThrowsException() {
		CreateServiceInstanceRequest createRequest = CreateServiceInstanceRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("unknown-plan-id")
				.build();

		ServiceInstanceController controller = createControllerUnderTest();

		assertThrows(ServiceDefinitionPlanDoesNotExistException.class, () ->
				controller.createServiceInstance(pathVariables, null, false,
						null, null, null, createRequest)
						.block());
	}

	@Test
	void getServiceInstanceParametersAreMappedToRequest() {
		GetServiceInstanceRequest expectedRequest = GetServiceInstanceRequest.builder()
				.serviceInstanceId("service-instance-id")
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("api-info-location")
				.originatingIdentity(identityContext)
				.requestIdentity("request-id")
				.build();

		ServiceInstanceController controller = createControllerUnderTest(expectedRequest);

		controller.getServiceInstance(pathVariables, "service-instance-id",
				"api-info-location", encodeOriginatingIdentity(identityContext), "request-id")
				.block();
	}

	@Test
	void getServiceInstanceLastOperationParametersAreMappedToRequest() {
		GetLastServiceOperationRequest expectedRequest = GetLastServiceOperationRequest.builder()
				.serviceInstanceId("service-instance-id")
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.operation("operation")
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("api-info-location")
				.originatingIdentity(identityContext)
				.requestIdentity("request-id")
				.build();

		ServiceInstanceController controller = createControllerUnderTest(expectedRequest);

		controller.getServiceInstanceLastOperation(pathVariables, "service-instance-id",
				"service-definition-id", "plan-id", "operation",
				"api-info-location", encodeOriginatingIdentity(identityContext), "request-id")
				.block();
	}

	@Test
	void deleteServiceInstanceParametersAreMappedToRequest() {
		DeleteServiceInstanceRequest expectedRequest = DeleteServiceInstanceRequest.builder()
				.asyncAccepted(true)
				.serviceInstanceId("service-instance-id")
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("api-info-location")
				.originatingIdentity(identityContext)
				.requestIdentity("request-id")
				.serviceDefinition(serviceDefinition)
				.plan(plan)
				.build();

		ServiceInstanceController controller = createControllerUnderTest(expectedRequest);

		controller.deleteServiceInstance(pathVariables, "service-instance-id",
				"service-definition-id", "plan-id", true, "api-info-location",
				encodeOriginatingIdentity(identityContext), "request-id")
				.block();
	}

	@Test
	void deleteServiceInstanceWithInvalidServiceDefinitionIdThrowsException() {
		DeleteServiceInstanceRequest expectedRequest = DeleteServiceInstanceRequest.builder()
				.asyncAccepted(true)
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.build();

		ServiceInstanceController controller = createControllerUnderTest(expectedRequest);

		assertThrows(ServiceDefinitionDoesNotExistException.class, () ->
				controller.deleteServiceInstance(pathVariables, null, "unknown-service-definition-id", null, true, null,
						encodeOriginatingIdentity(identityContext), "request-id")
						.block());
	}

	@Test
	void deleteServiceInstanceWithInvalidPlanIdThrowsException() {
		DeleteServiceInstanceRequest expectedRequest = DeleteServiceInstanceRequest.builder()
				.asyncAccepted(true)
				.serviceDefinitionId("service-definition-id")
				.planId("unknown-plan-id")
				.build();

		ServiceInstanceController controller = createControllerUnderTest(expectedRequest);

		assertThrows(ServiceDefinitionPlanDoesNotExistException.class, () ->
				controller.deleteServiceInstance(pathVariables, null, "service-definition-id", "unknown-plan-id", true,
						null, encodeOriginatingIdentity(identityContext), "request-id")
						.block());
	}

	@Test
	void updateServiceInstanceParametersAreMappedToRequest() {
		UpdateServiceInstanceRequest parsedRequest = buildUpdateRequest().build();

		UpdateServiceInstanceRequest expectedRequest = buildUpdateRequest()
				.asyncAccepted(true)
				.serviceInstanceId("service-instance-id")
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("api-info-location")
				.originatingIdentity(identityContext)
				.requestIdentity("request-id")
				.serviceDefinition(serviceDefinition)
				.plan(plan)
				.build();

		ServiceInstanceController controller = createControllerUnderTest(expectedRequest);

		controller.updateServiceInstance(pathVariables, "service-instance-id", true,
				"api-info-location", encodeOriginatingIdentity(identityContext), "request-id",
				parsedRequest)
				.block();
	}

	private UpdateServiceInstanceRequestBuilder buildUpdateRequest() {
		return UpdateServiceInstanceRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.previousValues(new PreviousValues("previous-plan-id", null))
				.parameters("create-param-1", "value1")
				.parameters("create-param-2", "value2")
				.context(requestContext);
	}

	@Test
	void updateServiceInstanceWithInvalidServiceDefinitionIdThrowsException() {
		UpdateServiceInstanceRequest updateRequest = UpdateServiceInstanceRequest.builder()
				.serviceDefinitionId("unknown-service-definition-id")
				.build();

		ServiceInstanceController controller = createControllerUnderTest();

		assertThrows(ServiceDefinitionDoesNotExistException.class, () ->
				controller.updateServiceInstance(pathVariables, null, false,
						null, null, null, updateRequest)
						.block());
	}

	private ServiceInstanceController createControllerUnderTest(ServiceBrokerRequest expectedRequest) {
		return new ServiceInstanceController(catalogService, new VerifyingService(expectedRequest));
	}

	private ServiceInstanceController createControllerUnderTest() {
		return createControllerUnderTest(null);
	}

	private static class VerifyingService implements ServiceInstanceService {

		private final ServiceBrokerRequest expectedRequest;

		VerifyingService(ServiceBrokerRequest expectedRequest) {
			this.expectedRequest = expectedRequest;
		}

		@Override
		public Mono<CreateServiceInstanceResponse> createServiceInstance(CreateServiceInstanceRequest request) {
			assertThat(request).isEqualTo(expectedRequest);
			return Mono.empty();
		}

		@Override
		public Mono<GetServiceInstanceResponse> getServiceInstance(GetServiceInstanceRequest request) {
			assertThat(request).isEqualTo(expectedRequest);
			return Mono.empty();
		}

		@Override
		public Mono<GetLastServiceOperationResponse> getLastOperation(GetLastServiceOperationRequest request) {
			assertThat(request).isEqualTo(expectedRequest);
			return Mono.just(GetLastServiceOperationResponse.builder()
					.operationState(OperationState.SUCCEEDED)
					.build());
		}

		@Override
		public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(DeleteServiceInstanceRequest request) {
			assertThat(request).isEqualTo(expectedRequest);
			return Mono.empty();
		}

		@Override
		public Mono<UpdateServiceInstanceResponse> updateServiceInstance(UpdateServiceInstanceRequest request) {
			assertThat(request).isEqualTo(expectedRequest);
			return Mono.empty();
		}

	}

}
