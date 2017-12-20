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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A service offered by this broker.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceDefinition {
	/**
	 * An identifier used to correlate this service in future requests to the catalog. This must be unique within
	 * a Cloud Foundry deployment. Using a GUID is recommended.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("id")
	private final String id;

	/**
	 * A CLI-friendly name of the service that will appear in the catalog. The value should be all lowercase,
	 * with no spaces.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("name")
	private final String name;

	/**
	 * A user-friendly short description of the service that will appear in the catalog.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("description")
	private final String description;

	/**
	 * Indicates whether the service can be bound to applications.
	 */
	@JsonSerialize
	@JsonProperty("bindable")
	private final boolean bindable;

	/**
	 * Indicates whether the service supports requests to update instances to use a different plan from the one
	 * used to provision a service instance.
	 */
	@JsonSerialize
	@JsonProperty("plan_updateable")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final Boolean planUpdateable;

	/**
	 * A list of plans for this service.
	 */
	@NotEmpty
	@JsonSerialize(nullsUsing = EmptyListSerializer.class)
	@JsonProperty("plans")
	private final List<Plan> plans;

	/**
	 * A list of tags to aid in categorizing and classifying services with similar characteristics.
	 */
	@JsonSerialize(nullsUsing = EmptyListSerializer.class)
	@JsonProperty("tags")
	private final List<String> tags;

	/**
	 * A map of metadata to further describe a service offering.
	 */
	@JsonSerialize(nullsUsing = EmptyMapSerializer.class)
	@JsonProperty("metadata")
	private final Map<String, Object> metadata;

	/**
	 * A list of permissions that the user would have to give the service, if they provision it. See
	 * {@link ServiceDefinitionRequires} for supported permissions.
	 */
	@JsonSerialize(nullsUsing = EmptyListSerializer.class)
	@JsonProperty("requires")
	private final List<String> requires;

	/**
	 * Data necessary to activate the Dashboard SSO feature for this service.
	 */
	@JsonSerialize
	@JsonProperty("dashboard_client")
	private final DashboardClient dashboardClient;

	private ServiceDefinition(String id, String name, String description, boolean bindable, Boolean planUpdateable,
							 List<Plan> plans, List<String> tags, Map<String, Object> metadata, List<String> requires,
							 DashboardClient dashboardClient) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.bindable = bindable;
		this.plans = plans;
		this.tags = tags;
		this.metadata = metadata;
		this.requires = requires;
		this.planUpdateable = planUpdateable;
		this.dashboardClient = dashboardClient;
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

	public boolean isBindable() {
		return this.bindable;
	}

	public boolean isPlanUpdateable() {
		return this.planUpdateable == null ? false : this.planUpdateable;
	}

	public List<Plan> getPlans() {
		return this.plans;
	}

	public List<String> getTags() {
		return this.tags;
	}

	public Map<String, Object> getMetadata() {
		return this.metadata;
	}

	public List<String> getRequires() {
		return this.requires;
	}

	public DashboardClient getDashboardClient() {
		return this.dashboardClient;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ServiceDefinition)) return false;
		ServiceDefinition that = (ServiceDefinition) o;
		return bindable == that.bindable &&
				planUpdateable == that.planUpdateable &&
				Objects.equals(id, that.id) &&
				Objects.equals(name, that.name) &&
				Objects.equals(description, that.description) &&
				Objects.equals(plans, that.plans) &&
				Objects.equals(tags, that.tags) &&
				Objects.equals(metadata, that.metadata) &&
				Objects.equals(requires, that.requires) &&
				Objects.equals(dashboardClient, that.dashboardClient);
	}

	public static ServiceDefinitionBuilder builder() {
		return new ServiceDefinitionBuilder();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, description, bindable, planUpdateable,
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
				", plans=" + plans +
				", tags=" + tags +
				", metadata=" + metadata +
				", requires=" + requires +
				", dashboardClient=" + dashboardClient +
				'}';
	}

	public static class ServiceDefinitionBuilder {
		private String id;
		private String name;
		private String description;
		private boolean bindable;
		private Boolean planUpdateable;
		private List<Plan> plans = new ArrayList<>();
		private List<String> tags = new ArrayList<>();
		private Map<String, Object> metadata = new HashMap<>();
		private List<String> requires = new ArrayList<>();
		private DashboardClient dashboardClient;

		ServiceDefinitionBuilder() {
		}

		public ServiceDefinitionBuilder id(String id) {
			this.id = id;
			return this;
		}

		public ServiceDefinitionBuilder name(String name) {
			this.name = name;
			return this;
		}

		public ServiceDefinitionBuilder description(String description) {
			this.description = description;
			return this;
		}

		public ServiceDefinitionBuilder bindable(boolean bindable) {
			this.bindable = bindable;
			return this;
		}

		public ServiceDefinitionBuilder planUpdateable(boolean planUpdateable) {
			this.planUpdateable = planUpdateable;
			return this;
		}

		public ServiceDefinitionBuilder plans(Plan... plans) {
			Collections.addAll(this.plans, plans);
			return this;
		}

		public ServiceDefinitionBuilder tags(String... tags) {
			Collections.addAll(this.tags, tags);
			return this;
		}

		public ServiceDefinitionBuilder metadata(Map<String, Object> metadata) {
			this.metadata.putAll(metadata);
			return this;
		}

		public ServiceDefinitionBuilder metadata(String key, Object value) {
			this.metadata.put(key, value);
			return this;
		}

		public ServiceDefinitionBuilder requires(String... requires) {
			Collections.addAll(this.requires, requires);
			return this;
		}

		public ServiceDefinitionBuilder dashboardClient(DashboardClient dashboardClient) {
			this.dashboardClient = dashboardClient;
			return this;
		}

		public ServiceDefinition build() {
			return new ServiceDefinition(id, name, description, bindable, planUpdateable, plans, tags, metadata, requires, dashboardClient);
		}
	}
}
