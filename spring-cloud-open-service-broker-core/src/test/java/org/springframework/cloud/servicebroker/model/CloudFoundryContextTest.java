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

package org.springframework.cloud.servicebroker.model;

import java.util.Collections;

import com.jayway.jsonpath.DocumentContext;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonPathAssert;
import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.CLOUD_FOUNDRY_PLATFORM;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.INSTANCE_NAME_KEY;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.ORGANIZATION_ANNOTATIONS_KEY;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.ORGANIZATION_GUID_KEY;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.ORGANIZATION_NAME_KEY;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.SPACE_ANNOTATIONS_KEY;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.SPACE_GUID_KEY;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.SPACE_NAME_KEY;

class CloudFoundryContextTest {

	@Test
	void emptyContext() {
		CloudFoundryContext context = CloudFoundryContext.builder()
				.build();

		assertThat(context.getPlatform()).isEqualTo(CLOUD_FOUNDRY_PLATFORM);
		assertThat(context.getOrganizationGuid()).isNull();
		assertThat(context.getOrganizationName()).isNull();
		assertThat(context.getOrganizationAnnotations()).isNull();
		assertThat(context.getSpaceGuid()).isNull();
		assertThat(context.getSpaceName()).isNull();
		assertThat(context.getSpaceAnnotations()).isNull();
		assertThat(context.getInstanceName()).isNull();
		assertThat(context.getProperties()).isEmpty();
	}

	@Test
	void populatedContext() {
		CloudFoundryContext context = CloudFoundryContext.builder()
				.property("key1", "value1")
				.organizationGuid("org-guid")
				.organizationName("org-name")
				.organizationAnnotations(Collections.singletonMap("prefix-here.org/name-here",
						"org-annotation-value-here"))
				.spaceGuid("space-guid")
				.spaceName("space-name")
				.spaceAnnotations(Collections.singletonMap("prefix-here.org/name-here",
						"space-annotation-value-here"))
				.instanceName("instance-name")
				.property("key2", "value2")
				.build();

		assertThat(context.getPlatform()).isEqualTo(CLOUD_FOUNDRY_PLATFORM);

		assertThat(context.getOrganizationGuid()).isEqualTo("org-guid");
		assertThat(context.getOrganizationName()).isEqualTo("org-name");
		assertThat(context.getOrganizationAnnotations()).isEqualTo(
				Collections.singletonMap("prefix-here.org/name-here", "org-annotation-value-here"));
		assertThat(context.getSpaceGuid()).isEqualTo("space-guid");
		assertThat(context.getSpaceName()).isEqualTo("space-name");
		assertThat(context.getSpaceAnnotations()).isEqualTo(
				Collections.singletonMap("prefix-here.org/name-here", "space-annotation-value-here"));
		assertThat(context.getInstanceName()).isEqualTo("instance-name");
		assertThat(context.getProperty("key1")).isEqualTo("value1");
		assertThat(context.getProperty("key2")).isEqualTo("value2");

		//ensure we don't break super class contract
		assertThat(context.getProperties()).containsOnly(
				entry("key1", "value1"),
				entry("key2", "value2"),
				entry(ORGANIZATION_GUID_KEY, "org-guid"),
				entry(ORGANIZATION_NAME_KEY, "org-name"),
				entry(ORGANIZATION_ANNOTATIONS_KEY, Collections.singletonMap("prefix-here.org/name-here",
						"org-annotation-value-here")),
				entry(SPACE_GUID_KEY, "space-guid"),
				entry(SPACE_NAME_KEY, "space-name"),
				entry(SPACE_ANNOTATIONS_KEY, Collections.singletonMap("prefix-here.org/name-here",
						"space-annotation-value-here")),
				entry(INSTANCE_NAME_KEY, "instance-name"));
	}

