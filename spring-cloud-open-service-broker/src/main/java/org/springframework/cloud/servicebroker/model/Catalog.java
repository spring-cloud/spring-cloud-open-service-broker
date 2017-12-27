/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The catalog of services offered by the service broker.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
public class Catalog {

	/**
	 * A list of service offerings provided by the service broker.
	 */
	@NotEmpty
	@JsonProperty("services")
	private final List<ServiceDefinition> serviceDefinitions;

	private Catalog(List<ServiceDefinition> serviceDefinitions) {
		this.serviceDefinitions = serviceDefinitions;
	}

	public List<ServiceDefinition> getServiceDefinitions() {
		return this.serviceDefinitions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Catalog)) return false;
		Catalog catalog = (Catalog) o;
		return Objects.equals(serviceDefinitions, catalog.serviceDefinitions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(serviceDefinitions);
	}

	@Override
	public String toString() {
		return "Catalog{" +
				"serviceDefinitions=" + serviceDefinitions +
				'}';
	}

	public static CatalogBuilder builder() {
		return new CatalogBuilder();
	}

	public static class CatalogBuilder {
		private List<ServiceDefinition> serviceDefinitions = new ArrayList<>();

		CatalogBuilder() {
		}

		public CatalogBuilder serviceDefinitions(List<ServiceDefinition> serviceDefinitions) {
			this.serviceDefinitions.addAll(serviceDefinitions);
			return this;
		}

		public CatalogBuilder serviceDefinitions(ServiceDefinition... serviceDefinitions) {
			Collections.addAll(this.serviceDefinitions, serviceDefinitions);
			return this;
		}

		public Catalog build() {
			return new Catalog(serviceDefinitions);
		}
	}
}
