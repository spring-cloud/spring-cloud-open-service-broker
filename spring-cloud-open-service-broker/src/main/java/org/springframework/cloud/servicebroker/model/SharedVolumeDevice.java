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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SharedVolumeDevice extends VolumeDevice {
	private final String volumeId;

	private final Map<String, Object> mountConfig;

	private SharedVolumeDevice(String volumeId, Map<String, Object> mountConfig) {
		this.volumeId = volumeId;
		this.mountConfig = mountConfig;
	}

	public String getVolumeId() {
		return this.volumeId;
	}

	public Map<String, Object> getMountConfig() {
		return this.mountConfig;
	}

	public static SharedVolumeDeviceBuilder builder() {
		return new SharedVolumeDeviceBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SharedVolumeDevice)) return false;
		SharedVolumeDevice that = (SharedVolumeDevice) o;
		return Objects.equals(volumeId, that.volumeId) &&
				Objects.equals(mountConfig, that.mountConfig);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(volumeId, mountConfig);
	}

	@Override
	public String toString() {
		return "SharedVolumeDevice{" +
				"volumeId='" + volumeId + '\'' +
				", mountConfig=" + mountConfig +
				'}';
	}

	public static class SharedVolumeDeviceBuilder {
		private String volumeId;
		private Map<String, Object> mountConfig = new HashMap<>();

		SharedVolumeDeviceBuilder() {
		}

		public SharedVolumeDeviceBuilder volumeId(String volumeId) {
			this.volumeId = volumeId;
			return this;
		}

		public SharedVolumeDeviceBuilder mountConfig(Map<String, Object> mountConfig) {
			this.mountConfig.putAll(mountConfig);
			return this;
		}

		public SharedVolumeDeviceBuilder mountConfig(String key, Object value) {
			this.mountConfig.put(key, value);
			return this;
		}

		public SharedVolumeDevice build() {
			return new SharedVolumeDevice(volumeId, mountConfig);
		}
	}
}
