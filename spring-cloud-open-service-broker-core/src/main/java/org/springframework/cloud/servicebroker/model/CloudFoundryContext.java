/*
 * Copyright 2002-2023 the original author or authors.
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

package org.springframework.cloud.servicebroker.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.springframework.util.CollectionUtils;

/**
 * Cloud Foundry specific contextual information under which the service instance is to be provisioned or updated.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class CloudFoundryContext extends Context {

	/**
	 * Cloud Foundry platform key
	 */
	public static final String CLOUD_FOUNDRY_PLATFORM = "cloudfoundry";

	/**
	 * Organization GUID key
	 */
	public static final String ORGANIZATION_GUID_KEY = "organizationGuid";

	/**
	 * Organization Name key
	 */
	public static final String ORGANIZATION_NAME_KEY = "organizationName";

	/**
	 * Organization Annotations key
	 */
	public static final String ORGANIZATION_ANNOTATIONS_KEY = "organizationAnnotations";

	/**
	 * Space GUID key
	 */
	public static final String SPACE_GUID_KEY = "spaceGuid";

	/**
	 * Space Name key
	 */
	public static final String SPACE_NAME_KEY = "spaceName";

	/**
	 * Space Annotations key
	 */
	public static final String SPACE_ANNOTATIONS_KEY = "spaceAnnotations";

	/**
	 * Instance Name key
	 */
	public static final String INSTANCE_NAME_KEY = "instanceName";

	private CloudFoundryContext() {
		super(CLOUD_FOUNDRY_PLATFORM, null);
	}

	/**
	 * Create a new CloudFoundryContext
	 *
	 * @param organizationGuid the organization GUID
	 * @param organizationName the organization name
	 * @param spaceGuid the space GUID
	 * @param spaceName the space name
	 * @param instanceName the instance name
	 * @param properties additional properties
	 */
	public CloudFoundryContext(String organizationGuid, String organizationName, String spaceGuid, String spaceName,
			String instanceName, Map<String, Object> properties) {
		super(CLOUD_FOUNDRY_PLATFORM, properties);
		setOrganizationGuid(organizationGuid);
		setOrganizationName(organizationName);
		setSpaceGuid(spaceGuid);
		setSpaceName(spaceName);
		setInstanceName(instanceName);
	}

	/**
	 * Create a new CloudFoundryContext
	 *
	 * @param organizationGuid the organization GUID
	 * @param organizationName the organization name
	 * @param organizationAnnotations organization annotations
	 * @param spaceGuid the space GUID
	 * @param spaceName the space name
	 * @param spaceAnnotations the space annotations
	 * @param instanceName the instance name
	 * @param properties additional properties
	 */
	public CloudFoundryContext(String organizationGuid, String organizationName,
			Map<String, Object> organizationAnnotations, String spaceGuid, String spaceName,
			Map<String, Object> spaceAnnotations, String instanceName, Map<String, Object> properties) {
		super(CLOUD_FOUNDRY_PLATFORM, properties);
		setOrganizationGuid(organizationGuid);
		setOrganizationName(organizationName);
		setOrganizationAnnotations(organizationAnnotations);
		setSpaceGuid(spaceGuid);
		setSpaceName(spaceName);
		setSpaceAnnotations(spaceAnnotations);
		setInstanceName(instanceName);
	}

	/**
	 * Avoid polluting the serialized context with duplicated keys
	 *
	 * @return a map of properties
	 */
	@JsonAnyGetter
	public Map<String, Object> getSerializableProperties() {
		HashMap<String, Object> properties = new HashMap<>(super.getProperties());
		properties.remove(ORGANIZATION_GUID_KEY);
		properties.remove(ORGANIZATION_NAME_KEY);
		properties.remove(ORGANIZATION_ANNOTATIONS_KEY);
		properties.remove(SPACE_GUID_KEY);
		properties.remove(SPACE_NAME_KEY);
		properties.remove(SPACE_ANNOTATIONS_KEY);
		properties.remove(INSTANCE_NAME_KEY);
		properties.remove(Context.PLATFORM_KEY);
		return properties;
	}

	/**
	 * Retrieve the organization GUID from the collection of platform properties
	 *
	 * @return the organization GUID
	 */
	@JsonProperty
	public String getOrganizationGuid() {
		return getStringProperty(ORGANIZATION_GUID_KEY);
	}

	private void setOrganizationGuid(String organizationGuid) {
		setStringProperty(ORGANIZATION_GUID_KEY, organizationGuid);
	}

	/**
	 * Retrieve the organization name from the collection of platform properties
	 *
	 * @return the organization name
	 */
	@JsonProperty
	public String getOrganizationName() {
		return getStringProperty(ORGANIZATION_NAME_KEY);
	}

	private void setOrganizationName(String organizationName) {
		setStringProperty(ORGANIZATION_NAME_KEY, organizationName);
	}

	/**
	 * Retrieve the organization annotations from the collection of platform properties
	 *
	 * @return the organization annotations
	 */
	@JsonProperty
	public Map<String, Object> getOrganizationAnnotations() {
		return getMapProperty(ORGANIZATION_ANNOTATIONS_KEY);
	}

	private void setOrganizationAnnotations(Map<String, Object> organizationAnnotations) {
		setMapProperty(ORGANIZATION_ANNOTATIONS_KEY, organizationAnnotations);
	}

	/**
	 * Retrieve the space GUID from the collection of platform properties
	 *
	 * @return the space GUID
	 */
	@JsonProperty
	public String getSpaceGuid() {
		return getStringProperty(SPACE_GUID_KEY);
	}

	private void setSpaceGuid(String spaceGuid) {
		setStringProperty(SPACE_GUID_KEY, spaceGuid);
	}

	/**
	 * Retrieve the space name from the collection of platform properties
	 *
	 * @return the space name
	 */
	@JsonProperty
	public String getSpaceName() {
		return getStringProperty(SPACE_NAME_KEY);
	}

	private void setSpaceName(String spaceName) {
		setStringProperty(SPACE_NAME_KEY, spaceName);
	}

	/**
	 * Retrieve the space annotations from the collection of platform properties
	 *
	 * @return the space annotations
	 */
	@JsonProperty
	public Map<String, Object> getSpaceAnnotations() {
		return getMapProperty(SPACE_ANNOTATIONS_KEY);
	}

	private void setSpaceAnnotations(Map<String, Object> spaceAnnotations) {
		setMapProperty(SPACE_ANNOTATIONS_KEY, spaceAnnotations);
	}

	/**
	 * Retrieve the instance name from the collection of platform properties
	 *
	 * @return the instance name
	 */
	@JsonProperty
	public String getInstanceName() {
		return getStringProperty(INSTANCE_NAME_KEY);
	}

	private void setInstanceName(String instanceName) {
		setStringProperty(INSTANCE_NAME_KEY, instanceName);
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
	public static final class CloudFoundryContextBuilder
			extends ContextBaseBuilder<CloudFoundryContext, CloudFoundryContextBuilder> {

		private String organizationGuid;

		private String organizationName;

		private final Map<String, Object> organizationAnnotations = new HashMap<>();

		private String spaceGuid;

		private String spaceName;

		private final Map<String, Object> spaceAnnotations = new HashMap<>();

		private String instanceName;

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
		 * Set the organization name
		 *
		 * @param organizationName the organization name
		 * @return the builder
		 */
		public CloudFoundryContextBuilder organizationName(String organizationName) {
			this.organizationName = organizationName;
			return this;
		}

		/**
		 * Set the organization annotations
		 *
		 * @param organizationAnnotations the organization annotations
		 * @return the builder
		 */
		public CloudFoundryContextBuilder organizationAnnotations(Map<String, Object> organizationAnnotations) {
			if (!CollectionUtils.isEmpty(organizationAnnotations)) {
				this.organizationAnnotations.clear();
				this.organizationAnnotations.putAll(organizationAnnotations);
			}
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

		/**
		 * Set the space name
		 *
		 * @param spaceName the space name
		 * @return the builder
		 */
		public CloudFoundryContextBuilder spaceName(String spaceName) {
			this.spaceName = spaceName;
			return this;
		}

		/**
		 * Set the space annotations
		 *
		 * @param spaceAnnotations the space annotations
		 * @return the builder
		 */
		public CloudFoundryContextBuilder spaceAnnotations(Map<String, Object> spaceAnnotations) {
			if (!CollectionUtils.isEmpty(spaceAnnotations)) {
				this.spaceAnnotations.clear();
				this.spaceAnnotations.putAll(spaceAnnotations);
			}
			return this;
		}

		/**
		 * Set the instance name
		 *
		 * @param instanceName the instance name
		 * @return the builder
		 */
		public CloudFoundryContextBuilder instanceName(String instanceName) {
			this.instanceName = instanceName;
			return this;
		}

		@Override
		public CloudFoundryContext build() {
			return new CloudFoundryContext(organizationGuid, organizationName, organizationAnnotations, spaceGuid,
					spaceName, spaceAnnotations, instanceName, properties);
		}

	}

}
