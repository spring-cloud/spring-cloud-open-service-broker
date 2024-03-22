/*
 * Copyright 2002-2024 the original author or authors.
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

package org.springframework.cloud.servicebroker.model.instance;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.util.CollectionUtils;

/**
 * Service instance metadata.
 *
 * @author Andrea Alkalay
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceInstanceMetadata {

	/**
	 * Broker specified key-value pairs specifying attributes of Service Instances.
	 */
	private final Map<String, Object> labels = new HashMap<>();

	/**
	 * Create a new ServiceInstanceMetadata.
	 */
	public ServiceInstanceMetadata() {
		this(null);
	}

	/**
	 * Create a new ServiceInstanceMetadata.
	 * @param labels collection of labels
	 */
	public ServiceInstanceMetadata(Map<String, Object> labels) {
		if (!CollectionUtils.isEmpty(labels)) {
			this.labels.putAll(labels);
		}
	}

	/**
	 * Get the labels.
	 * @return the labels
	 */
	public Map<String, Object> getLabels() {
		return this.labels;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a
	 * {@literal ServiceInstanceMetadata}.
	 * @return the builder
	 */
	public static ServiceInstanceMetadataBuilder builder() {
		return new ServiceInstanceMetadataBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ServiceInstanceMetadata)) {
			return false;
		}
		ServiceInstanceMetadata that = (ServiceInstanceMetadata) o;
		return that.canEqual(this) && Objects.equals(this.labels, that.labels);
	}

	/**
	 * Is another object type compatible with this object.
	 * @param other the other object
	 * @return true of compatible
	 */
	public final boolean canEqual(Object other) {
		return other instanceof ServiceInstanceMetadata;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(this.labels);
	}

	@Override
	public final String toString() {
		return "ServiceInstanceMetadata{" + "labels='" + this.labels + '\'' + '}';
	}

	/**
	 * Provides a fluent API for constructing a {@link ServiceInstanceMetadata}.
	 */
	public static final class ServiceInstanceMetadataBuilder {

		/**
		 * Broker specified key-value pairs specifying attributes of Service Instances.
		 */
		private final Map<String, Object> labels = new HashMap<>();

		private ServiceInstanceMetadataBuilder() {
		}

		/**
		 * Add a set of labels from the provided {@literal Map} to the metadata labels.
		 * @param labels the labels to add
		 * @return the builder
		 * @see #labels(Map)
		 */
		public ServiceInstanceMetadataBuilder labels(Map<String, Object> labels) {
			if (!CollectionUtils.isEmpty(labels)) {
				this.labels.putAll(labels);
			}
			return this;
		}

		/**
		 * Add a key/value pair to the metadata labels.
		 * @param key the label key to add
		 * @param value the label value to add
		 * @return the builder
		 * @see #label(String, Object)
		 */
		public ServiceInstanceMetadataBuilder label(String key, Object value) {
			this.labels.put(key, value);
			return this;
		}

		/**
		 * Construct a {@link ServiceInstanceMetadata} from the provided values.
		 * @return the newly constructed {@literal ServiceInstanceMetadata}
		 */
		public ServiceInstanceMetadata build() {
			return new ServiceInstanceMetadata(this.labels);
		}

	}

}
