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

package org.springframework.cloud.servicebroker.exception;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceBrokerApiVersionErrorMessageTest {

	@Test
	public void nulls() {
		ServiceBrokerApiVersionErrorMessage message = ServiceBrokerApiVersionErrorMessage.from(null, null);
		assertThat(message.toString()).isEqualTo("The provided service broker API version is not supported: expected version=null, provided version=null");
	}

	@Test
	public void message() {
		ServiceBrokerApiVersionErrorMessage message = ServiceBrokerApiVersionErrorMessage.from("expected-version", "actual-version");
		assertThat(message.toString()).isEqualTo("The provided service broker API version is not supported: expected version=expected-version, provided version=actual-version");
	}

	@Test
	public void from() {
		ServiceBrokerApiVersionErrorMessage message = ServiceBrokerApiVersionErrorMessage.from("expected-version2", "actual-version2");
		assertThat(message.toString()).isEqualTo("The provided service broker API version is not supported: expected version=expected-version2, provided version=actual-version2");
	}
}