/*
 * Copyright 2002-2026 the original author or authors.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonPathAssert;
import org.springframework.cloud.servicebroker.JsonUtils;
import org.springframework.cloud.servicebroker.model.KubernetesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ServiceInstanceMetadataTests {

	@Test
	void emptyServiceInstanceMetadata() {
		ServiceInstanceMetadata serviceInstanceMetadata = ServiceInstanceMetadata.builder().build();

		assertThat(serviceInstanceMetadata.getLabels()).isEqualTo(Collections.emptyMap());
		assertThat(serviceInstanceMetadata.getAttributes()).isEqualTo(Collections.emptyMap());
	}

	@Test
	void populatedServiceInstanceMetadataWithLabels() {
		Map<String, Object> labels = new HashMap<>();
		labels.put("key", "value");
		labels.put("key2", "value2");

		ServiceInstanceMetadata serviceInstanceMetadata = ServiceInstanceMetadata.builder().labels(labels).build();

		assertThat(serviceInstanceMetadata.getLabels()).containsOnly(entry("key", "value"), entry("key2", "value2"));
	}

	@Test
	void populatedServiceInstanceMetadataSetLabels() {
		Map<String, Object> labels = new HashMap<>();
		labels.put("key", "value");
		labels.put("key2", "value2");

		ServiceInstanceMetadata serviceInstanceMetadata = ServiceInstanceMetadata.builder().labels(labels).build();

		assertThat(serviceInstanceMetadata.getLabels()).containsOnly(entry("key", "value"), entry("key2", "value2"));
	}

	@Test
	void populatedServiceInstanceMetadataWithLabel() {
		ServiceInstanceMetadata serviceInstanceMetadata = ServiceInstanceMetadata.builder()
			.label("key", "value")
			.label("key2", "value2")
			.build();

		assertThat(serviceInstanceMetadata.getLabels()).containsOnly(entry("key", "value"), entry("key2", "value2"));
	}

	@Test
	void populatedServiceInstanceMetadataWithAttributes() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("key", "value");
		attributes.put("key2", "value2");

		ServiceInstanceMetadata serviceInstanceMetadata = ServiceInstanceMetadata.builder()
			.attributes(attributes)
			.build();

		assertThat(serviceInstanceMetadata.getAttributes()).containsOnly(entry("key", "value"),
				entry("key2", "value2"));
	}

	@Test
	void populatedServiceInstanceMetadataWithAttribute() {
		ServiceInstanceMetadata serviceInstanceMetadata = ServiceInstanceMetadata.builder()
			.attribute("key", "value")
			.attribute("key2", "value2")
			.build();

		assertThat(serviceInstanceMetadata.getAttributes()).containsOnly(entry("key", "value"),
				entry("key2", "value2"));
	}

	@Test
	void testEqualsFalse() {
		ServiceInstanceMetadata serviceInstanceMetadata = ServiceInstanceMetadata.builder()
			.label("key", "value")
			.build();
		ServiceInstanceMetadata serviceInstanceMetadata2 = ServiceInstanceMetadata.builder()
			.label("key2", "value2")
			.build();

		assertThat(serviceInstanceMetadata).isNotEqualTo(serviceInstanceMetadata2);
	}

	@Test
	void testEqualsTrue() {
		ServiceInstanceMetadata serviceInstanceMetadata = ServiceInstanceMetadata.builder()
			.label("key", "value")
			.build();
		ServiceInstanceMetadata serviceInstanceMetadata2 = ServiceInstanceMetadata.builder()
			.label("key", "value")
			.build();

		assertThat(serviceInstanceMetadata).isEqualTo(serviceInstanceMetadata2);
	}

	@Test
	void testEqualsDifferentObject() {
		ServiceInstanceMetadata serviceInstanceMetadata = ServiceInstanceMetadata.builder()
			.label("key", "value")
			.build();
		KubernetesContext context = KubernetesContext.builder().build();

		assertThat(serviceInstanceMetadata).isNotEqualTo(context);
	}

	@Test
	void testToStringReturnsNullLabels() {
		ServiceInstanceMetadata serviceInstanceMetadata = new ServiceInstanceMetadata();

		assertThat(serviceInstanceMetadata.toString())
			.isEqualTo("ServiceInstanceMetadata{labels='{}', attributes='{}'}");
	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier.forClass(ServiceInstanceMetadata.class).verify();
	}

	@Test
	void emptyLabelsAndAttributesAreOmittedFromJson() {
		ServiceInstanceMetadata serviceInstanceMetadata = ServiceInstanceMetadata.builder().build();

		DocumentContext json = JsonUtils.toJsonPath(serviceInstanceMetadata);

		JsonPathAssert.assertThat(json).hasNoPath("$.labels");
		JsonPathAssert.assertThat(json).hasNoPath("$.attributes");
	}

	@Test
	void populatedLabelsIsSerializedButEmptyAttributesIsOmitted() {
		ServiceInstanceMetadata serviceInstanceMetadata = ServiceInstanceMetadata.builder()
			.label("key", "value")
			.build();

		DocumentContext json = JsonUtils.toJsonPath(serviceInstanceMetadata);

		JsonPathAssert.assertThat(json).hasPath("$.labels.key").isEqualTo("value");
		JsonPathAssert.assertThat(json).hasNoPath("$.attributes");
	}

	@Test
	void populatedAttributesIsSerializedButEmptyLabelsIsOmitted() {
		ServiceInstanceMetadata serviceInstanceMetadata = ServiceInstanceMetadata.builder()
			.attribute("key", "value")
			.build();

		DocumentContext json = JsonUtils.toJsonPath(serviceInstanceMetadata);

		JsonPathAssert.assertThat(json).hasPath("$.attributes.key").isEqualTo("value");
		JsonPathAssert.assertThat(json).hasNoPath("$.labels");
	}

}
