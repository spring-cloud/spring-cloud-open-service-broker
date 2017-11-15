package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

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
