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

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Map;
import java.util.Objects;

/**
 * Cloud Foundry specific contextual information under which the service instance is to be provisioned or updated.
 *
 * @author Scott Frederick
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public final class CloudFoundryContext extends Context {
	public static final String CLOUD_FOUNDRY_PLATFORM = "cloudfoundry";

	/**
	 * The Cloud Controller GUID of the organization for which the operation is requested.
	 */
	@NotEmpty
	private final String organizationGuid;

	/**
	 * The Cloud Controller GUID of the space for which the operation is requested.
	 */
	@NotEmpty
	private final String spaceGuid;

	private CloudFoundryContext() {
		super(CLOUD_FOUNDRY_PLATFORM, null);
		this.organizationGuid = null;
		this.spaceGuid = null;
	}

	private CloudFoundryContext(String organizationGuid, String spaceGuid, Map<String, Object> properties) {
		super(CLOUD_FOUNDRY_PLATFORM, properties);
		this.organizationGuid = organizationGuid;
		this.spaceGuid = spaceGuid;
	}

	public String getOrganizationGuid() {
		return this.organizationGuid;
	}

	public String getSpaceGuid() {
		return this.spaceGuid;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CloudFoundryContext)) return false;
		if (!super.equals(o)) return false;
		CloudFoundryContext that = (CloudFoundryContext) o;
		return Objects.equals(organizationGuid, that.organizationGuid) &&
				Objects.equals(spaceGuid, that.spaceGuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), organizationGuid, spaceGuid);
	}

	@Override
	public String toString() {
		return super.toString() +
				"CloudFoundryContext{" +
				"organizationGuid='" + organizationGuid + '\'' +
				", spaceGuid='" + spaceGuid + '\'' +
				'}';
	}
}
