package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a response to a request to create a new service instance.
 *
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class CreateServiceInstanceResponse extends AsyncServiceInstanceResponse {
	/**
	 * The URL of a web-based management user interface for the service instance. Can be <code>null</code> to indicate
	 * that a management dashboard is not provided.
	 */
	@JsonSerialize
	@JsonProperty("dashboard_url")
	private String dashboardUrl;

	/**
	 * <code>true</code> to indicated that the service instance already existed with the same parameters as the
	 * requested service instance, <code>false</code> to indicate that the instance was created as new
	 */
	@JsonIgnore
	private boolean instanceExisted;

	public CreateServiceInstanceResponse withDashboardUrl(final String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
		return this;
	}

	public CreateServiceInstanceResponse withInstanceExisted(final boolean instanceExisted) {
		this.instanceExisted = instanceExisted;
		return this;
	}

	public CreateServiceInstanceResponse withAsync(final boolean async) {
		this.async = async;
		return this;
	}

	public CreateServiceInstanceResponse withOperation(final String operation) {
		this.operation = operation;
		return this;
	}
}
