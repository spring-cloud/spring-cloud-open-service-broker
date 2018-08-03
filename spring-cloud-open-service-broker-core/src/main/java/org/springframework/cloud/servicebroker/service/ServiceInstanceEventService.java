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

import org.springframework.cloud.servicebroker.event.instance.CreateServiceInstanceEvent;
import org.springframework.cloud.servicebroker.event.instance.CreateServiceInstanceCompletedEvent;
import org.springframework.cloud.servicebroker.event.instance.DeleteServiceInstanceCompletedEvent;
import org.springframework.cloud.servicebroker.event.instance.DeleteServiceInstanceEvent;
import org.springframework.cloud.servicebroker.event.instance.UpdateServiceInstanceCompletedEvent;
import org.springframework.cloud.servicebroker.event.instance.UpdateServiceInstanceEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;

/**
 * Internal implementation of {@link ServiceInstanceService} that attaches event hooks to
 * requests related to provisioning, updating, and deprovisioning service instances.
 *
 * @author Roy Clarkson
 */
public class ServiceInstanceEventService implements ServiceInstanceService {

	private final ServiceInstanceService service;
	private final ApplicationEventPublisher applicationEventPublisher;

	public ServiceInstanceEventService(ServiceInstanceService serviceInstanceService,
									   ApplicationEventPublisher applicationEventPublisher) {
		this.service = serviceInstanceService;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public Mono<CreateServiceInstanceResponse> createServiceInstance(CreateServiceInstanceRequest request) {
		return publishEvent(new CreateServiceInstanceEvent(request))
				.then(service.createServiceInstance(request))
				.onErrorResume(e -> publishEvent(new CreateServiceInstanceCompletedEvent(request, e))
						.then(Mono.error(e)))
				.flatMap(response -> publishEvent(new CreateServiceInstanceCompletedEvent(request, response))
						.then(Mono.just(response)));
	}

	@Override
	public Mono<GetServiceInstanceResponse> getServiceInstance(GetServiceInstanceRequest request) {
		return service.getServiceInstance(request);
	}

	@Override
	public Mono<GetLastServiceOperationResponse> getLastOperation(GetLastServiceOperationRequest request) {
		return service.getLastOperation(request);
	}

	@Override
	public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(DeleteServiceInstanceRequest request) {
		return publishEvent(new DeleteServiceInstanceEvent(request))
				.then(service.deleteServiceInstance(request))
				.onErrorResume(e -> publishEvent(new DeleteServiceInstanceCompletedEvent(request, e))
						.then(Mono.error(e)))
				.flatMap(response -> publishEvent(new DeleteServiceInstanceCompletedEvent(request, response))
						.then(Mono.just(response)));
	}

	@Override
	public Mono<UpdateServiceInstanceResponse> updateServiceInstance(UpdateServiceInstanceRequest request) {
		return publishEvent(new UpdateServiceInstanceEvent(request))
				.then(service.updateServiceInstance(request))
				.onErrorResume(e -> publishEvent(new UpdateServiceInstanceCompletedEvent(request, e))
						.then(Mono.error(e)))
				.flatMap(response -> publishEvent(new UpdateServiceInstanceCompletedEvent(request, response))
						.then(Mono.just(response)));
	}

	private Mono<Void> publishEvent(ApplicationEvent event) {
		return Mono.fromCallable(() -> {
			applicationEventPublisher.publishEvent(event);
			return Mono.empty();
		}).then();
	}
}
