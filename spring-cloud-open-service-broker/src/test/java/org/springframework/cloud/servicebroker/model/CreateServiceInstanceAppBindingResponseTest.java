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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class CreateServiceInstanceAppBindingResponseTest {
	@Test
	public void responseWithDefaultsIsBuilt() {
		CreateServiceInstanceAppBindingResponse response = CreateServiceInstanceAppBindingResponse.builder()
				.build();

		assertThat(response.isBindingExisted(), equalTo(false));
		assertThat(response.getCredentials(), nullValue());
		assertThat(response.getSyslogDrainUrl(), nullValue());
		assertThat(response.getVolumeMounts(), nullValue());
	}

	@Test
	public void responseWithDiscreteValuesIsBuilt() {
		Map<String, Object> credentials = new HashMap<String, Object>() {{
			put("credential4", "value4");
			put("credential5", "value5");
		}};

		List<VolumeMount> volumeMounts = Arrays.asList(
				VolumeMount.builder().build(),
				VolumeMount.builder().build()
		);

		CreateServiceInstanceAppBindingResponse response = CreateServiceInstanceAppBindingResponse.builder()
				.bindingExisted(true)
				.credentials("credential1", "value1")
				.credentials("credential2", 2)
				.credentials("credential3", true)
				.credentials(credentials)
				.syslogDrainUrl("https://logs.example.com")
				.volumeMounts(VolumeMount.builder().build())
				.volumeMounts(VolumeMount.builder().build())
				.volumeMounts(volumeMounts)
				.build();

		assertThat(response.isBindingExisted(), equalTo(true));

		assertThat(response.getCredentials(), aMapWithSize(5));
		assertThat(response.getCredentials().get("credential1"), equalTo("value1"));
		assertThat(response.getCredentials().get("credential2"), equalTo(2));
		assertThat(response.getCredentials().get("credential3"), equalTo(true));
		assertThat(response.getCredentials().get("credential4"), equalTo("value4"));
		assertThat(response.getCredentials().get("credential5"), equalTo("value5"));

		assertThat(response.getSyslogDrainUrl(), equalTo("https://logs.example.com"));
		
		assertThat(response.getVolumeMounts(), hasSize(4));
	}

	@Test
	public void responseIsSerializedToJson() throws Exception {
		CreateServiceInstanceAppBindingResponse response = CreateServiceInstanceAppBindingResponse.builder()
				.credentials("credential1", "value1")
				.credentials("credential2", 2)
				.credentials("credential3", true)
				.syslogDrainUrl("https://logs.example.com")
				.volumeMounts(VolumeMount.builder().build())
				.volumeMounts(VolumeMount.builder().build())
				.build();

		String json = DataFixture.toJson(response);

		assertThat(json, isJson(allOf(
				withJsonPath("$.credentials", aMapWithSize(3)),
				withJsonPath("$.credentials.credential1", equalTo("value1")),
				withJsonPath("$.credentials.credential2", equalTo(2)),
				withJsonPath("$.credentials.credential3", equalTo(true)),
				withJsonPath("$.syslog_drain_url", equalTo("https://logs.example.com")),
				withJsonPath("$.volume_mounts", hasSize(2))
		)));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(CreateServiceInstanceAppBindingResponse.class)
				.withRedefinedSuperclass()
				.verify();
	}
}