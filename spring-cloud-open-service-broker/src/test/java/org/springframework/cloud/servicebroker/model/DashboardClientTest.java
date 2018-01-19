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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class DashboardClientTest {
	@Test
	public void dashboardClientIsBuiltWithDefaults() {
		DashboardClient client = DashboardClient.builder()
				.build();

		assertThat(client.getId(), nullValue());
		assertThat(client.getSecret(), nullValue());
		assertThat(client.getRedirectUri(), nullValue());
	}

	@Test
	public void dashboardClientIsBuiltWithAllValues() {
		DashboardClient client = DashboardClient.builder()
				.id("client-id")
				.secret("client-secret")
				.redirectUri("https://token.example.com")
				.build();

		assertThat(client.getId(), equalTo("client-id"));
		assertThat(client.getSecret(), equalTo("client-secret"));
		assertThat(client.getRedirectUri(), equalTo("https://token.example.com"));
	}

	@Test
	public void dashboardClientIsSerializedToJson() throws Exception {
		DashboardClient client = DashboardClient.builder()
				.id("client-id")
				.secret("client-secret")
				.redirectUri("https://token.example.com")
				.build();

		String json = DataFixture.toJson(client);

		assertThat(json, isJson(allOf(
				withJsonPath("$.id", equalTo("client-id")),
				withJsonPath("$.secret", equalTo("client-secret")),
				withJsonPath("$.redirect_uri", equalTo("https://token.example.com"))
		)));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(DashboardClient.class)
				.verify();
	}
}