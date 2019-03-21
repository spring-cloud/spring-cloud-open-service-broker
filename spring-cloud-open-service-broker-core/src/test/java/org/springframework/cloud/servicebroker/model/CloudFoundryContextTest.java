/*
 * Copyright 2002-2018 the original author or authors.
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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.CLOUD_FOUNDRY_PLATFORM;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.ORGANIZATION_GUID_KEY;
import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.SPACE_GUID_KEY;

public class CloudFoundryContextTest {
	@Test
	public void emptyContext() {
		CloudFoundryContext context = CloudFoundryContext.builder()
				.build();

		assertThat(context.getPlatform()).isEqualTo(CLOUD_FOUNDRY_PLATFORM);

		assertThat(context.getOrganizationGuid()).isNull();
		assertThat(context.getSpaceGuid()).isNull();

		assertThat(context.getProperties()).isEmpty();
	}

	@Test
	public void populatedContext() {
		CloudFoundryContext context = CloudFoundryContext.builder()
				.property("key1", "value1")
				.organizationGuid("org-guid")
				.spaceGuid("space-guid")
				.property("key2", "value2")
				.build();

		assertThat(context.getPlatform()).isEqualTo(CLOUD_FOUNDRY_PLATFORM);

		assertThat(context.getOrganizationGuid()).isEqualTo("org-guid");
		assertThat(context.getSpaceGuid()).isEqualTo("space-guid");
		assertThat(context.getProperty(ORGANIZATION_GUID_KEY)).isEqualTo("org-guid");
		assertThat(context.getProperty(SPACE_GUID_KEY)).isEqualTo("space-guid");

		assertThat(context.getProperty("key1")).isEqualTo("value1");
		assertThat(context.getProperty("key2")).isEqualTo("value2");
	}
}