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

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceInstanceBindingEventServiceTest {

	private TestServiceInstanceBindingService serviceInstanceBindingService;

	private ServiceInstanceBindingEventService serviceInstanceBindingEventService;

	@Before
	public void setUp() {
		this.serviceInstanceBindingService = new TestServiceInstanceBindingService();
		this.serviceInstanceBindingEventService =
				new ServiceInstanceBindingEventService(serviceInstanceBindingService);
	}

	@Test
	public void createServiceInstanceBindingSucceeds() {
		assertInitialState();

		StepVerifier
				.create(serviceInstanceBindingEventService.createServiceInstanceBinding(
						CreateServiceInstanceBindingRequest.builder().build()))
				.expectNext(CreateServiceInstanceAppBindingResponse.builder().build())
				.verifyComplete();

		assertThat(this.serviceInstanceBindingService.getBeforeCreate()).isEqualTo("beforeCreate");
		assertThat(this.serviceInstanceBindingService.getAfterCreate()).isEqualTo("afterCreate");
		assertThat(this.serviceInstanceBindingService.getErrorCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getBeforeDelete()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getAfterDelete()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getErrorDelete()).isNullOrEmpty();
	}

	@Test
	public void createServiceInstanceBindingFails() {
		assertInitialState();

		StepVerifier
				.create(serviceInstanceBindingEventService.createServiceInstanceBinding(null))
				.expectError()
				.verify();

		assertThat(this.serviceInstanceBindingService.getBeforeCreate()).isEqualTo("beforeCreate");
		assertThat(this.serviceInstanceBindingService.getAfterCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getErrorCreate()).isEqualTo("errorCreate");
		assertThat(this.serviceInstanceBindingService.getBeforeDelete()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getAfterDelete()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getErrorDelete()).isNullOrEmpty();
	}

	@Test
	public void getServiceInstanceBindingS() {
		assertInitialState();

		StepVerifier
				.create(serviceInstanceBindingEventService.getServiceInstanceBinding(
						GetServiceInstanceBindingRequest.builder().build()))
				.expectNext(GetServiceInstanceAppBindingResponse.builder().build())
				.verifyComplete();
	}

	@Test
	public void deleteServiceInstanceBindingSucceeds() {
		assertInitialState();

		StepVerifier
				.create(serviceInstanceBindingEventService.deleteServiceInstanceBinding(
						DeleteServiceInstanceBindingRequest.builder().build()))
				.expectNext()
				.verifyComplete();

		assertThat(this.serviceInstanceBindingService.getBeforeCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getAfterCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getErrorCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getBeforeDelete()).isEqualTo("beforeDelete");
		assertThat(this.serviceInstanceBindingService.getAfterDelete()).isEqualTo("afterDelete");
		assertThat(this.serviceInstanceBindingService.getErrorDelete()).isNullOrEmpty();
	}

	@Test
	public void deleteServiceInstanceBindingFails() {
		assertInitialState();

		StepVerifier
				.create(serviceInstanceBindingEventService.deleteServiceInstanceBinding(null))
				.expectError()
				.verify();

		assertThat(this.serviceInstanceBindingService.getBeforeCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getAfterCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getErrorCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getBeforeDelete()).isEqualTo("beforeDelete");
		assertThat(this.serviceInstanceBindingService.getAfterDelete()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getErrorDelete()).isEqualTo("errorDelete");
	}

	private void assertInitialState() {
		assertThat(this.serviceInstanceBindingService.getBeforeCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getAfterCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getErrorCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getBeforeDelete()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getAfterDelete()).isNullOrEmpty();
		assertThat(this.serviceInstanceBindingService.getErrorDelete()).isNullOrEmpty();
	}

	private static class TestServiceInstanceBindingService implements ServiceInstanceBindingService {

		private String beforeCreate = null;

		private String afterCreate = null;

		private String errorCreate = null;

		private String beforeDelete = null;

		private String afterDelete = null;

		private String errorDelete = null;

		String getBeforeCreate() {
			return beforeCreate;
		}

		String getAfterCreate() {
			return afterCreate;
		}

		String getErrorCreate() {
			return errorCreate;
		}

		String getBeforeDelete() {
			return beforeDelete;
		}

		String getAfterDelete() {
			return afterDelete;
		}

		String getErrorDelete() {
			return errorDelete;
		}

		@Override
		public Mono<CreateServiceInstanceBindingResponse> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
			if (request == null) {
				return Mono.error(new ServiceInstanceBindingExistsException("foo", "arrrr"));
			}
			return Mono.just(CreateServiceInstanceAppBindingResponse.builder().build());
		}

		@Override
		public Mono<GetServiceInstanceBindingResponse> getServiceInstanceBinding(GetServiceInstanceBindingRequest request) {
			if (request == null) {
				return Mono.error(new ServiceInstanceDoesNotExistException("foo"));
			}
			return Mono.just(GetServiceInstanceAppBindingResponse.builder().build());
		}

		@Override
		public Mono<Void> deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
			if (request == null) {
				return Mono.error(new ServiceInstanceBindingDoesNotExistException("bar"));
			}
			return Mono.empty();
		}

		@Override
		public Mono<Void> getBeforeCreateFlow() {
			return Mono.fromCallable(() -> this.beforeCreate = "beforeCreate")
					.then();
		}

		@Override
		public Mono<Void> getAfterCreateFlow() {
			return Mono.fromCallable(() -> this.afterCreate = "afterCreate")
					.then();
		}

		@Override
		public Mono<Void> getErrorCreateFlow() {
			return Mono.fromCallable(() -> this.errorCreate = "errorCreate")
					.then();
		}

		@Override
		public Mono<Void> getBeforeDeleteFlow() {
			return Mono.fromCallable(() -> this.beforeDelete = "beforeDelete")
					.then();
		}

		@Override
		public Mono<Void> getAfterDeleteFlow() {
			return Mono.fromCallable(() -> this.afterDelete = "afterDelete")
					.then();
		}

		@Override
		public Mono<Void> getErrorDeleteFlow() {
			return Mono.fromCallable(() -> this.errorDelete = "errorDelete")
					.then();
		}
	}
}