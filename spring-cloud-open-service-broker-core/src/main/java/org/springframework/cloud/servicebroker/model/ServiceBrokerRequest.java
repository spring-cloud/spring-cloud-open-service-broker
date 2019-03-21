/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model;

import java.util.Objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Details common to all service broker requests.
 *
 * @author Scott Frederick
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ServiceBrokerRequest {
	public final static String API_INFO_LOCATION_HEADER = "X-Api-Info-Location";
	public final static String ORIGINATING_IDENTITY_HEADER = "X-Broker-API-Originating-Identity";
	public static final String INSTANCE_ID_PATH_VARIABLE = "instanceId";
	public static final String BINDING_ID_PATH_VARIABLE = "bindingId";
	public static final String SERVICE_ID_PARAMETER = "service_id";
	public static final String PLAN_ID_PARAMETER = "plan_id";
	public static final String PLATFORM_INSTANCE_ID_VARIABLE = "platformInstanceId";

	protected transient String platformInstanceId;

	protected transient String apiInfoLocation;

	protected transient Context originatingIdentity;

	public ServiceBrokerRequest() {
	}

	protected ServiceBrokerRequest(String platformInstanceId, String apiInfoLocation, Context originatingIdentity) {
		this.platformInstanceId = platformInstanceId;
		this.apiInfoLocation = apiInfoLocation;
		this.originatingIdentity = originatingIdentity;
	}

	/**
	 * Get the ID used to identify the platform instance.
	 *
	 * This is useful when the service broker is registered to multiple instances of a platform.
	 *
	 * This value is set from any path element that precedes {@literal /v2} in the request from the platform.
	 * Will be {@literal null} if the service broker is not registered with an instance ID in the registration URL.
	 *
	 * @return the platform instance ID, or {@literal null} if not provided
	 */
	public String getPlatformInstanceId() {
		return this.platformInstanceId;
	}

	/**
	 * This method is intended to be used internally only; use a {@literal builder} to construct an object of this
	 * type and set all field values.
	 *
	 * @param platformInstanceId the platform instance ID
	 */
	public void setPlatformInstanceId(String platformInstanceId) {
		this.platformInstanceId = platformInstanceId;
	}

	/**
	 * Get the location of the API info endpoint of the platform instance.
	 *
	 * This endpoint can be used to retrieve additional information about the platform making the request on platforms
	 * that support the header.
	 *
	 * This value is set from the {@literal X-Api-Info-Location} header in the request from the platform.
	 *
	 * @return the API info endpoint location, or {@literal null} if not provided
	 */
	public String getApiInfoLocation() {
		return this.apiInfoLocation;
	}

	/**
	 * This method is intended to be used internally only; use a {@literal builder} to construct an object of this
	 * type and set all field values.
	 *
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 */
	public void setApiInfoLocation(String apiInfoLocation) {
		this.apiInfoLocation = apiInfoLocation;
	}

	/**
	 * Get the identity of the user that initiated the request from the platform.
	 *
	 * This value is set from the {@literal X-Broker-API-Originating-Identity} header in the request from the platform.
	 *
	 * @return the user identity, or {@literal null} if not provided
	 */
	public Context getOriginatingIdentity() {
		return this.originatingIdentity;
	}

	/**
	 * This method is intended to be used internally only; use a {@literal builder} to construct an object of this
	 * type and set all field values.
	 *
	 * @param originatingIdentity identity of the user that initiated the request from the platform
	 */
	public void setOriginatingIdentity(Context originatingIdentity) {
		this.originatingIdentity = originatingIdentity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ServiceBrokerRequest)) return false;
		ServiceBrokerRequest that = (ServiceBrokerRequest) o;
		return that.canEqual(this) &&
				Objects.equals(platformInstanceId, that.platformInstanceId) &&
				Objects.equals(apiInfoLocation, that.apiInfoLocation) &&
				Objects.equals(originatingIdentity, that.originatingIdentity);
	}

	public boolean canEqual(Object other) {
		return (other instanceof ServiceBrokerRequest);
	}

	@Override
	public int hashCode() {
		return Objects.hash(platformInstanceId, apiInfoLocation, originatingIdentity);
	}

	@Override
	public String toString() {
		return "ServiceBrokerRequest{" +
				"platformInstanceId='" + platformInstanceId + '\'' +
				", apiInfoLocation='" + apiInfoLocation + '\'' +
				", originatingIdentity=" + originatingIdentity +
				'}';
	}

}
