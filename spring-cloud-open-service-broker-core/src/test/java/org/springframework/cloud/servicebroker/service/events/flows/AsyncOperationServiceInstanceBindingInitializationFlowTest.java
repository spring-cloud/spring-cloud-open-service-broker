package org.springframework.cloud.servicebroker.service.events.flows;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationRequest;

import static org.mockito.Mockito.mock;

class AsyncOperationServiceInstanceBindingInitializationFlowTest {

	@Test
	void initialize() {
		AsyncOperationServiceInstanceBindingInitializationFlow flow =
				new AsyncOperationServiceInstanceBindingInitializationFlow() {};
		StepVerifier
				.create(flow.initialize(mock(GetLastServiceBindingOperationRequest.class)))
				.verifyComplete();
	}
}