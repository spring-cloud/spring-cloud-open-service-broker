/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
 * JSON Schemas available for a Plan.
 *
 * @author sgunaratne@pivotal.io
 * @author Sam Gunaratne
 * @see <a href= "https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#schemas-object">Open Service
 * 		Broker API specification</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Schemas {

	private final ServiceInstanceSchema serviceInstanceSchema;

	private final ServiceBindingSchema serviceBindingSchema;

	/**
	 * Construct a new {@link Schemas}
	 */
	public Schemas() {
		this(null, null);
	}

	/**
	 * Construct a new {@link Schemas}
	 *
	 * @param serviceInstanceSchema the service instance schema
	 * @param serviceBindingSchema the service binding schema
	 */
	public Schemas(ServiceInstanceSchema serviceInstanceSchema,
			ServiceBindingSchema serviceBindingSchema) {
		this.serviceInstanceSchema = serviceInstanceSchema;
		this.serviceBindingSchema = serviceBindingSchema;
	}

	/**
	 * The schemas available on a service instance.
	 *
	 * @return the schemas
	 */
	@JsonProperty("service_instance")
	public ServiceInstanceSchema getServiceInstanceSchema() {
		return this.serviceInstanceSchema;
	}

	/**
	 * The schemas available on a service binding.
	 *
	 * @return the schemas
	 */
	@JsonProperty("service_binding")
	public ServiceBindingSchema getServiceBindingSchema() {
		return this.serviceBindingSchema;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal Schemas}.
	 *
	 * @return the builder
	 */
	public static SchemasBuilder builder() {
		return new SchemasBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Schemas)) {
			return false;
		}
		Schemas schemas = (Schemas) o;
		return Objects.equals(serviceInstanceSchema, schemas.serviceInstanceSchema) &&
				Objects.equals(serviceBindingSchema, schemas.serviceBindingSchema);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(serviceInstanceSchema, serviceBindingSchema);
	}

	@Override
	public String toString() {
		return "Schemas{" +
				"serviceInstanceSchema=" + serviceInstanceSchema +
				", serviceBindingSchema=" + serviceBindingSchema +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@literal Schemas}.
	 */
	public static final class SchemasBuilder {

		private ServiceInstanceSchema serviceInstanceSchema;

		private ServiceBindingSchema serviceBindingSchema;

		private SchemasBuilder() {
		}

		/**
		 * The schemas available on a service instance.
		 *
		 * @param serviceInstanceSchema the schemas
		 * @return the binder instance
		 */
		public SchemasBuilder serviceInstanceSchema(ServiceInstanceSchema serviceInstanceSchema) {
			this.serviceInstanceSchema = serviceInstanceSchema;
			return this;
		}

		/**
		 * The schemas available on a service binding.
		 *
		 * @param serviceBindingSchema the schemas
		 * @return the binder instance
		 */
		public SchemasBuilder serviceBindingSchema(ServiceBindingSchema serviceBindingSchema) {
			this.serviceBindingSchema = serviceBindingSchema;
			return this;
		}

		/**
		 * Construct a {@link Schemas} from the provided values.
		 *
		 * @return the newly constructed {@literal Schemas}
		 */
		public Schemas build() {
			return new Schemas(serviceInstanceSchema, serviceBindingSchema);
		}

	}

}
