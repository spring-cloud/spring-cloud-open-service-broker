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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class BindResource {
	/**
	 * The GUID of an application associated with the binding. May be provided for credentials bindings.
	 */
	@Getter
	@JsonProperty("app_guid")
	private final String appGuid;

	/**
	 * The URL of an application to be intermediated. May be provided for route services bindings.
	 */
	@Getter
	@JsonProperty
	private final String route;

	private Map<String, Object> properties = new HashMap<>();

	public BindResource() {
		this.appGuid = null;
		this.route = null;
	}

	public BindResource(String appGuid, String route, Map<String, Object> properties) {
		this.appGuid = appGuid;
		this.route = route;
		if (properties != null) {
			this.properties.putAll(properties);
		}
	}

	public BindResource(Map<String, Object> properties) {
		this(null, null, properties);
	}

	@JsonAnySetter
	private void setProperty(String key, Object value) {
		properties.put(key, value);
	}

	/**
	 * Get the value of a field in the request with the given key.
	 *
	 * @param key the key of the value to retrieve
	 * @return the value of the field, or {@literal null} if the key is not present in the request
	 */
	public Object getProperty(String key) {
		return properties.get(key);
	}
}
