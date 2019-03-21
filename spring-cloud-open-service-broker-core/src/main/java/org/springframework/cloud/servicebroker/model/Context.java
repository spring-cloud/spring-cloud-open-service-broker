/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.cloud.servicebroker.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Platform specific contextual information under which the service instance is to be provisioned or updated. Fields
 * known by concrete subtypes will be parsed into discrete properties of the appropriate subtype. Any additional
 * properties will available using {@link #getProperty(String)}.
 *
 * @author Scott Frederick
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
		property = "platform", visible = true, defaultImpl = PlatformContext.class)
@JsonSubTypes({
		@JsonSubTypes.Type(value = CloudFoundryContext.class, name = CloudFoundryContext.CLOUD_FOUNDRY_PLATFORM),
		@JsonSubTypes.Type(value = KubernetesContext.class, name = KubernetesContext.KUBERNETES_PLATFORM),
})
public class Context {
	protected final String platform;

	@JsonAnySetter
	protected final Map<String, Object> properties = new HashMap<>();

	protected Context() {
		this.platform = null;
	}

	protected Context(String platform, Map<String, Object> properties) {
		this.platform = platform;
		if (properties != null) {
			this.properties.putAll(properties);
		}
	}

	/**
	 * Get the name of the platform making the request.
	 *
	 * @return the platform identifier
	 */
	public String getPlatform() {
		return this.platform;
	}

	/**
	 * Get all properties in the context.
	 *
	 * @return the collection of properties
	 */
	@JsonAnyGetter
	public Map<String, Object> getProperties() {
		return this.properties;
	}

	/**
	 * Get the value of a property in the context with the given key.
	 *
	 * @param key the key of the property to retrieve
	 * @return the value of the property, or {@literal null} if the key is not present in the request
	 */
	public Object getProperty(String key) {
		return this.properties.get(key);
	}

	protected String getStringProperty(String key) {
		if (getProperty(key) != null) {
			return getProperty(key).toString();
		}
		return null;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Context)) return false;
		Context that = (Context) o;
		return that.canEqual(this) &&
				Objects.equals(platform, that.platform) &&
				Objects.equals(properties, that.properties);
	}

	public final boolean canEqual(Object other) {
		return (other instanceof Context);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(platform, properties);
	}

	@Override
	public final String toString() {
		return "Context{" +
				"platform='" + platform + '\'' +
				", properties=" + properties +
				'}';
	}

	protected static abstract class ContextBaseBuilder<R extends Context, B extends ContextBaseBuilder<R, B>> {
		private final B thisObj;

		protected String platform;
		protected Map<String, Object> properties = new HashMap<>();

		protected ContextBaseBuilder() {
			this.thisObj = createBuilder();
		}

		protected abstract B createBuilder();

		/**
		 * Set the name of the platform as would be provided in the request from the platform.
		 *
		 * @param platform the platform name
		 * @return the builder
		 */
		public B platform(String platform) {
			this.platform = platform;
			return thisObj;
		}

		/**
		 * Add a set of properties from the provided {@literal Map} to the context properties
		 * as would be provided in the request from the platform.
		 *
		 * @param properties the properties to add
		 * @return the builder
		 * @see #getProperties()
		 */
		public B properties(Map<String, Object> properties) {
			this.properties.putAll(properties);
			return thisObj;
		}

		/**
		 * Add a key/value pair to the context properties as would be provided in the request from the platform.
		 *
		 * @param key the parameter key to add
		 * @param value the parameter value to add
		 * @return the builder
		 * @see #getProperties()
		 */
		public B property(String key, Object value) {
			this.properties.put(key, value);
			return thisObj;
		}

		public abstract R build();
	}
}
