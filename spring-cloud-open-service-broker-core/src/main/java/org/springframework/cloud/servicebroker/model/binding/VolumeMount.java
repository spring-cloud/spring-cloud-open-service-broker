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

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * Details of a volume mount in a service binding response.
 * 
 * <p>
 * Objects of this type are constructed by the service broker application,
 * and used to build the response to the platform.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#volume-mount-object">Open Service Broker API specification</a>
 *
 * @author Scott Frederick
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VolumeMount {
	/**
	 * Values designating whether the mounted volume can be written to or read from.
	 */
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

	/**
	 * Values specifying the type of device to mount.
	 */
	public enum DeviceType {
		/**
		 * Indicates a distributed volume which can be mounted on multiple app instances simultaneously.
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

	private final String driver;

	private final String containerDir;

	@JsonSerialize(using = ToStringSerializer.class)
	@JsonDeserialize(using = ModeDeserializer.class)
	private final Mode mode;

	@JsonSerialize(using = ToStringSerializer.class)
	@JsonDeserialize(using = DeviceTypeDeserializer.class)
	private final DeviceType deviceType;

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "device_type")
	@JsonSubTypes({@JsonSubTypes.Type(value = SharedVolumeDevice.class, name = "shared") })
	private final VolumeDevice device;

	VolumeMount(String driver, String containerDir, Mode mode, DeviceType deviceType, VolumeDevice device) {
		this.driver = driver;
		this.containerDir = containerDir;
		this.mode = mode;
		this.deviceType = deviceType;
		this.device = device;
	}

	VolumeMount() {
		this(null, null, null, null, null);
	}

	/**
	 * Get the name of the volume driver plugin which manages the device.
	 *
	 * @return the name of the driver
	 */
	public String getDriver() {
		return this.driver;
	}

	/**
	 * Set the directory to mount inside the application container.
	 *
	 * @return the container directory
	 */
	public String getContainerDir() {
		return this.containerDir;
	}

	/**
	 * Get a value indicating whether the volume can be mounted in read-only or read-write mode.
	 *
	 * @return the volume read/write mode
	 */
	public Mode getMode() {
		return this.mode;
	}

	/**
	 * Get the type of the volume device to mount.
	 *
	 * @return the volume device type
	 */
	public DeviceType getDeviceType() {
		return this.deviceType;
	}

	/**
	 * Get the details of the volume device to mount.
	 *
	 * @return the volume device details
	 */
	public VolumeDevice getDevice() {
		return this.device;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal VolumeMount}.
	 *
	 * @return the builder
	 */
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

	/**
	 * Provides a fluent API for constructing a {@link VolumeMount}.
	 */
	public static class VolumeMountBuilder {
		private String driver;
		private String containerDir;
		private Mode mode;
		private DeviceType deviceType;
		private VolumeDevice device;

		VolumeMountBuilder() {
		}

		/**
		 * Set the name of the volume driver plugin which manages the device.
		 *
		 * <p>
		 * This value will set the {@literal driver} field in the body of the response to the platform.
		 *
		 * @param driver the driver name
		 * @return the builder
		 */
		public VolumeMountBuilder driver(String driver) {
			this.driver = driver;
			return this;
		}

		/**
		 * Set the directory to mount inside the application container.
		 * 
		 * <p>
		 * This value will set the {@literal container_dir} field in the body of the response to the platform.
		 *
		 * @param containerDir the container directory
		 * @return the builder
		 */
		public VolumeMountBuilder containerDir(String containerDir) {
			this.containerDir = containerDir;
			return this;
		}

		/**
		 * Set a value indicating whether the volume can be mounted in read-only or read-write mode.
		 *
		 * <p>
		 * This value will set the {@literal mode} field in the body of the response to the platform.
		 *
		 * @param mode the volume read/write mode
		 * @return the builder
		 */
		public VolumeMountBuilder mode(Mode mode) {
			this.mode = mode;
			return this;
		}

		/**
		 * Set the type of the volume device to mount.
		 *
		 * <p>
		 * This value will set the {@literal device_type} field in the body of the response to the platform.
		 *
		 * @param deviceType the volume device type
		 * @return the builder
		 */
		public VolumeMountBuilder deviceType(DeviceType deviceType) {
			this.deviceType = deviceType;
			return this;
		}

		/**
		 * Set the details of the volume device to mount, specific to the device type.
		 *
		 * <p>
		 * This value will set the {@literal device} field in the body of the response to the platform.
		 *
		 * @param device the volume device details
		 * @return the builder
		 */
		public VolumeMountBuilder device(VolumeDevice device) {
			this.device = device;
			return this;
		}

		/**
		 * Construct a {@link VolumeMount} from the provided values.
		 *
		 * @return the newly constructed {@literal VolumeMount}
		 */
		public VolumeMount build() {
			return new VolumeMount(driver, containerDir, mode, deviceType, device);
		}
	}

	private static class DeviceTypeDeserializer extends StdDeserializer<DeviceType> {

		private static final long serialVersionUID = -7935903407118198514L;

		public DeviceTypeDeserializer(){
			this(null);
		}

		public DeviceTypeDeserializer(Class<?> c){
			super(c);
		}

		@Override
		public DeviceType deserialize(JsonParser jsonParser,
				DeserializationContext deserializationContext) throws IOException {
			for(DeviceType d : DeviceType.values()) {
				if (d.toString().equalsIgnoreCase(jsonParser.getText())) {
					return d;
				}
			}
			throw new IllegalArgumentException("DeviceType is not defined");
		}
	}

	private static class ModeDeserializer extends StdDeserializer<Mode> {

		private static final long serialVersionUID = -4985037236705821009L;

		public ModeDeserializer(){
			this(null);
		}

		public ModeDeserializer(Class<?> c){
			super(c);
		}

		@Override
		public Mode deserialize(JsonParser jsonParser, DeserializationContext
				deserializationContext) throws IOException {
			for(Mode m : Mode.values()) {
				if (m.toString().equalsIgnoreCase(jsonParser.getText())) {
					return m;
				}
			}
			throw new IllegalArgumentException("Mode not defined");
		}
	}

}
