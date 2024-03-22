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

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.cloud.servicebroker.autoconfigure.web.ServiceInstanceBindingIntegrationTests;
import org.springframework.cloud.servicebroker.controller.ServiceBrokerWebMvcExceptionHandler;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceBindingController;
import org.springframework.cloud.servicebroker.service.NonBindableServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NonBindableServiceInstanceBindingControllerIntegrationTests extends ServiceInstanceBindingIntegrationTests {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		ServiceInstanceBindingService serviceInstanceBindingService = new NonBindableServiceInstanceBindingService();
		ServiceInstanceBindingController controller = new ServiceInstanceBindingController(catalogService,
				serviceInstanceBindingService);

		this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
			.setControllerAdvice(ServiceBrokerWebMvcExceptionHandler.class)
			.setMessageConverters(new MappingJackson2HttpMessageConverter())
			.build();
	}

	@Test
	void createBindingToAppFails() throws Exception {
		setupCatalogService();

		MvcResult mvcResult = this.mockMvc
			.perform(put(buildCreateUrl()).content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(request().asyncStarted())
			.andReturn();

		this.mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isInternalServerError());
	}

	@Test
	void deleteBindingFails() throws Exception {
		this.mockMvc.perform(delete(buildDeleteUrl()))
			.andExpect(request().asyncNotStarted())
			.andExpect(status().isInternalServerError())
			.andReturn();
	}

}
