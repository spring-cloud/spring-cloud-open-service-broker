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

import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceInstanceEventServiceTest {

	private TestServiceInstanceService serviceInstanceService;

	private ServiceInstanceEventService serviceInstanceEventService;

	@Before
	public void setUp() throws Exception {
		this.serviceInstanceService = new TestServiceInstanceService();
		this.serviceInstanceEventService = new ServiceInstanceEventService(
				serviceInstanceService);
	}

	@Test
	public void createServiceInstanceSucceeds() {
		assertInitalState();

		StepVerifier
				.create(serviceInstanceEventService.createServiceInstance(
						CreateServiceInstanceRequest.builder().build()))
				.expectNext(CreateServiceInstanceResponse.builder().build())
				.verifyComplete();

		assertThat(this.serviceInstanceService.getBeforeCreate()).isEqualTo("before");
		assertThat(this.serviceInstanceService.getAfterCreate()).isEqualTo("after");
		assertThat(this.serviceInstanceService.getErrorCreate()).isNullOrEmpty();
	}

	@Test
	public void createServiceInstanceFails() {
		assertInitalState();

		StepVerifier.create(serviceInstanceEventService.createServiceInstance(null))
				.expectError()
				.verify();

		assertThat(this.serviceInstanceService.getBeforeCreate()).isEqualTo("before");
		assertThat(this.serviceInstanceService.getAfterCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceService.getErrorCreate()).isEqualTo("error");
	}

	@Test
	public void deleteServiceInstanceSucceeds() {
		assertInitalState();

		StepVerifier
				.create(serviceInstanceEventService.deleteServiceInstance(
						DeleteServiceInstanceRequest.builder().build()))
				.expectNext(DeleteServiceInstanceResponse.builder().build())
				.verifyComplete();

		assertThat(this.serviceInstanceService.getBeforeDelete()).isEqualTo("before");
		assertThat(this.serviceInstanceService.getAfterDelete()).isEqualTo("after");
		assertThat(this.serviceInstanceService.getErrorDelete()).isNullOrEmpty();
	}

	@Test
	public void deleteServiceInstanceFails() {
		assertInitalState();

		StepVerifier.create(serviceInstanceEventService.deleteServiceInstance(null))
				.expectError()
				.verify();

		assertThat(this.serviceInstanceService.getBeforeDelete()).isEqualTo("before");
		assertThat(this.serviceInstanceService.getAfterDelete()).isNullOrEmpty();
		assertThat(this.serviceInstanceService.getErrorDelete()).isEqualTo("error");
	}

	@Test
	public void updateServiceInstanceSucceeds() {
		assertInitalState();

		StepVerifier
				.create(serviceInstanceEventService.updateServiceInstance(
						UpdateServiceInstanceRequest.builder().build()))
				.expectNext(UpdateServiceInstanceResponse.builder().build())
				.verifyComplete();

		assertThat(this.serviceInstanceService.getBeforeUpdate()).isEqualTo("before");
		assertThat(this.serviceInstanceService.getAfterUpdate()).isEqualTo("after");
		assertThat(this.serviceInstanceService.getErrorUpdate()).isNullOrEmpty();
	}

	@Test
	public void udpateServiceInstanceFails() {
		assertInitalState();

		StepVerifier
				.create(serviceInstanceEventService.updateServiceInstance(null))
				.expectError()
				.verify();

		assertThat(this.serviceInstanceService.getBeforeUpdate()).isEqualTo("before");
		assertThat(this.serviceInstanceService.getAfterUpdate()).isNullOrEmpty();
		assertThat(this.serviceInstanceService.getErrorUpdate()).isEqualTo("error");
	}

	@Test
	public void getServiceInstance() {
	}

	@Test
	public void getLastOperation() {
	}

	private void assertInitalState() {
		assertThat(this.serviceInstanceService.getBeforeCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceService.getAfterCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceService.getErrorCreate()).isNullOrEmpty();
		assertThat(this.serviceInstanceService.getBeforeUpdate()).isNullOrEmpty();
		assertThat(this.serviceInstanceService.getAfterUpdate()).isNullOrEmpty();
		assertThat(this.serviceInstanceService.getErrorUpdate()).isNullOrEmpty();
		assertThat(this.serviceInstanceService.getBeforeDelete()).isNullOrEmpty();
		assertThat(this.serviceInstanceService.getAfterDelete()).isNullOrEmpty();
		assertThat(this.serviceInstanceService.getErrorDelete()).isNullOrEmpty();
	}

	private static class TestServiceInstanceService implements ServiceInstanceService {

		private String beforeCreate = null;

		private String afterCreate = null;

		private String errorCreate = null;

		private String beforeDelete = null;

		private String afterDelete = null;

		private String errorDelete = null;

		private String beforeUpdate = null;

		private String afterUpdate = null;

		private String errorUpdate = null;

		String getBeforeCreate() {
			return this.beforeCreate;
		}

		String getAfterCreate() {
			return this.afterCreate;
		}

		String getErrorCreate() {
			return this.errorCreate;
		}

		String getBeforeDelete() {
			return this.beforeDelete;
		}

		String getAfterDelete() {
			return this.afterDelete;
		}

		String getErrorDelete() {
			return this.errorDelete;
		}

		String getBeforeUpdate() {
			return this.beforeUpdate;
		}

		String getAfterUpdate() {
			return this.afterUpdate;
		}

		String getErrorUpdate() {
			return this.errorUpdate;
		}

		@Override
		public Mono<CreateServiceInstanceResponse> createServiceInstance(
				CreateServiceInstanceRequest request) {
			if (request == null) {
				return Mono.error(new ServiceBrokerInvalidParametersException("arrrr"));
			}
			return Mono.just(CreateServiceInstanceResponse.builder().build());
		}

		@Override
		public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(
				DeleteServiceInstanceRequest request) {
			if (request == null) {
				return Mono.error(new ServiceInstanceDoesNotExistException("foo"));
			}
			return Mono.just(DeleteServiceInstanceResponse.builder().build());
		}

		@Override
		public Mono<UpdateServiceInstanceResponse> updateServiceInstance(
				UpdateServiceInstanceRequest request) {
			if (request == null) {
				return Mono.error(new ServiceBrokerInvalidParametersException("arrrr"));
			}
			return Mono.just(UpdateServiceInstanceResponse.builder().build());
		}

		@Override
		public Mono<Void> getBeforeCreateFlow() {
			return Mono.fromCallable(() -> beforeCreate = "before")
					.then();
		}

		@Override
		public Mono<Void> getAfterCreateFlow() {
			return Mono.fromCallable(() -> afterCreate = "after")
					.then();
		}

		@Override
		public Mono<Void> getErrorCreateFlow() {
			return Mono.fromCallable(() -> errorCreate = "error")
					.then();
		}

		@Override
		public Mono<Void> getBeforeDeleteFlow() {
			return Mono.fromCallable(() -> beforeDelete = "before")
					.then();
		}

		@Override
		public Mono<Void> getAfterDeleteFlow() {
			return Mono.fromCallable(() -> afterDelete = "after")
					.then();
		}

		@Override
		public Mono<Void> getErrorDeleteFlow() {
			return Mono.fromCallable(() -> errorDelete = "error")
					.then();
		}

		@Override
		public Mono<Void> getBeforeUpdateFlow() {
			return Mono.fromCallable(() -> beforeUpdate = "before")
					.then();
		}

		@Override
		public Mono<Void> getAfterUpdateFlow() {
			return Mono.fromCallable(() -> afterUpdate = "after")
					.then();
		}

		@Override
		public Mono<Void> getErrorUpdateFlow() {
			return Mono.fromCallable(() -> errorUpdate = "error")
					.then();
		}

	}
}