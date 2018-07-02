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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.model.KubernetesContext.NAMESPACE_KEY;

public class KubernetesContextTest {
	@Test
	public void emptyContext() {
		KubernetesContext context = new KubernetesContext();
		assertThat(context.getNamespace()).isNull();
		assertThat(context.getProperty(NAMESPACE_KEY)).isNull();
	}

	@Test
	public void populatedContext() {
		KubernetesContext context = new KubernetesContext("namespace");
		assertThat(context.getNamespace()).isEqualTo("namespace");
		assertThat(context.getProperty(NAMESPACE_KEY)).isEqualTo("namespace");
	}
}