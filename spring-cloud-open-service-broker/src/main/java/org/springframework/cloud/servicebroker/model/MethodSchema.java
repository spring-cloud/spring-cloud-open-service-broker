package org.springframework.cloud.servicebroker.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * JSON Schema for a service broker object method.
 *
 * @author sgunaratne@pivotal.io
 * @author Sam Gunaratne
 */
@Getter
@ToString
@EqualsAndHashCode
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MethodSchema {

	/**
	 * A map of JSON schema for configuration parameters.
	 */
	@JsonProperty("parameters")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, Object> configParametersSchema;

	public MethodSchema() {
		configParametersSchema = null;
	}

	public MethodSchema(Map<String, Object> configParametersSchema) {
		this.configParametersSchema = configParametersSchema;
	}
}
