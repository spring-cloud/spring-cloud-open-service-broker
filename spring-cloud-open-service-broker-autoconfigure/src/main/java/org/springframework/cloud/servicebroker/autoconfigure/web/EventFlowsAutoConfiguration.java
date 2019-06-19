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

package org.springframework.cloud.servicebroker.autoconfigure.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.servicebroker.service.events.AsyncOperationServiceInstanceBindingEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.AsyncOperationServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.CreateServiceInstanceBindingEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.CreateServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.DeleteServiceInstanceBindingEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.DeleteServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.EventFlowRegistries;
import org.springframework.cloud.servicebroker.service.events.UpdateServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceBindingInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceInitializationFlow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for the event flow implementation beans.
 *
 * @author Roy Clarkson
 */
@Configuration
public class EventFlowsAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(CreateServiceInstanceEventFlowRegistry.class)
	public CreateServiceInstanceEventFlowRegistry createInstanceRegistry(
			@Autowired(required = false) List<CreateServiceInstanceInitializationFlow> initializationFlows,
			@Autowired(required = false) List<CreateServiceInstanceCompletionFlow> completionFlows,
			@Autowired(required = false) List<CreateServiceInstanceErrorFlow> errorFlows) {
		return new CreateServiceInstanceEventFlowRegistry(initializationFlows, completionFlows, errorFlows);
	}

	@Bean
	@ConditionalOnMissingBean(DeleteServiceInstanceEventFlowRegistry.class)
	public DeleteServiceInstanceEventFlowRegistry deleteInstanceRegistry(
			@Autowired(required = false) List<DeleteServiceInstanceInitializationFlow> initializationFlows,
			@Autowired(required = false) List<DeleteServiceInstanceCompletionFlow> completionFlows,
			@Autowired(required = false) List<DeleteServiceInstanceErrorFlow> errorFlows) {
		return new DeleteServiceInstanceEventFlowRegistry(initializationFlows, completionFlows, errorFlows);
	}

	@Bean
	@ConditionalOnMissingBean(UpdateServiceInstanceEventFlowRegistry.class)
	public UpdateServiceInstanceEventFlowRegistry updateInstanceRegistry(
			@Autowired(required = false) List<UpdateServiceInstanceInitializationFlow> initializationFlows,
			@Autowired(required = false) List<UpdateServiceInstanceCompletionFlow> completionFlows,
			@Autowired(required = false) List<UpdateServiceInstanceErrorFlow> errorFlows) {
		return new UpdateServiceInstanceEventFlowRegistry(initializationFlows, completionFlows, errorFlows);
	}

	@Bean
	@ConditionalOnMissingBean(AsyncOperationServiceInstanceEventFlowRegistry.class)
	public AsyncOperationServiceInstanceEventFlowRegistry asyncOperationRegistry(
			@Autowired(required = false) List<AsyncOperationServiceInstanceInitializationFlow> initializationFlows,
			@Autowired(required = false) List<AsyncOperationServiceInstanceCompletionFlow> completionFlows,
			@Autowired(required = false) List<AsyncOperationServiceInstanceErrorFlow> errorFlows) {
		return new AsyncOperationServiceInstanceEventFlowRegistry(initializationFlows, completionFlows, errorFlows);
	}

	@Bean
	@ConditionalOnMissingBean(CreateServiceInstanceBindingEventFlowRegistry.class)
	public CreateServiceInstanceBindingEventFlowRegistry createInstanceBindingRegistry(
			@Autowired(required = false) List<CreateServiceInstanceBindingInitializationFlow> initializationFlows,
			@Autowired(required = false) List<CreateServiceInstanceBindingCompletionFlow> completionFlows,
			@Autowired(required = false) List<CreateServiceInstanceBindingErrorFlow> errorFlows) {
		return new CreateServiceInstanceBindingEventFlowRegistry(initializationFlows, completionFlows, errorFlows);
	}

	@Bean
	@ConditionalOnMissingBean(DeleteServiceInstanceBindingEventFlowRegistry.class)
	public DeleteServiceInstanceBindingEventFlowRegistry deleteInstanceBindingRegistry(
			@Autowired(required = false) List<DeleteServiceInstanceBindingInitializationFlow> initializationFlows,
			@Autowired(required = false) List<DeleteServiceInstanceBindingCompletionFlow> completionFlows,
			@Autowired(required = false) List<DeleteServiceInstanceBindingErrorFlow> errorFlows) {
		return new DeleteServiceInstanceBindingEventFlowRegistry(initializationFlows, completionFlows, errorFlows);
	}

	@Bean
	@ConditionalOnMissingBean(AsyncOperationServiceInstanceBindingEventFlowRegistry.class)
	public AsyncOperationServiceInstanceBindingEventFlowRegistry asyncOperationBindingRegistry(
			@Autowired(required = false) List<AsyncOperationServiceInstanceBindingInitializationFlow> initializationFlows,
			@Autowired(required = false) List<AsyncOperationServiceInstanceBindingCompletionFlow> completionFlows,
			@Autowired(required = false) List<AsyncOperationServiceInstanceBindingErrorFlow> errorFlows) {
		return new AsyncOperationServiceInstanceBindingEventFlowRegistry(initializationFlows, completionFlows, errorFlows);
	}

	@Bean
	@ConditionalOnMissingBean(EventFlowRegistries.class)
	public EventFlowRegistries eventFlowRegistries(
			CreateServiceInstanceEventFlowRegistry createInstanceRegistry,
			UpdateServiceInstanceEventFlowRegistry updateInstanceRegistry,
			DeleteServiceInstanceEventFlowRegistry deleteInstanceRegistry,
			AsyncOperationServiceInstanceEventFlowRegistry asyncOperationRegistry,
			CreateServiceInstanceBindingEventFlowRegistry createInstanceBindingRegistry,
			DeleteServiceInstanceBindingEventFlowRegistry deleteInstanceBindingRegistry,
			AsyncOperationServiceInstanceBindingEventFlowRegistry asyncOperationBindingRegistry) {
		return new EventFlowRegistries(createInstanceRegistry, updateInstanceRegistry, deleteInstanceRegistry,
				asyncOperationRegistry, createInstanceBindingRegistry,
				deleteInstanceBindingRegistry, asyncOperationBindingRegistry);
	}

}
