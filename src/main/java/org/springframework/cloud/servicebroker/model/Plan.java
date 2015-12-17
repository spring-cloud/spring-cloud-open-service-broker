package org.springframework.cloud.servicebroker.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A service plan available for a ServiceDefinition
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@Getter
@ToString
@EqualsAndHashCode
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Plan {

	/**
	 * An identifier used to correlate this plan in future requests to the catalog. This must be unique within
	 * a Cloud Foundry deployment. Using a GUID is recommended.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("id")
	private String id;

	/**
	 * A CLI-friendly name of the plan that will appear in the catalog. The value should be all lowercase,
	 * with no spaces.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("name")
	private String name;

	/**
	 * A user-friendly short description of the plan that will appear in the catalog.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("description")
	private String description;

	/**
	 * A map of metadata to further describe a service plan.
	 */
	@JsonSerialize(nullsUsing = EmptyMapSerializer.class)
	@JsonProperty("metadata")
	private Map<String, Object> metadata;

	/**
	 * Indicates whether the plan can be limited by the non_basic_services_allowed field in a Cloud Foundry Quota.
	 */
	@JsonSerialize
	@JsonProperty("free")
	private boolean free;

	public Plan() {
	}

	public Plan(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Plan(String id, String name, String description, Map<String, Object> metadata) {
		this(id, name, description);
		this.metadata = metadata;
	}

	public Plan(String id, String name, String description, Map<String, Object> metadata, boolean free) {
		this(id, name, description, metadata);
		this.free = free;
	}
}
