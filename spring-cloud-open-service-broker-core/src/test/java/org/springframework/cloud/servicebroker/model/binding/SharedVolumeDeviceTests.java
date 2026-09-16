/*
 * Copyright 2002-2026 the original author or authors.
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

package org.springframework.cloud.servicebroker.model.binding;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

class SharedVolumeDeviceTests {

	@Test
	void deviceWithDefaultsIsSerializedToJson() {
		SharedVolumeDevice device = SharedVolumeDevice.builder().build();

		assertThat(device.getVolumeId()).isNull();
		assertThat(device.getMountConfig()).isEmpty();

		DocumentContext json = JsonUtils.toJsonPath(device);

		assertThat(json).hasNoPath("$.volume_id");
		assertThat(json).hasNoPath("$.mount_config");
	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier.forClass(SharedVolumeDevice.class).verify();
	}

}
