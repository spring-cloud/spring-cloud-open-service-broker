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

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * A service plan available for a ServiceDefinition
 *
 * @see <a href=
 * "https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#plan-object">Open
 * Service Broker API specification</a>
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@JsonInclude(Include.NON_NULL)
public class Plan {

	@NotEmpty
	private final String id;

	@NotEmpty
	private final String name;

	@NotEmpty
	private final String description;

	private final Map<String, Object> metadata;

	private final Schemas schemas;

	private final Boolean bindable;

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

	Plan() {
		this(null, null, null, new HashMap<>(), null, null, null);
	}

	/**
	 * An identifier used to correlate this plan in future requests to the catalog. This
	 * must be unique within the platform. Using a GUID is recommended.
	 *
	 * @return the plan ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * A CLI-friendly name of the plan that will appear in the catalog. The value should
	 * be all lowercase, with no spaces.
	 *
	 * @return the plan name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * A user-friendly short description of the plan that will appear in the catalog.
	 *
	 * @return the plan description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * A map of metadata to further describe a service plan.
	 *
	 * @return the plan metadata
	 * @see <a href=
	 * "https://github.com/openservicebrokerapi/servicebroker/blob/master/profile.md#service-metadata">Service
	 * metadata conventions</a>
	 */
	public Map<String, Object> getMetadata() {
		return this.metadata;
	}

	/**
	 * The schemas for this plan.
	 *
	 * @return the plan schemas
	 */
	public Schemas getSchemas() {
		return this.schemas;
	}

	/**
	 * Indicates whether the service with this plan can be bound to applications. This is
	 * an optional field. If the value is <code>null</code>, the field will be omitted
	 * from the serialized JSON.
	 *
	 * @return true if the service with this plan may be bound
	 */
	public Boolean isBindable() {
		return bindable;
	}

	/**
	 * Indicates whether the plan can be limited by the non_basic_services_allowed field
	 * in a platform quota.
	 *
	 * @return true if the plan has no cost
	 */
	public Boolean isFree() {
		return this.free;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal Plan}.
	 *
	 * @return the builder
	 */
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

	/**
	 * Provides a fluent API for constructing a {@literal Plan}.
	 */
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

		/**
		 * An identifier used to correlate this plan in future requests to the catalog.
		 * This must be unique within the platform. Using a GUID is recommended.
		 *
		 * @param id the unique identifier of the plan
		 * @return the builder instance
		 */
		public PlanBuilder id(String id) {
			this.id = id;
			return this;
		}

		/**
		 * A CLI-friendly name of the plan that will appear in the catalog. The value
		 * should be all lowercase, with no spaces.
		 *
		 * @param name plan name
		 * @return the builder instance
		 */
		public PlanBuilder name(String name) {
			this.name = name;
			return this;
		}

		/**
		 * A user-friendly short description of the plan that will appear in the catalog.
		 *
		 * @param description plan description
		 * @return the builder instance
		 */
		public PlanBuilder description(String description) {
			this.description = description;
			return this;
		}

		/**
		 * A map of plan metadata to further describe the plan
		 *
		 * @param metadata plan metadata
		 * @return the builder instance
		 * @see <a href=
		 * "https://github.com/openservicebrokerapi/servicebroker/blob/master/profile.md#service-metadata">Service
		 * metadata conventions</a>
		 */
		public PlanBuilder metadata(Map<String, Object> metadata) {
			if (this.metadata == null) {
				this.metadata = new HashMap<>();
			}
			this.metadata.putAll(metadata);
			return this;
		}

		/**
		 * A key/value pair to add to the map of plan metadata
		 *
		 * @param key a unique key
		 * @param value the value
		 * @return the builder instance
		 * @see <a href=
		 * "https://github.com/openservicebrokerapi/servicebroker/blob/master/profile.md#service-metadata">Service
		 * metadata conventions</a>
		 */
		public PlanBuilder metadata(String key, Object value) {
			if (this.metadata == null) {
				this.metadata = new HashMap<>();
			}
			this.metadata.put(key, value);
			return this;
		}

		/**
		 * Whether the plan has a cost associated with it or not
		 *
		 * @param free true if the plan has no cost
		 * @return the build instance
		 */
		public PlanBuilder free(boolean free) {
			this.free = free;
			return this;
		}

		/**
		 * Indicates whether the service with this plan can be bound to applications. This
		 * is an optional field. If the value is <code>null</code>, the field will be
		 * omitted from the serialized JSON.
		 *
		 * @param bindable true if the service with this plan may be bound
		 * @return the binder instance
		 */
		public PlanBuilder bindable(boolean bindable) {
			this.bindable = bindable;
			return this;
		}

		/**
		 * The schemas for this plan
		 *
		 * @param schemas plan schemas
		 * @return the binder instance
		 */
		public PlanBuilder schemas(Schemas schemas) {
			this.schemas = schemas;
			return this;
		}

		/**
		 * Construct a {@link Plan} from the provided values.
		 *
		 * @return the newly constructed {@literal Plan}
		 */
		public Plan build() {
			return new Plan(id, name, description, metadata, free, bindable, schemas);
		}
	}
}
