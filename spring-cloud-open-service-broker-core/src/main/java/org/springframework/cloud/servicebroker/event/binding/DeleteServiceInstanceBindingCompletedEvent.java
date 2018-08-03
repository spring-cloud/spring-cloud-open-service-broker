/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.event.binding;

import org.springframework.cloud.servicebroker.event.ServiceInstanceCompletedEvent;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;

public class DeleteServiceInstanceBindingCompletedEvent
		extends ServiceInstanceCompletedEvent<DeleteServiceInstanceBindingRequest, Void> {
	private static final long serialVersionUID = 3137815087238016680L;

	public DeleteServiceInstanceBindingCompletedEvent(DeleteServiceInstanceBindingRequest request) {
		super(request);
	}

	public DeleteServiceInstanceBindingCompletedEvent(DeleteServiceInstanceBindingRequest request,
													  Throwable exception) {
		super(request, exception);
	}
}
