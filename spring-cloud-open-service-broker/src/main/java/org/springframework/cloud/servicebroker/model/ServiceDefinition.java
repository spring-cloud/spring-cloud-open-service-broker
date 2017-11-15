package org.springframework.cloud.servicebroker.model;

import java.util.List;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A service offered by this broker.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@Getter
@ToString
@EqualsAndHashCode
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceDefinition {
	/**
	 * An identifier used to correlate this service in future requests to the catalog. This must be unique within
	 * a Cloud Foundry deployment. Using a GUID is recommended.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("id")
	private String id;

	/**
	 * A CLI-friendly name of the service that will appear in the catalog. The value should be all lowercase,
	 * with no spaces.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("name")
	private String name;

	/**
	 * A user-friendly short description of the service that will appear in the catalog.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("description")
	private String description;

	/**
	 * Indicates whether the service can be bound to applications.
	 */
	@JsonSerialize
	@JsonProperty("bindable")
	private boolean bindable;

	/**
	 * Indicates whether the service supports requests to update instances to use a different plan from the one
	 * used to provision a service instance.
	 */
	@JsonSerialize
	@JsonProperty("plan_updateable")
	private boolean planUpdateable;

	/**
	 * A list of plans for this service.
	 */
	@NotEmpty
	@JsonSerialize(nullsUsing = EmptyListSerializer.class)
	@JsonProperty("plans")
	private List<Plan> plans;

	/**
	 * A list of tags to aid in categorizing and classifying services with similar characteristics.
	 */
	@JsonSerialize(nullsUsing = EmptyListSerializer.class)
	@JsonProperty("tags")
	private List<String> tags;

	/**
	 * A map of metadata to further describe a service offering.
	 */
	@JsonSerialize(nullsUsing = EmptyMapSerializer.class)
	@JsonProperty("metadata")
	private Map<String, Object> metadata;

	/**
	 * A list of permissions that the user would have to give the service, if they provision it. See
	 * {@link ServiceDefinitionRequires} for supported permissions.
	 */
	@JsonSerialize(nullsUsing = EmptyListSerializer.class)
	@JsonProperty("requires")
	private List<String> requires;

	/**
	 * Data necessary to activate the Dashboard SSO feature for this service.
	 */
	@JsonSerialize
	@JsonProperty("dashboard_client")
	private DashboardClient dashboardClient;

	public ServiceDefinition() {
	}

	public ServiceDefinition(String id, String name, String description, boolean bindable, List<Plan> plans) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.bindable = bindable;
		this.plans = plans;
	}

	public ServiceDefinition(String id, String name, String description, boolean bindable, boolean planUpdateable,
							 List<Plan> plans, List<String> tags, Map<String, Object> metadata, List<String> requires,
							 DashboardClient dashboardClient) {
		this(id, name, description, bindable, plans);
		this.tags = tags;
		this.metadata = metadata;
		this.requires = requires;
		this.planUpdateable = planUpdateable;
		this.dashboardClient = dashboardClient;
	}
}
