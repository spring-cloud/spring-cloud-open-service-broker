package com.example.servicebroker;

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.events.AsyncOperationServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.CreateServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.DeleteServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.UpdateServiceInstanceEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.UpdateServiceInstanceInitializationFlow;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExampleServiceInstanceEventFlowsConfiguration {

	private final CreateServiceInstanceEventFlowRegistry createRegistry;

	private final UpdateServiceInstanceEventFlowRegistry updateRegistry;

	private final DeleteServiceInstanceEventFlowRegistry deleteRegistry;

	private final AsyncOperationServiceInstanceEventFlowRegistry asyncRegistry;

	public ExampleServiceInstanceEventFlowsConfiguration(CreateServiceInstanceEventFlowRegistry createRegistry,
			UpdateServiceInstanceEventFlowRegistry updateRegistry,
			DeleteServiceInstanceEventFlowRegistry deleteRegistry,
			AsyncOperationServiceInstanceEventFlowRegistry asyncRegistry) {
		this.createRegistry = createRegistry;
		this.updateRegistry = updateRegistry;
		this.deleteRegistry = deleteRegistry;
		this.asyncRegistry = asyncRegistry;

		prepareCreateEventFlows()
				.then(prepareUpdateEventFlows())
				.then(prepareDeleteEventFlows())
				.then(prepareLastOperationEventFlows())
				.subscribe();
	}

	private Mono<Void> prepareCreateEventFlows() {
		return Mono.just(createRegistry)
				.map(registry -> registry.addInitializationFlow(new CreateServiceInstanceInitializationFlow() {
					@Override
					public Mono<Void> initialize(CreateServiceInstanceRequest request) {
						//
						// do something before the instance is created
						//
						return Mono.empty();
					}
				})
						.then(registry.addCompletionFlow(new CreateServiceInstanceCompletionFlow() {
							@Override
							public Mono<Void> complete(CreateServiceInstanceRequest request,
									CreateServiceInstanceResponse response) {
								//
								// do something after the instance is created
								//
								return Mono.empty();
							}
						}))
						.then(registry.addErrorFlow(new CreateServiceInstanceErrorFlow() {
							@Override
							public Mono<Void> error(CreateServiceInstanceRequest request, Throwable t) {
								//
								// do something if an error occurs while creating an instance
								//
								return Mono.empty();
							}
						})))
				.then();
	}

	private Mono<Void> prepareUpdateEventFlows() {
		return Mono.just(updateRegistry)
				.map(registry -> registry.addInitializationFlow(new UpdateServiceInstanceInitializationFlow() {
					@Override
					public Mono<Void> initialize(UpdateServiceInstanceRequest request) {
						//
						// do something before the instance is updated
						//
						return Mono.empty();
					}
				})
						.then(registry.addCompletionFlow(new UpdateServiceInstanceCompletionFlow() {
							@Override
							public Mono<Void> complete(UpdateServiceInstanceRequest request,
									UpdateServiceInstanceResponse response) {
								//
								// do something after the instance is updated
								//
								return Mono.empty();
							}
						}))
						.then(registry.addErrorFlow(new UpdateServiceInstanceErrorFlow() {
							@Override
							public Mono<Void> error(UpdateServiceInstanceRequest request, Throwable t) {
								//
								// do something if an error occurs while updating an instance
								//
								return Mono.empty();
							}
						})))
				.then();
	}

	private Mono<Void> prepareDeleteEventFlows() {
		return Mono.just(deleteRegistry)
				.map(registry -> registry.addInitializationFlow(new DeleteServiceInstanceInitializationFlow() {
					@Override
					public Mono<Void> initialize(DeleteServiceInstanceRequest request) {
						//
						// do something before the instance is deleted
						//
						return Mono.empty();
					}
				})
						.then(registry.addCompletionFlow(new DeleteServiceInstanceCompletionFlow() {
							@Override
							public Mono<Void> complete(DeleteServiceInstanceRequest request,
									DeleteServiceInstanceResponse response) {
								//
								// do something after the instance is deleted
								//
								return Mono.empty();
							}
						}))
						.then(registry.addErrorFlow(new DeleteServiceInstanceErrorFlow() {
							@Override
							public Mono<Void> error(DeleteServiceInstanceRequest request, Throwable t) {
								//
								// do something if an error occurs while deleting an instance
								//
								return Mono.empty();
							}
						})))
				.then();
	}

	private Mono<Void> prepareLastOperationEventFlows() {
		return Mono.just(asyncRegistry)
				.map(registry -> registry.addInitializationFlow(new AsyncOperationServiceInstanceInitializationFlow() {
					@Override
					public Mono<Void> initialize(GetLastServiceOperationRequest request) {
						//
						// do something before returning the last operation
						//
						return Mono.empty();
					}
				})
						.then(registry.addCompletionFlow(new AsyncOperationServiceInstanceCompletionFlow() {
							@Override
							public Mono<Void> complete(GetLastServiceOperationRequest request,
									GetLastServiceOperationResponse response) {
								//
								// do something after returning the last operation
								//
								return Mono.empty();
							}
						}))
						.then(registry.addErrorFlow(new AsyncOperationServiceInstanceErrorFlow() {
							@Override
							public Mono<Void> error(GetLastServiceOperationRequest request, Throwable t) {
								//
								// do something if an error occurs while processing the last operation response
								//
								return Mono.empty();
							}
						})))
				.then();
	}

}
