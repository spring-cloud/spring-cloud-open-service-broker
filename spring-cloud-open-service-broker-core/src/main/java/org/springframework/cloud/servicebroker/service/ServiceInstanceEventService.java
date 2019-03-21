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
import org.springframework.cloud.servicebroker.service.events.EventFlowRegistries;

/**
 * Internal implementation of {@link ServiceInstanceService} that attaches event hooks to
 * requests related to provisioning, updating, and deprovisioning service instances.
 *
 * @author Roy Clarkson
 */
public class ServiceInstanceEventService implements ServiceInstanceService {

	private final ServiceInstanceService service;

	private final EventFlowRegistries flows;

	public ServiceInstanceEventService(ServiceInstanceService serviceInstanceService,
									   EventFlowRegistries eventFlowRegistries) {
		this.service = serviceInstanceService;
		this.flows = eventFlowRegistries;
	}

	@Override
	public Mono<CreateServiceInstanceResponse> createServiceInstance(CreateServiceInstanceRequest request) {
		return flows.getCreateInstanceRegistry().getInitializationFlows(request)
				.then(service.createServiceInstance(request))
				.onErrorResume(e -> flows.getCreateInstanceRegistry().getErrorFlows(request, e)
						.then(Mono.error(e)))
				.flatMap(response -> flows.getCreateInstanceRegistry().getCompletionFlows(request, response)
						.then(Mono.just(response)));
	}

	@Override
	public Mono<GetServiceInstanceResponse> getServiceInstance(GetServiceInstanceRequest request) {
		return service.getServiceInstance(request);
	}

	@Override
	public Mono<GetLastServiceOperationResponse> getLastOperation(GetLastServiceOperationRequest request) {
		return flows.getAsyncOperationRegistry().getInitializationFlows(request)
				.then(service.getLastOperation(request))
				.onErrorResume(e -> flows.getAsyncOperationRegistry().getErrorFlows(request, e)
						.then(Mono.error(e)))
				.flatMap(response -> flows.getAsyncOperationRegistry().getCompletionFlows(request, response)
						.then(Mono.just(response)));
	}

	@Override
	public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(DeleteServiceInstanceRequest request) {
		return flows.getDeleteInstanceRegistry().getInitializationFlows(request)
				.then(service.deleteServiceInstance(request))
				.onErrorResume(e -> flows.getDeleteInstanceRegistry().getErrorFlows(request, e)
						.then(Mono.error(e)))
				.flatMap(response -> flows.getDeleteInstanceRegistry().getCompletionFlows(request, response)
						.then(Mono.just(response)));
	}

	@Override
	public Mono<UpdateServiceInstanceResponse> updateServiceInstance(UpdateServiceInstanceRequest request) {
		return flows.getUpdateInstanceRegistry().getInitializationFlows(request)
				.then(service.updateServiceInstance(request))
				.onErrorResume(e -> flows.getUpdateInstanceRegistry().getErrorFlows(request, e)
						.then(Mono.error(e)))
				.flatMap(response -> flows.getUpdateInstanceRegistry().getCompletionFlows(request, response)
						.then(Mono.just(response)));
	}
}
