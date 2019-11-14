/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.cloud.servicebroker.service.events;

import java.util.List;

import reactor.core.publisher.Flux;

import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceInitializationFlow;

/**
 * Event flow registry for asynchronous get last operation requests
 *
 * @author Roy Clarkson
 */
public class AsyncOperationServiceInstanceEventFlowRegistry
		extends EventFlowRegistry<AsyncOperationServiceInstanceInitializationFlow,
		AsyncOperationServiceInstanceCompletionFlow, AsyncOperationServiceInstanceErrorFlow, GetLastServiceOperationRequest,
		GetLastServiceOperationResponse> {

	/**
	 * Construct a new {@link AsyncOperationServiceInstanceEventFlowRegistry}
	 */
	@Deprecated
	public AsyncOperationServiceInstanceEventFlowRegistry() {
		super();
	}

	/**
	 * Construct a new {@link AsyncOperationServiceInstanceEventFlowRegistry}
	 *
	 * @param initializationFlows the initialization flows
	 * @param completionFlows the completion flows
	 * @param errorFlows the error flows
	 */
	public AsyncOperationServiceInstanceEventFlowRegistry(
			final List<AsyncOperationServiceInstanceInitializationFlow> initializationFlows,
			final List<AsyncOperationServiceInstanceCompletionFlow> completionFlows,
			final List<AsyncOperationServiceInstanceErrorFlow> errorFlows) {
		super(initializationFlows, completionFlows, errorFlows);
	}

	@Override
	public Flux<Void> getInitializationFlows(GetLastServiceOperationRequest request) {
		return getInitializationFlowsInternal()
				.flatMap(flow -> flow.initialize(request));
	}

	@Override
	public Flux<Void> getCompletionFlows(GetLastServiceOperationRequest request,
			GetLastServiceOperationResponse response) {
		return getCompletionFlowsInternal()
				.flatMap(flow -> flow.complete(request, response));
	}

	@Override
	public Flux<Void> getErrorFlows(GetLastServiceOperationRequest request, Throwable t) {
		return getErrorFlowsInternal()
				.flatMap(flow -> flow.error(request, t));
	}

}
