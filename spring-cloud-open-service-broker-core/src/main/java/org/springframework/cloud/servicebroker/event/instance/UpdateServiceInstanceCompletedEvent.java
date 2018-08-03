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

package org.springframework.cloud.servicebroker.event.instance;

import org.springframework.cloud.servicebroker.event.ServiceInstanceCompletedEvent;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;

public class UpdateServiceInstanceCompletedEvent
		extends ServiceInstanceCompletedEvent<UpdateServiceInstanceRequest, UpdateServiceInstanceResponse> {
	private static final long serialVersionUID = -8560692608402027960L;

	public UpdateServiceInstanceCompletedEvent(UpdateServiceInstanceRequest request,
											   UpdateServiceInstanceResponse response) {
		super(request, response);
	}

	public UpdateServiceInstanceCompletedEvent(UpdateServiceInstanceRequest request, Throwable exception) {
		super(request, exception);
	}
}
