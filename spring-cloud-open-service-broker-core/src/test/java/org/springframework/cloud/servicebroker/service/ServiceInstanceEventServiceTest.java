/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.cloud.servicebroker.service.ServiceInstanceEventAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInstanceEventServiceTest {

	private static final ServiceBrokerInvalidParametersException CREATE_INSTANCE_EXCEPTION =
			new ServiceBrokerInvalidParametersException("arrrr");
	private static final ServiceInstanceDoesNotExistException DELETE_INSTANCE_EXCEPTION =
			new ServiceInstanceDoesNotExistException("foo");
	private static final ServiceBrokerInvalidParametersException UPDATE_INSTANCE_EXCEPTION =
			new ServiceBrokerInvalidParametersException("arrrr");

	private ServiceInstanceEventService serviceInstanceEventService;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		TestServiceInstanceService serviceInstanceService = new TestServiceInstanceService();
		this.serviceInstanceEventService = new ServiceInstanceEventService(
				serviceInstanceService, applicationEventPublisher);
	}

	@Test
	public void createServiceInstanceSucceeds() {
		CreateServiceInstanceRequest request = CreateServiceInstanceRequest.builder()
				.serviceInstanceId("foo")
				.serviceDefinitionId("bar")
				.build();

		CreateServiceInstanceResponse response = CreateServiceInstanceResponse.builder()
				.build();

		StepVerifier
				.create(serviceInstanceEventService.createServiceInstance(request))
				.expectNext(response)
				.verifyComplete();

		List<ApplicationEvent> events = captureApplicationEvents();
		assertThat(events.get(0)).isCreateInstanceEvent(request);
		assertThat(events.get(1)).isCreateInstanceCompletedEvent(request, response);
	}

	@Test
	public void createServiceInstanceFails() {
		CreateServiceInstanceRequest request = CreateServiceInstanceRequest.builder()
				.serviceInstanceId("foo")
				.build();

		StepVerifier.create(serviceInstanceEventService.createServiceInstance(request))
				.expectError(ServiceBrokerInvalidParametersException.class)
				.verify();

		List<ApplicationEvent> events = captureApplicationEvents();
		assertThat(events.get(0)).isCreateInstanceEvent(request);
		assertThat(events.get(1)).isCreateInstanceFailedEvent(request, CREATE_INSTANCE_EXCEPTION);
	}

	@Test
	public void deleteServiceInstanceSucceeds() {
		DeleteServiceInstanceRequest request = DeleteServiceInstanceRequest.builder()
				.serviceInstanceId("foo")
				.serviceDefinitionId("bar")
				.build();

		DeleteServiceInstanceResponse response = DeleteServiceInstanceResponse.builder()
				.build();

		StepVerifier
				.create(serviceInstanceEventService.deleteServiceInstance(request))
				.expectNext(response)
				.verifyComplete();

		List<ApplicationEvent> events = captureApplicationEvents();
		assertThat(events.get(0)).isDeleteInstanceEvent(request);
		assertThat(events.get(1)).isDeleteInstanceCompletedEvent(request, response);
	}

	@Test
	public void deleteServiceInstanceFails() {
		DeleteServiceInstanceRequest request = DeleteServiceInstanceRequest.builder()
				.serviceInstanceId("foo")
				.build();

		StepVerifier
				.create(serviceInstanceEventService.deleteServiceInstance(request))
				.expectError(ServiceInstanceDoesNotExistException.class)
				.verify();

		List<ApplicationEvent> events = captureApplicationEvents();
		assertThat(events.get(0)).isDeleteInstanceEvent(request);
		assertThat(events.get(1)).isDeleteInstanceFailedEvent(request, DELETE_INSTANCE_EXCEPTION);
	}

	@Test
	public void updateServiceInstanceSucceeds() {
		UpdateServiceInstanceRequest request = UpdateServiceInstanceRequest.builder()
				.serviceInstanceId("foo")
				.serviceDefinitionId("bar")
				.build();

		UpdateServiceInstanceResponse response = UpdateServiceInstanceResponse.builder()
				.build();
		
		StepVerifier
				.create(serviceInstanceEventService.updateServiceInstance(request))
				.expectNext(response)
				.verifyComplete();

		List<ApplicationEvent> events = captureApplicationEvents();
		assertThat(events.get(0)).isUpdateInstanceEvent(request);
		assertThat(events.get(1)).isUpdateInstanceCompletedEvent(request, response);
	}

	@Test
	public void udpateServiceInstanceFails() {
		UpdateServiceInstanceRequest request = UpdateServiceInstanceRequest.builder()
				.serviceInstanceId("foo")
				.build();

		StepVerifier
				.create(serviceInstanceEventService.updateServiceInstance(
						request))
				.expectError(ServiceBrokerInvalidParametersException.class)
				.verify();

		List<ApplicationEvent> events = captureApplicationEvents();
		assertThat(events.get(0)).isUpdateInstanceEvent(request);
		assertThat(events.get(1)).isUpdateInstanceFailedEvent(request, UPDATE_INSTANCE_EXCEPTION);
	}

	@Test
	public void getServiceInstance() {
	}

	@Test
	public void getLastOperation() {
	}

	private List<ApplicationEvent> captureApplicationEvents() {
		ArgumentCaptor<ApplicationEvent> eventCaptor = ArgumentCaptor.forClass(ApplicationEvent.class);
		verify(applicationEventPublisher, times(2)).publishEvent(eventCaptor.capture());
		return eventCaptor.getAllValues();
	}

	private static class TestServiceInstanceService implements ServiceInstanceService {
		@Override
		public Mono<CreateServiceInstanceResponse> createServiceInstance(
				CreateServiceInstanceRequest request) {
			if (request.getServiceDefinitionId() == null) {
				return Mono.error(CREATE_INSTANCE_EXCEPTION);
			}
			return Mono.just(CreateServiceInstanceResponse.builder().build());
		}

		@Override
		public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(
				DeleteServiceInstanceRequest request) {
			if (request.getServiceDefinitionId() == null) {
				return Mono.error(DELETE_INSTANCE_EXCEPTION);
			}
			return Mono.just(DeleteServiceInstanceResponse.builder().build());
		}

		@Override
		public Mono<UpdateServiceInstanceResponse> updateServiceInstance(
				UpdateServiceInstanceRequest request) {
			if (request.getServiceDefinitionId() == null) {
				return Mono.error(UPDATE_INSTANCE_EXCEPTION);
			}
			return Mono.just(UpdateServiceInstanceResponse.builder().build());
		}
	}
}