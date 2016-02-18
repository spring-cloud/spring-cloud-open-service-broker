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
 * Details of a response to a request to create a new service instance binding for an application.
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
public class CreateServiceInstanceAppBindingResponse implements CreateServiceInstanceBindingResponse {

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

	public CreateServiceInstanceAppBindingResponse() {
		this.credentials = null;
		this.syslogDrainUrl = null;
	}

	public CreateServiceInstanceAppBindingResponse(Map<String, Object> credentials) {
		this(credentials, null);
	}

	public CreateServiceInstanceAppBindingResponse(Map<String, Object> credentials, String syslogDrainUrl) {
		this.credentials = credentials;
		this.syslogDrainUrl = syslogDrainUrl;
	}
}
