/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web;

/**
 * Internal class for marshaling {@link ServiceBrokerProperties} configuration properties that describes a maintenance
 * info available for a {@link Plan}.
 *
 * @author ilyavy
 * @see org.springframework.cloud.servicebroker.model.catalog.MaintenanceInfo
 */
public class MaintenanceInfo {

	/**
	 * The version of the maintenance update available for a plan.
	 */
	private String version;

	/**
	 * The description of the impact of the maintenance update.
	 */
	private String description;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Converts this object into its corresponding model.
	 *
	 * @return a MaintenanceInfo model
	 * @see org.springframework.cloud.servicebroker.model.catalog.MaintenanceInfo
	 */
	public org.springframework.cloud.servicebroker.model.catalog.MaintenanceInfo toModel() {
		return org.springframework.cloud.servicebroker.model.catalog.MaintenanceInfo.builder()
				.version(this.version)
				.description(this.description)
				.build();
	}

}
