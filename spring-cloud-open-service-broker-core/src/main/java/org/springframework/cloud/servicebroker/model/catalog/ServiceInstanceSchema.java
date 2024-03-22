/*
 * Copyright 2002-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
 * Service instance JSON Schemas.
 *
 * @author Sam Gunaratne
 * @see <a href=
 * "https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#service-instance-schema-object">Open
 * Service Broker API specification</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceInstanceSchema {

	private final MethodSchema createMethodSchema;

	private final MethodSchema updateMethodSchema;

	/**
	 * Construct a new {@link ServiceInstanceSchema}.
	 */
	public ServiceInstanceSchema() {
		this(null, null);
	}

	/**
	 * Construct a new {@link ServiceInstanceSchema}.
	 * @param createMethodSchema the schema
	 * @param updateMethodSchema the schema
	 */
	public ServiceInstanceSchema(MethodSchema createMethodSchema, MethodSchema updateMethodSchema) {
		this.createMethodSchema = createMethodSchema;
		this.updateMethodSchema = updateMethodSchema;
	}

	/**
	 * The JSON schema for configuration parameters when creating a service instance.
	 * @return the schema
	 */
	@JsonProperty("create")
	public MethodSchema getCreateMethodSchema() {
		return this.createMethodSchema;
	}

	/**
	 * The JSON schema for configuration parameters when updating a service instance.
	 * @return the schema
	 */
	@JsonProperty("update")
	public MethodSchema getUpdateMethodSchema() {
		return this.updateMethodSchema;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a
	 * {@literal ServiceInstanceSchema}.
	 * @return the builder
	 */
	public static ServiceInstanceSchemaBuilder builder() {
		return new ServiceInstanceSchemaBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ServiceInstanceSchema)) {
			return false;
		}
		ServiceInstanceSchema that = (ServiceInstanceSchema) o;
		return Objects.equals(this.createMethodSchema, that.createMethodSchema)
				&& Objects.equals(this.updateMethodSchema, that.updateMethodSchema);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.createMethodSchema, this.updateMethodSchema);
	}

	@Override
	public String toString() {
		return "ServiceInstanceSchema{" + "createMethodSchema=" + this.createMethodSchema + ", updateMethodSchema="
				+ this.updateMethodSchema + '}';
	}

	/**
	 * Provides a fluent API for constructing a {@literal ServiceInstanceSchema}.
	 */
	public static final class ServiceInstanceSchemaBuilder {

		private MethodSchema createMethodSchema;

		private MethodSchema updateMethodSchema;

		private ServiceInstanceSchemaBuilder() {
		}

		/**
		 * The JSON schema for configuration parameters when creating a service instance.
		 * @param createMethodSchema the schema
		 * @return the binder instance
		 */
		public ServiceInstanceSchemaBuilder createMethodSchema(MethodSchema createMethodSchema) {
			this.createMethodSchema = createMethodSchema;
			return this;
		}

		/**
		 * The JSON schema for configuration parameters when updating a service instance.
		 * @param updateMethodSchema the schema
		 * @return the binder instance
		 */
		public ServiceInstanceSchemaBuilder updateMethodSchema(MethodSchema updateMethodSchema) {
			this.updateMethodSchema = updateMethodSchema;
			return this;
		}

		/**
		 * Construct a {@link ServiceInstanceSchema} from the provided values.
		 * @return the newly constructed {@literal ServiceInstanceSchema}
		 */
		public ServiceInstanceSchema build() {
			return new ServiceInstanceSchema(this.createMethodSchema, this.updateMethodSchema);
		}

	}

}
