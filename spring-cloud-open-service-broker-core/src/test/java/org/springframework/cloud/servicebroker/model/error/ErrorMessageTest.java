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

package org.springframework.cloud.servicebroker.model.error;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.springframework.cloud.servicebroker.JsonUtils;

import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

public class ErrorMessageTest {
	@Test
	public void errorWithCodeAndDescriptionIsBuilt() {
		ErrorMessage errorMessage = new ErrorMessage("ErrorCode", "error description");

		DocumentContext json = JsonUtils.toJsonPath(errorMessage);

		assertThat(json).hasPath("$.error").isEqualTo("ErrorCode");
		assertThat(json).hasPath("$.description").isEqualTo("error description");
	}

	@Test
	public void errorWithDescriptionIsBuilt() {
		ErrorMessage errorMessage = new ErrorMessage("error description");

		DocumentContext json = JsonUtils.toJsonPath(errorMessage);

		assertThat(json).hasNoPath("$.error");
		assertThat(json).hasPath("$.description").isEqualTo("error description");
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(ErrorMessage.class)
				.verify();
	}
}