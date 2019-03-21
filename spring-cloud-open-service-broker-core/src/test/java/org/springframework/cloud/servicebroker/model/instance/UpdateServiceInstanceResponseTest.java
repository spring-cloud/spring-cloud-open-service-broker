/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.cloud.servicebroker.model.instance;

import com.jayway.jsonpath.DocumentContext;
import org.junit.Test;
import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

public class UpdateServiceInstanceResponseTest {
	@Test
	public void responseWithDefaultsIsBuilt() {
		UpdateServiceInstanceResponse response = UpdateServiceInstanceResponse.builder()
				.build();

		assertThat(response.isAsync()).isEqualTo(false);
		assertThat(response.getOperation()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasNoPath("$.operation");
	}

	@Test
	public void responseWithAllValuesIsBuilt() {
		UpdateServiceInstanceResponse response = UpdateServiceInstanceResponse.builder()
				.async(true)
				.operation("in progress")
				.build();

		assertThat(response.isAsync()).isEqualTo(true);
		assertThat(response.getOperation()).isEqualTo("in progress");

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasPath("$.operation").isEqualTo("in progress");
	}
}
