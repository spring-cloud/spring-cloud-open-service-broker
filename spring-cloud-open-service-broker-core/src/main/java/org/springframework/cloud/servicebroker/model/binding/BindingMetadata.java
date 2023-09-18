/*
 * Copyright 2002-2021 the original author or authors.
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

package org.springframework.cloud.servicebroker.model.binding;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Service Instance Binding Metadata
 *
 * <p>
 * An OPTIONAL object containing metadata about this Service Binding. This metadata is mainly used to manage the Service
 * Binding itself and SHOULD NOT contain any data that is needed to connect to the Service Instance.
 *
 * @author Roy Clarkson
 */
public class BindingMetadata {

	private final String expiresAt;

	/**
	 * Construct a new BindingMetadata
	 */
	public BindingMetadata() {
		this(null);
	}

	/**
	 * Construct a new BindingMetadata
	 *
	 * @param expiresAt the date and time in ISO 8601 format
	 */
	public BindingMetadata(String expiresAt) {
		this.expiresAt = expiresAt;
	}

	/**
	 * Get the expiration date and time
	 *
	 * @return the date and time in ISO 8601 format
	 */
	@JsonProperty("expires_at")
	public String getExpiresAt() {
		return expiresAt;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal BindingMetadata}.
	 *
	 * @return the builder
	 */
	public static BindingMetadataBuilder builder() {
		return new BindingMetadataBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BindingMetadata)) {
			return false;
		}
		BindingMetadata that = (BindingMetadata) o;
		return that.canEqual(this) &&
				Objects.equals(expiresAt, that.expiresAt);
	}

	/**
	 * Is another object type compatible with this object
	 *
	 * @param other the other object
	 * @return true of compatible
	 */
	public final boolean canEqual(Object other) {
		return other instanceof BindingMetadata;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(expiresAt);
	}

	@Override
	public final String toString() {
		return "BindingMetadata{" +
				"expiresAt='" + expiresAt + '\'' +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link BindingMetadata}.
	 */
	public static final class BindingMetadataBuilder {

		private String expiresAt;

		private BindingMetadataBuilder() {
		}

		/**
		 * The date and time when the Service Binding becomes invalid and SHOULD NOT or CANNOT be used anymore.
		 * Applications or Platforms MAY use this field to initiate a Service Binding/credential rotation. If present,
		 * the string MUST follow ISO 8601 and this pattern: yyyy-mm-ddThh:mm:ss.ssZ.
		 *
		 * @param expiresAt the date and time in ISO 8601 format
		 * @return the builder
		 * @see #expiresAt(String)
		 */
		public BindingMetadata.BindingMetadataBuilder expiresAt(String expiresAt) {
			this.expiresAt = expiresAt;
			return this;
		}

		/**
		 * Construct a {@link BindingMetadata} from the provided values.
		 *
		 * @return the newly constructed {@literal BindingMetadata}
		 */
		public BindingMetadata build() {
			return new BindingMetadata(expiresAt);
		}

	}

}
