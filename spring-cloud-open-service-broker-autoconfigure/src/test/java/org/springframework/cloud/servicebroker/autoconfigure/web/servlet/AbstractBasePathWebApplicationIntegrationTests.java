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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cloud.servicebroker.autoconfigure.web.AbstractBasePathIntegrationTests;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "spring.main.web-application-type=servlet")
@AutoConfigureMockMvc
public abstract class AbstractBasePathWebApplicationIntegrationTests extends AbstractBasePathIntegrationTests {

	@Autowired
	private MockMvc mvc;

	@Override
	protected void assertFound(String baseUri, String expectedPlatform) throws Exception {
		makeRequest(baseUri, true).andExpect(status().isCreated())
			.andExpect(jsonPath("operation").value(format("platform: %s", expectedPlatform)));
	}

	@Override
	protected void assertNotFound(String baseUri) throws Exception {
		makeRequest(baseUri, false).andExpect(status().isNotFound());
	}

	private ResultActions makeRequest(String baseUri, boolean async) throws Exception {
		ResultActions actions = this.mvc
			.perform(put(format("%s/v2/service_instances/default-service", baseUri)).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(serviceCreationPayload()));

		if (!async) {
			return actions;
		}

		return this.mvc.perform(asyncDispatch(actions.andExpect(request().asyncStarted()).andReturn()));
	}

}
