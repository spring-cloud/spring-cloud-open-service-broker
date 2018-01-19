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

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class BindResourceTest {
	@Test
	public void bindResourceIsBuiltWithDefaults() {
		BindResource bindResource = BindResource.builder()
				.build();

		assertThat(bindResource.getAppGuid(), nullValue());
		assertThat(bindResource.getRoute(), nullValue());
		assertThat(bindResource.getProperties(), aMapWithSize(0));
	}

	@Test
	public void bindResourceIsBuiltWithAllValues() {
		Map<String, Object> parameters = new HashMap<String, Object>() {{
			put("parameter3", "value3");
			put("parameter4", true);
		}};

		BindResource bindResource = BindResource.builder()
				.appGuid("app-guid")
				.route("route")
				.parameters("parameter1", "value1")
				.parameters("parameter2", 2)
				.parameters(parameters)
				.build();

		assertThat(bindResource.getAppGuid(), equalTo("app-guid"));
		assertThat(bindResource.getRoute(), equalTo("route"));
		assertThat(bindResource.getProperties(), aMapWithSize(4));
		assertThat(bindResource.getProperties().get("parameter1"), equalTo("value1"));
		assertThat(bindResource.getProperties().get("parameter2"), equalTo(2));
		assertThat(bindResource.getProperties().get("parameter3"), equalTo("value3"));
		assertThat(bindResource.getProperties().get("parameter4"), equalTo(true));
	}

	@Test
	public void bindResourceIsDeserializedFromJson() {
		BindResource bindResource = DataFixture.readTestDataFile("bindResource.json", BindResource.class);

		assertThat(bindResource.getAppGuid(), equalTo("test-app-guid"));
		assertThat(bindResource.getRoute(), equalTo("http://test.example.com"));
		assertThat(bindResource.getProperties(), aMapWithSize(3));
		assertThat(bindResource.getProperty("property1"), equalTo(1));
		assertThat(bindResource.getProperty("property2"), equalTo("value2"));
		assertThat(bindResource.getProperty("property3"), equalTo(true));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(BindResource.class)
				.verify();
	}
}