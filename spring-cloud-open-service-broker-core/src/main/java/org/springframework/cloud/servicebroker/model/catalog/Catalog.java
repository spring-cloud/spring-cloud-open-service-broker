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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The catalog of services offered by the service broker.
 *
 * <p>
 * Objects of this type are constructed by the service broker application, and used to
 * build the response to the platform.
 *
 * @see <a href=
 * "https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#catalog-management">Open
 * Service Broker API specification</a>
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
public class Catalog {
	@NotEmpty
	@JsonProperty("services")
	private final List<ServiceDefinition> serviceDefinitions;

	Catalog(List<ServiceDefinition> serviceDefinitions) {
		this.serviceDefinitions = serviceDefinitions;
	}

	Catalog() {
		this(new ArrayList<>());
	}

	/**
	 * Get the set of service offerings.
	 *
	 * @return a set of service offerings
	 */
	public List<ServiceDefinition> getServiceDefinitions() {
		return this.serviceDefinitions;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Catalog)) return false;
		Catalog catalog = (Catalog) o;
		return Objects.equals(serviceDefinitions, catalog.serviceDefinitions);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(serviceDefinitions);
	}

	@Override
	public String toString() {
		return "Catalog{" +
				"serviceDefinitions=" + serviceDefinitions +
				'}';
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal Catalog}.
	 *
	 * @return the builder
	 */
	public static CatalogBuilder builder() {
		return new CatalogBuilder();
	}

	/**
	 * Provides a fluent API for constructing a {@literal Catalog}.
	 */
	public static class CatalogBuilder {
		private final List<ServiceDefinition> serviceDefinitions = new ArrayList<>();

		CatalogBuilder() {
		}

		/**
		 * Add a set of service offerings from the provided {@literal List} to the
		 * service offerings provided by the service broker.
		 *
		 * <p>
		 * This value sets the {@literal services} field in the response to the platform.
		 *
		 * @param serviceDefinitions a {@literal List} of service offerings
		 * @return the catalog builder instance
		 */
		public CatalogBuilder serviceDefinitions(List<ServiceDefinition> serviceDefinitions) {
			this.serviceDefinitions.addAll(serviceDefinitions);
			return this;
		}

		/**
		 * Add a set of service offerings from the provided array to the
		 * service offerings provided by the service broker.
		 *
		 * <p>
		 * This value sets the {@literal services} field in the response to the platform.
		 *
		 * @param serviceDefinitions an array of service offerings
		 * @return the catalog builder instance
		 */
		public CatalogBuilder serviceDefinitions(ServiceDefinition... serviceDefinitions) {
			Collections.addAll(this.serviceDefinitions, serviceDefinitions);
			return this;
		}

		/**
		 * Construct a {@link Catalog} from the provided values.
		 *
		 * @return the newly constructed {@literal Catalog}
		 */
		public Catalog build() {
			return new Catalog(serviceDefinitions);
		}
	}
}
