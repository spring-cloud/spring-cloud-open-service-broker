/*
 * Copyright 2002-2021 the original author or authors.
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
import net.bytebuddy.utility.RandomString;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

class OperationInProgressMessageTest {

	@Test
	void messageIsEmptyWithNoOperation() {
		OperationInProgressMessage message = new OperationInProgressMessage();

		DocumentContext json = JsonUtils.toJsonPath(message);

		assertThat(json).hasNoPath("$.operation");
	}

	@Test
	void messageHasOperation() {
		OperationInProgressMessage message = new OperationInProgressMessage("task_10");

		DocumentContext json = JsonUtils.toJsonPath(message);

		assertThat(json).hasPath("$.operation").isEqualTo("task_10");
	}

	@Test
	void operationExceedsMaxAllowedLength() {
		assertThrows(IllegalArgumentException.class, () ->
				new OperationInProgressMessage(RandomString.make(10_001)));
	}

	@Test
	void operationMaxLength() {
		new OperationInProgressMessage(RandomString.make(10_000));
	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier
				.forClass(OperationInProgressMessage.class)
				.verify();
	}

}
