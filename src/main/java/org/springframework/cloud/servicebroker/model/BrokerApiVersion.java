package org.springframework.cloud.servicebroker.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class BrokerApiVersion {

	public final static String DEFAULT_API_VERSION_HEADER = "X-Broker-Api-Version";
	public final static String API_VERSION_ANY = "*";
	public final static String API_VERSION_CURRENT = "2.8";

	/**
	 * The name of the HTTP header field expected to contain the API version of the service broker client.
	 */
	private final String brokerApiVersionHeader;

	/**
	 * The version of the broker API supported by the broker. A value of <code>null</code> or
	 * <code>API_VERSION_ANY</code> will disable API version validation.
	 */
	private final String apiVersion;

	public BrokerApiVersion(String brokerApiVersionHeader, String apiVersion) {
		this.brokerApiVersionHeader = brokerApiVersionHeader;
		this.apiVersion = apiVersion;
	}

	public BrokerApiVersion(String apiVersion) {
		this(DEFAULT_API_VERSION_HEADER, apiVersion);
	}

	public BrokerApiVersion() {
		this(DEFAULT_API_VERSION_HEADER, API_VERSION_ANY);
	}
}
