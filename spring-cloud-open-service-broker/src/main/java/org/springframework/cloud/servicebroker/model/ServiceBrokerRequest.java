package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details common to all service broker requests.
 *
 * @author Scott Frederick
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class ServiceBrokerRequest {
	public final static String API_INFO_LOCATION_HEADER = "X-Api-Info-Location";
	public final static String ORIGINATING_IDENTITY_HEADER = "X-Broker-API-Originating-Identity";

	/**
	 * The ID used to identify the platform instance when the service broker is registered
	 * to multiple instances. Will be <code>null</code> if the service broker is not registered with an instance ID
	 * in the registered URL.
	 */
	@JsonIgnore
	protected transient String cfInstanceId;

	/**
	 * The API info endpoint of the platform instance making the call to the service broker.
	 */
	@JsonIgnore
	protected transient String apiInfoLocation;

	/**
	 * The identity of the of the user that initiated the request from the platform.
	 */
	@JsonIgnore
	protected transient Context originatingIdentity;


	public ServiceBrokerRequest() {
		this.cfInstanceId = null;
		this.apiInfoLocation = null;
		this.originatingIdentity = null;
	}
}
