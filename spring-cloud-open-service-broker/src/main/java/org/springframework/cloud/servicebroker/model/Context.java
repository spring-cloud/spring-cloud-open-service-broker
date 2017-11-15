package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.cloud.servicebroker.model.CloudFoundryContext.CLOUD_FOUNDRY_PLATFORM;
import static org.springframework.cloud.servicebroker.model.KubernetesContext.KUBERNETES_PLATFORM;

/**
 * Platform specific contextual information under which the service instance is to be provisioned or updated. Fields
 * known by concrete subtypes will be parsed into discrete properties of the appropriate subtype. Any additional
 * properties will available using {@link #getProperty(String)}.
 *
 * @author Scott Frederick
 */
@ToString
@EqualsAndHashCode
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
	@Getter
	@JsonProperty("platform")
	private String platform;

	private Map<String, Object> properties = new HashMap<>();

	public Context() {
	}

	public Context(String platform, Map<String, Object> properties) {
		this.platform = platform;
		if (properties != null) {
			this.properties.putAll(properties);
		}
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
