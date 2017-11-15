package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Service binding JSON Schemas.
 *
 * @author sgunaratne@pivotal.io
 * @author Sam Gunaratne
 */
@Getter
@ToString
@EqualsAndHashCode
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceBindingSchema {

	/**
	 * The JSON schema for configuration parameters when creating a service binding.
	 */
	@JsonSerialize
	@JsonProperty("create")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private MethodSchema createMethodSchema;

	public ServiceBindingSchema() {
		createMethodSchema = null;
	}

	public ServiceBindingSchema(MethodSchema createMethodSchema) {
		this.createMethodSchema = createMethodSchema;
	}
}
