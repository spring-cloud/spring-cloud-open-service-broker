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

package org.springframework.cloud.servicebroker.model.binding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

public class GetServiceInstanceAppBindingResponseTest {
	@Test
	public void responseWithDefaultsIsBuilt() {
		GetServiceInstanceAppBindingResponse response = GetServiceInstanceAppBindingResponse.builder()
				.build();

		assertThat(response.getParameters()).hasSize(0);
		assertThat(response.getCredentials()).hasSize(0);
		assertThat(response.getSyslogDrainUrl()).isNull();
		assertThat(response.getVolumeMounts()).hasSize(0);

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasNoPath("$.parameters");
		assertThat(json).hasNoPath("$.credentials");
		assertThat(json).hasNoPath("$.syslog_drain_url");
		assertThat(json).hasNoPath("$.volume_mounts");
	}

	@Test
	@SuppressWarnings("serial")
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

		assertThat(response.getParameters()).hasSize(5);
		assertThat(response.getParameters().get("field1")).isEqualTo("value1");
		assertThat(response.getParameters().get("field2")).isEqualTo(2);
		assertThat(response.getParameters().get("field3")).isEqualTo(true);
		assertThat(response.getParameters().get("field4")).isEqualTo("value4");
		assertThat(response.getParameters().get("field5")).isEqualTo("value5");

		assertThat(response.getCredentials()).hasSize(5);
		assertThat(response.getCredentials().get("credential1")).isEqualTo("value1");
		assertThat(response.getCredentials().get("credential2")).isEqualTo(2);
		assertThat(response.getCredentials().get("credential3")).isEqualTo(true);
		assertThat(response.getCredentials().get("credential4")).isEqualTo("value4");
		assertThat(response.getCredentials().get("credential5")).isEqualTo("value5");

		assertThat(response.getSyslogDrainUrl()).isEqualTo("https://logs.example.com");
		
		assertThat(response.getVolumeMounts()).hasSize(4);

		DocumentContext json = JsonUtils.toJsonPath(response);

		assertThat(json).hasPath("$.parameters.field1").isEqualTo("value1");
		assertThat(json).hasPath("$.parameters.field2").isEqualTo(2);
		assertThat(json).hasPath("$.parameters.field3").isEqualTo(true);
		assertThat(json).hasPath("$.parameters.field4").isEqualTo("value4");
		assertThat(json).hasPath("$.parameters.field5").isEqualTo("value5");

		assertThat(json).hasPath("$.credentials.credential1").isEqualTo("value1");
		assertThat(json).hasPath("$.credentials.credential2").isEqualTo(2);
		assertThat(json).hasPath("$.credentials.credential3").isEqualTo(true);
		assertThat(json).hasPath("$.credentials.credential4").isEqualTo("value4");
		assertThat(json).hasPath("$.credentials.credential5").isEqualTo("value5");

		assertThat(json).hasPath("$.syslog_drain_url").isEqualTo("https://logs.example.com");

		assertThat(json).hasListAtPath("$.volume_mounts").hasSize(4);
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(GetServiceInstanceAppBindingResponse.class)
				.withRedefinedSuperclass()
				.verify();
	}
}
