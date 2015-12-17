package org.springframework.cloud.servicebroker.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Details of a response to a request to create a new service instance binding.
 * 
 * @author sgreenberg@pivotal.io
 * @author <A href="mailto:josh@joshlong.com">Josh Long</A>
 * @author Scott Frederick
 */
@Getter
@ToString
@EqualsAndHashCode
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateServiceInstanceBindingResponse {

	/**
	 * A free-form hash of credentials that the bound application can use to access the service.
	 */
	@JsonSerialize
	@JsonProperty("credentials")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final Map<String, Object> credentials;

	/**
	 * The URL to which Cloud Foundry should drain logs for the bound application. Can be <code>null</code> to
	 * indicate that the service binding does not support syslog drains.
	 */
	@JsonSerialize
	@JsonProperty("syslog_drain_url")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final String syslogDrainUrl;

	/**
	 * A URL to which Cloud Foundry should proxy requests for the bound route. Can be <code>null</code>.
	 */
	@JsonSerialize
	@JsonProperty("route_service_url")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final String routeServiceUrl;

	public CreateServiceInstanceBindingResponse() {
		this.routeServiceUrl = null;
		this.credentials = null;
		this.syslogDrainUrl = null;
	}

	public CreateServiceInstanceBindingResponse(Map<String, Object> credentials, String syslogDrainUrl) {
		this.credentials = credentials;
		this.syslogDrainUrl = syslogDrainUrl;
		this.routeServiceUrl = null;
	}

	public CreateServiceInstanceBindingResponse(Map<String, Object> credentials) {
		this(credentials, null);
	}

	public CreateServiceInstanceBindingResponse(String routeServiceUrl) {
		this.routeServiceUrl = routeServiceUrl;
		this.credentials = null;
		this.syslogDrainUrl = null;
	}
}
