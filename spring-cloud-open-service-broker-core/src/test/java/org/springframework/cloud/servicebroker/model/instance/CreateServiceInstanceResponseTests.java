/*
 * Copyright 2002-2023 the original author or authors.
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

package org.springframework.cloud.servicebroker.model.instance;

import java.util.HashMap;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import net.bytebuddy.utility.RandomString;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

class CreateServiceInstanceResponseTests {

	@Test
	void responseWithDefaultsIsBuilt() {
		CreateServiceInstanceResponse response = CreateServiceInstanceResponse.builder().build();

		assertThat(response.isAsync()).isEqualTo(false);
		assertThat(response.getOperation()).isNull();
		assertThat(response.isInstanceExisted()).isEqualTo(false);
		assertThat(response.getDashboardUrl()).isNull();
		assertThat(response.getMetadata()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasNoPath("$.operation");
		assertThat(json).hasNoPath("$.dashboard_url");
		assertThat(json).hasNoPath("$.metadata");
	}

	@Test
	void responseWithAllValuesIsBuilt() {
		CreateServiceInstanceResponse response = CreateServiceInstanceResponse.builder()
			.async(true)
			.operation("in progress")
			.instanceExisted(true)
			.dashboardUrl("https://dashboard.app.local")
			.metadata(ServiceInstanceMetadata.builder().label("key", "value").build())
			.build();

		assertThat(response.isAsync()).isEqualTo(true);
		assertThat(response.getOperation()).isEqualTo("in progress");
		assertThat(response.isInstanceExisted()).isEqualTo(true);
		assertThat(response.getDashboardUrl()).isEqualTo("https://dashboard.app.local");
		assertThat(response.getMetadata()).isEqualTo(ServiceInstanceMetadata.builder().label("key", "value").build());

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasPath("$.operation").isEqualTo("in progress");
		assertThat(json).hasPath("$.dashboard_url").isEqualTo("https://dashboard.app.local");
		assertThat(json).hasPath("$.metadata.labels").isEqualTo(Maps.newHashMap("key", "value"));
	}

	@Test
	void responseWithAllValuesIsDeserialized() {
		CreateServiceInstanceResponse response = JsonUtils.readTestDataFile("createResponse.json",
				CreateServiceInstanceResponse.class);

		assertThat(response.getOperation()).isEqualTo("in progress");
		assertThat(response.getDashboardUrl()).isEqualTo("https://dashboard.local");

		Map<String, Object> labels = new HashMap<>();
		labels.put("key1", "value1");
		labels.put("key2", "value2");

		assertThat(response.getMetadata()).isEqualTo(ServiceInstanceMetadata.builder().labels(labels).build());

	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier.forClass(CreateServiceInstanceResponse.class).withRedefinedSuperclass().verify();
	}

	@Test
	void withinOperationCharacterLimit() {
		CreateServiceInstanceResponse.builder().operation(RandomString.make(9_999)).build();
	}

	@Test
	void exceedsOperationCharacterLimit() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> CreateServiceInstanceResponse.builder().operation(RandomString.make(10_001)).build());
	}

	@Test
	void exactlyOperationCharacterLimit() {
		CreateServiceInstanceResponse.builder().operation(RandomString.make(10_000)).build();
	}

}
