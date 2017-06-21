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
 * JSON Schemas available for a Plan.
 *
 * @author sgunaratne@pivotal.io
 * @author Sam Gunaratne
 */
@Getter
@ToString
@EqualsAndHashCode
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Schemas {

	/**
	 * The schemas available on a service instance.
	 */
	@JsonSerialize
	@JsonProperty("service_instance")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ServiceInstanceSchema serviceInstanceSchema;

	/**
	 * The schemas available on a service binding.
	 */
	@JsonSerialize
	@JsonProperty("service_binding")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ServiceBindingSchema serviceBindingSchema;

	public Schemas() {
		serviceInstanceSchema = null;
        serviceBindingSchema = null;
    }

	public Schemas(ServiceInstanceSchema serviceInstanceSchema,
			ServiceBindingSchema serviceBindingSchema) {
		this.serviceInstanceSchema = serviceInstanceSchema;
		this.serviceBindingSchema = serviceBindingSchema;
	}
}
