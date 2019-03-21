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

package org.springframework.cloud.servicebroker.service;

import org.junit.Before;
import org.junit.Test;

import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;

public class NonBindableServiceInstanceBindingServiceTest {

	private NonBindableServiceInstanceBindingService service;

	@Before
	public void setUp() {
		service = new NonBindableServiceInstanceBindingService();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void createServiceInstanceBinding() {
		service.createServiceInstanceBinding(CreateServiceInstanceBindingRequest.builder().build())
				.block();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void deleteServiceInstanceBinding() {
		service.deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest.builder().build())
				.block();
	}

}