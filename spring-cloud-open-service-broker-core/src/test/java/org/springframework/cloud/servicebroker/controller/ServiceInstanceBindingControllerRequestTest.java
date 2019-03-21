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

import org.junit.Test;
import org.springframework.cloud.servicebroker.model.binding.BindResource;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest.CreateServiceInstanceBindingRequestBuilder;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceInstanceBindingControllerRequestTest extends ControllerRequestTest{

	@Test
	public void createServiceBindingParametersAreMappedToRequest() {
		CreateServiceInstanceBindingRequest parsedRequest = buildCreateRequest().build();

		CreateServiceInstanceBindingRequest expectedRequest = buildCreateRequest()
				.asyncAccepted(true)
				.serviceInstanceId("service-instance-id")
				.bindingId("binding-id")
				.serviceDefinition(serviceDefinition)
				.plan(plan)
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("api-info-location")
				.originatingIdentity(identityContext)
				.build();

		ServiceInstanceBindingController controller = createControllerUnderTest(expectedRequest);

		controller.createServiceInstanceBinding(pathVariables, "service-instance-id", "binding-id",
				true, "api-info-location", encodeOriginatingIdentity(identityContext), parsedRequest);
	}

	private CreateServiceInstanceBindingRequestBuilder buildCreateRequest() {
		return CreateServiceInstanceBindingRequest.builder()
				.serviceDefinitionId(serviceDefinition.getId())
				.planId("plan-id")
				.bindResource(BindResource.builder().build())
				.parameters("create-param-1", "value1")
				.parameters("create-param-2", "value2")
				.context(requestContext);
	}

	@Test
	public void getServiceBindingParametersAreMappedToRequest() {
		GetServiceInstanceBindingRequest expectedRequest = GetServiceInstanceBindingRequest.builder()
				.serviceInstanceId("service-instance-id")
				.bindingId("binding-id")
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("api-info-location")
				.originatingIdentity(identityContext)
				.build();

		ServiceInstanceBindingController controller = createControllerUnderTest(expectedRequest);

		controller.getServiceInstanceBinding(pathVariables, "service-instance-id", "binding-id",
				"api-info-location", encodeOriginatingIdentity(identityContext));
	}

	@Test
	public void deleteServiceBindingParametersAreMappedToRequest() {
		DeleteServiceInstanceBindingRequest expectedRequest = DeleteServiceInstanceBindingRequest.builder()
				.asyncAccepted(true)
				.serviceInstanceId("service-instance-id")
				.serviceDefinitionId(serviceDefinition.getId())
				.planId("plan-id")
				.bindingId("binding-id")
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("api-info-location")
				.originatingIdentity(identityContext)
				.serviceDefinition(serviceDefinition)
			 	.plan(plan)
				.build();

		ServiceInstanceBindingController controller = createControllerUnderTest(expectedRequest);

		controller.deleteServiceInstanceBinding(pathVariables, "service-instance-id", "binding-id",
				serviceDefinition.getId(), "plan-id", true,
				"api-info-location", encodeOriginatingIdentity(identityContext));
	}

	private ServiceInstanceBindingController createControllerUnderTest(ServiceBrokerRequest expectedRequest) {
		return new ServiceInstanceBindingController(catalogService, new VerifyingService(expectedRequest));
	}

	private class VerifyingService implements ServiceInstanceBindingService {
		private final ServiceBrokerRequest expectedRequest;

		public VerifyingService(ServiceBrokerRequest expectedRequest) {
			this.expectedRequest = expectedRequest;
		}

		@Override
		public CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
			assertThat(request).isEqualTo(expectedRequest);
			return null;
		}

		@Override
		public GetServiceInstanceBindingResponse getServiceInstanceBinding(GetServiceInstanceBindingRequest request) {
			assertThat(request).isEqualTo(expectedRequest);
			return null;
		}

		@Override
		public DeleteServiceInstanceBindingResponse deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
			assertThat(request).isEqualTo(expectedRequest);
			return null;
		}
	}
}
