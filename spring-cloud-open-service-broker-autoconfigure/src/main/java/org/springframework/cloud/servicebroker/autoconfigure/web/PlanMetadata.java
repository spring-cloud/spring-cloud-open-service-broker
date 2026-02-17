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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.CollectionUtils;

/**
 * Internal class for marshaling {@link ServiceBrokerProperties} configuration properties
 * that describes a service offered by this broker.
 *
 * @author Roy Clarkson
 * @see org.springframework.cloud.servicebroker.model.catalog.Plan
 */
public class PlanMetadata {

	private static final String COSTS_KEY = "costs";

	private static final String BULLETS_KEY = "bullets";

	private static final String DISPLAYNAME_KEY = "displayName";

	/**
	 * An array-of-objects that describes the costs of a service, in what currency, and
	 * the unit of measure. If there are multiple costs, all of them could be billed to
	 * the user (such as a monthly + usage costs at once).
	 */
	@NestedConfigurationProperty
	@Valid
	private final List<Cost> costs = new ArrayList<>();

	/**
	 * Features of this plan, to be displayed in a bulleted-list.
	 */
	private final List<String> bullets = new ArrayList<>();

	/**
	 * Name of the plan to be displayed to clients.
	 */
	private String displayName;

	/**
	 * Additional properties used to describe the plan.
	 */
	private final Map<String, Object> properties = new HashMap<>();

	/**
	 * Get the list of costs for this plan.
	 * @return the list of costs
	 */
	public List<Cost> getCosts() {
		return this.costs;
	}

	/**
	 * Set the list of costs for this plan.
	 * @param costs the list of costs
	 */
	public void setCosts(List<Cost> costs) {
		if (!CollectionUtils.isEmpty(costs)) {
			this.costs.clear();
			this.costs.addAll(costs);
		}
	}

	/**
	 * Get the list of bullet points describing plan features.
	 * @return the list of bullets
	 */
	public List<String> getBullets() {
		return this.bullets;
	}

	/**
	 * Set the list of bullet points describing plan features.
	 * @param bullets the list of bullets
	 */
	public void setBullets(List<String> bullets) {
		if (!CollectionUtils.isEmpty(bullets)) {
			this.bullets.clear();
			this.bullets.addAll(bullets);
		}
	}

	/**
	 * Get the display name of the plan.
	 * @return the display name
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Set the display name of the plan.
	 * @param displayName the display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Get the additional properties for this plan.
	 * @return the properties map
	 */
	public Map<String, Object> getProperties() {
		return this.properties;
	}

	/**
	 * Set additional properties for this plan.
	 * @param properties the properties map
	 */
	public void setProperties(Map<String, Object> properties) {
		if (!CollectionUtils.isEmpty(properties)) {
			this.properties.putAll(properties);
		}
	}

	/**
	 * Get the value of a property with the given key.
	 * @param key the key of the value to retrieve
	 * @return the value of the property, or {@literal null} if the key is not present in
	 * the bind resource
	 */
	public Object getProperty(String key) {
		return this.properties.get(key);
	}

	/**
	 * Converts this object into its corresponding model.
	 * @return a Map model
	 * @see org.springframework.cloud.servicebroker.model.catalog.Plan
	 */
	public Map<String, Object> toModel() {
		final List<Map<String, Object>> modelCosts = this.costs.stream()
			.map(Cost::toModel)
			.collect(Collectors.toList());

		final HashMap<String, Object> model = new HashMap<>();
		if (!CollectionUtils.isEmpty(modelCosts)) {
			model.put(COSTS_KEY, modelCosts);
		}
		if (!CollectionUtils.isEmpty(this.bullets)) {
			model.put(BULLETS_KEY, this.bullets);
		}
		if (this.displayName != null) {
			model.put(DISPLAYNAME_KEY, this.displayName);
		}
		if (!CollectionUtils.isEmpty(this.properties)) {
			model.putAll(this.properties);
		}
		return model;
	}

}
