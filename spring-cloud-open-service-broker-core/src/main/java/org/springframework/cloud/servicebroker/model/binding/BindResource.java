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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Details of any platform resources that a service binding will be associated with.
 *
 * <p>
 * Objects of this type are constructed by the framework from the message body passed to the
 * service broker by the platform in a service binding request.
 *
 * @see <a href=https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#bind-resource-object>Open Service Broker API specification</a>
 *
 * @author Scott Frederick
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BindResource {
	private final String appGuid;

	private final String route;

	@JsonAnySetter
	private final Map<String, Object> properties = new HashMap<>();

	BindResource() {
		this.appGuid = null;
		this.route = null;
	}

	BindResource(String appGuid, String route, Map<String, Object> properties) {
		this.appGuid = appGuid;
		this.route = route;
		if (properties != null) {
			this.properties.putAll(properties);
		}
	}

	/**
	 * Get the value of a property in the bind resource with the given key.
	 *
	 * @param key the key of the value to retrieve
	 * @return the value of the property, or {@literal null} if the key is not present in the bind resource
	 */
	public Object getProperty(String key) {
		return this.properties.get(key);
	}

	/**
	 * Get all properties in the bind resource.
	 *
	 * @return the set of bind resource properties
	 */
	@JsonAnyGetter
	public Map<String, Object> getProperties() {
		return this.properties;
	}

	/**
	 * Get the GUID of an application associated with the binding. May be provided for credentials bindings.
	 *
	 * @return the application GUID
	 */
	public String getAppGuid() {
		return this.appGuid;
	}

	/**
	 * Get the URL of an application to be intermediated. May be provided for route services bindings.
	 *
	 * @return the application route
	 */
	public String getRoute() {
		return this.route;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@link BindResource}.
	 *
	 * <p>
	 * This builder is provided to support testing of
	 * {@link org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService} implementations.
	 *
	 * @return the builder
	 */
	public static BindResourceBuilder builder() {
		return new BindResourceBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BindResource)) return false;
		BindResource that = (BindResource) o;
		return Objects.equals(appGuid, that.appGuid) &&
				Objects.equals(route, that.route) &&
				Objects.equals(properties, that.properties);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(appGuid, route, properties);
	}

	@Override
	public String toString() {
		return "BindResource{" +
				"appGuid='" + appGuid + '\'' +
				", route='" + route + '\'' +
				", properties=" + properties +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@literal BindResource}.
	 */
	public static class BindResourceBuilder {
		private String appGuid;
		private String route;
		private final Map<String, Object> properties = new HashMap<>();

		BindResourceBuilder() {
		}

		/**
		 * Set an application GUID as would be provided in an app binding request from the platform.
		 *
		 * @param appGuid the application GUID
		 * @return the builder
		 */
		public BindResourceBuilder appGuid(String appGuid) {
			this.appGuid = appGuid;
			return this;
		}

		/**
		 * Set an application route as would be provided in a route binding request from the platform.
		 *
		 * @param route the application GUID
		 * @return the builder
		 */
		public BindResourceBuilder route(String route) {
			this.route = route;
			return this;
		}

		/**
		 * Add a set of properties from the provided {@literal Map} to the bind resource properties
		 * as would be provided in the request from the platform.
		 *
		 * @param properties the properties to add
		 * @return the builder
		 * @see #getProperties()
		 */
		public BindResourceBuilder properties(Map<String, Object> properties) {
			this.properties.putAll(properties);
			return this;
		}

		/**
		 * Add a key/value pair to the bind resource properties as would be provided in the request from the platform.
		 *
		 * @param key the property key to add
		 * @param value the property value to add
		 * @return the builder
		 * @see #getProperties()
		 */
		public BindResourceBuilder properties(String key, Object value) {
			this.properties.put(key, value);
			return this;
		}

		/**
		 * Construct a {@link BindResource} from the provided values.
		 *
		 * @return the newly constructed {@literal BindResource}
		 */
		public BindResource build() {
			return new BindResource(appGuid, route, properties);
		}
	}
}
