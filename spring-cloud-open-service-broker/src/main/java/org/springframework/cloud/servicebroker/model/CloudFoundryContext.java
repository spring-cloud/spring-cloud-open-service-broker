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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Cloud Foundry specific contextual information under which the service instance is to be provisioned or updated.
 *
 * @author Scott Frederick
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public final class CloudFoundryContext extends Context {
	public static final String CLOUD_FOUNDRY_PLATFORM = "cloudfoundry";

	/**
	 * The Cloud Controller GUID of the organization for which the operation is requested.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("organization_guid")
	private final String organizationGuid;

	/**
	 * The Cloud Controller GUID of the space for which the operation is requested.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("space_guid")
	private final String spaceGuid;

	public CloudFoundryContext() {
		this.organizationGuid = null;
		this.spaceGuid = null;
	}

	public CloudFoundryContext(String organizationGuid, String spaceGuid) {
		this.organizationGuid = organizationGuid;
		this.spaceGuid = spaceGuid;
	}
}
