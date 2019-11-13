/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.cloud.servicebroker.model;

import com.jayway.jsonpath.DocumentContext;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonPathAssert;
import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.cloud.servicebroker.model.KubernetesContext.CLUSTERID_KEY;
import static org.springframework.cloud.servicebroker.model.KubernetesContext.KUBERNETES_PLATFORM;
import static org.springframework.cloud.servicebroker.model.KubernetesContext.NAMESPACE_KEY;

class KubernetesContextTest {

	@Test
	void emptyContext() {
		KubernetesContext context = KubernetesContext.builder()
				.build();

		assertThat(context.getPlatform()).isEqualTo(KUBERNETES_PLATFORM);

		assertThat(context.getNamespace()).isNull();

		assertThat(context.getProperties()).isEmpty();
	}

	@Test
	void populatedContext() {
		KubernetesContext context = KubernetesContext.builder()
				.property("key1", "value1")
				.namespace("namespace")
				.clusterid("clusterid")
				.property("key2", "value2")
				.build();

		assertThat(context.getPlatform()).isEqualTo(KUBERNETES_PLATFORM);

		assertThat(context.getNamespace()).isEqualTo("namespace");
		assertThat(context.getClusterid()).isEqualTo("clusterid");
		assertThat(context.getProperty(NAMESPACE_KEY)).isEqualTo("namespace");
		assertThat(context.getProperty(CLUSTERID_KEY)).isEqualTo("clusterid");

		assertThat(context.getProperty("key1")).isEqualTo("value1");
		assertThat(context.getProperty("key2")).isEqualTo("value2");

		//ensure we don't break super class contract
		assertThat(context.getProperties()).containsOnly(
				entry("key1", "value1"),
				entry("key2", "value2"),
				entry("namespace", "namespace"),
				entry("clusterid", "clusterid"));
	}

	@Test
	void partialContextIsSerialized() {
		KubernetesContext context = KubernetesContext.builder()
				.property("key1", "value1")
				.namespace("namespace")
				.property("key2", "value2")
				.build();

		DocumentContext json = JsonUtils.toJsonPath(context);

		JsonPathAssert.assertThat(json).hasPath("$.platform").isEqualTo("kubernetes");
		JsonPathAssert.assertThat(json).hasPath("$.namespace").isEqualTo("namespace");
		JsonPathAssert.assertThat(json).hasPath("$.key1").isEqualTo("value1");
		JsonPathAssert.assertThat(json).hasPath("$.key2").isEqualTo("value2");
		// detect any double serialization due to inheritance and naming mismatch
		JsonPathAssert.assertThat(json).hasMapAtPath("$").hasSize(4);
		JsonUtils.assertThatJsonHasExactNumberOfProperties(context, 4 + 1);//still have duplicated platform property
	}

	@Test
	void fullContextIsSerialized() {
		KubernetesContext context = KubernetesContext.builder()
				.property("key1", "value1")
				.namespace("namespace")
				.clusterid("clusterid")
				.property("key2", "value2")
				.build();

		DocumentContext json = JsonUtils.toJsonPath(context);

		JsonPathAssert.assertThat(json).hasPath("$.platform").isEqualTo("kubernetes");
		JsonPathAssert.assertThat(json).hasPath("$.namespace").isEqualTo("namespace");
		JsonPathAssert.assertThat(json).hasPath("$.clusterid").isEqualTo("clusterid");
		JsonPathAssert.assertThat(json).hasPath("$.key1").isEqualTo("value1");
		JsonPathAssert.assertThat(json).hasPath("$.key2").isEqualTo("value2");
		// detect any double serialization due to inheritance and naming mismatch
		JsonPathAssert.assertThat(json).hasMapAtPath("$").hasSize(5);
		JsonUtils.assertThatJsonHasExactNumberOfProperties(context, 5 + 1);//still have duplicated platform property
	}

}
