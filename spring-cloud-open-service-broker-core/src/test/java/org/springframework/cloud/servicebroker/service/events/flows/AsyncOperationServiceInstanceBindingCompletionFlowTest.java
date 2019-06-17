package org.springframework.cloud.servicebroker.service.events.flows;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationRequest;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationResponse;

import static org.mockito.Mockito.mock;

class AsyncOperationServiceInstanceBindingCompletionFlowTest {

	@Test
	void complete() {
		AsyncOperationServiceInstanceBindingCompletionFlow flow =
				new AsyncOperationServiceInstanceBindingCompletionFlow() {};
		StepVerifier.create(
				flow.complete(
						mock(GetLastServiceBindingOperationRequest.class),
						mock(GetLastServiceBindingOperationResponse.class)))
				.verifyComplete();
	}
}