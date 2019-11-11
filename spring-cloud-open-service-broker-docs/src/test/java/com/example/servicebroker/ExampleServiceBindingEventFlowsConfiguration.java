package com.example.servicebroker;

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationRequest;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationResponse;
import org.springframework.cloud.servicebroker.service.events.AsyncOperationServiceInstanceBindingEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.CreateServiceInstanceBindingEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.DeleteServiceInstanceBindingEventFlowRegistry;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceBindingInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingInitializationFlow;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExampleServiceBindingEventFlowsConfiguration {

	private final CreateServiceInstanceBindingEventFlowRegistry createRegistry;

	private final DeleteServiceInstanceBindingEventFlowRegistry deleteRegistry;

	private final AsyncOperationServiceInstanceBindingEventFlowRegistry asyncRegistry;

	public ExampleServiceBindingEventFlowsConfiguration(
			CreateServiceInstanceBindingEventFlowRegistry createRegistry,
			DeleteServiceInstanceBindingEventFlowRegistry deleteRegistry,
			AsyncOperationServiceInstanceBindingEventFlowRegistry asyncRegistry) {
		this.createRegistry = createRegistry;
		this.deleteRegistry = deleteRegistry;
		this.asyncRegistry = asyncRegistry;

		prepareCreateEventFlows()
				.then(prepareDeleteEventFlows())
				.then(prepareLastOperationEventFlows())
				.subscribe();
	}

	private Mono<Void> prepareCreateEventFlows() {
		return Mono.just(createRegistry)
				.map(registry -> registry.addInitializationFlow(new CreateServiceInstanceBindingInitializationFlow() {
					@Override
					public Mono<Void> initialize(CreateServiceInstanceBindingRequest request) {
						//
						// do something before the instance is created
						//
						return Mono.empty();
					}
				})
						.then(registry.addCompletionFlow(new CreateServiceInstanceBindingCompletionFlow() {
							@Override
							public Mono<Void> complete(CreateServiceInstanceBindingRequest request,
													   CreateServiceInstanceBindingResponse response) {
								//
								// do something after the instance is created
								//
								return Mono.empty();
							}
						}))
						.then(registry.addErrorFlow(new CreateServiceInstanceBindingErrorFlow() {
							@Override
							public Mono<Void> error(CreateServiceInstanceBindingRequest request, Throwable t) {
								//
								// do something if an error occurs while creating an instance
								//
								return Mono.empty();
							}
						})))
				.then();
	}

	private Mono<Void> prepareDeleteEventFlows() {
		return Mono.just(deleteRegistry)
				.map(registry -> registry.addInitializationFlow(new DeleteServiceInstanceBindingInitializationFlow() {
					@Override
					public Mono<Void> initialize(DeleteServiceInstanceBindingRequest request) {
						//
						// do something before the instance is deleted
						//
						return Mono.empty();
					}
				})
						.then(registry.addCompletionFlow(new DeleteServiceInstanceBindingCompletionFlow() {
							@Override
							public Mono<Void> complete(DeleteServiceInstanceBindingRequest request,
													   DeleteServiceInstanceBindingResponse response) {
								//
								// do something after the instance is deleted
								//
								return Mono.empty();
							}
						}))
						.then(registry.addErrorFlow(new DeleteServiceInstanceBindingErrorFlow() {
							@Override
							public Mono<Void> error(DeleteServiceInstanceBindingRequest request, Throwable t) {
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
				.map(registry -> registry.addInitializationFlow(new AsyncOperationServiceInstanceBindingInitializationFlow() {
					@Override
					public Mono<Void> initialize(GetLastServiceBindingOperationRequest request) {
						//
						// do something before the instance is deleted
						//
						return Mono.empty();
					}
				})
						.then(registry.addCompletionFlow(new AsyncOperationServiceInstanceBindingCompletionFlow() {
							@Override
							public Mono<Void> complete(GetLastServiceBindingOperationRequest request,
									GetLastServiceBindingOperationResponse response) {
								//
								// do something after the instance is deleted
								//
								return Mono.empty();
							}
						}))
						.then(registry.addErrorFlow(new AsyncOperationServiceInstanceBindingErrorFlow() {
							public Mono<Void> error(GetLastServiceBindingOperationRequest request, Throwable t) {
								//
								// do something if an error occurs while deleting an instance
								//
								return Mono.empty();
							}
						})))
				.then();
	}

}
