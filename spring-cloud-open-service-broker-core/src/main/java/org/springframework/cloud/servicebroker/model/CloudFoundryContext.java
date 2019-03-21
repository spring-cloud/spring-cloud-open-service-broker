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

package org.springframework.cloud.servicebroker.model;

import java.util.Map;
import javax.validation.constraints.NotEmpty;

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

	private CloudFoundryContext(String organizationGuid, String spaceGuid, Map<String, Object> properties) {
		super(CLOUD_FOUNDRY_PLATFORM, properties);
		if (organizationGuid != null) {
			setOrganizationGuid(organizationGuid);
		}
		if (spaceGuid != null) {
			setSpaceGuid(spaceGuid);
		}
	}

	public String getOrganizationGuid() {
		return getStringProperty(ORGANIZATION_GUID_KEY);
	}

	@JsonProperty
	@NotEmpty
	private void setOrganizationGuid(String organizationGuid) {
		properties.put(ORGANIZATION_GUID_KEY, organizationGuid);
	}

	public String getSpaceGuid() {
		return getStringProperty(SPACE_GUID_KEY);
	}

	@JsonProperty
	@NotEmpty
	private void setSpaceGuid(String spaceGuid) {
		properties.put(SPACE_GUID_KEY, spaceGuid);
	}

	public static CloudFoundryContextBuilder builder() {
		return new CloudFoundryContextBuilder();
	}

	public static class CloudFoundryContextBuilder extends ContextBaseBuilder<CloudFoundryContext, CloudFoundryContextBuilder> {
		private String organizationGuid;
		private String spaceGuid;

		CloudFoundryContextBuilder() {
		}

		@Override
		protected CloudFoundryContextBuilder createBuilder() {
			return this;
		}

		public CloudFoundryContextBuilder organizationGuid(String organizationGuid) {
			this.organizationGuid = organizationGuid;
			return this;
		}

		public CloudFoundryContextBuilder spaceGuid(String spaceGuid) {
			this.spaceGuid = spaceGuid;
			return this;
		}

		public CloudFoundryContext build() {
			return new CloudFoundryContext(organizationGuid, spaceGuid, properties);
		}
	}
}
