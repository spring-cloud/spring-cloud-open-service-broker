/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.binding;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class CreateServiceInstanceRouteBindingResponseTest {

	@Test
	public void responseWithDefaultsIsBuilt() {
		CreateServiceInstanceRouteBindingResponse response = CreateServiceInstanceRouteBindingResponse.builder()
				.build();

		assertThat(response.isBindingExisted(), equalTo(false));
		assertThat(response.getRouteServiceUrl(), nullValue());
	}

	@Test
	public void responseWithValuesIsBuilt() {
		CreateServiceInstanceRouteBindingResponse response = CreateServiceInstanceRouteBindingResponse.builder()
				.bindingExisted(true)
				.routeServiceUrl("https://routes.example.com")
				.build();

		assertThat(response.isBindingExisted(), equalTo(true));
		assertThat(response.getRouteServiceUrl(), equalTo("https://routes.example.com"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void responseIsSerializedToJson() {
		CreateServiceInstanceRouteBindingResponse response = CreateServiceInstanceRouteBindingResponse.builder()
				.routeServiceUrl("https://routes.example.com")
				.build();

		String json = DataFixture.toJson(response);

		assertThat(json, isJson(allOf(
				withJsonPath("$.route_service_url", equalTo("https://routes.example.com"))
		)));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(CreateServiceInstanceRouteBindingResponse.class)
				.withRedefinedSuperclass()
				.verify();
	}
}