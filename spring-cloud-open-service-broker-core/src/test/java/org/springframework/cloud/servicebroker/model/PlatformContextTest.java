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


import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlatformContextTest {
	@Test
	public void contextIsBuildWithDefaults() {
		PlatformContext context = PlatformContext.builder()
				.build();

		assertThat(context.getPlatform()).isNull();
		assertThat(context.getProperties()).hasSize(0);
	}

	@Test
	@SuppressWarnings("serial")
	public void contextIsBuildWithAllValues() {
		Map<String, Object> properties = new HashMap<String, Object>() {{
			put("property3", "value3");
			put("property4", true);
		}};

		PlatformContext context = PlatformContext.builder()
				.platform("test-platform")
				.property("property1", 1)
				.property("property2", "value2")
				.properties(properties)
				.build();

		assertThat(context.getPlatform()).isEqualTo("test-platform");
		assertThat(context.getProperties()).hasSize(4);
		assertThat(context.getProperty("property1")).isEqualTo(1);
		assertThat(context.getProperty("property2")).isEqualTo("value2");
		assertThat(context.getProperty("property3")).isEqualTo("value3");
		assertThat(context.getProperty("property4")).isEqualTo(true);
	}
}
