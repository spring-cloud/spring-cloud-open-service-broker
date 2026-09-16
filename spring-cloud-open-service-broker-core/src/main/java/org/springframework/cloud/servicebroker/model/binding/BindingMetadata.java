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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

/**
 * Service Instance Binding Metadata
 *
 * <p>
 * An OPTIONAL object containing metadata about this Service Binding. This metadata is
 * mainly used to manage the Service Binding itself and SHOULD NOT contain any data that
 * is needed to connect to the Service Instance.
 *
 * @author Roy Clarkson
 * @see <a href=
 * "https://github.com/openservicebrokerapi/servicebroker/blob/v2.17/spec.md#binding-metadata-object">Open
 * Service Broker API specification</a>
 */
public class BindingMetadata {

	private final @Nullable String expiresAt;

	private final @Nullable String renewBefore;

	/**
	 * Construct a new BindingMetadata.
	 */
	public BindingMetadata() {
		this(null, null);
	}

	/**
	 * Construct a new BindingMetadata.
	 * @param expiresAt the date and time in ISO 8601 format
	 * @param renewBefore the date and time before which the binding should be renewed, in
	 * ISO 8601 format
	 */
	@JsonCreator
	public BindingMetadata(@JsonProperty("expires_at") @Nullable String expiresAt,
			@JsonProperty("renew_before") @Nullable String renewBefore) {
		this.expiresAt = expiresAt;
		this.renewBefore = renewBefore;
	}

	/**
	 * Get the expiration date and time.
	 * @return the date and time in ISO 8601 format
	 */
	@JsonProperty("expires_at")
	public @Nullable String getExpiresAt() {
		return this.expiresAt;
	}

	/**
	 * Get the date and time before which the Service Binding SHOULD be renewed.
	 * <p>
	 * Since OSB API 2.17.
	 * @return the date and time in ISO 8601 format
	 */
	@JsonProperty("renew_before")
	public @Nullable String getRenewBefore() {
		return this.renewBefore;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a
	 * {@literal BindingMetadata}.
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
		return that.canEqual(this) && Objects.equals(this.expiresAt, that.expiresAt)
				&& Objects.equals(this.renewBefore, that.renewBefore);
	}

	/**
	 * Is another object type compatible with this object.
	 * @param other the other object
	 * @return true of compatible
	 */
	public final boolean canEqual(Object other) {
		return other instanceof BindingMetadata;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(this.expiresAt, this.renewBefore);
	}

	@Override
	public final String toString() {
		return "BindingMetadata{" + "expiresAt='" + this.expiresAt + '\'' + ", renewBefore='" + this.renewBefore + '\''
				+ '}';
	}

	/**
	 * Provides a fluent API for constructing a {@link BindingMetadata}.
	 */
	public static final class BindingMetadataBuilder {

		private @Nullable String expiresAt;

		private @Nullable String renewBefore;

		private BindingMetadataBuilder() {
		}

		/**
		 * The date and time when the Service Binding becomes invalid and SHOULD NOT or
		 * CANNOT be used anymore. Applications or Platforms MAY use this field to
		 * initiate a Service Binding/credential rotation. If present, the string MUST
		 * follow ISO 8601 and this pattern: yyyy-mm-ddThh:mm:ss.ssZ.
		 * @param expiresAt the date and time in ISO 8601 format
		 * @return the builder
		 * @see #expiresAt(String)
		 */
		public BindingMetadata.BindingMetadataBuilder expiresAt(String expiresAt) {
			this.expiresAt = expiresAt;
			return this;
		}

		/**
		 * The date and time before the Service Binding SHOULD be renewed. Applications or
		 * Platforms MAY use this field to initiate a Service Binding rotation or create a
		 * new Service Binding on time. If present, the string MUST follow ISO 8601 and
		 * this pattern: yyyy-mm-ddThh:mm:ss.sZ.
		 * <p>
		 * Since OSB API 2.17.
		 * @param renewBefore the date and time in ISO 8601 format
		 * @return the builder
		 * @see #getRenewBefore()
		 */
		public BindingMetadata.BindingMetadataBuilder renewBefore(String renewBefore) {
			this.renewBefore = renewBefore;
			return this;
		}

		/**
		 * Construct a {@link BindingMetadata} from the provided values.
		 * @return the newly constructed {@literal BindingMetadata}
		 */
		public BindingMetadata build() {
			return new BindingMetadata(this.expiresAt, this.renewBefore);
		}

	}

}
