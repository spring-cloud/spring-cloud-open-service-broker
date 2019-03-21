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

package org.springframework.cloud.servicebroker.service.events;

/**
 * A collection of registries for various event flows
 *
 * @author Roy Clarkson
 */
public class EventFlowRegistries {

	private CreateServiceInstanceEventFlowRegistry createInstanceRegistry;

	private UpdateServiceInstanceEventFlowRegistry updateInstanceRegistry;

	private DeleteServiceInstanceEventFlowRegistry deleteInstanceRegistry;

	private AsyncOperationServiceInstanceEventFlowRegistry asyncOperationRegistry;

	private CreateServiceInstanceBindingEventFlowRegistry createInstanceBindingRegistry;

	private DeleteServiceInstanceBindingEventFlowRegistry deleteInstanceBindingRegistry;

	public EventFlowRegistries() {
		this.createInstanceRegistry = new CreateServiceInstanceEventFlowRegistry();
		this.updateInstanceRegistry = new UpdateServiceInstanceEventFlowRegistry();
		this.deleteInstanceRegistry = new DeleteServiceInstanceEventFlowRegistry();
		this.asyncOperationRegistry = new AsyncOperationServiceInstanceEventFlowRegistry();
		this.createInstanceBindingRegistry = new CreateServiceInstanceBindingEventFlowRegistry();
		this.deleteInstanceBindingRegistry = new DeleteServiceInstanceBindingEventFlowRegistry();
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

}
