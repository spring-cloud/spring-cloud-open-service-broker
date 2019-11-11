package com.example.servicebroker;

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationRequest;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationResponse;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.AsyncOperationServiceInstanceBindingInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.CreateServiceInstanceBindingInitializationFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingCompletionFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingErrorFlow;
import org.springframework.cloud.servicebroker.service.events.flows.DeleteServiceInstanceBindingInitializationFlow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExampleServiceBindingEventFlowsConfiguration2 {

	//
	// Create Service Instance Binding flows
	//

	@Bean
	public CreateServiceInstanceBindingInitializationFlow createServiceInstanceBindingInitializationFlow() {
		return new CreateServiceInstanceBindingInitializationFlow() {
			@Override
			public Mono<Void> initialize(CreateServiceInstanceBindingRequest request) {
				//
				// do something before the service instance binding completes
				//
				return Mono.empty();
			}
		};
	}

	@Bean
	public CreateServiceInstanceBindingCompletionFlow createServiceInstanceBindingCompletionFlow() {
		return new CreateServiceInstanceBindingCompletionFlow() {
			@Override
			public Mono<Void> complete(CreateServiceInstanceBindingRequest request,
					CreateServiceInstanceBindingResponse response) {
				//
				// do something after the service instance binding completes
				//
				return Mono.empty();
			}
		};
	}

	@Bean
	public CreateServiceInstanceBindingErrorFlow createServiceInstanceBindingErrorFlow() {
		return new CreateServiceInstanceBindingErrorFlow() {
			@Override
			public Mono<Void> error(CreateServiceInstanceBindingRequest request, Throwable t) {
				//
				// do something if an error occurs while creating a service instance binding
				//
				return Mono.empty();
			}
		};
	}

	//
	// Delete Service Instance Binding flows
	//

	@Bean
	public DeleteServiceInstanceBindingInitializationFlow deleteServiceInstanceBindingInitializationFlow() {
		return new DeleteServiceInstanceBindingInitializationFlow() {
			@Override
			public Mono<Void> initialize(DeleteServiceInstanceBindingRequest request) {
				//
				// do something before the service instance binding is deleted
				//
				return Mono.empty();
			}
		};
	}

	@Bean
	public DeleteServiceInstanceBindingCompletionFlow deleteServiceInstanceBindingCompletionFlow() {
		return new DeleteServiceInstanceBindingCompletionFlow() {
			@Override
			public Mono<Void> complete(DeleteServiceInstanceBindingRequest request,
					DeleteServiceInstanceBindingResponse response) {
				//
				// do something after the service instance binding is deleted
				//
				return Mono.empty();
			}
		};
	}

	@Bean
	public DeleteServiceInstanceBindingErrorFlow deleteServiceInstanceBindingErrorFlow() {
		return new DeleteServiceInstanceBindingErrorFlow() {
			@Override
			public Mono<Void> error(DeleteServiceInstanceBindingRequest request, Throwable t) {
				//
				// do something if an error occurs while deleting a service instance binding
				//
				return Mono.empty();
			}
		};
	}

	//
	// Get Last Service Instance Binding Operation flows
	//

	@Bean
	public AsyncOperationServiceInstanceBindingInitializationFlow getLastOperationServiceInstanceBindingInitializationFlow() {
		return new AsyncOperationServiceInstanceBindingInitializationFlow() {
			@Override
			public Mono<Void> initialize(GetLastServiceBindingOperationRequest request) {
				//
				// do something before getting the last operation
				//
				return Mono.empty();
			}
		};
	}

	@Bean
	public AsyncOperationServiceInstanceBindingCompletionFlow getLastOperationServiceInstanceBindingCompletionFlow() {
		return new AsyncOperationServiceInstanceBindingCompletionFlow() {
			@Override
			public Mono<Void> complete(GetLastServiceBindingOperationRequest request,
					GetLastServiceBindingOperationResponse response) {
				//
				// do something after getting the last operation
				//
				return Mono.empty();
			}
		};
	}

	@Bean
	public AsyncOperationServiceInstanceBindingErrorFlow getLastOperationServiceInstanceBindingErrorFlow() {
		return new AsyncOperationServiceInstanceBindingErrorFlow() {
			@Override
			public Mono<Void> error(GetLastServiceBindingOperationRequest request, Throwable t) {
				//
				// do something if an error occurs while getting the last operation
				//
				return Mono.empty();
			}
		};
	}
}
