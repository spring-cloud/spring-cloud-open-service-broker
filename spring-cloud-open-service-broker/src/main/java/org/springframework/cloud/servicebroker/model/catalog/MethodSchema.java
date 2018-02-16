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

package org.springframework.cloud.servicebroker.model.catalog;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * JSON Schema for a service broker object method.
 *
 * @author Sam Gunaratne
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MethodSchema {

	/**
	 * A map of JSON schema for configuration parameters.
	 */
	private final Map<String, Object> parameters;

	MethodSchema(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	public static MethodSchemaBuilder builder() {
		return new MethodSchemaBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MethodSchema)) return false;
		MethodSchema that = (MethodSchema) o;
		return Objects.equals(parameters, that.parameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(parameters);
	}

	@Override
	public String toString() {
		return "MethodSchema{" +
				"parameters=" + parameters +
				'}';
	}

	public static class MethodSchemaBuilder {
		private final Map<String, Object> parameters = new HashMap<>();

		MethodSchemaBuilder() {
		}

		public MethodSchema.MethodSchemaBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		public MethodSchema.MethodSchemaBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		public MethodSchema build() {
			return new MethodSchema(parameters);
		}
	}
}
