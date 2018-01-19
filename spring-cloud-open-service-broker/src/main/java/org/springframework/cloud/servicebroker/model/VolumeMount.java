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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Objects;

/**
 * Details of a volume mount in a binding response.
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class VolumeMount {
	public enum Mode {
		READ_ONLY("r"),
		READ_WRITE("rw");

		private final String value;

		Mode(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	public enum DeviceType {
		/**
		 * A shared volume mount represents a distributed file system which can be mounted on all app instances
		 * simultaneously.
		 */
		SHARED("shared");

		private final String value;

		DeviceType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	/**
	 * The name of the volume driver plugin which manages the device.
	 */
	private final String driver;

	/**
	 * The directory to mount inside the application container.
	 */
	private final String containerDir;

	/**
	 * Indicates whether the volume can be mounted in read-only or read-write mode.
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private final Mode mode;

	/**
	 * The type of the volume device to mount.
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private final DeviceType deviceType;

	/**
	 * Details of the volume device to mount, specific to the device type.
	 */
	private final VolumeDevice device;

	private VolumeMount(String driver, String containerDir, Mode mode, DeviceType deviceType, VolumeDevice device) {
		this.driver = driver;
		this.containerDir = containerDir;
		this.mode = mode;
		this.deviceType = deviceType;
		this.device = device;
	}

	public String getDriver() {
		return this.driver;
	}

	public String getContainerDir() {
		return this.containerDir;
	}

	public Mode getMode() {
		return this.mode;
	}

	public DeviceType getDeviceType() {
		return this.deviceType;
	}

	public VolumeDevice getDevice() {
		return this.device;
	}

	public static VolumeMountBuilder builder() {
		return new VolumeMountBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof VolumeMount)) return false;
		VolumeMount that = (VolumeMount) o;
		return Objects.equals(driver, that.driver) &&
				Objects.equals(containerDir, that.containerDir) &&
				mode == that.mode &&
				deviceType == that.deviceType &&
				Objects.equals(device, that.device);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(driver, containerDir, mode, deviceType, device);
	}

	@Override
	public String toString() {
		return "VolumeMount{" +
				"driver='" + driver + '\'' +
				", containerDir='" + containerDir + '\'' +
				", mode=" + mode +
				", deviceType=" + deviceType +
				", device=" + device +
				'}';
	}

	public static class VolumeMountBuilder {
		private String driver;
		private String containerDir;
		private Mode mode;
		private DeviceType deviceType;
		private VolumeDevice device;

		VolumeMountBuilder() {
		}

		public VolumeMountBuilder driver(String driver) {
			this.driver = driver;
			return this;
		}

		public VolumeMountBuilder containerDir(String containerDir) {
			this.containerDir = containerDir;
			return this;
		}

		public VolumeMountBuilder mode(Mode mode) {
			this.mode = mode;
			return this;
		}

		public VolumeMountBuilder deviceType(DeviceType deviceType) {
			this.deviceType = deviceType;
			return this;
		}

		public VolumeMountBuilder device(VolumeDevice device) {
			this.device = device;
			return this;
		}

		public VolumeMount build() {
			return new VolumeMount(driver, containerDir, mode, deviceType, device);
		}
	}
}
