/*
 * Copyright 2002-2024 the original author or authors.
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

package org.springframework.cloud.servicebroker.exception;

import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.model.error.ErrorMessage;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceBrokerInvalidParametersExceptionTests {

	@Test
	void createServiceBrokerInvalidParametersExceptionWithNullValues() {
		ServiceBrokerException ex = new ServiceBrokerInvalidParametersException(null, null, null, null, null);
		ErrorMessage message = ex.getErrorMessage();

		assertThat(message.getError()).isNull();
		assertThat(message.getMessage()).isEqualTo("Service broker parameters are invalid: null");
		assertThat(message.isInstanceUsable()).isNull();
		assertThat(message.isUpdateRepeatable()).isNull();
		assertThat(ex.getCause()).isNull();
	}

	@Test
	void createServiceBrokerInvalidParametersExceptionWithAllValues() {
		Throwable throwable = new RuntimeException("can't run");
		ServiceBrokerException ex = new ServiceBrokerInvalidParametersException("helloError", "hello", true, false,
				throwable);
		ErrorMessage message = ex.getErrorMessage();

		assertThat(message.getError()).isEqualTo("helloError");
		assertThat(message.getMessage()).isEqualTo("Service broker parameters are invalid: hello");
		assertThat(message.isInstanceUsable()).isTrue();
		assertThat(message.isUpdateRepeatable()).isFalse();
		assertThat(ex.getCause()).isExactlyInstanceOf(RuntimeException.class);
		assertThat(ex.getCause().getMessage()).isEqualTo("can't run");
	}

}
