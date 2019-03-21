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

package org.springframework.cloud.servicebroker.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.instance.AsyncParameterizedServiceInstanceRequest;

public class AsyncServiceBrokerRequestTest {
	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(AsyncServiceBrokerRequest.class)
				.withRedefinedSuperclass()
				.withRedefinedSubclass(AsyncParameterizedServiceInstanceRequest.class)
				.withRedefinedSubclass(CreateServiceInstanceBindingRequest.class)
				.withRedefinedSubclass(DeleteServiceInstanceBindingRequest.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.suppress(Warning.TRANSIENT_FIELDS)
				.verify();
	}
}