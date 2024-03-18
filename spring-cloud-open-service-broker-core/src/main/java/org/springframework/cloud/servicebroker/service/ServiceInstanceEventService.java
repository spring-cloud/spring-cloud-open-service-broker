/*
 * Copyright 2002-2023 the original author or authors.
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

	/**
	 * Constructs a new {@link ServiceInstanceEventService}.
	 * @param serviceInstanceService the service instance service
	 * @param eventFlowRegistries the event flow registries
	 */
	public ServiceInstanceEventService(ServiceInstanceService serviceInstanceService,
			EventFlowRegistries eventFlowRegistries) {
		this.service = serviceInstanceService;
		this.flows = eventFlowRegistries;
	}

	@Override
	public Mono<CreateServiceInstanceResponse> createServiceInstance(CreateServiceInstanceRequest request) {
		return this.flows.getCreateInstanceRegistry()
			.getInitializationFlows(request)
			.then(this.service.createServiceInstance(request))
			.onErrorResume((e) -> this.flows.getCreateInstanceRegistry().getErrorFlows(request, e).then(Mono.error(e)))
			.flatMap((response) -> this.flows.getCreateInstanceRegistry()
				.getCompletionFlows(request, response)
				.then(Mono.just(response)));
	}

	@Override
	public Mono<GetServiceInstanceResponse> getServiceInstance(GetServiceInstanceRequest request) {
		return this.service.getServiceInstance(request);
	}

	@Override
	public Mono<GetLastServiceOperationResponse> getLastOperation(GetLastServiceOperationRequest request) {
		return this.flows.getAsyncOperationRegistry()
			.getInitializationFlows(request)
			.then(this.service.getLastOperation(request))
			.onErrorResume((e) -> this.flows.getAsyncOperationRegistry().getErrorFlows(request, e).then(Mono.error(e)))
			.flatMap((response) -> this.flows.getAsyncOperationRegistry()
				.getCompletionFlows(request, response)
				.then(Mono.just(response)));
	}

	@Override
	public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(DeleteServiceInstanceRequest request) {
		return this.flows.getDeleteInstanceRegistry()
			.getInitializationFlows(request)
			.then(this.service.deleteServiceInstance(request))
			.onErrorResume((e) -> this.flows.getDeleteInstanceRegistry().getErrorFlows(request, e).then(Mono.error(e)))
			.flatMap((response) -> this.flows.getDeleteInstanceRegistry()
				.getCompletionFlows(request, response)
				.then(Mono.just(response)));
	}

	@Override
	public Mono<UpdateServiceInstanceResponse> updateServiceInstance(UpdateServiceInstanceRequest request) {
		return this.flows.getUpdateInstanceRegistry()
			.getInitializationFlows(request)
			.then(this.service.updateServiceInstance(request))
			.onErrorResume((e) -> this.flows.getUpdateInstanceRegistry().getErrorFlows(request, e).then(Mono.error(e)))
			.flatMap((response) -> this.flows.getUpdateInstanceRegistry()
				.getCompletionFlows(request, response)
				.then(Mono.just(response)));
	}

}
