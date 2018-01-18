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

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class ContextTest {
	@Test
	public void contextIsBuildWithDefaults() {
		Context context = Context.builder()
				.build();

		assertThat(context.getPlatform(), nullValue());
		assertThat(context.getProperties(), aMapWithSize(0));
	}

	@Test
	public void contextIsBuildWithAllValues() {
		Map<String, Object> properties = new HashMap<String, Object>() {{
			put("property3", "value3");
			put("property4", true);
		}};

		Context context = Context.builder()
				.platform("test-platform")
				.property("property1", 1)
				.property("property2", "value2")
				.properties(properties)
				.build();

		assertThat(context.getPlatform(), equalTo("test-platform"));
		assertThat(context.getProperties(), aMapWithSize(4));
		assertThat(context.getProperty("property1"), equalTo(1));
		assertThat(context.getProperty("property2"), equalTo("value2"));
		assertThat(context.getProperty("property3"), equalTo("value3"));
		assertThat(context.getProperty("property4"), equalTo(true));
	}
}