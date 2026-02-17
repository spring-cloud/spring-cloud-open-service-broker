/*
 * Copyright 2002-2025 the original author or authors.
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
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Internal class for marshaling {@link ServiceBrokerProperties} configuration properties
 * that describes a service offered by this broker.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition
 */
public class ServiceDefinition {

	/**
	 * An identifier used to correlate this service in future requests to the catalog.
	 * This must be unique within the platform. Using a GUID is recommended.
	 */
	@NotEmpty
	private String id;

	/**
	 * A CLI-friendly name of the service that will appear in the catalog. The value
	 * should be all lowercase, with no spaces.
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
	 * Indicates whether the service supports requests to update instances to use a
	 * different plan from the one used to provision a service instance.
	 */
	private Boolean planUpdateable;

	/**
	 * Indicates whether the service broker supports retrieving service instances.
	 */
	private Boolean instancesRetrievable;

	/**
	 * Indicates whether the service broker supports retrieving service bindings.
	 */
	private Boolean bindingsRetrievable;

	/**
	 * Specifies whether a Service Instance supports Update requests when contextual data
	 * for the Service Instance in the Platform changes.
	 */
	private Boolean allowContextUpdates;

	/**
	 * A list of tags to aid in categorizing and classifying services with similar
	 * characteristics.
	 */
	private final List<String> tags = new ArrayList<>();

	/**
	 * A map of metadata to further describe a service offering.
	 */
	@NestedConfigurationProperty
	@Valid
	private ServiceMetadata metadata;

	/**
	 * A list of permissions that the user would have to give the service, if they
	 * provision it.
	 *
	 * @see org.springframework.cloud.servicebroker.model.catalog.ServiceDefinitionRequires
	 * supported permissions
	 */
	private final List<String> requires = new ArrayList<>();

	/**
	 * Data necessary to activate the Dashboard SSO feature for this service.
	 */
	@NestedConfigurationProperty
	@Valid
	private DashboardClient dashboardClient;

	/**
	 * A list of plans for this service.
	 */
	@NestedConfigurationProperty
	@NotEmpty
	@Valid
	private final List<Plan> plans = new ArrayList<>();

	/**
	 * Get the service ID.
	 * @return the service ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Set the service ID.
	 * @param id the service ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the service name.
	 * @return the service name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the service name.
	 * @param name the service name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the service description.
	 * @return the service description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Set the service description.
	 * @param description the service description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get whether the service is bindable.
	 * @return true if bindable, false otherwise
	 */
	public Boolean isBindable() {
		return this.bindable;
	}

	/**
	 * Set whether the service is bindable.
	 * @param bindable true if bindable, false otherwise
	 */
	public void setBindable(Boolean bindable) {
		this.bindable = bindable;
	}

	/**
	 * Get whether the service supports plan updates.
	 * @return true if plan updates are supported, false otherwise
	 */
	public Boolean isPlanUpdateable() {
		return this.planUpdateable;
	}

	/**
	 * Set whether the service supports plan updates.
	 * @param planUpdateable true if plan updates are supported, false otherwise
	 */
	public void setPlanUpdateable(Boolean planUpdateable) {
		this.planUpdateable = planUpdateable;
	}

	/**
	 * Get whether service instances are retrievable.
	 * @return true if instances are retrievable, false otherwise
	 */
	public Boolean isInstancesRetrievable() {
		return this.instancesRetrievable;
	}

	/**
	 * Set whether service instances are retrievable.
	 * @param instancesRetrievable true if instances are retrievable, false otherwise
	 */
	public void setInstancesRetrievable(Boolean instancesRetrievable) {
		this.instancesRetrievable = instancesRetrievable;
	}

	/**
	 * Get whether service bindings are retrievable.
	 * @return true if bindings are retrievable, false otherwise
	 */
	public Boolean isBindingsRetrievable() {
		return this.bindingsRetrievable;
	}

	/**
	 * Set whether service bindings are retrievable.
	 * @param bindingsRetrievable true if bindings are retrievable, false otherwise
	 */
	public void setBindingsRetrievable(Boolean bindingsRetrievable) {
		this.bindingsRetrievable = bindingsRetrievable;
	}

	/**
	 * Get whether context updates are allowed.
	 * @return true if context updates are allowed, false otherwise
	 */
	public Boolean isAllowContextUpdates() {
		return this.allowContextUpdates;
	}

	/**
	 * Set whether context updates are allowed.
	 * @param allowContextUpdates true if context updates are allowed, false otherwise
	 */
	public void setAllowContextUpdates(Boolean allowContextUpdates) {
		this.allowContextUpdates = allowContextUpdates;
	}

	/**
	 * Get the list of service tags.
	 * @return the list of tags
	 */
	public List<String> getTags() {
		return this.tags;
	}

	/**
	 * Get the service metadata.
	 * @return the service metadata
	 */
	public ServiceMetadata getMetadata() {
		return this.metadata;
	}

	/**
	 * Set the service metadata.
	 * @param metadata the service metadata
	 */
	public void setMetadata(ServiceMetadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * Get the list of required permissions.
	 * @return the list of required permissions
	 */
	public List<String> getRequires() {
		return this.requires;
	}

	/**
	 * Get the dashboard client configuration.
	 * @return the dashboard client
	 */
	public DashboardClient getDashboardClient() {
		return this.dashboardClient;
	}

	/**
	 * Set the dashboard client configuration.
	 * @param dashboardClient the dashboard client
	 */
	public void setDashboardClient(DashboardClient dashboardClient) {
		this.dashboardClient = dashboardClient;
	}

	/**
	 * Get the list of service plans.
	 * @return the list of plans
	 */
	public List<Plan> getPlans() {
		return this.plans;
	}

	/**
	 * Convert this object to its corresponding model.
	 * @return a converted ServiceDefinition
	 * @see org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition
	 */
	public org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition toModel() {
		List<org.springframework.cloud.servicebroker.model.catalog.Plan> modelPlans = this.plans.stream()
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
			.allowContextUpdates(this.allowContextUpdates)
			.tags(this.tags)
			.metadata((this.metadata == null) ? null : this.metadata.toModel())
			.requires(this.requires)
			.dashboardClient((this.dashboardClient == null) ? null : this.dashboardClient.toModel())
			.plans(modelPlans)
			.build();
	}

}
