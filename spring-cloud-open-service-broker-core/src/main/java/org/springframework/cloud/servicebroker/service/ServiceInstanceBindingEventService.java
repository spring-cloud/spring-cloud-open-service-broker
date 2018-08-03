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

import org.springframework.cloud.servicebroker.event.binding.CreateServiceInstanceBindingCompletedEvent;
import org.springframework.cloud.servicebroker.event.binding.CreateServiceInstanceBindingEvent;
import org.springframework.cloud.servicebroker.event.binding.DeleteServiceInstanceBindingCompletedEvent;
import org.springframework.cloud.servicebroker.event.binding.DeleteServiceInstanceBindingEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingResponse;

/**
 * Internal implementation of {@link ServiceInstanceBindingService} that attaches event
 * hooks to the requests to create and delete service instance bindings
 *
 * @author Roy Clarkson
 */
public class ServiceInstanceBindingEventService implements ServiceInstanceBindingService {

	private final ServiceInstanceBindingService service;
	private final ApplicationEventPublisher applicationEventPublisher;

	public ServiceInstanceBindingEventService(ServiceInstanceBindingService service,
											  ApplicationEventPublisher applicationEventPublisher) {
		this.service = service;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public Mono<CreateServiceInstanceBindingResponse> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
		return publishEvent(new CreateServiceInstanceBindingEvent(request))
				.then(service.createServiceInstanceBinding(request))
				.onErrorResume(e -> publishEvent(new CreateServiceInstanceBindingCompletedEvent(request, e))
						.then(Mono.error(e)))
				.flatMap(response -> publishEvent(new CreateServiceInstanceBindingCompletedEvent(request, response))
						.then(Mono.just(response)));
	}

	@Override
	public Mono<GetServiceInstanceBindingResponse> getServiceInstanceBinding(GetServiceInstanceBindingRequest request) {
		return service.getServiceInstanceBinding(request);
	}

	@Override
	public Mono<Void> deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
		return publishEvent(new DeleteServiceInstanceBindingEvent(request))
				.then(service.deleteServiceInstanceBinding(request))
				.onErrorResume(e -> publishEvent(new DeleteServiceInstanceBindingCompletedEvent(request, e))
						.then(Mono.error(e)))
				.then(publishEvent(new DeleteServiceInstanceBindingCompletedEvent(request)));
	}

	private Mono<Void> publishEvent(ApplicationEvent event) {
		return Mono.fromCallable(() -> {
			applicationEventPublisher.publishEvent(event);
			return Mono.empty();
		}).then();
	}
}
