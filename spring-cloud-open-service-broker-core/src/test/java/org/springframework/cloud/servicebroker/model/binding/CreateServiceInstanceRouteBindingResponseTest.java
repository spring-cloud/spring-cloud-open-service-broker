/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.binding;

import com.jayway.jsonpath.DocumentContext;
import net.bytebuddy.utility.RandomString;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

public class CreateServiceInstanceRouteBindingResponseTest {

	@Test
	public void responseWithDefaultsIsBuilt() {
		CreateServiceInstanceRouteBindingResponse response = CreateServiceInstanceRouteBindingResponse.builder()
				.build();

		assertThat(response.isBindingExisted()).isEqualTo(false);
		assertThat(response.getRouteServiceUrl()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasNoPath("$.route_service_url");
	}

	@Test
	public void responseWithValuesIsBuilt() {
		CreateServiceInstanceRouteBindingResponse response = CreateServiceInstanceRouteBindingResponse.builder()
				.bindingExisted(true)
				.routeServiceUrl("https://routes.example.com")
				.build();

		assertThat(response.isBindingExisted()).isEqualTo(true);
		assertThat(response.getRouteServiceUrl()).isEqualTo("https://routes.example.com");

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasPath("$.route_service_url").isEqualTo("https://routes.example.com");
	}

	@Test
	public void responseWithValuesIsDeserialized() {
		CreateServiceInstanceRouteBindingResponse response = JsonUtils.readTestDataFile(
				"createRouteBindingResponse.json",
				CreateServiceInstanceRouteBindingResponse.class);

		assertThat(response.getRouteServiceUrl()).isEqualTo("https://route.local");
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(CreateServiceInstanceRouteBindingResponse.class)
				.withRedefinedSuperclass()
				.verify();
	}

	@Test
	public void withinOperationCharacterLimit() throws Exception {
		CreateServiceInstanceRouteBindingResponse.builder()
				.operation(RandomString.make(9_999))
				.build();
	}

	@Test
	public void exceedsOperationCharacterLimit() throws Exception {
		assertThrows(IllegalArgumentException.class, () ->
				CreateServiceInstanceRouteBindingResponse.builder()
						.operation(RandomString.make(10_001))
						.build());
	}

	@Test
	public void exactlyOperationCharacterLimit() throws Exception {
		CreateServiceInstanceRouteBindingResponse.builder()
				.operation(RandomString.make(10_000))
				.build();
	}

}
