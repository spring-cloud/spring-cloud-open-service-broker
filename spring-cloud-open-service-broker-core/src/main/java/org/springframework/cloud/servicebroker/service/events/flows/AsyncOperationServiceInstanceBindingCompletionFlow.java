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

package org.springframework.cloud.servicebroker.service.events.flows;

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationRequest;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationResponse;

/**
 * Completion flow for service binding asynchronous get last operation request.
 *
 * @author Ilya Vy
 */
public interface AsyncOperationServiceInstanceBindingCompletionFlow {

	/**
	 * Performs the operation on the completion flow.
	 * @param request the service broker request
	 * @param response the service broker response
	 * @return an empty Mono
	 */
	default Mono<Void> complete(GetLastServiceBindingOperationRequest request,
			GetLastServiceBindingOperationResponse response) {
		return Mono.empty();
	}

}
