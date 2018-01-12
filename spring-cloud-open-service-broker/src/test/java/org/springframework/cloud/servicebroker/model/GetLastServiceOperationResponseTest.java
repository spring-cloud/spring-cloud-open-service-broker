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

package org.springframework.cloud.servicebroker.model;

import org.junit.Test;

import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

public class GetLastServiceOperationResponseTest {
	@Test
	public void responseWithDefaultsIsBuilt() {
		GetLastServiceOperationResponse response = GetLastServiceOperationResponse.builder()
				.build();

		assertThat(response.getState(), nullValue());
		assertThat(response.getDescription(), nullValue());
		assertThat(response.isDeleteOperation(), equalTo(false));
	}

	@Test
	public void responseWithAllValuesIsBuilt() {
		GetLastServiceOperationResponse response = GetLastServiceOperationResponse.builder()
				.operationState(OperationState.SUCCEEDED)
				.description("description")
				.deleteOperation(true)
				.build();

		assertThat(response.getState(), equalTo(OperationState.SUCCEEDED));
		assertThat(response.getDescription(), equalTo("description"));
		assertThat(response.isDeleteOperation(), equalTo(true));
	}

	@Test
	public void responseWithSuccessIsSerializedToJson() throws Exception {
		responseWithStateIsSerializedToJson(OperationState.SUCCEEDED, "succeeded");
	}

	@Test
	public void responseWithFailureIsSerializedToJson() throws Exception {
		responseWithStateIsSerializedToJson(OperationState.FAILED, "failed");
	}

	@Test
	public void responseWithInProgressIsSerializedToJson() throws Exception {
		responseWithStateIsSerializedToJson(OperationState.IN_PROGRESS, "in progress");
	}

	private void responseWithStateIsSerializedToJson(OperationState stateValue, String stateString) throws Exception {
		GetLastServiceOperationResponse response = GetLastServiceOperationResponse.builder()
				.operationState(stateValue)
				.description("description")
				.build();

		String json = DataFixture.toJson(response);

		assertThat(json, isJson(allOf(
				withJsonPath("$.state", equalTo(stateString)),
				withJsonPath("$.description", equalTo("description"))
		)));
	}
}