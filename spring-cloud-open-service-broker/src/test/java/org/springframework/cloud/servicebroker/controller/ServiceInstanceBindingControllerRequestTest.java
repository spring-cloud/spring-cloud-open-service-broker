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
import org.springframework.cloud.servicebroker.model.BindResource;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ServiceInstanceBindingControllerRequestTest extends ControllerRequestTest{

	@Test
	public void createServiceBindingParametersAreMappedToRequest() {
		CreateServiceInstanceBindingRequest parsedRequest = buildCreateRequest();

		CreateServiceInstanceBindingRequest expectedRequest = buildCreateRequest();
		expectedRequest.setServiceInstanceId("service-instance-id");
		expectedRequest.setBindingId("binding-id");
		expectedRequest.setPlatformInstanceId("platform-instance-id");
		expectedRequest.setApiInfoLocation("api-info-location");
		expectedRequest.setOriginatingIdentity(identityContext);
		expectedRequest.setServiceDefinition(serviceDefinition);

		ServiceInstanceBindingController controller = createControllerUnderTest(expectedRequest);

		controller.createServiceInstanceBinding(pathVariables, "service-instance-id", "binding-id",
				"api-info-location", encodeOriginatingIdentity(identityContext), parsedRequest);
	}

	private CreateServiceInstanceBindingRequest buildCreateRequest() {
		return CreateServiceInstanceBindingRequest.builder()
				.serviceDefinitionId(serviceDefinition.getId())
				.planId("plan-id")
				.bindResource(BindResource.builder().build())
				.parameters("create-param-1", "value1")
				.parameters("create-param-2", "value2")
				.context(requestContext)
				.build();
	}

	@Test
	public void deleteServiceBindingParametersAreMappedToRequest() {
		DeleteServiceInstanceBindingRequest expectedRequest = new DeleteServiceInstanceBindingRequest();
		expectedRequest.setServiceDefinitionId(serviceDefinition.getId());
		expectedRequest.setPlanId("plan-id");
		expectedRequest.setBindingId("binding-id");
		expectedRequest.setServiceInstanceId("service-instance-id");
		expectedRequest.setPlatformInstanceId("platform-instance-id");
		expectedRequest.setApiInfoLocation("api-info-location");
		expectedRequest.setOriginatingIdentity(identityContext);
		expectedRequest.setServiceDefinition(serviceDefinition);

		ServiceInstanceBindingController controller = createControllerUnderTest(expectedRequest);

		controller.deleteServiceInstanceBinding(pathVariables, "service-instance-id", "binding-id",
				serviceDefinition.getId(), "plan-id",
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
			assertThat(request, equalTo(expectedRequest));
			return null;
		}

		@Override
		public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
			assertThat(request, equalTo(expectedRequest));
		}
	}
}