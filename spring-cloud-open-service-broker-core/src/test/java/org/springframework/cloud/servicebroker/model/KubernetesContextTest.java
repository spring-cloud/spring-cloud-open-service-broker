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

package org.springframework.cloud.servicebroker.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.model.KubernetesContext.KUBERNETES_PLATFORM;
import static org.springframework.cloud.servicebroker.model.KubernetesContext.NAMESPACE_KEY;

public class KubernetesContextTest {
	@Test
	public void emptyContext() {
		KubernetesContext context = KubernetesContext.builder()
				.build();

		assertThat(context.getPlatform()).isEqualTo(KUBERNETES_PLATFORM);

		assertThat(context.getNamespace()).isNull();

		assertThat(context.getProperties()).isEmpty();
	}

	@Test
	public void populatedContext() {
		KubernetesContext context = KubernetesContext.builder()
				.property("key1", "value1")
				.namespace("namespace")
				.property("key2", "value2")
				.build();

		assertThat(context.getPlatform()).isEqualTo(KUBERNETES_PLATFORM);

		assertThat(context.getNamespace()).isEqualTo("namespace");
		assertThat(context.getProperty(NAMESPACE_KEY)).isEqualTo("namespace");

		assertThat(context.getProperty("key1")).isEqualTo("value1");
		assertThat(context.getProperty("key2")).isEqualTo("value2");
	}
}