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
 * known by concrete subtypes will be parsed into discrete fields of the appropriate subtype. Any additional fields
 * will available using {@link #getField(String)}.
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

	private Map<String, Object> fields = new HashMap<>();

	public Context() {
	}

	public Context(String platform, Map<String, Object> fields) {
		this.platform = platform;
		if (fields != null) {
			this.fields.putAll(fields);
		}
	}

	@JsonAnySetter
	private void setField(String key, Object value) {
		fields.put(key, value);
	}

	/**
	 * Get the value of a field in the request with the given key.
	 *
	 * @param key the key of the value to retrieve
	 * @return the value of the field, or {@literal null} if the key is not present in the request
	 */
	public Object getField(String key) {
		return fields.get(key);
	}
}
