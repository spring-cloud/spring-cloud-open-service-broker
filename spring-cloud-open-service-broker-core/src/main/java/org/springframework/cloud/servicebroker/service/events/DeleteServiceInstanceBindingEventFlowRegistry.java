/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.service.events;

import java.util.List;

import reactor.core.publisher.Flux;

import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingInitializationFlow;

/**
 * Event flow registry for delete service instance binding requests
 *
 * @author Roy Clarkson
 */
public class DeleteServiceInstanceBindingEventFlowRegistry
		extends EventFlowRegistry<DeleteServiceInstanceBindingInitializationFlow,
		DeleteServiceInstanceBindingCompletionFlow, DeleteServiceInstanceBindingErrorFlow, DeleteServiceInstanceBindingRequest,
		DeleteServiceInstanceBindingResponse> {

	/**
	 * Construct a new {@link DeleteServiceInstanceBindingEventFlowRegistry}
	 *
	 * @param initializationFlows the initialization flows
	 * @param completionFlows the completion flows
	 * @param errorFlows the error flows
	 */
	public DeleteServiceInstanceBindingEventFlowRegistry(
			final List<DeleteServiceInstanceBindingInitializationFlow> initializationFlows,
			final List<DeleteServiceInstanceBindingCompletionFlow> completionFlows,
			final List<DeleteServiceInstanceBindingErrorFlow> errorFlows) {
		super(initializationFlows, completionFlows, errorFlows);
	}

	@Override
	public Flux<Void> getInitializationFlows(DeleteServiceInstanceBindingRequest request) {
		return getInitializationFlowsInternal()
				.flatMap(flow -> flow.initialize(request));
	}

	@Override
	public Flux<Void> getCompletionFlows(DeleteServiceInstanceBindingRequest request,
			DeleteServiceInstanceBindingResponse response) {
		return getCompletionFlowsInternal()
				.flatMap(flow -> flow.complete(request, response));
	}

	@Override
	public Flux<Void> getErrorFlows(DeleteServiceInstanceBindingRequest request, Throwable t) {
		return getErrorFlowsInternal()
				.flatMap(flow -> flow.error(request, t));
	}

}
