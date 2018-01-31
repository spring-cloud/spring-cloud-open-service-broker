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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A service plan available for a ServiceDefinition
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@JsonInclude(Include.NON_NULL)
public class Plan {

	/**
	 * An identifier used to correlate this plan in future requests to the catalog. This must be unique within
	 * a Cloud Foundry deployment. Using a GUID is recommended.
	 */
	@NotEmpty
	private final String id;

	/**
	 * A CLI-friendly name of the plan that will appear in the catalog. The value should be all lowercase,
	 * with no spaces.
	 */
	@NotEmpty
	private final String name;

	/**
	 * A user-friendly short description of the plan that will appear in the catalog.
	 */
	@NotEmpty
	private final String description;

	/**
	 * A map of metadata to further describe a service plan.
	 */
	private final Map<String, Object> metadata;

	/**
	 * The schemas for this plan.
	 */
	private final Schemas schemas;

	/**
	 * Indicates whether the service with this plan can be bound to applications. This is an optional field. If the
	 * value is <code>null</code>, the field will be omitted from the serialized JSON.
	 */
	private final Boolean bindable;

	/**
	 * Indicates whether the plan can be limited by the non_basic_services_allowed field in a Cloud Foundry Quota.
	 */
	private final Boolean free;

	Plan(String id, String name, String description, Map<String, Object> metadata, Boolean free, Boolean bindable, Schemas schemas) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.metadata = metadata;
		this.free = free;
		this.bindable = bindable;
		this.schemas = schemas;
	}

	public Boolean isBindable() {
		return bindable;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public Map<String, Object> getMetadata() {
		return this.metadata;
	}

	public Schemas getSchemas() {
		return this.schemas;
	}

	public Boolean isFree() {
		return this.free;
	}

	public static PlanBuilder builder() {
		return new PlanBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Plan)) return false;
		Plan plan = (Plan) o;
		return Objects.equals(free, plan.free) &&
				Objects.equals(id, plan.id) &&
				Objects.equals(name, plan.name) &&
				Objects.equals(description, plan.description) &&
				Objects.equals(metadata, plan.metadata) &&
				Objects.equals(schemas, plan.schemas) &&
				Objects.equals(bindable, plan.bindable);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(id, name, description, metadata, schemas, bindable, free);
	}

	@Override
	public String toString() {
		return "Plan{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", metadata=" + metadata +
				", schemas=" + schemas +
				", bindable=" + bindable +
				", free=" + free +
				'}';
	}

	public static class PlanBuilder {
		private String id;
		private String name;
		private String description;
		private Map<String, Object> metadata;
		private Boolean free = true;
		private Boolean bindable;
		private Schemas schemas;

		PlanBuilder() {
		}

		public PlanBuilder id(String id) {
			this.id = id;
			return this;
		}

		public PlanBuilder name(String name) {
			this.name = name;
			return this;
		}

		public PlanBuilder description(String description) {
			this.description = description;
			return this;
		}

		public PlanBuilder metadata(Map<String, Object> metadata) {
			if (this.metadata == null) {
				this.metadata = new HashMap<>();
			}
			this.metadata.putAll(metadata);
			return this;
		}

		public PlanBuilder metadata(String key, Object value) {
			if (this.metadata == null) {
				this.metadata = new HashMap<>();
			}
			this.metadata.put(key, value);
			return this;
		}

		public PlanBuilder free(boolean free) {
			this.free = free;
			return this;
		}

		public PlanBuilder bindable(boolean bindable) {
			this.bindable = bindable;
			return this;
		}

		public PlanBuilder schemas(Schemas schemas) {
			this.schemas = schemas;
			return this;
		}

		public Plan build() {
			return new Plan(id, name, description, metadata, free, bindable, schemas);
		}
	}
}
