/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

class CreateServiceInstanceRouteBindingResponseTest {

	@SuppressWarnings("deprecation")
	@Test
	void responseWithDefaultsIsBuilt() {
		CreateServiceInstanceRouteBindingResponse response = CreateServiceInstanceRouteBindingResponse.builder()
				.build();

		assertThat(response.getBindingStatus()).isNull();
		assertThat(response.isBindingExisted()).isEqualTo(false);
		assertThat(response.getMetadata()).isNull();
		assertThat(response.getRouteServiceUrl()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasNoPath("$.metadata");
		assertThat(json).hasNoPath("$.route_service_url");
	}

	@SuppressWarnings("deprecation")
	@Test
	void responseWithValuesIsBuilt() {
		CreateServiceInstanceRouteBindingResponse response = CreateServiceInstanceRouteBindingResponse.builder()
				.metadata(BindingMetadata.builder()
						.expiresAt("2019-12-31T23:59:59.0Z")
						.build())
				.bindingStatus(BindingStatus.EXISTS_WITH_IDENTICAL_PARAMETERS)
				.routeServiceUrl("https://routes.app.local")
				.build();

		assertThat(response.getBindingStatus()).isEqualTo(BindingStatus.EXISTS_WITH_IDENTICAL_PARAMETERS);
		assertThat(response.isBindingExisted()).isEqualTo(true);
		assertThat(response.getRouteServiceUrl()).isEqualTo("https://routes.app.local");

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasPath("$.metadata.expires_at").isEqualTo("2019-12-31T23:59:59.0Z");
		assertThat(json).hasPath("$.route_service_url").isEqualTo("https://routes.app.local");
	}

	@Test
	void responseWithValuesIsDeserialized() {
		CreateServiceInstanceRouteBindingResponse response = JsonUtils.readTestDataFile(
				"createRouteBindingResponse.json",
				CreateServiceInstanceRouteBindingResponse.class);

		assertThat(response.getMetadata().getExpiresAt()).isEqualTo("2019-12-31T23:59:59.0Z");
		assertThat(response.getRouteServiceUrl()).isEqualTo("https://route.local");
	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier
				.forClass(CreateServiceInstanceRouteBindingResponse.class)
				.withRedefinedSuperclass()
				.verify();
	}

	@Test
	void withinOperationCharacterLimit() {
		CreateServiceInstanceRouteBindingResponse.builder()
				.operation(RandomString.make(9_999))
				.build();
	}

	@Test
	void exceedsOperationCharacterLimit() {
		assertThrows(IllegalArgumentException.class, () ->
				CreateServiceInstanceRouteBindingResponse.builder()
						.operation(RandomString.make(10_001))
						.build());
	}

	@Test
	void exactlyOperationCharacterLimit() {
		CreateServiceInstanceRouteBindingResponse.builder()
				.operation(RandomString.make(10_000))
				.build();
	}

	@SuppressWarnings("deprecation")
	@Test
	void responseWithBindingExistedFalseIsBuilt() {
		CreateServiceInstanceAppBindingResponse response = CreateServiceInstanceAppBindingResponse.builder()
				.bindingExisted(false)
				.build();

		assertThat(response.getBindingStatus()).isEqualTo(BindingStatus.NEW);
		assertThat(response.isBindingExisted()).isEqualTo(false);
	}

	@SuppressWarnings("deprecation")
	@Test
	void responseWithBindingExistingTrueIsBuilt() {
		CreateServiceInstanceRouteBindingResponse response = CreateServiceInstanceRouteBindingResponse.builder()
				.bindingExisted(true)
				.build();

		assertThat(response.getBindingStatus()).isEqualTo(BindingStatus.EXISTS_WITH_IDENTICAL_PARAMETERS);
		assertThat(response.isBindingExisted()).isEqualTo(true);
	}

	@SuppressWarnings("deprecation")
	@Test
	void responseWithNewBindingIsBuilt() {
		CreateServiceInstanceRouteBindingResponse response = CreateServiceInstanceRouteBindingResponse.builder()
				.bindingStatus(BindingStatus.NEW)
				.build();

		assertThat(response.getBindingStatus()).isEqualTo(BindingStatus.NEW);
		assertThat(response.isBindingExisted()).isEqualTo(false);
	}

	@SuppressWarnings("deprecation")
	@Test
	void responseWithExistingBindingWithIdenticalParametersIsBuilt() {
		CreateServiceInstanceRouteBindingResponse response = CreateServiceInstanceRouteBindingResponse.builder()
				.bindingStatus(BindingStatus.EXISTS_WITH_IDENTICAL_PARAMETERS)
				.build();

		assertThat(response.getBindingStatus()).isEqualTo(BindingStatus.EXISTS_WITH_IDENTICAL_PARAMETERS);
		assertThat(response.isBindingExisted()).isEqualTo(true);
	}

	@SuppressWarnings("deprecation")
	@Test
	void responseWithExistingBindingWithDifferentParametersIsBuilt() {
		CreateServiceInstanceRouteBindingResponse response = CreateServiceInstanceRouteBindingResponse.builder()
				.bindingStatus(BindingStatus.EXISTS_WITH_DIFFERENT_PARAMETERS)
				.build();

		assertThat(response.getBindingStatus()).isEqualTo(BindingStatus.EXISTS_WITH_DIFFERENT_PARAMETERS);
		assertThat(response.isBindingExisted()).isEqualTo(true);
	}

}