	/**
	 * OSB API 2.14 and older context properties
	 */
	@Test
	void contextIsSerialized214() {
		Context context = CloudFoundryContext.builder()
				.organizationGuid("abc-org-guid")
				.spaceGuid("abc-space-guid")
				.build();

		DocumentContext json = JsonUtils.toJsonPath(context);

		JsonPathAssert.assertThat(json).hasPath("$.organization_guid").isEqualTo("abc-org-guid");
		JsonPathAssert.assertThat(json).hasPath("$.space_guid").isEqualTo("abc-space-guid");
		JsonPathAssert.assertThat(json).hasPath("$.platform").isEqualTo("cloudfoundry");
		// detect any double serialization due to inheritance and naming mismatch
		JsonPathAssert.assertThat(json).hasMapAtPath("$").hasSize(3);
		JsonUtils.assertThatJsonHasExactNumberOfProperties(context, 3);
	}

	/**
	 * OSB API 2.15 and newer supports additional properties in the CloudFoundry context
	 */
	@Test
	void contextIsSerialized215() {
		Context context = CloudFoundryContext.builder()
				.organizationGuid("abc-org-guid")
				.organizationName("abc-org-name")
				.spaceGuid("abc-space-guid")
				.spaceName("abc-space-name")
				.instanceName("abc-instance-name")
				.build();

		DocumentContext json = JsonUtils.toJsonPath(context);

		JsonPathAssert.assertThat(json).hasPath("$.organization_guid").isEqualTo("abc-org-guid");
		JsonPathAssert.assertThat(json).hasPath("$.organization_name").isEqualTo("abc-org-name");
		JsonPathAssert.assertThat(json).hasPath("$.space_guid").isEqualTo("abc-space-guid");
		JsonPathAssert.assertThat(json).hasPath("$.space_name").isEqualTo("abc-space-name");
		JsonPathAssert.assertThat(json).hasPath("$.instance_name").isEqualTo("abc-instance-name");
		JsonPathAssert.assertThat(json).hasPath("$.platform").isEqualTo("cloudfoundry");
		// detect any double serialization due to inheritance and naming mismatch
		JsonPathAssert.assertThat(json).hasMapAtPath("$").hasSize(6);
		JsonUtils.assertThatJsonHasExactNumberOfProperties(context, 6);
	}

	/**
	 * OSB API 2.16 and newer supports additional properties in the CloudFoundry context
	 */
	@Test
	void contextIsSerialized216() {
		Context context = CloudFoundryContext.builder()
				.organizationGuid("abc-org-guid")
				.organizationName("abc-org-name")
				.organizationAnnotations(Collections.singletonMap("myprovider.com/send-alerts-to-email",
						"me@myorg.com"))
				.spaceGuid("abc-space-guid")
				.spaceName("abc-space-name")
				.spaceAnnotations(Collections.singletonMap("myprovider.com/send-alerts-to-email",
						"me@myspace.com"))
				.instanceName("abc-instance-name")
				.build();

		DocumentContext json = JsonUtils.toJsonPath(context);

		JsonPathAssert.assertThat(json).hasPath("$.organization_guid").isEqualTo("abc-org-guid");
		JsonPathAssert.assertThat(json).hasPath("$.organization_name").isEqualTo("abc-org-name");
		JsonPathAssert.assertThat(json).hasMapAtPath("$.organization_annotations").hasSize(1);
		JsonPathAssert.assertThat(json).hasPath("$.organization_annotations").isEqualTo(Collections.singletonMap(
				"myprovider.com/send-alerts-to-email", "me@myorg.com"));
		JsonPathAssert.assertThat(json).hasPath("$.space_guid").isEqualTo("abc-space-guid");
		JsonPathAssert.assertThat(json).hasPath("$.space_name").isEqualTo("abc-space-name");
		JsonPathAssert.assertThat(json).hasPath("$.space_annotations").isEqualTo(Collections.singletonMap(
				"myprovider.com/send-alerts-to-email", "me@myspace.com"));
		JsonPathAssert.assertThat(json).hasPath("$.instance_name").isEqualTo("abc-instance-name");
		JsonPathAssert.assertThat(json).hasPath("$.platform").isEqualTo("cloudfoundry");
		// detect any double serialization due to inheritance and naming mismatch
		JsonPathAssert.assertThat(json).hasMapAtPath("$").hasSize(8);
		JsonUtils.assertThatJsonHasExactNumberOfProperties(context, 10); // +1 for annotations
	}

}
