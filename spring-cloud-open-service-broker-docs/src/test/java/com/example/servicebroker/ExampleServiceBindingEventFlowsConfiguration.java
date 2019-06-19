package com.example.servicebroker;

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.service.events.EventFlowRegistries;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingInitializationFlow;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExampleServiceBindingEventFlowsConfiguration {

	// Autowire the EventFlowRegistries bean
	private final EventFlowRegistries eventFlowRegistries;

	public ExampleServiceBindingEventFlowsConfiguration(EventFlowRegistries eventFlowRegistries) {
		this.eventFlowRegistries = eventFlowRegistries;

		prepareCreateEventFlows()
				.then(prepareDeleteEventFlows())
				.subscribe();
	}

	private Mono<Void> prepareCreateEventFlows() {
		return Mono.just(eventFlowRegistries.getCreateInstanceBindingRegistry())
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
		return Mono.just(eventFlowRegistries.getDeleteInstanceBindingRegistry())
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

}
