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

import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceInitializationFlow;

/**
 * Event flow registry for update service instance requests
 *
 * @author Roy Clarkson
 */
public class UpdateServiceInstanceEventFlowRegistry extends EventFlowRegistry<UpdateServiceInstanceInitializationFlow,
		UpdateServiceInstanceCompletionFlow, UpdateServiceInstanceErrorFlow, UpdateServiceInstanceRequest,
		UpdateServiceInstanceResponse> {

	/**
	 * Construct a new {@link UpdateServiceInstanceEventFlowRegistry}
	 */
	@Deprecated
	public UpdateServiceInstanceEventFlowRegistry() {
		super();
	}

	/**
	 * Construct a new {@link UpdateServiceInstanceEventFlowRegistry}
	 *
	 * @param initializationFlows the initialization flows
	 * @param completionFlows the completion flows
	 * @param errorFlows the error flows
	 */
	public UpdateServiceInstanceEventFlowRegistry(
			final List<UpdateServiceInstanceInitializationFlow> initializationFlows,
			final List<UpdateServiceInstanceCompletionFlow> completionFlows,
			final List<UpdateServiceInstanceErrorFlow> errorFlows) {
		super(initializationFlows, completionFlows, errorFlows);
	}

	@Override
	public Flux<Void> getInitializationFlows(UpdateServiceInstanceRequest request) {
		return getInitializationFlowsInternal()
				.flatMap(flow -> flow.initialize(request));
	}

	@Override
	public Flux<Void> getCompletionFlows(UpdateServiceInstanceRequest request, UpdateServiceInstanceResponse response) {
		return getCompletionFlowsInternal()
				.flatMap(flow -> flow.complete(request, response));
	}

	@Override
	public Flux<Void> getErrorFlows(UpdateServiceInstanceRequest request, Throwable t) {
		return getErrorFlowsInternal()
				.flatMap(flow -> flow.error(request, t));
	}

}
