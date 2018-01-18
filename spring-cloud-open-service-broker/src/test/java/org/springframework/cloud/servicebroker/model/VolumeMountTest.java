/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model;

import org.junit.Test;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.servicebroker.model.VolumeMount.DeviceType.SHARED;
import static org.springframework.cloud.servicebroker.model.VolumeMount.Mode.READ_ONLY;
import static org.springframework.cloud.servicebroker.model.VolumeMount.Mode.READ_WRITE;

public class VolumeMountTest {
	@Test
	public void volumeMountIsBuiltWithDefaults() {
		VolumeMount mount = VolumeMount.builder()
				.build();

		assertThat(mount.getDriver(), nullValue());
		assertThat(mount.getContainerDir(), nullValue());
		assertThat(mount.getMode(), nullValue());
		assertThat(mount.getDeviceType(), nullValue());
		assertThat(mount.getDevice(), nullValue());
	}

	@Test
	public void volumeMountIsBuiltWithAllValues() {
		Map<String, Object> config = new HashMap<String, Object>() {{
			put("config3", "value3");
			put("config4", true);
		}};

		VolumeMount mount = VolumeMount.builder()
				.driver("test-driver")
				.containerDir("/data/images")
				.mode(READ_ONLY)
				.deviceType(SHARED)
				.device(SharedVolumeDevice.builder()
						.volumeId("volume-id")
						.mountConfig("config1", "value1")
						.mountConfig("config2", 2)
						.mountConfig(config)
						.build())
				.build();

		assertThat(mount.getDriver(), equalTo("test-driver"));
		assertThat(mount.getContainerDir(), equalTo("/data/images"));
		assertThat(mount.getMode(), equalTo(READ_ONLY));
		assertThat(mount.getDeviceType(), equalTo(SHARED));

		SharedVolumeDevice device = (SharedVolumeDevice) mount.getDevice();
		assertThat(device.getVolumeId(), equalTo("volume-id"));
		assertThat(device.getMountConfig(), aMapWithSize(4));
		assertThat(device.getMountConfig().get("config1"), equalTo("value1"));
		assertThat(device.getMountConfig().get("config2"), equalTo(2));
		assertThat(device.getMountConfig().get("config3"), equalTo("value3"));
		assertThat(device.getMountConfig().get("config4"), equalTo(true));
	}

	@Test
	public void volumeMountIsSerializedToJson() throws Exception {
		VolumeMount mount = VolumeMount.builder()
				.driver("test-driver")
				.containerDir("/data/images")
				.mode(READ_WRITE)
				.deviceType(SHARED)
				.device(SharedVolumeDevice.builder()
						.volumeId("volume-id")
						.mountConfig("config1", "value1")
						.mountConfig("config2", 2)
						.build())
				.build();

		String json = DataFixture.toJson(mount);

		assertThat(json, isJson(allOf(
				withJsonPath("$.driver", equalTo("test-driver")),
				withJsonPath("$.container_dir", equalTo("/data/images")),
				withJsonPath("$.mode", equalTo("rw")),
				withJsonPath("$.device_type", equalTo("shared")),
				withJsonPath("$.device.volume_id", equalTo("volume-id")),
				withJsonPath("$.device.mount_config", aMapWithSize(2)),
				withJsonPath("$.device.mount_config.config1", equalTo("value1")),
				withJsonPath("$.device.mount_config.config2", equalTo(2))
		)));
	}
}