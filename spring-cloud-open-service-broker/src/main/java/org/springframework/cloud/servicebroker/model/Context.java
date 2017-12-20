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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.CLOUD_FOUNDRY_PLATFORM;
import static org.springframework.cloud.servicebroker.model.KubernetesContext.KUBERNETES_PLATFORM;

/**
 * Platform specific contextual information under which the service instance is to be provisioned or updated. Fields
 * known by concrete subtypes will be parsed into discrete properties of the appropriate subtype. Any additional
 * properties will available using {@link #getProperty(String)}.
 *
 * @author Scott Frederick
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
		property = "platform", visible = true, defaultImpl = Context.class)
@JsonSubTypes({
		@JsonSubTypes.Type(value = CloudFoundryContext.class, name = CLOUD_FOUNDRY_PLATFORM),
		@JsonSubTypes.Type(value = KubernetesContext.class, name = KUBERNETES_PLATFORM),
})
public class Context {
	/**
	 * The name of the platform making the request.
	 */
	@JsonProperty("platform")
	private final String platform;

	@JsonAnySetter
	private final Map<String, Object> properties = new HashMap<>();

	protected Context() {
		this.platform = null;
	}

	protected Context(String platform, Map<String, Object> properties) {
		this.platform = platform;
		if (properties != null) {
			this.properties.putAll(properties);
		}
	}

	public String getPlatform() {
		return this.platform;
	}

	@JsonAnyGetter
	public Map<String, Object> getProperties() {
		return this.properties;
	}

	/**
	 * Get the value of a field in the request with the given key.
	 *
	 * @param key the key of the value to retrieve
	 * @return the value of the field, or {@literal null} if the key is not present in the request
	 */
	public Object getProperty(String key) {
		return this.properties.get(key);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Context)) return false;
		Context context = (Context) o;
		return Objects.equals(platform, context.platform) &&
				Objects.equals(properties, context.properties);
	}

	@Override
	public int hashCode() {
		return Objects.hash(platform, properties);
	}

	@Override
	public String toString() {
		return "Context{" +
				"platform='" + platform + '\'' +
				", properties=" + properties +
				'}';
	}

	public static ContextBuilder builder() {
		return new ContextBuilder();
	}

	protected static abstract class ContextBaseBuilder<R extends Context, B extends ContextBaseBuilder<R, B>> {
		private final B thisObj;

		protected String platform;
		protected Map<String, Object> properties = new HashMap<>();

		protected ContextBaseBuilder() {
			this.thisObj = createBuilder();
		}

		/**
		 * Provide the concrete builder.
		 *
		 * @return the builder
		 */
		protected abstract B createBuilder();

		public B platform(String platform) {
			this.platform = platform;
			return thisObj;
		}

		public B properties(Map<String, Object> properties) {
			this.properties.putAll(properties);
			return thisObj;
		}

		public B property(String key, Object value) {
			this.properties.put(key, value);
			return thisObj;
		}

		public abstract R build();
	}

	public static class ContextBuilder extends ContextBaseBuilder<Context, ContextBuilder> {
		ContextBuilder() {
		}

		@Override
		protected ContextBuilder createBuilder() {
			return this;
		}

		public Context build() {
			return new Context(platform, properties);
		}
	}
}
