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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * A service offered by this broker.
 *
 * @see <a href=
 * "https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#service-object">Open
 * Service Broker API specification</a>
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceDefinition {

	@NotEmpty
	private final String id;

	@NotEmpty
	private final String name;

	@NotEmpty
	private final String description;

	private final boolean bindable;

	private final Boolean planUpdateable;

	private final Boolean instancesRetrievable;

	private final Boolean bindingsRetrievable;

	@NotEmpty
	private final List<Plan> plans;

	private final List<String> tags;

	private final Map<String, Object> metadata;

	private final List<String> requires;

	private final DashboardClient dashboardClient;

	ServiceDefinition(String id, String name, String description, boolean bindable, Boolean planUpdateable,
							 Boolean instancesRetrievable, Boolean bindingsRetrievable,
							 List<Plan> plans, List<String> tags, Map<String, Object> metadata, List<String> requires,
							 DashboardClient dashboardClient) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.bindable = bindable;
		this.planUpdateable = planUpdateable;
		this.instancesRetrievable = instancesRetrievable;
		this.bindingsRetrievable = bindingsRetrievable;
		this.plans = plans;
		this.tags = tags;
		this.metadata = metadata;
		this.requires = requires;
		this.dashboardClient = dashboardClient;
	}

	ServiceDefinition() {
		this(null, null, null, false, null, null, null, new ArrayList<>(),
				new ArrayList<>(), new HashMap<>(), new ArrayList<>(), null);
	}

	/**
	 * An identifier used to correlate this service in future requests to the catalog.
	 * This must be unique within the platform. Using a GUID is recommended.
	 *
	 * @return the service ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * A CLI-friendly name of the service that will appear in the catalog. The value
	 * should be all lowercase, with no spaces.
	 *
	 * @return the service name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * A user-friendly short description of the service that will appear in the catalog.
	 *
	 * @return the service description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Indicates whether the service can be bound to applications.
	 *
	 * @return true if the service may be bound
	 */
	public boolean isBindable() {
		return this.bindable;
	}

	/**
	 * Indicates whether the service supports requests to update instances to use a
	 * different plan from the one used to provision a service instance.
	 *
	 * @return true if the plan may be updated
	 */
	public Boolean isPlanUpdateable() {
		return this.planUpdateable;
	}

	/**
	 * Indicates whether the service broker supports retrieving service instances.
	 *
	 * @return true if the service instances may be retrieved
	 */
	public Boolean isInstancesRetrievable() {
		return this.instancesRetrievable;
	}

	/**
	 * Indicates whether the service broker supports retrieving service bindings.
	 *
	 * @return true if the service bindings may be retrieved
	 */
	public Boolean isBindingsRetrievable() {
		return this.bindingsRetrievable;
	}

	/**
	 * A list of plans for this service.
	 *
	 * @return the service plans
	 */
	public List<Plan> getPlans() {
		return this.plans;
	}

	/**
	 * A list of tags to aid in categorizing and classifying services with similar
	 * characteristics.
	 *
	 * @return the tags
	 */
	public List<String> getTags() {
		return this.tags;
	}

	/**
	 * A map of metadata to further describe a service offering.
	 *
	 * @return the service metadata
	 */
	public Map<String, Object> getMetadata() {
		return this.metadata;
	}

	/**
	 * A list of permissions that the user would have to give the service, if they
	 * provision it. See {@link ServiceDefinitionRequires} for supported permissions.
	 *
	 * @return the required permissions
	 */
	public List<String> getRequires() {
		return this.requires;
	}

	/**
	 * Data necessary to activate the Dashboard SSO feature for this service.
	 *
	 * @return the service dashboard URI
	 */
	public DashboardClient getDashboardClient() {
		return this.dashboardClient;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal ServiceDefinition}.
	 *
	 * @return the builder
	 */
	public static ServiceDefinitionBuilder builder() {
		return new ServiceDefinitionBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ServiceDefinition)) return false;
		ServiceDefinition that = (ServiceDefinition) o;
		return bindable == that.bindable &&
				Objects.equals(planUpdateable, that.planUpdateable) &&
				Objects.equals(instancesRetrievable, that.instancesRetrievable) &&
				Objects.equals(bindingsRetrievable, that.bindingsRetrievable) &&
				Objects.equals(id, that.id) &&
				Objects.equals(name, that.name) &&
				Objects.equals(description, that.description) &&
				Objects.equals(plans, that.plans) &&
				Objects.equals(tags, that.tags) &&
				Objects.equals(metadata, that.metadata) &&
				Objects.equals(requires, that.requires) &&
				Objects.equals(dashboardClient, that.dashboardClient);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(id, name, description, bindable, planUpdateable,
				instancesRetrievable, bindingsRetrievable,
				plans, tags, metadata, requires, dashboardClient);
	}

	@Override
	public String toString() {
		return "ServiceDefinition{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", bindable=" + bindable +
				", planUpdateable=" + planUpdateable +
				", instancesRetrievable=" + instancesRetrievable +
				", bindingsRetrievable=" + bindingsRetrievable +
				", plans=" + plans +
				", tags=" + tags +
				", metadata=" + metadata +
				", requires=" + requires +
				", dashboardClient=" + dashboardClient +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@literal ServiceDefinition}.
	 */
	public static class ServiceDefinitionBuilder {
		private String id;
		private String name;
		private String description;
		private boolean bindable;
		private Boolean planUpdateable;
		private Boolean instancesRetrievable;
		private Boolean bindingsRetrievable;
		private final List<Plan> plans = new ArrayList<>();
		private List<String> tags;
		private Map<String, Object> metadata;
		private List<String> requires;
		private DashboardClient dashboardClient;

		ServiceDefinitionBuilder() {
		}

		/**
		 * An identifier used to correlate this service in future requests to the catalog.
		 * This must be unique within the platform. Using a GUID is recommended.
		 *
		 * @param id the service ID
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder id(String id) {
			this.id = id;
			return this;
		}

		/**
		 * A CLI-friendly name of the service that will appear in the catalog. The value
		 * should be all lowercase, with no spaces.
		 *
		 * @param name the service name
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder name(String name) {
			this.name = name;
			return this;
		}

		/**
		 * A user-friendly short description of the service that will appear in the catalog.
		 *
		 * @param description the service description
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder description(String description) {
			this.description = description;
			return this;
		}

		/**
		 * Indicates whether the service can be bound to applications.
		 *
		 * @param bindable true if the service may be bound
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder bindable(boolean bindable) {
			this.bindable = bindable;
			return this;
		}

		/**
		 * Indicates whether the service supports requests to update instances to use a
		 * different plan from the one used to provision a service instance.
		 *
		 * @param planUpdateable true if the plan may be updated
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder planUpdateable(boolean planUpdateable) {
			this.planUpdateable = planUpdateable;
			return this;
		}

		/**
		 * Indicates whether the service broker supports retrieving service instances.
		 *
		 * @param instancesRetrievable true if the service instances may be retrieved
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder instancesRetrievable(boolean instancesRetrievable) {
			this.instancesRetrievable = instancesRetrievable;
			return this;
		}

		/**
		 * Indicates whether the service broker supports retrieving service bindings.
		 *
		 * @param bindingsRetrievable true if the service bindings may be retrieved
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder bindingsRetrievable(boolean bindingsRetrievable) {
			this.bindingsRetrievable = bindingsRetrievable;
			return this;
		}

		/**
		 * A list of plans for this service.
		 *
		 * @param plans the service plans
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder plans(Plan... plans) {
			Collections.addAll(this.plans, plans);
			return this;
		}

		/**
		 * A list of plans for this service.
		 *
		 * @param plans the service plans
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder plans(List<Plan> plans) {
			this.plans.addAll(plans);
			return this;
		}

		/**
		 * A list of tags to aid in categorizing and classifying services with similar
		 * characteristics.
		 *
		 * @param tags the tags
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder tags(String... tags) {
			if (this.tags == null) {
				this.tags = new ArrayList<>();
			}
			Collections.addAll(this.tags, tags);
			return this;
		}

		/**
		 * A list of tags to aid in categorizing and classifying services with similar
		 * characteristics.
		 *
		 * @param tags the tags
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder tags(List<String> tags) {
			if (this.tags == null) {
				this.tags = new ArrayList<>();
			}
			this.tags.addAll(tags);
			return this;
		}

		/**
		 * A map of metadata to further describe a service offering.
		 *
		 * @param metadata the service metadata
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder metadata(Map<String, Object> metadata) {
			if (this.metadata == null) {
				this.metadata = new HashMap<>();
			}
			this.metadata.putAll(metadata);
			return this;
		}

		/**
		 * A key/value pair to add to the map of metadata to further describe a service offering.
		 *
		 * @param key the unique key
		 * @param value the value
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder metadata(String key, Object value) {
			if (this.metadata == null) {
				this.metadata = new HashMap<>();
			}
			this.metadata.put(key, value);
			return this;
		}

		/**
		 * A list of permissions that the user would have to give the service, if they
		 * provision it. See {@link ServiceDefinitionRequires} for supported permissions.
		 *
		 * @param requires the required permissions
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder requires(String... requires) {
			if (this.requires == null) {
				this.requires = new ArrayList<>();
			}
			Collections.addAll(this.requires, requires);
			return this;
		}

		/**
		 * A list of permissions that the user would have to give the service, if they
		 * provision it. See {@link ServiceDefinitionRequires} for supported permissions.
		 *
		 * @param requires the required permissions
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder requires(List<String> requires) {
			if (this.requires == null) {
				this.requires = new ArrayList<>();
			}
			this.requires.addAll(requires);
			return this;
		}

		/**
		 * A list of permissions that the user would have to give the service, if they
		 * provision it. See {@link ServiceDefinitionRequires} for supported permissions.
		 *
		 * @param requires the required permissions
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder requires(ServiceDefinitionRequires... requires) {
			if (this.requires == null) {
				this.requires = new ArrayList<>();
			}
			this.requires.addAll(Arrays.stream(requires)
					.map(ServiceDefinitionRequires::toString)
					.collect(Collectors.toList()));
			return this;
		}

		/**
		 * Data necessary to activate the Dashboard SSO feature for this service.
		 *
		 * @param dashboardClient the service dashboard URI
		 * @return the binder instance
		 */
		public ServiceDefinitionBuilder dashboardClient(DashboardClient dashboardClient) {
			this.dashboardClient = dashboardClient;
			return this;
		}

		/**
		 * Construct a {@link ServiceDefinition} from the provided values.
		 *
		 * @return the newly constructed {@literal ServiceDefinition}
		 */
		public ServiceDefinition build() {
			return new ServiceDefinition(id, name, description, bindable, planUpdateable,
					instancesRetrievable, bindingsRetrievable,
					plans, tags, metadata, requires, dashboardClient);
		}
	}
}
