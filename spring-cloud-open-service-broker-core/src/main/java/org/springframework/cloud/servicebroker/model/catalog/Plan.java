/*
 * Copyright 2002-2022 the original author or authors.
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

import jakarta.validation.constraints.NotEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.springframework.util.CollectionUtils;

/**
 * A service plan available for a ServiceDefinition
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href= "https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#service-plan-object">Open
 * 		Service Broker API specification</a>
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
public class Plan {

	@NotEmpty
	private final String id;

	@NotEmpty
	private final String name;

	@NotEmpty
	private final String description;

	private final Map<String, Object> metadata;

	private final Boolean free;

	private final Boolean bindable;

	private final Boolean planUpdateable;

	private final Schemas schemas;

	private final Integer maximumPollingDuration;

	private final MaintenanceInfo maintenanceInfo;


	/**
	 * Construct a new {@link Plan}
	 */
	public Plan() {
		this(null, null, null, new HashMap<>(), null, null, null, null, null, null);
	}

	/**
	 * Construct a new {@link Plan}
	 *
	 * @param id the plan ID
	 * @param name the plan name
	 * @param description the plan description
	 * @param metadata the plan metadata
	 * @param free true if the plan has no cost
	 * @param bindable true if the service with this plan may be bound
	 * @param planUpdateable true if the plan may be updated
	 * @param schemas the plan schemas
	 * @param maximumPollingDuration the maximum polling duration in seconds
	 * @param maintenanceInfo the maintentance information
	 */
	public Plan(String id, String name, String description, Map<String, Object> metadata, Boolean free,
			Boolean bindable, Boolean planUpdateable, Schemas schemas, Integer maximumPollingDuration,
			MaintenanceInfo maintenanceInfo) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.metadata = metadata;
		this.free = free;
		this.bindable = bindable;
		this.planUpdateable = planUpdateable;
		this.schemas = schemas;
		this.maximumPollingDuration = maximumPollingDuration;
		this.maintenanceInfo = maintenanceInfo;
	}

	/**
	 * An identifier used to correlate this plan in future requests to the catalog. This must be unique within the
	 * platform. Using a GUID is recommended.
	 *
	 * @return the plan ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * A CLI-friendly name of the plan that will appear in the catalog. The value should be all lowercase, with no
	 * spaces.
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
	 * @see <a href= "https://github.com/openservicebrokerapi/servicebroker/blob/master/profile.md#service-metadata">Service
	 * 		metadata conventions</a>
	 */
	public Map<String, Object> getMetadata() {
		return this.metadata;
	}

	/**
	 * Indicates whether the plan can be limited by the non_basic_services_allowed field in a platform quota.
	 *
	 * @return true if the plan has no cost
	 */
	public Boolean isFree() {
		return this.free;
	}

	/**
	 * Indicates whether the service with this plan can be bound to applications. This is an optional field. If the
	 * value is <code>null</code>, the field will be omitted from the serialized JSON.
	 *
	 * @return true if the service with this plan may be bound
	 */
	public Boolean isBindable() {
		return bindable;
	}

	/**
	 * Whether the Plan supports upgrade/downgrade/sidegrade to another version. This field is OPTIONAL. If specified,
	 * this takes precedence over the Service Offering's plan_updateable field. If not specified, the default is derived
	 * from the Service Offering. If the value is <code>null</code>, the field will be omitted from the serialized
	 * JSON.
	 *
	 * @return true if the plan may be updated
	 */
	public Boolean isPlanUpdateable() {
		return planUpdateable;
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
	 * A duration, in seconds, that the Platform SHOULD use as the Service's maximum polling duration. If the maximum
	 * polling duration is reached, the platform should cease polling and the operation state MUST be considered failed.
	 * If the value is <code>null</code>, the field will be omitted from the serialized JSON.
	 *
	 * @return the maximum polling duration
	 */
	public Integer getMaximumPollingDuration() {
		return this.maximumPollingDuration;
	}

	/**
	 * Maintenance information for a Service Instance which is provisioned using the Service Plan. If provided, a
	 * version string MUST be provided and platforms MAY use this when Provisioning or Updating a Service Instance.
	 *
	 * @return the maintenance info
	 */
	public MaintenanceInfo getMaintenanceInfo() {
		return maintenanceInfo;
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
		if (this == o) {
			return true;
		}
		if (!(o instanceof Plan)) {
			return false;
		}
		Plan plan = (Plan) o;
		return Objects.equals(id, plan.id) &&
				Objects.equals(name, plan.name) &&
				Objects.equals(description, plan.description) &&
				Objects.equals(metadata, plan.metadata) &&
				Objects.equals(free, plan.free) &&
				Objects.equals(bindable, plan.bindable) &&
				Objects.equals(planUpdateable, plan.planUpdateable) &&
				Objects.equals(schemas, plan.schemas) &&
				Objects.equals(maximumPollingDuration, plan.maximumPollingDuration) &&
				Objects.equals(maintenanceInfo, plan.maintenanceInfo);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(id, name, description, metadata, free, bindable,
				planUpdateable, schemas, maximumPollingDuration, maintenanceInfo);
	}

	@Override
	public String toString() {
		return "Plan{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", metadata=" + metadata +
				", free=" + free +
				", bindable=" + bindable +
				", planUpdateable=" + planUpdateable +
				", schemas=" + schemas +
				", maximumPollingDuration=" + maximumPollingDuration +
				", maintenanceInfo=" + maintenanceInfo +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@literal Plan}.
	 */
	public static final class PlanBuilder {

		private String id;

		private String name;

		private String description;

		private Map<String, Object> metadata;

		private Boolean free = true;

		private Boolean bindable;

		private Boolean planUpdateable;

		private Schemas schemas;

		private Integer maximumPollingDuration;

		private MaintenanceInfo maintenanceInfo;

		private PlanBuilder() {
		}

		/**
		 * An identifier used to correlate this plan in future requests to the catalog. This must be unique within the
		 * platform. Using a GUID is recommended.
		 *
		 * @param id the unique identifier of the plan
		 * @return the builder instance
		 */
		public PlanBuilder id(String id) {
			this.id = id;
			return this;
		}

		/**
		 * A CLI-friendly name of the plan that will appear in the catalog. The value should be all lowercase, with no
		 * spaces.
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
		 * @see <a href= "https://github.com/openservicebrokerapi/servicebroker/blob/master/profile.md#service-metadata">Service
		 * 		metadata conventions</a>
		 */
		public PlanBuilder metadata(Map<String, Object> metadata) {
			if (CollectionUtils.isEmpty(metadata)) {
				return this;
			}
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
		 * @see <a href= "https://github.com/openservicebrokerapi/servicebroker/blob/master/profile.md#service-metadata">Service
		 * 		metadata conventions</a>
		 */
		public PlanBuilder metadata(String key, Object value) {
			if (key == null || value == null) {
				return this;
			}
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
		 * @return the builder instance
		 */
		public PlanBuilder free(Boolean free) {
			this.free = free;
			return this;
		}

		/**
		 * Indicates whether the service with this plan can be bound to applications. This is an optional field. If the
		 * value is <code>null</code>, the field will be omitted from the serialized JSON.
		 *
		 * @param bindable true if the service with this plan may be bound
		 * @return the builder instance
		 */
		public PlanBuilder bindable(Boolean bindable) {
			this.bindable = bindable;
			return this;
		}

		/**
		 * Indicates whether the the plan can be updated. This is an optional field. If the value is
		 * <code>null</code>, the field will be omitted from the serialized JSON.
		 *
		 * @param planUpdateable true if the service with this plan may be bound
		 * @return the builder instance
		 */
		public PlanBuilder planUpdateable(Boolean planUpdateable) {
			this.planUpdateable = planUpdateable;
			return this;
		}

		/**
		 * The schemas for this plan
		 *
		 * @param schemas plan schemas
		 * @return the builder instance
		 */
		public PlanBuilder schemas(Schemas schemas) {
			this.schemas = schemas;
			return this;
		}

		/**
		 * A duration, in seconds, that the Platform SHOULD use as the Service's maximum polling duration. If the value
		 * is <code>null</code>, the field will be omitted from the serialized JSON.
		 *
		 * @param maximumPollingDuration the maximum polling duration
		 * @return the builder instance
		 */
		public PlanBuilder maximumPollingDuration(Integer maximumPollingDuration) {
			this.maximumPollingDuration = maximumPollingDuration;
			return this;
		}

		/**
		 * Maintenance information for a Service Instance which is provisioned using the Service Plan. If provided a
		 * version string MUST be provided and platforms MAY use this when Provisioning or Updating a Service Instance.
		 *
		 * @param maintenanceInfo the maintenanceInfo
		 * @return the builder instance
		 */
		public PlanBuilder maintenanceInfo(MaintenanceInfo maintenanceInfo) {
			this.maintenanceInfo = maintenanceInfo;
			return this;
		}

		/**
		 * Construct a {@link Plan} from the provided values.
		 *
		 * @return the newly constructed {@literal Plan}
		 */
		public Plan build() {
			return new Plan(id, name, description, metadata, free, bindable,
					planUpdateable, schemas, maximumPollingDuration, maintenanceInfo);
		}

	}

}
