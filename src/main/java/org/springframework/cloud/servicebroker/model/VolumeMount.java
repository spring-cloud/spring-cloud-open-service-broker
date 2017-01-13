package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a volume mount in a binding response.
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
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
	@JsonSerialize
	private String driver;

	/**
	 * The directory to mount inside the application container.
	 */
	@JsonSerialize
	@JsonProperty("container_dir")
	private String containerDir;

	/**
	 * Indicates whether the volume can be mounted in read-only or read-write mode.
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Mode mode;

	/**
	 * The type of the volume device to mount.
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@JsonProperty("device_type")
	private DeviceType deviceType;

	/**
	 * Details of the volume device to mount, specific to the device type.
	 */
	@JsonSerialize
	private VolumeDevice device;
}
