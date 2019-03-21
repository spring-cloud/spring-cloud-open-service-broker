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

package org.springframework.cloud.servicebroker.service;

import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.service.events.EventFlowRegistries;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingInitializationFlow;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceInstanceBindingEventServiceTest {

	private TestServiceInstanceBindingService serviceInstanceBindingService;

	private ServiceInstanceBindingEventService serviceInstanceBindingEventService;

	private EventFlowRegistries eventFlowRegistries;

	private EventFlowTestResults results;

	@Before
	public void setUp() {
		this.serviceInstanceBindingService = new TestServiceInstanceBindingService();
		this.eventFlowRegistries = new EventFlowRegistries();
		this.serviceInstanceBindingEventService =
				new ServiceInstanceBindingEventService(serviceInstanceBindingService, eventFlowRegistries);
		this.results = new EventFlowTestResults();
	}

	@Test
	public void createServiceInstanceBindingSucceeds() {
		prepareBindingFlows();

		StepVerifier
				.create(serviceInstanceBindingEventService.createServiceInstanceBinding(
						CreateServiceInstanceBindingRequest.builder()
								.serviceInstanceId("foo")
								.serviceDefinitionId("bar")
								.build()))
				.expectNext(CreateServiceInstanceAppBindingResponse.builder().build())
				.verifyComplete();

		assertThat(this.results.getBeforeCreate()).isEqualTo("before create foo");
		assertThat(this.results.getAfterCreate()).isEqualTo("after create foo");
		assertThat(this.results.getErrorCreate()).isNullOrEmpty();
		assertThat(this.results.getBeforeDelete()).isNullOrEmpty();
		assertThat(this.results.getAfterDelete()).isNullOrEmpty();
		assertThat(this.results.getErrorDelete()).isNullOrEmpty();
	}

	@Test
	public void createServiceInstanceBindingFails() {
		prepareBindingFlows();

		StepVerifier
				.create(serviceInstanceBindingEventService.createServiceInstanceBinding(
						CreateServiceInstanceBindingRequest.builder()
								.serviceInstanceId("foo")
								.build()))
				.expectError()
				.verify();

		assertThat(this.results.getBeforeCreate()).isEqualTo("before create foo");
		assertThat(this.results.getAfterCreate()).isNullOrEmpty();
		assertThat(this.results.getErrorCreate()).isEqualTo("error create foo");
		assertThat(this.results.getBeforeDelete()).isNullOrEmpty();
		assertThat(this.results.getAfterDelete()).isNullOrEmpty();
		assertThat(this.results.getErrorDelete()).isNullOrEmpty();
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
		prepareBindingFlows();

		StepVerifier
				.create(serviceInstanceBindingEventService.deleteServiceInstanceBinding(
						DeleteServiceInstanceBindingRequest.builder()
								.serviceInstanceId("foo")
								.bindingId("bar")
								.build()))
				.expectNext(DeleteServiceInstanceBindingResponse.builder().build())
				.verifyComplete();

		assertThat(this.results.getBeforeCreate()).isNullOrEmpty();
		assertThat(this.results.getAfterCreate()).isNullOrEmpty();
		assertThat(this.results.getErrorCreate()).isNullOrEmpty();
		assertThat(this.results.getBeforeDelete()).isEqualTo("before delete foo");
		assertThat(this.results.getAfterDelete()).isEqualTo("after delete foo");
		assertThat(this.results.getErrorDelete()).isNullOrEmpty();
	}

	@Test
	public void deleteServiceInstanceBindingFails() {
		prepareBindingFlows();

		StepVerifier
				.create(serviceInstanceBindingEventService.deleteServiceInstanceBinding(
						DeleteServiceInstanceBindingRequest.builder()
								.serviceInstanceId("foo")
								.build()))
				.expectError()
				.verify();

		assertThat(this.results.getBeforeCreate()).isNullOrEmpty();
		assertThat(this.results.getAfterCreate()).isNullOrEmpty();
		assertThat(this.results.getErrorCreate()).isNullOrEmpty();
		assertThat(this.results.getBeforeDelete()).isEqualTo("before delete foo");
		assertThat(this.results.getAfterDelete()).isNullOrEmpty();
		assertThat(this.results.getErrorDelete()).isEqualTo("error delete foo");
	}


	private void prepareBindingFlows() {
		this.eventFlowRegistries.getCreateInstanceBindingRegistry()
				.addInitializationFlow(new CreateServiceInstanceBindingInitializationFlow() {
					@Override
					public Mono<Void> initialize(CreateServiceInstanceBindingRequest request) {
						return results.setBeforeCreate("before create " + request.getServiceInstanceId());
					}
				})
				.then(eventFlowRegistries.getCreateInstanceBindingRegistry()
						.addErrorFlow(new CreateServiceInstanceBindingErrorFlow() {
							@Override
							public Mono<Void> error(CreateServiceInstanceBindingRequest request, Throwable t) {
								return results.setErrorCreate("error create " + request.getServiceInstanceId());
							}
						}))
				.then(eventFlowRegistries.getCreateInstanceBindingRegistry()
						.addCompletionFlow(new CreateServiceInstanceBindingCompletionFlow() {
							@Override
							public Mono<Void> complete(CreateServiceInstanceBindingRequest request, CreateServiceInstanceBindingResponse response) {
								return results.setAfterCreate("after create " + request.getServiceInstanceId());
							}
						}))
				.then(eventFlowRegistries.getDeleteInstanceBindingRegistry()
						.addInitializationFlow(new DeleteServiceInstanceBindingInitializationFlow() {
							@Override
							public Mono<Void> initialize(DeleteServiceInstanceBindingRequest request) {
								return results.setBeforeDelete("before delete " + request.getServiceInstanceId());
							}
						}))
				.then(eventFlowRegistries.getDeleteInstanceBindingRegistry()
						.addErrorFlow(new DeleteServiceInstanceBindingErrorFlow() {
							@Override
							public Mono<Void> error(DeleteServiceInstanceBindingRequest request, Throwable t) {
								return results.setErrorDelete("error delete " + request.getServiceInstanceId());
							}
						}))
				.then(eventFlowRegistries.getDeleteInstanceBindingRegistry()
						.addCompletionFlow(new DeleteServiceInstanceBindingCompletionFlow() {
							@Override
							public Mono<Void> complete(DeleteServiceInstanceBindingRequest request, DeleteServiceInstanceBindingResponse response) {
								return results.setAfterDelete("after delete " + request.getServiceInstanceId());
							}
						}))
				.subscribe();
	}

	private static class TestServiceInstanceBindingService implements ServiceInstanceBindingService {

		@Override
		public Mono<CreateServiceInstanceBindingResponse> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
			if (request.getServiceDefinitionId() == null) {
				return Mono.error(new ServiceInstanceBindingExistsException("foo", "arrrr"));
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
		public Mono<DeleteServiceInstanceBindingResponse> deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
			if (request.getBindingId() == null) {
				return Mono.error(new ServiceInstanceBindingDoesNotExistException("bar"));
			}
			return Mono.just(DeleteServiceInstanceBindingResponse.builder().build());
		}

	}

	private static class EventFlowTestResults {

		private String beforeCreate = null;

		private String afterCreate = null;

		private String errorCreate = null;

		private String beforeDelete = null;

		private String afterDelete = null;

		private String errorDelete = null;

		String getBeforeCreate() {
			return beforeCreate;
		}

		public Mono<Void> setBeforeCreate(String beforeCreate) {
			return Mono.fromCallable(() -> this.beforeCreate = beforeCreate)
					.then();
		}

		String getAfterCreate() {
			return afterCreate;
		}

		public Mono<Void> setAfterCreate(String afterCreate) {
			return Mono.fromCallable(() -> this.afterCreate = afterCreate)
					.then();
		}

		String getErrorCreate() {
			return errorCreate;
		}

		public Mono<Void> setErrorCreate(String errorCreate) {
			return Mono.fromCallable(() -> this.errorCreate = errorCreate)
					.then();
		}

		String getBeforeDelete() {
			return beforeDelete;
		}

		public Mono<Void> setBeforeDelete(String beforeDelete) {
			return Mono.fromCallable(() -> this.beforeDelete = beforeDelete)
					.then();
		}

		String getAfterDelete() {
			return afterDelete;
		}

		public Mono<Void> setAfterDelete(String afterDelete) {
			return Mono.fromCallable(() -> this.afterDelete = afterDelete)
					.then();
		}

		String getErrorDelete() {
			return errorDelete;
		}

		public Mono<Void> setErrorDelete(String errorDelete) {
			return Mono.fromCallable(() -> this.errorDelete = errorDelete)
					.then();
		}
	}
}