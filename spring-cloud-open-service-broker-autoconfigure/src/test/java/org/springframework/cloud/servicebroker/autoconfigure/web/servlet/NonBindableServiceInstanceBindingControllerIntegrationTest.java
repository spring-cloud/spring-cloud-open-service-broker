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

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.cloud.servicebroker.autoconfigure.web.ServiceInstanceBindingIntegrationTest;
import org.springframework.cloud.servicebroker.controller.ServiceBrokerExceptionHandler;
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

@RunWith(MockitoJUnitRunner.class)
public class NonBindableServiceInstanceBindingControllerIntegrationTest extends ServiceInstanceBindingIntegrationTest {

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		ServiceInstanceBindingService serviceInstanceBindingService = new NonBindableServiceInstanceBindingService();
		ServiceInstanceBindingController controller =
				new ServiceInstanceBindingController(catalogService, serviceInstanceBindingService);

		this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setControllerAdvice(ServiceBrokerExceptionHandler.class)
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}

	@Test
	public void createBindingToAppFails() throws Exception {
		setupCatalogService();

		MvcResult mvcResult = mockMvc.perform(put(buildCreateUrl())
				.content(createRequestBody)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void deleteBindingFails() throws Exception {
		mockMvc.perform(delete(buildDeleteUrl()))
				.andExpect(request().asyncNotStarted())
				.andExpect(status().isInternalServerError())
				.andReturn();
	}

}
