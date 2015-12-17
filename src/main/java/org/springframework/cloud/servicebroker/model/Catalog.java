package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * The catalog of services offered by the service broker.
 * 
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@Getter
@ToString
@EqualsAndHashCode
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Catalog {

	/**
	 * A list of service offerings provided by the service broker.
	 */
	@NotEmpty
	@JsonSerialize(nullsUsing = EmptyListSerializer.class)
	@JsonProperty("services")
	private final List<ServiceDefinition> serviceDefinitions;

	public Catalog() {
		this.serviceDefinitions = null;
	}

	public Catalog(List<ServiceDefinition> serviceDefinitions) {
		this.serviceDefinitions = serviceDefinitions;
	}
}

