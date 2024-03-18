/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Details common to all service broker requests.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ServiceBrokerRequest {

	/**
	 * API Info Location header
	 */
	public final static String API_INFO_LOCATION_HEADER = "X-Api-Info-Location";

	/**
	 * API Originating Identity header
	 */
	public final static String ORIGINATING_IDENTITY_HEADER = "X-Broker-API-Originating-Identity";

	/**
	 * API Request Identity header
	 */
	public final static String REQUEST_IDENTITY_HEADER = "X-Broker-API-Request-Identity";

	/**
	 * Instance ID path variable name
	 */
	public static final String INSTANCE_ID_PATH_VARIABLE = "instanceId";

	/**
	 * Binding ID path variable name
	 */
	public static final String BINDING_ID_PATH_VARIABLE = "bindingId";

	/**
	 * Service ID parameter name
	 */
	public static final String SERVICE_ID_PARAMETER = "service_id";

	/**
	 * Plan ID parameter name
	 */
	public static final String PLAN_ID_PARAMETER = "plan_id";

	/**
	 * Platform Instance ID variable name
	 */
	public static final String PLATFORM_INSTANCE_ID_VARIABLE = "platformInstanceId";

	protected transient String platformInstanceId;

	protected transient String apiInfoLocation;

	protected transient Context originatingIdentity;

	protected transient String requestIdentity;

	/**
	 * Construct a new {@link ServiceBrokerRequest}
	 */
	public ServiceBrokerRequest() {
		// This constructor is intentionally empty to support JSON serialization
	}

	/**
	 * Construct a new {@link ServiceBrokerRequest}
	 *
	 * @param platformInstanceId the platform instance ID
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentity identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request being sent from the platform
	 */
	protected ServiceBrokerRequest(String platformInstanceId, String apiInfoLocation, Context originatingIdentity,
			String requestIdentity) {
		this.platformInstanceId = platformInstanceId;
		this.apiInfoLocation = apiInfoLocation;
		this.originatingIdentity = originatingIdentity;
		this.requestIdentity = requestIdentity;
	}

	/**
	 * Get the ID used to identify the platform instance.
	 * <p>
	 * This is useful when the service broker is registered to multiple instances of a platform.
	 * <p>
	 * This value is set from any path element that precedes {@literal /v2} in the request from the platform. Will be
	 * {@literal null} if the service broker is not registered with an instance ID in the registration URL.
	 *
	 * @return the platform instance ID, or {@literal null} if not provided
	 */
	@JsonIgnore //relative path to Osb query path, not to include in Json body
	public String getPlatformInstanceId() {
		return this.platformInstanceId;
	}

	/**
	 * This method is intended to be used internally only; use a {@literal builder} to construct an object of this type
	 * and set all field values.
	 *
	 * @param platformInstanceId the platform instance ID
	 */
	public void setPlatformInstanceId(String platformInstanceId) {
		this.platformInstanceId = platformInstanceId;
	}

	/**
	 * Get the location of the API info endpoint of the platform instance.
	 * <p>
	 * This endpoint can be used to retrieve additional information about the platform making the request on platforms
	 * that support the header.
	 * <p>
	 * This value is set from the {@literal X-Api-Info-Location} header in the request from the platform.
	 *
	 * @return the API info endpoint location, or {@literal null} if not provided
	 */
	@JsonIgnore //mapped as X-Api-Info-Location Header
	public String getApiInfoLocation() {
		return this.apiInfoLocation;
	}

	/**
	 * This method is intended to be used internally only; use a {@literal builder} to construct an object of this type
	 * and set all field values.
	 *
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 */
	public void setApiInfoLocation(String apiInfoLocation) {
		this.apiInfoLocation = apiInfoLocation;
	}

	/**
	 * Get the identity of the user that initiated the request from the platform.
	 * <p>
	 * This value is set from the {@literal X-Broker-API-Originating-Identity} header in the request from the platform.
	 *
	 * @return the user identity, or {@literal null} if not provided
	 */
	@JsonIgnore //mapped as X-Broker-API-Originating-Identity Header
	public Context getOriginatingIdentity() {
		return this.originatingIdentity;
	}

	/**
	 * This method is intended to be used internally only; use a {@literal builder} to construct an object of this type
	 * and set all field values.
	 *
	 * @param originatingIdentity identity of the user that initiated the request from the platform
	 */
	public void setOriginatingIdentity(Context originatingIdentity) {
		this.originatingIdentity = originatingIdentity;
	}

	/**
	 * Get the identify of the request that was sent from the platform
	 * <p>
	 * This value is set from the {@literal X-Broker-API-Request-Identity} header in the request from the platform
	 *
	 * @return the request identity, or {@literal null} if not provided
	 */
	@JsonIgnore //mapped as X-Broker-API-Request-Identity Header
	public String getRequestIdentity() {
		return this.requestIdentity;
	}

	/**
	 * For internal use only; use a {@literal builder} to construct an object of this type and set all field values.
	 *
	 * @param requestIdentity identify of the request sent from the platform
	 */
	public void setRequestIdentity(String requestIdentity) {
		this.requestIdentity = requestIdentity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ServiceBrokerRequest)) {
			return false;
		}
		ServiceBrokerRequest that = (ServiceBrokerRequest) o;
		return that.canEqual(this) &&
				Objects.equals(platformInstanceId, that.platformInstanceId) &&
				Objects.equals(apiInfoLocation, that.apiInfoLocation) &&
				Objects.equals(originatingIdentity, that.originatingIdentity) &&
				Objects.equals(requestIdentity, that.requestIdentity);
	}

	/**
	 * Is another object type compatible with this object
	 *
	 * @param other the other object
	 * @return true of compatible
	 */
	public boolean canEqual(Object other) {
		return other instanceof ServiceBrokerRequest;
	}

	@Override
	public int hashCode() {
		return Objects.hash(platformInstanceId, apiInfoLocation, originatingIdentity, requestIdentity);
	}

	@Override
	public String toString() {
		return "ServiceBrokerRequest{" +
				"platformInstanceId='" + platformInstanceId + '\'' +
				", apiInfoLocation='" + apiInfoLocation + '\'' +
				", originatingIdentity=" + originatingIdentity + '\'' +
				", requestIdentity=" + requestIdentity +
				'}';
	}

}
