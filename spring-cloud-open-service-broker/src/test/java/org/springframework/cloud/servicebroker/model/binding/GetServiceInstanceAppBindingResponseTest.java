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

package org.springframework.cloud.servicebroker.model.binding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withoutJsonPath;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class GetServiceInstanceAppBindingResponseTest {
	@Test
	public void responseWithDefaultsIsBuilt() {
		GetServiceInstanceAppBindingResponse response = GetServiceInstanceAppBindingResponse.builder()
				.build();

		assertThat(response.getParameters(), aMapWithSize(0));
		assertThat(response.getCredentials(), aMapWithSize(0));
		assertThat(response.getSyslogDrainUrl(), nullValue());
		assertThat(response.getVolumeMounts(), hasSize(0));

		String json = DataFixture.toJson(response);

		assertThat(json, isJson(allOf(
				withoutJsonPath("$.parameters"),
				withoutJsonPath("$.credentials"),
				withoutJsonPath("$.syslog_drain_url"),
				withoutJsonPath("$.volume_mounts")
		)));
	}

	@Test
	@SuppressWarnings({"serial", "unchecked"})
	public void responseWithDiscreteValuesIsBuilt() {
		Map<String, Object> parameters = new HashMap<String, Object>() {{
			put("field4", "value4");
			put("field5", "value5");
		}};

		Map<String, Object> credentials = new HashMap<String, Object>() {{
			put("credential4", "value4");
			put("credential5", "value5");
		}};

		List<VolumeMount> volumeMounts = Arrays.asList(
				VolumeMount.builder().build(),
				VolumeMount.builder().build()
		);

		GetServiceInstanceAppBindingResponse response = GetServiceInstanceAppBindingResponse.builder()
				.parameters("field1", "value1")
				.parameters("field2", 2)
				.parameters("field3", true)
				.parameters(parameters)
				.credentials("credential1", "value1")
				.credentials("credential2", 2)
				.credentials("credential3", true)
				.credentials(credentials)
				.syslogDrainUrl("https://logs.example.com")
				.volumeMounts(VolumeMount.builder().build())
				.volumeMounts(VolumeMount.builder().build())
				.volumeMounts(volumeMounts)
				.build();

		assertThat(response.getParameters(), aMapWithSize(5));
		assertThat(response.getParameters().get("field1"), equalTo("value1"));
		assertThat(response.getParameters().get("field2"), equalTo(2));
		assertThat(response.getParameters().get("field3"), equalTo(true));
		assertThat(response.getParameters().get("field4"), equalTo("value4"));
		assertThat(response.getParameters().get("field5"), equalTo("value5"));

		assertThat(response.getCredentials(), aMapWithSize(5));
		assertThat(response.getCredentials().get("credential1"), equalTo("value1"));
		assertThat(response.getCredentials().get("credential2"), equalTo(2));
		assertThat(response.getCredentials().get("credential3"), equalTo(true));
		assertThat(response.getCredentials().get("credential4"), equalTo("value4"));
		assertThat(response.getCredentials().get("credential5"), equalTo("value5"));

		assertThat(response.getSyslogDrainUrl(), equalTo("https://logs.example.com"));
		
		assertThat(response.getVolumeMounts(), hasSize(4));

		String json = DataFixture.toJson(response);

		assertThat(json, isJson(allOf(
				withJsonPath("$.parameters", aMapWithSize(5)),
				withJsonPath("$.parameters.field1", equalTo("value1")),
				withJsonPath("$.parameters.field2", equalTo(2)),
				withJsonPath("$.parameters.field3", equalTo(true)),
				withJsonPath("$.parameters.field4", equalTo("value4")),
				withJsonPath("$.parameters.field5", equalTo("value5")),
				withJsonPath("$.credentials", aMapWithSize(5)),
				withJsonPath("$.credentials.credential1", equalTo("value1")),
				withJsonPath("$.credentials.credential2", equalTo(2)),
				withJsonPath("$.credentials.credential3", equalTo(true)),
				withJsonPath("$.credentials.credential4", equalTo("value4")),
				withJsonPath("$.credentials.credential5", equalTo("value5")),
				withJsonPath("$.syslog_drain_url", equalTo("https://logs.example.com")),
				withJsonPath("$.volume_mounts", hasSize(4))
		)));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(GetServiceInstanceAppBindingResponse.class)
				.withRedefinedSuperclass()
				.verify();
	}
}