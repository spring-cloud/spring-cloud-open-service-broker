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
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BindResource {
	/**
	 * The GUID of an application associated with the binding. May be provided for credentials bindings.
	 */
	private final String appGuid;

	/**
	 * The URL of an application to be intermediated. May be provided for route services bindings.
	 */
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
	 * Get the value of a field in the request with the given key.
	 *
	 * @param key the key of the value to retrieve
	 * @return the value of the field, or {@literal null} if the key is not present in the request
	 */
	public Object getProperty(String key) {
		return this.properties.get(key);
	}

	@JsonAnyGetter
	public Map<String, Object> getProperties() {
		return this.properties;
	}

	public String getAppGuid() {
		return this.appGuid;
	}

	public String getRoute() {
		return this.route;
	}

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

	public static class BindResourceBuilder {
		private String appGuid;
		private String route;
		private final Map<String, Object> parameters = new HashMap<>();

		BindResourceBuilder() {
		}

		public BindResourceBuilder appGuid(String appGuid) {
			this.appGuid = appGuid;
			return this;
		}

		public BindResourceBuilder route(String route) {
			this.route = route;
			return this;
		}

		public BindResourceBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		public BindResourceBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		public BindResource build() {
			return new BindResource(appGuid, route, parameters);
		}
	}
}
