package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * JSON Schema for a service broker object method.
 *
 * @author Sam Gunaratne
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MethodSchema {

	/**
	 * A map of JSON schema for configuration parameters.
	 */
	@JsonProperty("parameters")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final Map<String, Object> configParametersSchema;

	private MethodSchema(Map<String, Object> configParametersSchema) {
		this.configParametersSchema = configParametersSchema;
	}

	public Map<String, Object> getConfigParametersSchema() {
		return this.configParametersSchema;
	}

	public static MethodSchemaBuilder builder() {
		return new MethodSchemaBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MethodSchema)) return false;
		MethodSchema that = (MethodSchema) o;
		return Objects.equals(configParametersSchema, that.configParametersSchema);
	}

	@Override
	public int hashCode() {
		return Objects.hash(configParametersSchema);
	}

	@Override
	public String toString() {
		return "MethodSchema{" +
				"configParametersSchema=" + configParametersSchema +
				'}';
	}

	public static class MethodSchemaBuilder {
		private Map<String, Object> configParametersSchema = new HashMap<>();

		MethodSchemaBuilder() {
		}

		public MethodSchema.MethodSchemaBuilder configParametersSchema(Map<String, Object> configParametersSchema) {
			this.configParametersSchema.putAll(configParametersSchema);
			return this;
		}

		public MethodSchema.MethodSchemaBuilder configParametersSchema(String key, Object value) {
			this.configParametersSchema.put(key, value);
			return this;
		}

		public MethodSchema build() {
			return new MethodSchema(configParametersSchema);
		}
	}
}
