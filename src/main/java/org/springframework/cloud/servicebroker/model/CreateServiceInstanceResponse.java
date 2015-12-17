package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
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

	public CreateServiceInstanceResponse(String dashboardUrl, boolean async) {
		super(async);
		this.dashboardUrl = dashboardUrl;
	}

	public CreateServiceInstanceResponse() {
		this(null, false);
	}

	public CreateServiceInstanceResponse(boolean async) {
		this(null, async);
	}

	public CreateServiceInstanceResponse(String dashboardUrl) {
		this(dashboardUrl, false);
	}
}
