/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinitionRequires;

/**
 * Internal class for marshaling {@link ServiceBrokerProperties} configuration properties
 * that describes a service offered by this broker.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition
 */
class ServiceDefinition {

	/**
	 * An identifier used to correlate this service in future requests to the catalog. This must be unique within
	 * the platform. Using a GUID is recommended.
	 */
	@NotEmpty
	private String id;

	/**
	 * A CLI-friendly name of the service that will appear in the catalog. The value should be all lowercase,
	 * with no spaces.
	 */
	@NotEmpty
	private String name;

	/**
	 * A user-friendly short description of the service that will appear in the catalog.
	 */
	@NotEmpty
	private String description;

	/**
	 * Indicates whether the service can be bound to applications.
	 */
	private Boolean bindable = false;

	/**
	 * Indicates whether the service supports requests to update instances to use a different plan from the one
	 * used to provision a service instance.
	 */
	private Boolean planUpdateable = false;

	/**
	 * Indicates whether the service broker supports retrieving service instances.
	 */
	private Boolean instancesRetrievable = false;

	/**
	 * Indicates whether the service broker supports retrieving service bindings.
	 */
	private Boolean bindingsRetrievable = false;

	/**
	 * A list of tags to aid in categorizing and classifying services with similar characteristics.
	 */
	private final List<String> tags = new ArrayList<>();

	/**
	 * A map of metadata to further describe a service offering.
	 */
	private final Map<String, Object> metadata = new HashMap<>();

	/**
	 * A list of permissions that the user would have to give the service, if they provision it. See
	 * {@link ServiceDefinitionRequires} for supported permissions.
	 */
	private final List<String> requires = new ArrayList<>();

	/**
	 * Data necessary to activate the Dashboard SSO feature for this service.
	 */
	@NestedConfigurationProperty
	private DashboardClient dashboardClient;

	/**
	 * A list of plans for this service.
	 */
	@NestedConfigurationProperty
	@NotEmpty
	private final List<Plan> plans = new ArrayList<>();

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean isBindable() {
		return this.bindable;
	}

	public void setBindable(Boolean bindable) {
		this.bindable = bindable;
	}

	public Boolean isPlanUpdateable() {
		return this.planUpdateable;
	}

	public void setPlanUpdateable(Boolean planUpdateable) {
		this.planUpdateable = planUpdateable;
	}

	public Boolean isInstancesRetrievable() {
		return this.instancesRetrievable;
	}

	public void setInstancesRetrievable(Boolean instancesRetrievable) {
		this.instancesRetrievable = instancesRetrievable;
	}

	public Boolean isBindingsRetrievable() {
		return this.bindingsRetrievable;
	}

	public void setBindingsRetrievable(Boolean bindingsRetrievable) {
		this.bindingsRetrievable = bindingsRetrievable;
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

	public void setDashboardClient(DashboardClient dashboardClient) {
		this.dashboardClient = dashboardClient;
	}

	public List<Plan> getPlans() {
		return this.plans;
	}

	/**
	 * Convert this object to its corresponding model
	 *
	 * @return a converted ServiceDefinition
	 * @see org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition
	 */
	public org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition toModel() {
		List<org.springframework.cloud.servicebroker.model.catalog.Plan> modelPlans =
				this.plans.stream()
						.map(Plan::toModel)
						.collect(Collectors.toList());

		return org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition.builder()
				.id(this.id)
				.name(this.name)
				.description(this.description)
				.bindable(this.bindable)
				.planUpdateable(this.planUpdateable)
				.instancesRetrievable(this.instancesRetrievable)
				.bindingsRetrievable(this.bindingsRetrievable)
				.tags(this.tags)
				.metadata(this.metadata)
				.requires(this.requires)
				.dashboardClient(this.dashboardClient != null ? this.dashboardClient.toModel() : null)
				.plans(modelPlans)
				.build();
	}
}
