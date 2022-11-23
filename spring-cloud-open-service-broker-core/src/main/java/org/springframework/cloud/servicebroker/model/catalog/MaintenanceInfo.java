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

package org.springframework.cloud.servicebroker.model.catalog;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Maintenance info available for a Plan. If this info is provided, a version string must be provided and platforms
 * may use this when Provisioning or Updating a Service Instance.
 *
 * @author ilyavy
 * @see <a href= "https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#maintenance-info-object">Open
 * 		Service Broker API specification</a>
 * @see Plan
 * @see org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest
 * @see org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MaintenanceInfo {

	private static final Pattern SEMANTIC_VERSION_V2_PATTERN = Pattern.compile("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|" +
			"[1-9]\\d*)(-(0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(\\.(0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*)?" +
			"(\\+[0-9a-zA-Z-]+(\\.[0-9a-zA-Z-]+)*)?$");

	@NotNull
	private final String version;

	private final String description;

	/**
	 * Constructs a new {@link MaintenanceInfo}
	 */
	@SuppressWarnings("PMD.NullAssignment")
	public MaintenanceInfo() {
		this.version = null;
		this.description = null;
	}

	/**
	 * Constructs a new {@link MaintenanceInfo}
	 *
	 * @param version maintenance version (conforming to a semantic version 2.0)
	 * @param description description of the impact of the maintenance update
	 * @throws IllegalArgumentException if the provided to the builder version does not comply to semantic versioning
	 * 		v2 specification
	 */
	public MaintenanceInfo(String version, String description) {
		if (!SEMANTIC_VERSION_V2_PATTERN.matcher(version).matches()) {
			throw new IllegalArgumentException(
					"Version provided should comply to semantic version v2 specification");
		}
		this.version = version;
		this.description = description;
	}

	/**
	 * The version of the maintenance update available for a plan.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * The description of the impact of the maintenance update.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Creates a builder that provides a fluent API for constructing a {@literal MaintenanceInfo}.
	 *
	 * @return the builder
	 */
	public static MaintenanceInfoBuilder builder() {
		return new MaintenanceInfoBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MaintenanceInfo that = (MaintenanceInfo) o;
		return Objects.equals(version, that.version) &&
				Objects.equals(description, that.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(version, description);
	}

	@Override
	public String toString() {
		return "MaintenanceInfo{" +
				"version='" + version +
				"', description='" + description +
				"'}";
	}

	/**
	 * Provides a fluent API for constructing a {@literal MaintenanceInfo}.
	 */
	public static final class MaintenanceInfoBuilder {

		private String version;

		private String description;

		private MaintenanceInfoBuilder() {
		}

		/**
		 * The version of the maintenance update available for a plan.
		 *
		 * @param version the version
		 * @return the builder instance
		 */
		public MaintenanceInfoBuilder version(String version) {
			this.version = version;
			return this;
		}

		/**
		 * The version of the maintenance update available for a plan.
		 *
		 * @param major MAJOR version when you make incompatible API changes
		 * @param minor MINOR version when you add functionality in a backwards-compatible manner
		 * @param patch PATCH version when you make backwards-compatible bug fixes
		 * @param extension additional labels for pre-release and build metadata
		 * @return the builder instance
		 */
		public MaintenanceInfoBuilder version(int major, int minor, int patch, String extension) {
			return this.version(major + "." + minor + "." + patch + extension);
		}

		/**
		 * The description of the impact of the maintenance update.
		 *
		 * @param description the description
		 * @return the builder instance
		 */
		public MaintenanceInfoBuilder description(String description) {
			this.description = description;
			return this;
		}

		/**
		 * Constructs a {@link MaintenanceInfo} from the provided values.
		 *
		 * @return the newly constructed {@literal MaintenanceInfo}
		 * @throws IllegalArgumentException if the provided to the builder version does not comply to semantic
		 * 		versioning v2 specification
		 */
		public MaintenanceInfo build() {
			return new MaintenanceInfo(version, description);
		}

	}

}
