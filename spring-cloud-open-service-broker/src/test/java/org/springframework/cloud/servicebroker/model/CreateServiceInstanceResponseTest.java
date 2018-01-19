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

public class CreateServiceInstanceResponseTest {

	@Test
	public void responseWithDefaultsIsBuilt() {
		CreateServiceInstanceResponse response = CreateServiceInstanceResponse.builder()
				.build();

		assertThat(response.isAsync(), equalTo(false));
		assertThat(response.getOperation(), nullValue());
		assertThat(response.isInstanceExisted(), equalTo(false));
		assertThat(response.getDashboardUrl(), nullValue());
	}

	@Test
	public void responseWithAllValuesIsBuilt() {
		CreateServiceInstanceResponse response = CreateServiceInstanceResponse.builder()
				.async(true)
				.operation("in progress")
				.instanceExisted(true)
				.dashboardUrl("http://dashboard.example.com")
				.build();

		assertThat(response.isAsync(), equalTo(true));
		assertThat(response.getOperation(), equalTo("in progress"));
		assertThat(response.isInstanceExisted(), equalTo(true));
		assertThat(response.getDashboardUrl(), equalTo("http://dashboard.example.com"));
	}

	@Test
	public void responseIsSerializedToJson() throws Exception {
		CreateServiceInstanceResponse response = CreateServiceInstanceResponse.builder()
				.operation("in progress")
				.dashboardUrl("http://dashboard.example.com")
				.build();

		String json = DataFixture.toJson(response);

		assertThat(json, isJson(allOf(
				withJsonPath("$.operation", equalTo("in progress")),
				withJsonPath("$.dashboard_url", equalTo("http://dashboard.example.com"))
		)));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(CreateServiceInstanceResponse.class)
				.withRedefinedSuperclass()
				.verify();
	}
}