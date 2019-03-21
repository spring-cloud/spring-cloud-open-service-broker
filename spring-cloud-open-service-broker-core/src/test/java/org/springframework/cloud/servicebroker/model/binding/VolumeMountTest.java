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

package org.springframework.cloud.servicebroker.model.binding;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.springframework.cloud.servicebroker.JsonUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;
import static org.springframework.cloud.servicebroker.model.binding.VolumeMount.DeviceType.SHARED;
import static org.springframework.cloud.servicebroker.model.binding.VolumeMount.Mode.READ_ONLY;

public class VolumeMountTest {
	@Test
	public void volumeMountIsBuiltWithDefaults() {
		VolumeMount mount = VolumeMount.builder()
				.build();

		assertThat(mount.getDriver()).isNull();
		assertThat(mount.getContainerDir()).isNull();
		assertThat(mount.getMode()).isNull();
		assertThat(mount.getDeviceType()).isNull();
		assertThat(mount.getDevice()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(mount);

		assertThat(json).hasNoPath("$.driver");
		assertThat(json).hasNoPath("$.container_dir");
		assertThat(json).hasNoPath("$.mode");
		assertThat(json).hasNoPath("$.device_type");
		assertThat(json).hasNoPath("$.device");
	}

	@Test
	@SuppressWarnings("serial")
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

		assertThat(mount.getDriver()).isEqualTo("test-driver");
		assertThat(mount.getContainerDir()).isEqualTo("/data/images");
		assertThat(mount.getMode()).isEqualTo(READ_ONLY);
		assertThat(mount.getDeviceType()).isEqualTo(SHARED);

		SharedVolumeDevice device = (SharedVolumeDevice) mount.getDevice();
		assertThat(device.getVolumeId()).isEqualTo("volume-id");
		assertThat(device.getMountConfig()).hasSize(4);
		assertThat(device.getMountConfig().get("config1")).isEqualTo("value1");
		assertThat(device.getMountConfig().get("config2")).isEqualTo(2);
		assertThat(device.getMountConfig().get("config3")).isEqualTo("value3");
		assertThat(device.getMountConfig().get("config4")).isEqualTo(true);

		DocumentContext json = JsonUtils.toJsonPath(mount);

		assertThat(json).hasPath("$.driver").isEqualTo("test-driver");
		assertThat(json).hasPath("$.container_dir").isEqualTo("/data/images");
		assertThat(json).hasPath("$.mode").isEqualTo("r");
		assertThat(json).hasPath("$.device_type").isEqualTo("shared");
		assertThat(json).hasPath("$.device.volume_id").isEqualTo("volume-id");
		assertThat(json).hasPath("$.device.mount_config.config1").isEqualTo("value1");
		assertThat(json).hasPath("$.device.mount_config.config2").isEqualTo(2);
		assertThat(json).hasPath("$.device.mount_config.config3").isEqualTo("value3");
		assertThat(json).hasPath("$.device.mount_config.config4").isEqualTo(true);
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(VolumeMount.class)
				.verify();
	}
}
