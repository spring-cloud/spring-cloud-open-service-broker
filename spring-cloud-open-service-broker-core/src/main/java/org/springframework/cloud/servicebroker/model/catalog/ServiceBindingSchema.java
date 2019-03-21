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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Service binding JSON Schemas.
 *
 * @see <a href=
 * "https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#service-binding-schema-object">Open
 * Service Broker API specification</a>
 *
 * @author sgunaratne@pivotal.io
 * @author Sam Gunaratne
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceBindingSchema {

	@JsonProperty("create")
	private final MethodSchema createMethodSchema;

	ServiceBindingSchema(MethodSchema createMethodSchema) {
		this.createMethodSchema = createMethodSchema;
	}

	ServiceBindingSchema() {
		this(null);
	}

	/**
	 * The JSON schema for configuration parameters when creating a service binding.
	 *
	 * @return the schema
	 */
	public MethodSchema getCreateMethodSchema() {
		return this.createMethodSchema;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal ServiceBindingSchema}.
	 *
	 * @return the builder
	 */
	public static ServiceBindingSchemaBuilder builder() {
		return new ServiceBindingSchemaBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ServiceBindingSchema)) return false;
		ServiceBindingSchema that = (ServiceBindingSchema) o;
		return Objects.equals(createMethodSchema, that.createMethodSchema);
	}

	@Override
	public int hashCode() {
		return Objects.hash(createMethodSchema);
	}

	@Override
	public String toString() {
		return "ServiceBindingSchema{" +
				"createMethodSchema=" + createMethodSchema +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@literal ServiceBindingSchema}.
	 */
	public static class ServiceBindingSchemaBuilder {
		private MethodSchema createMethodSchema;

		ServiceBindingSchemaBuilder() {
		}

		/**
		 * The JSON schema for configuration parameters when creating a service binding.
		 *
		 * @param createMethodSchema the schema
		 * @return the binder instance
		 */
		public ServiceBindingSchema.ServiceBindingSchemaBuilder createMethodSchema(MethodSchema createMethodSchema) {
			this.createMethodSchema = createMethodSchema;
			return this;
		}

		/**
		 * Construct a {@link ServiceBindingSchema} from the provided values.
		 *
		 * @return the newly constructed {@literal ServiceBindingSchema}
		 */
		public ServiceBindingSchema build() {
			return new ServiceBindingSchema(createMethodSchema);
		}
	}
}
