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

package org.springframework.cloud.servicebroker.model.catalog;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * JSON Schema for a service broker object method.
 *
 * @author Sam Gunaratne
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MethodSchema {

	private final Map<String, Object> parameters;

	MethodSchema(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	/**
	 * A map of JSON schema for configuration parameters.
	 *
	 * @return the configuration parameters
	 */
	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a
	 * {@literal MethodSchema}.
	 *
	 * @return the builder
	 */
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

	/**
	 * Provides a fluent API for constructing a {@literal MethodSchema}.
	 */
	public static class MethodSchemaBuilder {
		private final Map<String, Object> parameters = new HashMap<>();

		MethodSchemaBuilder() {
		}

		/**
		 * A map of JSON schema for configuration parameters.
		 *
		 * @param parameters the configuration parameters
		 * @return the builder instance
		 */
		public MethodSchema.MethodSchemaBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		/**
		 * A key/value pair to add to the JSON schema configuration parameters
		 *
		 * @param key the unique key
		 * @param value the value
		 * @return the builder instance
		 */
		public MethodSchema.MethodSchemaBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		/**
		 * Construct a {@link MethodSchema} from the provided values.
		 *
		 * @return the newly constructed {@literal MethodSchema}
		 */
		public MethodSchema build() {
			return new MethodSchema(parameters);
		}
	}
}
