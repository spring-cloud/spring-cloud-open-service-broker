/*
 * Copyright 2002-2020 the original author or authors.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.events.EventFlowRegistries;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceInitializationFlow;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("deprecation")
class ServiceInstanceEventServiceTest {

	private ServiceInstanceEventService serviceInstanceEventService;

	private EventFlowRegistries eventFlowRegistries;

	private EventFlowTestResults results;

	@BeforeEach
	void setUp() {
		this.eventFlowRegistries = new EventFlowRegistries();
		this.serviceInstanceEventService = new ServiceInstanceEventService(
				new TestServiceInstanceService(), eventFlowRegistries);
		this.results = new EventFlowTestResults();
	}

	@Test
	void createServiceInstanceSucceeds() {
		prepareCreateEventFlows();

		StepVerifier
				.create(serviceInstanceEventService.createServiceInstance(
						CreateServiceInstanceRequest.builder()
								.serviceInstanceId("service-instance-id")
								.serviceDefinitionId("service-def-id")
								.build()))
				.expectNext(CreateServiceInstanceResponse.builder().build())
				.verifyComplete();

		assertThat(this.results.getBeforeCreate()).isEqualTo("before service-instance-id");
		assertThat(this.results.getAfterCreate()).isEqualTo("after service-instance-id");
		assertThat(this.results.getErrorCreate()).isNullOrEmpty();
	}

	private void prepareCreateEventFlows() {
		this.eventFlowRegistries.getCreateInstanceRegistry()
				.addInitializationFlow(new CreateServiceInstanceInitializationFlow() {
					@Override
					public Mono<Void> initialize(CreateServiceInstanceRequest request) {
						return results.setBeforeCreate("before " + request.getServiceInstanceId());
					}
				})
				.then(this.eventFlowRegistries.getCreateInstanceRegistry()
						.addCompletionFlow(new CreateServiceInstanceCompletionFlow() {
							@Override
							public Mono<Void> complete(CreateServiceInstanceRequest request,
									CreateServiceInstanceResponse response) {
								return results.setAfterCreate("after " + request.getServiceInstanceId());
							}
						}))
				.then(eventFlowRegistries.getCreateInstanceRegistry()
						.addErrorFlow(new CreateServiceInstanceErrorFlow() {
							@Override
							public Mono<Void> error(CreateServiceInstanceRequest request, Throwable t) {
								return results.setErrorCreate("error " + request.getServiceInstanceId());
							}
						}))
				.subscribe();
	}

	@Test
	void createServiceInstanceFails() {
		prepareCreateEventFlows();

		StepVerifier.create(serviceInstanceEventService.createServiceInstance(
				CreateServiceInstanceRequest.builder()
						.serviceInstanceId("service-instance-id")
						.build()))
				.expectError()
				.verify();

		assertThat(this.results.getBeforeCreate()).isEqualTo("before service-instance-id");
		assertThat(this.results.getAfterCreate()).isNullOrEmpty();
		assertThat(this.results.getErrorCreate()).isEqualTo("error service-instance-id");
	}

	@Test
	void deleteServiceInstanceSucceeds() {
		prepareDeleteEventFlows();

		StepVerifier
				.create(serviceInstanceEventService.deleteServiceInstance(
						DeleteServiceInstanceRequest.builder()
								.serviceInstanceId("service-instance-id")
								.serviceDefinitionId("service-def-id")
								.build()))
				.expectNext(DeleteServiceInstanceResponse.builder().build())
				.verifyComplete();

		assertThat(this.results.getBeforeDelete()).isEqualTo("before service-instance-id");
		assertThat(this.results.getAfterDelete()).isEqualTo("after service-instance-id");
		assertThat(this.results.getErrorDelete()).isNullOrEmpty();
	}

	private void prepareDeleteEventFlows() {
		this.eventFlowRegistries.getDeleteInstanceRegistry()
				.addInitializationFlow(new DeleteServiceInstanceInitializationFlow() {
					@Override
					public Mono<Void> initialize(DeleteServiceInstanceRequest request) {
						return results.setBeforeDelete("before " + request.getServiceInstanceId());
					}
				})
				.then(this.eventFlowRegistries.getDeleteInstanceRegistry()
						.addCompletionFlow(new DeleteServiceInstanceCompletionFlow() {
							@Override
							public Mono<Void> complete(
									DeleteServiceInstanceRequest request,
									DeleteServiceInstanceResponse response) {
								return results.setAfterDelete("after " + request.getServiceInstanceId());
							}
						}))
				.then(eventFlowRegistries.getDeleteInstanceRegistry()
						.addErrorFlow(new DeleteServiceInstanceErrorFlow() {
							@Override
							public Mono<Void> error(DeleteServiceInstanceRequest request,
									Throwable t) {
								return results.setErrorDelete("error " + request.getServiceInstanceId());
							}
						}))
				.subscribe();
	}

	@Test
	void deleteServiceInstanceFails() {
		prepareDeleteEventFlows();

		StepVerifier
				.create(serviceInstanceEventService.deleteServiceInstance(
						DeleteServiceInstanceRequest.builder()
								.serviceInstanceId("service-instance-id")
								.build()))
				.expectError()
				.verify();

		assertThat(this.results.getBeforeDelete()).isEqualTo("before service-instance-id");
		assertThat(this.results.getAfterDelete()).isNullOrEmpty();
		assertThat(this.results.getErrorDelete()).isEqualTo("error service-instance-id");
	}

	@Test
	void updateServiceInstanceSucceeds() {
		prepareUpdateEventFlows();

		StepVerifier
				.create(serviceInstanceEventService.updateServiceInstance(
						UpdateServiceInstanceRequest.builder()
								.serviceInstanceId("service-instance-id")
								.serviceDefinitionId("service-def-id")
								.build()))
				.expectNext(UpdateServiceInstanceResponse.builder().build())
				.verifyComplete();

		assertThat(this.results.getBeforeUpdate()).isEqualTo("before service-instance-id");
		assertThat(this.results.getAfterUpdate()).isEqualTo("after service-instance-id");
		assertThat(this.results.getErrorUpdate()).isNullOrEmpty();
	}

	private void prepareUpdateEventFlows() {
		this.eventFlowRegistries.getUpdateInstanceRegistry()
				.addInitializationFlow(new UpdateServiceInstanceInitializationFlow() {
					@Override
					public Mono<Void> initialize(UpdateServiceInstanceRequest request) {
						return results.setBeforeUpdate("before " + request.getServiceInstanceId());
					}
				})
				.then(this.eventFlowRegistries.getUpdateInstanceRegistry()
						.addCompletionFlow(new UpdateServiceInstanceCompletionFlow() {
							@Override
							public Mono<Void> complete(
									UpdateServiceInstanceRequest request,
									UpdateServiceInstanceResponse response) {
								return results.setAfterUpdate("after " + request.getServiceInstanceId());
							}
						}))
				.then(eventFlowRegistries.getUpdateInstanceRegistry()
						.addErrorFlow(new UpdateServiceInstanceErrorFlow() {
							@Override
							public Mono<Void> error(UpdateServiceInstanceRequest request,
									Throwable t) {
								return results.setErrorUpdate("error " + request.getServiceInstanceId());
							}
						}))
				.subscribe();
	}

	@Test
	void updateServiceInstanceFails() {
		prepareUpdateEventFlows();

		StepVerifier
				.create(serviceInstanceEventService.updateServiceInstance(
						UpdateServiceInstanceRequest.builder()
								.serviceInstanceId("service-instance-id")
								.build()))
				.expectError()
				.verify();

		assertThat(this.results.getBeforeUpdate()).isEqualTo("before service-instance-id");
		assertThat(this.results.getAfterUpdate()).isNullOrEmpty();
		assertThat(this.results.getErrorUpdate()).isEqualTo("error service-instance-id");
	}

	@Test
	void getServiceInstance() {
	}

	@Test
	void getLastOperationSucceeds() {
		prepareLastOperationEventFlows();

		StepVerifier
				.create(serviceInstanceEventService.getLastOperation(GetLastServiceOperationRequest.builder()
						.serviceInstanceId("service-instance-id")
						.serviceDefinitionId("service-def-id")
						.build()))
				.expectNext(GetLastServiceOperationResponse.builder().build())
				.verifyComplete();

		assertThat(this.results.getBeforeLastOperation()).isEqualTo("before service-instance-id");
		assertThat(this.results.getAfterLastOperation()).isEqualTo("after service-instance-id");
		assertThat(this.results.getErrorLastOperation()).isNullOrEmpty();
	}

	@Test
	void getLastOperationFails() {
		prepareLastOperationEventFlows();

		StepVerifier
				.create(serviceInstanceEventService.getLastOperation(GetLastServiceOperationRequest.builder()
						.serviceInstanceId("service-instance-id")
						.build()))
				.expectError()
				.verify();

		assertThat(this.results.getBeforeLastOperation()).isEqualTo("before service-instance-id");
		assertThat(this.results.getAfterLastOperation()).isNullOrEmpty();
		assertThat(this.results.getErrorLastOperation()).isEqualTo("error service-instance-id");
	}

	private void prepareLastOperationEventFlows() {
		this.eventFlowRegistries.getAsyncOperationRegistry()
				.addInitializationFlow(new AsyncOperationServiceInstanceInitializationFlow() {
					@Override
					public Mono<Void> initialize(GetLastServiceOperationRequest request) {
						return results.setBeforeLastOperation("before " + request.getServiceInstanceId());
					}
				})
				.then(this.eventFlowRegistries.getAsyncOperationRegistry()
						.addCompletionFlow(new AsyncOperationServiceInstanceCompletionFlow() {
							@Override
							public Mono<Void> complete(
									GetLastServiceOperationRequest request,
									GetLastServiceOperationResponse response) {
								return results.setAfterLastOperation("after " + request.getServiceInstanceId());
							}
						}))
				.then(eventFlowRegistries.getAsyncOperationRegistry()
						.addErrorFlow(new AsyncOperationServiceInstanceErrorFlow() {
							@Override
							public Mono<Void> error(GetLastServiceOperationRequest request,
									Throwable t) {
								return results.setErrorLastOperation("error " + request.getServiceInstanceId());
							}
						}))
				.subscribe();
	}

	private static class TestServiceInstanceService implements ServiceInstanceService {

		@Override
		public Mono<CreateServiceInstanceResponse> createServiceInstance(
				CreateServiceInstanceRequest request) {
			if (request.getServiceDefinitionId() == null) {
				return Mono.error(new ServiceBrokerInvalidParametersException("arrrr"));
			}
			return Mono.just(CreateServiceInstanceResponse.builder().build());
		}

		@Override
		public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(
				DeleteServiceInstanceRequest request) {
			if (request.getServiceDefinitionId() == null) {
				return Mono.error(new ServiceInstanceDoesNotExistException("service-instance-id"));
			}
			return Mono.just(DeleteServiceInstanceResponse.builder().build());
		}

		@Override
		public Mono<UpdateServiceInstanceResponse> updateServiceInstance(
				UpdateServiceInstanceRequest request) {
			if (request.getServiceDefinitionId() == null) {
				return Mono.error(new ServiceBrokerInvalidParametersException("arrrr"));
			}
			return Mono.just(UpdateServiceInstanceResponse.builder().build());
		}

		@Override
		public Mono<GetLastServiceOperationResponse> getLastOperation(GetLastServiceOperationRequest request) {
			if (request.getServiceDefinitionId() == null) {
				return Mono.error(new ServiceBrokerInvalidParametersException("arrrr"));
			}
			return Mono.just(GetLastServiceOperationResponse.builder().build());
		}

	}

}
