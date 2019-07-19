/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.cloud.servicebroker.model;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Cloud Foundry specific contextual information under which the service instance is to be provisioned or updated.
 *
 * @author Scott Frederick
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public final class CloudFoundryContext extends Context {

	public static final String CLOUD_FOUNDRY_PLATFORM = "cloudfoundry";

	public static final String ORGANIZATION_GUID_KEY = "organizationGuid";

	public static final String SPACE_GUID_KEY = "spaceGuid";

	private CloudFoundryContext() {
		super(CLOUD_FOUNDRY_PLATFORM, null);
	}

	/**
	 * Create a new CloudFoundryContext
	 * @param organizationGuid the organization GUID
	 * @param spaceGuid the space GUID
	 * @param properties additional properties
	 */
	public CloudFoundryContext(String organizationGuid, String spaceGuid, Map<String, Object> properties) {
		super(CLOUD_FOUNDRY_PLATFORM, properties);
		if (organizationGuid != null) {
			setOrganizationGuid(organizationGuid);
		}
		if (spaceGuid != null) {
			setSpaceGuid(spaceGuid);
		}
	}

	/**
	 * Retrieve the organization GUID from the collection of platform properties
	 *
	 * @return the organization GUID
	 */
	public String getOrganizationGuid() {
		return getStringProperty(ORGANIZATION_GUID_KEY);
	}

    /**
     * Avoid polluting the serialized context with duplicated keys
	 * @return a map of properties
     */
	@JsonAnyGetter
    public Map<String, Object> getSerializableProperties() {
		HashMap<String, Object> properties = new HashMap<>(super.getProperties());
		properties.remove(ORGANIZATION_GUID_KEY);
		properties.remove(SPACE_GUID_KEY);
		properties.remove(Context.PLATFORM_KEY);
		return properties;
	}

	@JsonProperty
	@NotEmpty
	private void setOrganizationGuid(String organizationGuid) {
		properties.put(ORGANIZATION_GUID_KEY, organizationGuid);
	}

	/**
	 * Retrieve the space GUID from the collection of platform properties
	 *
	 * @return the space GUID
	 */
	public String getSpaceGuid() {
		return getStringProperty(SPACE_GUID_KEY);
	}

	@JsonProperty
	@NotEmpty
	private void setSpaceGuid(String spaceGuid) {
		properties.put(SPACE_GUID_KEY, spaceGuid);
	}

	/**
	 * Builder for constructing a {@link CloudFoundryContext}
	 *
	 * @return the builder
	 */
	public static CloudFoundryContextBuilder builder() {
		return new CloudFoundryContextBuilder();
	}

	/**
	 * Provides a fluent API for constructing a {@link CloudFoundryContext}
	 */
	public static class CloudFoundryContextBuilder extends ContextBaseBuilder<CloudFoundryContext, CloudFoundryContextBuilder> {

		private String organizationGuid;

		private String spaceGuid;

		private CloudFoundryContextBuilder() {
			super();
		}

		@Override
		protected CloudFoundryContextBuilder createBuilder() {
			return this;
		}

		/**
		 * Set the organization GUID
		 *
		 * @param organizationGuid the organization GUID
		 * @return the builder
		 */
		public CloudFoundryContextBuilder organizationGuid(String organizationGuid) {
			this.organizationGuid = organizationGuid;
			return this;
		}

		/**
		 * Set the space GUID
		 *
		 * @param spaceGuid the space GUID
		 * @return the builder
		 */
		public CloudFoundryContextBuilder spaceGuid(String spaceGuid) {
			this.spaceGuid = spaceGuid;
			return this;
		}

		@Override
		public CloudFoundryContext build() {
			return new CloudFoundryContext(organizationGuid, spaceGuid, properties);
		}
	}
}
