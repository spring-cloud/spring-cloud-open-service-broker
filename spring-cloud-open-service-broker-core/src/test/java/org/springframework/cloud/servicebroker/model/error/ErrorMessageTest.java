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

package org.springframework.cloud.servicebroker.model.error;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

class ErrorMessageTest {

	@Test
	void errorWithCodeAndDescriptionIsBuilt() {
		ErrorMessage errorMessage = new ErrorMessage("ErrorCode", "error description");

		assertThat(errorMessage.getError()).isEqualTo("ErrorCode");
		assertThat(errorMessage.getMessage()).isEqualTo("error description");
		assertThat(errorMessage.isInstanceUsable()).isNull();
		assertThat(errorMessage.isInstanceUsable()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(errorMessage);

		assertThat(json).hasPath("$.error").isEqualTo("ErrorCode");
		assertThat(json).hasPath("$.description").isEqualTo("error description");
		assertThat(json).hasNoPath("$.instance_usable");
		assertThat(json).hasNoPath("$.update_repeatable");
	}

	@Test
	void errorWithAllValuesIsBuilt() {
		ErrorMessage errorMessage = new ErrorMessage("ErrorCode", "error description", false, false);

		assertThat(errorMessage.getError()).isEqualTo("ErrorCode");
		assertThat(errorMessage.getMessage()).isEqualTo("error description");
		assertThat(errorMessage.isInstanceUsable()).isFalse();
		assertThat(errorMessage.isUpdateRepeatable()).isFalse();

		DocumentContext json = JsonUtils.toJsonPath(errorMessage);

		assertThat(json).hasPath("$.error").isEqualTo("ErrorCode");
		assertThat(json).hasPath("$.description").isEqualTo("error description");
		assertThat(json).hasPath("$.instance_usable").isEqualTo(false);
		assertThat(json).hasPath("$.update_repeatable").isEqualTo(false);
	}

	@Test
	void errorWithCodeAndDescriptionIsBuiltWithBuilder() {
		ErrorMessage errorMessage = ErrorMessage.builder()
				.error("ErrorCode")
				.message("error description")
				.build();

		DocumentContext json = JsonUtils.toJsonPath(errorMessage);

		assertThat(json).hasPath("$.error").isEqualTo("ErrorCode");
		assertThat(json).hasPath("$.description").isEqualTo("error description");
		assertThat(json).hasNoPath("$.instance_usable");
		assertThat(json).hasNoPath("$.update_repeatable");
	}

	@Test
	void errorWithAllValuesIsBuiltWithBuilder() {
		ErrorMessage errorMessage = ErrorMessage.builder()
				.error("ErrorCode")
				.message("error description")
				.instanceUsable(false)
				.updateRepeatable(true)
				.build();

		assertThat(errorMessage.getError()).isEqualTo("ErrorCode");
		assertThat(errorMessage.getMessage()).isEqualTo("error description");
		assertThat(errorMessage.isInstanceUsable()).isFalse();
		assertThat(errorMessage.isUpdateRepeatable()).isTrue();

		DocumentContext json = JsonUtils.toJsonPath(errorMessage);

		assertThat(json).hasPath("$.error").isEqualTo("ErrorCode");
		assertThat(json).hasPath("$.description").isEqualTo("error description");
		assertThat(json).hasPath("$.instance_usable").isEqualTo(false);
		assertThat(json).hasPath("$.update_repeatable").isEqualTo(true);
	}

	@Test
	void errorWithDescriptionIsBuilt() {
		ErrorMessage errorMessage = new ErrorMessage("error description");

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).isEqualTo("error description");
		assertThat(errorMessage.isInstanceUsable()).isNull();
		assertThat(errorMessage.isInstanceUsable()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(errorMessage);

		assertThat(json).hasNoPath("$.error");
		assertThat(json).hasPath("$.description").isEqualTo("error description");
		assertThat(json).hasNoPath("$.instance_usable");
		assertThat(json).hasNoPath("$.update_repeatable");
	}

	@Test
	void errorWithDescriptionIsBuiltWithBuilder() {
		ErrorMessage errorMessage = ErrorMessage.builder()
				.message("error description")
				.build();

		assertThat(errorMessage.getError()).isNull();
		assertThat(errorMessage.getMessage()).isEqualTo("error description");
		assertThat(errorMessage.isInstanceUsable()).isNull();
		assertThat(errorMessage.isInstanceUsable()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(errorMessage);

		assertThat(json).hasNoPath("$.error");
		assertThat(json).hasPath("$.description").isEqualTo("error description");
		assertThat(json).hasNoPath("$.instance_usable");
		assertThat(json).hasNoPath("$.update_repeatable");
	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier
				.forClass(ErrorMessage.class)
				.verify();
	}

}
