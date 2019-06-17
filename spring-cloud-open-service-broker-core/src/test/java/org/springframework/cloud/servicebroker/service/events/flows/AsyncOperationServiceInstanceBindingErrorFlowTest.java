package org.springframework.cloud.servicebroker.service.events.flows;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationRequest;

import static org.mockito.Mockito.mock;

class AsyncOperationServiceInstanceBindingErrorFlowTest {

	@Test
	void error() {
		AsyncOperationServiceInstanceBindingErrorFlow flow =
				new AsyncOperationServiceInstanceBindingErrorFlow() {};
		StepVerifier.create(
				flow.error(mock(GetLastServiceBindingOperationRequest.class), mock(Exception.class)))
				.verifyComplete();
	}
}