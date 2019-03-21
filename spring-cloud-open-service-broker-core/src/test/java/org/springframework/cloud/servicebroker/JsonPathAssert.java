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

package org.springframework.cloud.servicebroker;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.TypeRef;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;

import java.util.List;
import java.util.Map;

public class JsonPathAssert extends AbstractAssert<JsonPathAssert, DocumentContext> {
	public JsonPathAssert(DocumentContext actual) {
		super(actual, JsonPathAssert.class);
	}

	public static JsonPathAssert assertThat(DocumentContext jsonPathDocument) {
		return new JsonPathAssert(jsonPathDocument);
	}

	public JsonPathAssert hasNoPath(String jsonPath) {
		try {
			Object value = actual.read(jsonPath);
			failWithMessage("The path " + jsonPath + " was not expected but evaluated to " + value);
			return null;
		} catch (JsonPathException e) {
			return this;
		}
	}

	public AbstractObjectAssert<?, Object> hasPath(String path) {
		return Assertions.assertThat(actual.read(path, Object.class));
	}

	public AbstractCharSequenceAssert<?, String> hasStringAtPath(String path) {
		return Assertions.assertThat(actual.read(path, String.class));
	}

	public AbstractIntegerAssert<?> hasIntegerAtPath(String path) {
		return Assertions.assertThat(actual.read(path, Integer.class));
	}

	public AbstractBooleanAssert<?> hasBooleanAtPath(String path) {
		return Assertions.assertThat(actual.read(path, Boolean.class));
	}

	public <T> ListAssert<T> hasListAtPath(String path) {
		TypeRef<List<T>> listTypeRef = new TypeRef<List<T>>() {
		};
		return Assertions.assertThat(actual.read(path, listTypeRef));
	}

	public <K, V> MapAssert<K, V> hasMapAtPath(String path) {
		TypeRef<Map<K, V>> mapTypeRef = new TypeRef<Map<K, V>>() {
		};
		return Assertions.assertThat(actual.read(path, mapTypeRef));
	}
}
