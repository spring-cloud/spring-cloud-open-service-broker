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
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonUtils;
import org.springframework.cloud.servicebroker.model.instance.OperationState;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

class GetLastServiceBindingOperationResponseTest {

	@Test
	void responseWithDefaultsIsBuilt() {
		GetLastServiceBindingOperationResponse response = GetLastServiceBindingOperationResponse.builder()
				.build();

		assertThat(response.getState()).isNull();
		assertThat(response.getDescription()).isNull();
		assertThat(response.isDeleteOperation()).isEqualTo(false);

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasNoPath("$.state");
		assertThat(json).hasNoPath("$.description");
	}

	@Test
	void responseWithEmptyDescriptionIsBuilt() {
		GetLastServiceBindingOperationResponse response = GetLastServiceBindingOperationResponse.builder()
				.description("")
				.build();

		assertThat(response.getState()).isNull();
		assertThat(response.getDescription()).isNotNull();
		assertThat(response.isDeleteOperation()).isEqualTo(false);

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasNoPath("$.state");
		assertThat(json).hasPath("$.description").isEqualTo("");
	}

	@Test
	void responseWithAllValuesIsBuilt() {
		GetLastServiceBindingOperationResponse response = GetLastServiceBindingOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.description("description")
				.deleteOperation(true)
				.build();

		assertThat(response.getState()).isEqualTo(OperationState.SUCCEEDED);
		assertThat(response.getDescription()).isEqualTo("description");
		assertThat(response.isDeleteOperation()).isEqualTo(true);

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasPath("$.state").isEqualTo(OperationState.SUCCEEDED.toString());
		assertThat(json).hasPath("$.description").isEqualTo("description");
	}

	@Test
	void responseWithSuccessIsSerializedToJson() {
		responseWithStateIsSerializedToJson(OperationState.SUCCEEDED, "succeeded");
	}

	@Test
	void responseWithFailureIsSerializedToJson() {
		responseWithStateIsSerializedToJson(OperationState.FAILED, "failed");
	}

	@Test
	void responseWithInProgressIsSerializedToJson() {
		responseWithStateIsSerializedToJson(OperationState.IN_PROGRESS, "in progress");
	}

	@Test
	void responseWithAllValuesIsDeserialized() {
		GetLastServiceBindingOperationResponse response = JsonUtils.readTestDataFile(
				"getLastBindingOperationResponse.json", GetLastServiceBindingOperationResponse.class);

		assertThat(response.getState()).isEqualTo(OperationState.SUCCEEDED);
		assertThat(response.getDescription()).isEqualTo("description");
	}

	private void responseWithStateIsSerializedToJson(OperationState stateValue, String stateString) {
		GetLastServiceBindingOperationResponse response = GetLastServiceBindingOperationResponse.builder()
				.operationState(stateValue)
				.description("description")
				.build();

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasPath("$.state").isEqualTo(stateString);
		assertThat(json).hasPath("$.description").isEqualTo("description");
	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier
				.forClass(GetLastServiceBindingOperationResponse.class)
				.suppress(Warning.STRICT_INHERITANCE)
				.verify();
	}

}
