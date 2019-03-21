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

package org.springframework.cloud.servicebroker.model.catalog;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

public class DashboardClientTest {
	@Test
	public void dashboardClientIsBuiltWithDefaults() {
		DashboardClient client = DashboardClient.builder()
				.build();

		assertThat(client.getId()).isNull();
		assertThat(client.getSecret()).isNull();
		assertThat(client.getRedirectUri()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(client);

		assertThat(json).hasNoPath("$.id");
		assertThat(json).hasNoPath("$.secret");
		assertThat(json).hasNoPath("$.redirect_uri");
	}

	@Test
	public void dashboardClientIsBuiltWithAllValues() {
		DashboardClient client = DashboardClient.builder()
				.id("client-id")
				.secret("client-secret")
				.redirectUri("https://token.example.com")
				.build();

		assertThat(client.getId()).isEqualTo("client-id");
		assertThat(client.getSecret()).isEqualTo("client-secret");
		assertThat(client.getRedirectUri()).isEqualTo("https://token.example.com");

		DocumentContext json = JsonUtils.toJsonPath(client);

		assertThat(json).hasPath("$.id").isEqualTo("client-id");
		assertThat(json).hasPath("$.secret").isEqualTo("client-secret");
		assertThat(json).hasPath("$.redirect_uri").isEqualTo("https://token.example.com");
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(DashboardClient.class)
				.verify();
	}
}
