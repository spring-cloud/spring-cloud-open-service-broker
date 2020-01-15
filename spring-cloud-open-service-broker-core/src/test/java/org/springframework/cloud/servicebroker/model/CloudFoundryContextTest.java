/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model;

import com.jayway.jsonpath.DocumentContext;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonPathAssert;
import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.CLOUD_FOUNDRY_PLATFORM;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.INSTANCE_NAME_KEY;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.ORGANIZATION_GUID_KEY;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.ORGANIZATION_NAME_KEY;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.SPACE_GUID_KEY;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.SPACE_NAME_KEY;

class CloudFoundryContextTest {

	@Test
	void emptyContext() {
		CloudFoundryContext context = CloudFoundryContext.builder()
				.build();

		assertThat(context.getPlatform()).isEqualTo(CLOUD_FOUNDRY_PLATFORM);

		assertThat(context.getOrganizationGuid()).isNull();
		assertThat(context.getSpaceGuid()).isNull();

		assertThat(context.getProperties()).isEmpty();
	}

	@Test
	void populatedContext() {
		CloudFoundryContext context = CloudFoundryContext.builder()
				.property("key1", "value1")
				.organizationGuid("org-guid")
				.organizationName("org-name")
				.spaceGuid("space-guid")
				.spaceName("space-name")
				.instanceName("instance-name")
				.property("key2", "value2")
				.build();

		assertThat(context.getPlatform()).isEqualTo(CLOUD_FOUNDRY_PLATFORM);

		assertThat(context.getOrganizationGuid()).isEqualTo("org-guid");
		assertThat(context.getOrganizationName()).isEqualTo("org-name");
		assertThat(context.getSpaceGuid()).isEqualTo("space-guid");
		assertThat(context.getSpaceName()).isEqualTo("space-name");
		assertThat(context.getInstanceName()).isEqualTo("instance-name");
		assertThat(context.getProperty(ORGANIZATION_GUID_KEY)).isEqualTo("org-guid");
		assertThat(context.getProperty(ORGANIZATION_NAME_KEY)).isEqualTo("org-name");
		assertThat(context.getProperty(SPACE_GUID_KEY)).isEqualTo("space-guid");
		assertThat(context.getProperty(SPACE_NAME_KEY)).isEqualTo("space-name");
		assertThat(context.getProperty(INSTANCE_NAME_KEY)).isEqualTo("instance-name");

		assertThat(context.getProperty("key1")).isEqualTo("value1");
		assertThat(context.getProperty("key2")).isEqualTo("value2");

		//ensure we don't break super class contract
		assertThat(context.getProperties()).containsOnly(
				entry("key1", "value1"),
				entry("key2", "value2"),
				entry(ORGANIZATION_GUID_KEY, "org-guid"),
				entry(ORGANIZATION_NAME_KEY, "org-name"),
				entry(SPACE_GUID_KEY, "space-guid"),
				entry(SPACE_NAME_KEY, "space-name"),
				entry(INSTANCE_NAME_KEY, "instance-name"));
	}

	/**
	 * OSB API 2.14 and older context properties
	 */
	@Test
	void partialContextIsSerialized() {
		Context context = CloudFoundryContext.builder().organizationGuid("org-guid")
				.spaceGuid("space-guid").build();

		DocumentContext json = JsonUtils.toJsonPath(context);

		JsonPathAssert.assertThat(json).hasPath("$.organization_guid").isEqualTo("org-guid");
		JsonPathAssert.assertThat(json).hasPath("$.space_guid").isEqualTo("space-guid");
		JsonPathAssert.assertThat(json).hasPath("$.platform").isEqualTo("cloudfoundry");
		// detect any double serialization due to inheritance and naming mismatch
		JsonPathAssert.assertThat(json).hasMapAtPath("$").hasSize(3);
		JsonUtils.assertThatJsonHasExactNumberOfProperties(context, 3 + 1); //still have duplicated platform property
	}

	/**
	 * OSB API 2.15 and newer supports additional properties in the CloudFoundry context
	 */
	@Test
	void fullContextIsSerialized() {
		Context context = CloudFoundryContext.builder()
				.organizationGuid("org-guid")
				.organizationName("org-name")
				.spaceGuid("space-guid")
				.spaceName("space-name")
				.instanceName("instance-name")
				.build();

		DocumentContext json = JsonUtils.toJsonPath(context);

		JsonPathAssert.assertThat(json).hasPath("$.organization_guid").isEqualTo("org-guid");
		JsonPathAssert.assertThat(json).hasPath("$.organization_name").isEqualTo("org-name");
		JsonPathAssert.assertThat(json).hasPath("$.space_guid").isEqualTo("space-guid");
		JsonPathAssert.assertThat(json).hasPath("$.space_name").isEqualTo("space-name");
		JsonPathAssert.assertThat(json).hasPath("$.instance_name").isEqualTo("instance-name");
		JsonPathAssert.assertThat(json).hasPath("$.platform").isEqualTo("cloudfoundry");
		// detect any double serialization due to inheritance and naming mismatch
		JsonPathAssert.assertThat(json).hasMapAtPath("$").hasSize(6);
		JsonUtils.assertThatJsonHasExactNumberOfProperties(context, 6 + 1); //still have duplicated platform property
	}

}
