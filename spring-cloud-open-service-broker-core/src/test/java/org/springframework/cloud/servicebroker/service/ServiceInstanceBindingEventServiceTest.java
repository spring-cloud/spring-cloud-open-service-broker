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

import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingResponse;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.cloud.servicebroker.service.ServiceBindingEventAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInstanceBindingEventServiceTest {
	private static final ServiceInstanceBindingExistsException CREATE_BINDING_EXCEPTION =
			new ServiceInstanceBindingExistsException("foo", "arrrr");
	private static final ServiceInstanceBindingDoesNotExistException DELETE_BINDING_EXCEPTION =
			new ServiceInstanceBindingDoesNotExistException("bar");

	private ServiceInstanceBindingEventService serviceInstanceBindingEventService;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		TestServiceInstanceBindingService serviceInstanceBindingService = new TestServiceInstanceBindingService();
		this.serviceInstanceBindingEventService =
				new ServiceInstanceBindingEventService(serviceInstanceBindingService, applicationEventPublisher);
	}

	@Test
	public void createServiceInstanceBindingSucceeds() {
		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder()
				.serviceInstanceId("foo")
				.serviceDefinitionId("bar")
				.build();

		CreateServiceInstanceAppBindingResponse response = CreateServiceInstanceAppBindingResponse.builder()
				.build();
		
		StepVerifier
				.create(serviceInstanceBindingEventService.createServiceInstanceBinding(
						request))
				.expectNext(response)
				.verifyComplete();

		List<ApplicationEvent> events = captureApplicationEvents();
		assertThat(events.get(0)).isCreateInstanceBindingEvent(request);
		assertThat(events.get(1)).isCreateInstanceBindingCompletedEvent(request, response);
	}

	@Test
	public void createServiceInstanceBindingFails() {
		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder()
				.serviceInstanceId("foo")
				.build();

		StepVerifier
				.create(serviceInstanceBindingEventService.createServiceInstanceBinding(
						request))
				.expectError(ServiceInstanceBindingExistsException.class)
				.verify();

		List<ApplicationEvent> events = captureApplicationEvents();
		assertThat(events.get(0)).isCreateInstanceBindingEvent(request);
		assertThat(events.get(1)).isCreateInstanceBindingFailedEvent(request, CREATE_BINDING_EXCEPTION);
	}

	@Test
	public void getServiceInstanceBinding() {
		StepVerifier
				.create(serviceInstanceBindingEventService.getServiceInstanceBinding(
						GetServiceInstanceBindingRequest.builder()
								.serviceInstanceId("foo")
								.bindingId("bar")
								.build()))
				.expectNext(GetServiceInstanceAppBindingResponse.builder().build())
				.verifyComplete();
	}

	@Test
	public void deleteServiceInstanceBindingSucceeds() {
		DeleteServiceInstanceBindingRequest request = DeleteServiceInstanceBindingRequest.builder()
				.serviceInstanceId("foo")
				.bindingId("bar")
				.build();

		StepVerifier
				.create(serviceInstanceBindingEventService.deleteServiceInstanceBinding(request))
				.expectNext()
				.verifyComplete();

		List<ApplicationEvent> events = captureApplicationEvents();
		assertThat(events.get(0)).isDeleteInstanceBindingEvent(request);
		assertThat(events.get(1)).isDeleteInstanceBindingCompletedEvent(request);
	}

	@Test
	public void deleteServiceInstanceBindingFails() {
		DeleteServiceInstanceBindingRequest request = DeleteServiceInstanceBindingRequest.builder()
				.serviceInstanceId("foo")
				.build();

		StepVerifier
				.create(serviceInstanceBindingEventService.deleteServiceInstanceBinding(request))
				.expectError(ServiceInstanceBindingDoesNotExistException.class)
				.verify();

		List<ApplicationEvent> events = captureApplicationEvents();
		assertThat(events.get(0)).isDeleteInstanceBindingEvent(request);
		assertThat(events.get(1)).isDeleteInstanceBindingFailedEvent(request, DELETE_BINDING_EXCEPTION);
	}

	private List<ApplicationEvent> captureApplicationEvents() {
		ArgumentCaptor<ApplicationEvent> eventCaptor = ArgumentCaptor.forClass(ApplicationEvent.class);
		verify(applicationEventPublisher, times(2)).publishEvent(eventCaptor.capture());
		return eventCaptor.getAllValues();
	}

	private static class TestServiceInstanceBindingService implements ServiceInstanceBindingService {
		@Override
		public Mono<CreateServiceInstanceBindingResponse> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
			if (request.getServiceDefinitionId() == null) {
				return Mono.error(CREATE_BINDING_EXCEPTION);
			}
			return Mono.just(CreateServiceInstanceAppBindingResponse.builder().build());
		}

		@Override
		public Mono<GetServiceInstanceBindingResponse> getServiceInstanceBinding(GetServiceInstanceBindingRequest request) {
			if (request.getBindingId() == null) {
				return Mono.error(new ServiceInstanceDoesNotExistException("foo"));
			}
			return Mono.just(GetServiceInstanceAppBindingResponse.builder().build());
		}

		@Override
		public Mono<Void> deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
			if (request.getBindingId() == null) {
				return Mono.error(DELETE_BINDING_EXCEPTION);
			}
			return Mono.empty();
		}
	}
}