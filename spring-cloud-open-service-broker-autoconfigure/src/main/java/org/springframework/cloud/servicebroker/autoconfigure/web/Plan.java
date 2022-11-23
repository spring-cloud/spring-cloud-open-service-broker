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

package org.springframework.cloud.servicebroker.autoconfigure.web;

import jakarta.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Internal class for marshaling {@link ServiceBrokerProperties} configuration properties that describe a service plan
 * available for a {@link ServiceDefinition}.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see org.springframework.cloud.servicebroker.model.catalog.Plan
 */
public class Plan {

	/**
	 * An identifier used to correlate this plan in future requests to the catalog. This must be unique within the
	 * platform. Using a GUID is recommended.
	 */
	@NotEmpty
	private String id;

	/**
	 * A CLI-friendly name of the plan that will appear in the catalog. The value should be all lowercase, with no
	 * spaces.
	 */
	@NotEmpty
	private String name;

	/**
	 * A user-friendly short description of the plan that will appear in the catalog.
	 */
	@NotEmpty
	private String description;

	/**
	 * The metadata for this plan
	 */
	@NestedConfigurationProperty
	private PlanMetadata metadata;

	/**
	 * The schemas for this plan.
	 */
	@NestedConfigurationProperty
	private Schemas schemas;

	/**
	 * Indicates whether the service with this plan can be bound to applications. This is an optional field. If the
	 * value is <code>null</code>, the field will be omitted from the serialized JSON.
	 */
	private Boolean bindable;

	/**
	 * Indicates whether the plan can be limited by the non_basic_services_allowed field in a platform quota.
	 */
	private Boolean free;

	/**
	 * Indicates whether the plan can be updated. This is an optional field. If the value is <code>null</code>, the
	 * field will be omitted from the serialized JSON.
	 */
	private Boolean planUpdateable;

	/**
	 * A duration, in seconds, that the Platform SHOULD use as the Service's maximum polling duration. If the value is
	 * <code>null</code>, the field will be omitted from the serialized JSON.
	 */
	private Integer maximumPollingDuration;

	/**
	 * Maintenance information for a Service Instance which is provisioned using the Service Plan. If provided, a
	 * version string MUST be provided and platforms MAY use this when Provisioning or Updating a Service Instance.
	 */
	private MaintenanceInfo maintenanceInfo;

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

	public PlanMetadata getMetadata() {
		return this.metadata;
	}

	public void setMetadata(PlanMetadata metadata) {
		this.metadata = metadata;
	}

	public Schemas getSchemas() {
		return this.schemas;
	}

	public void setSchemas(Schemas schemas) {
		this.schemas = schemas;
	}

	public Boolean isBindable() {
		return this.bindable;
	}

	public void setBindable(Boolean bindable) {
		this.bindable = bindable;
	}

	public Boolean isFree() {
		return this.free;
	}

	public void setFree(Boolean free) {
		this.free = free;
	}

	public Boolean isPlanUpdateable() {
		return this.planUpdateable;
	}

	public void setPlanUpdateable(Boolean planUpdateable) {
		this.planUpdateable = planUpdateable;
	}

	public Integer getMaximumPollingDuration() {
		return this.maximumPollingDuration;
	}

	public void setMaximumPollingDuration(Integer maximumPollingDuration) {
		this.maximumPollingDuration = maximumPollingDuration;
	}

	public MaintenanceInfo getMaintenanceInfo() {
		return maintenanceInfo;
	}

	public void setMaintenanceInfo(MaintenanceInfo maintenanceInfo) {
		this.maintenanceInfo = maintenanceInfo;
	}

	/**
	 * Converts this object into its corresponding model
	 *
	 * @return a Plan model
	 * @see org.springframework.cloud.servicebroker.model.catalog.Plan
	 */
	public org.springframework.cloud.servicebroker.model.catalog.Plan toModel() {
		return org.springframework.cloud.servicebroker.model.catalog.Plan.builder()
				.id(this.id)
				.name(this.name)
				.description(this.description)
				.bindable(this.bindable)
				.free(this.free)
				.planUpdateable(this.planUpdateable)
				.schemas(this.schemas == null ? null : this.schemas.toModel())
				.metadata(this.metadata == null ? null : this.metadata.toModel())
				.maximumPollingDuration(this.maximumPollingDuration)
				.maintenanceInfo(this.maintenanceInfo == null ? null : this.maintenanceInfo.toModel())
				.build();
	}

}
