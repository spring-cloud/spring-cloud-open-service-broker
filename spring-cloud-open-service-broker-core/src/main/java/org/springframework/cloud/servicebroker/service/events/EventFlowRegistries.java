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

/**
 * A collection of registries for various event flows
 *
 * @author Roy Clarkson
 */
public class EventFlowRegistries {

	private final CreateServiceInstanceEventFlowRegistry createInstanceRegistry;

	private final UpdateServiceInstanceEventFlowRegistry updateInstanceRegistry;

	private final DeleteServiceInstanceEventFlowRegistry deleteInstanceRegistry;

	private final AsyncOperationServiceInstanceEventFlowRegistry asyncOperationRegistry;

	private final CreateServiceInstanceBindingEventFlowRegistry createInstanceBindingRegistry;

	private final DeleteServiceInstanceBindingEventFlowRegistry deleteInstanceBindingRegistry;

    private final AsyncOperationServiceInstanceBindingEventFlowRegistry asyncOperationBindingRegistry;

	@Deprecated
	public EventFlowRegistries() {
		this.createInstanceRegistry = new CreateServiceInstanceEventFlowRegistry();
		this.updateInstanceRegistry = new UpdateServiceInstanceEventFlowRegistry();
		this.deleteInstanceRegistry = new DeleteServiceInstanceEventFlowRegistry();
		this.asyncOperationRegistry = new AsyncOperationServiceInstanceEventFlowRegistry();
		this.createInstanceBindingRegistry = new CreateServiceInstanceBindingEventFlowRegistry();
		this.deleteInstanceBindingRegistry = new DeleteServiceInstanceBindingEventFlowRegistry();
		this.asyncOperationBindingRegistry = new AsyncOperationServiceInstanceBindingEventFlowRegistry();
	}

	public EventFlowRegistries(CreateServiceInstanceEventFlowRegistry createInstanceRegistry,
							   UpdateServiceInstanceEventFlowRegistry updateInstanceRegistry,
							   DeleteServiceInstanceEventFlowRegistry deleteInstanceRegistry,
							   AsyncOperationServiceInstanceEventFlowRegistry asyncOperationRegistry,
							   CreateServiceInstanceBindingEventFlowRegistry createInstanceBindingRegistry,
							   DeleteServiceInstanceBindingEventFlowRegistry deleteInstanceBindingRegistry,
							   AsyncOperationServiceInstanceBindingEventFlowRegistry asyncOperationBindingRegistry) {
		this.createInstanceRegistry = createInstanceRegistry;
		this.updateInstanceRegistry = updateInstanceRegistry;
		this.deleteInstanceRegistry = deleteInstanceRegistry;
		this.asyncOperationRegistry = asyncOperationRegistry;
		this.createInstanceBindingRegistry = createInstanceBindingRegistry;
		this.deleteInstanceBindingRegistry = deleteInstanceBindingRegistry;
		this.asyncOperationBindingRegistry = asyncOperationBindingRegistry;
	}

	public CreateServiceInstanceEventFlowRegistry getCreateInstanceRegistry() {
		return this.createInstanceRegistry;
	}

	public UpdateServiceInstanceEventFlowRegistry getUpdateInstanceRegistry() {
		return this.updateInstanceRegistry;
	}

	public DeleteServiceInstanceEventFlowRegistry getDeleteInstanceRegistry() {
		return this.deleteInstanceRegistry;
	}

	public AsyncOperationServiceInstanceEventFlowRegistry getAsyncOperationRegistry() {
		return this.asyncOperationRegistry;
	}

	public CreateServiceInstanceBindingEventFlowRegistry getCreateInstanceBindingRegistry() {
		return createInstanceBindingRegistry;
	}

	public DeleteServiceInstanceBindingEventFlowRegistry getDeleteInstanceBindingRegistry() {
		return deleteInstanceBindingRegistry;
	}

	public AsyncOperationServiceInstanceBindingEventFlowRegistry getAsyncOperationBindingRegistry() {
		return asyncOperationBindingRegistry;
	}

}
