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

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a type of distributed {@link VolumeDevice} which can be mounted on multiple app instances simultaneously.
 * 
 * <p>
 * Objects of this type are constructed by the service broker application,
 * and used to build the response to the platform.
 *
 * @author Scott Frederick
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SharedVolumeDevice extends VolumeDevice {
	private final String volumeId;

	private final Map<String, Object> mountConfig;

	SharedVolumeDevice(String volumeId, Map<String, Object> mountConfig) {
		this.volumeId = volumeId;
		this.mountConfig = mountConfig;
	}

	/**
	 * Get the ID of the shared volume device to mount on each app instance.
	 *
	 * @return the volume ID
	 */
	public String getVolumeId() {
		return this.volumeId;
	}

	/**
	 * Get the configuration properties for the volume device.
	 *
	 * @return the device configuration
	 */
	public Map<String, Object> getMountConfig() {
		return this.mountConfig;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal SharedVolumeDevice}.
	 *
	 * @return the builder
	 */
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

	/**
	 * Provides a fluent API for constructing a {@link SharedVolumeDevice}.
	 */
	public static class SharedVolumeDeviceBuilder {
		private String volumeId;
		private final Map<String, Object> mountConfig = new HashMap<>();

		SharedVolumeDeviceBuilder() {
		}

		/**
		 * Set the ID of the shared volume device to mount on each app instance.
		 *
		 * <p>
		 * This value sets the {@literal volume_id} field in the body of the response to the platform.
		 *
		 * @param volumeId the volume ID
		 * @return the builder
		 */
		public SharedVolumeDeviceBuilder volumeId(String volumeId) {
			this.volumeId = volumeId;
			return this;
		}

		/**
		 * Add a set of configuration properties from the provided {@literal Map} to the volume device configuration.
		 *
		 * <p>
		 * This value sets the {@literal mount_config} field in the body of the response to the platform.
		 *
		 * @param mountConfig the configuration properties to add
		 * @return the builder
		 */
		public SharedVolumeDeviceBuilder mountConfig(Map<String, Object> mountConfig) {
			this.mountConfig.putAll(mountConfig);
			return this;
		}

		/**
		 * Add a key/value pair to the volume device configuration.
		 *
		 * <p>
		 * This value sets the {@literal mount_config} field in the body of the response to the platform.
		 *
		 * @param key the configuration properties key
		 * @param value the configuration properties value
		 * @return the builder
		 */
		public SharedVolumeDeviceBuilder mountConfig(String key, Object value) {
			this.mountConfig.put(key, value);
			return this;
		}

		/**
		 * Construct a {@link SharedVolumeDevice} from the provided values.
		 *
		 * @return the newly constructed {@literal SharedVolumeDevice}
		 */
		public SharedVolumeDevice build() {
			return new SharedVolumeDevice(volumeId, mountConfig);
		}
	}
}
