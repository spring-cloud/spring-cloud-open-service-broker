/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Objects;

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

	/**
	 * The ID used to identify the platform instance when the service broker is registered
	 * to multiple instances. Will be <code>null</code> if the service broker is not registered with an instance ID
	 * in the registered URL.
	 */
	protected transient String platformInstanceId;

	/**
	 * The API info endpoint of the platform instance making the call to the service broker.
	 */
	protected transient String apiInfoLocation;

	/**
	 * The identity of the of the user that initiated the request from the platform.
	 */
	protected transient Context originatingIdentity;

	public ServiceBrokerRequest() {
	}

	protected ServiceBrokerRequest(String platformInstanceId, String apiInfoLocation, Context originatingIdentity) {
		this.platformInstanceId = platformInstanceId;
		this.apiInfoLocation = apiInfoLocation;
		this.originatingIdentity = originatingIdentity;
	}

	public String getPlatformInstanceId() {
		return this.platformInstanceId;
	}

	public void setPlatformInstanceId(String platformInstanceId) {
		this.platformInstanceId = platformInstanceId;
	}

	public String getApiInfoLocation() {
		return this.apiInfoLocation;
	}

	public void setApiInfoLocation(String apiInfoLocation) {
		this.apiInfoLocation = apiInfoLocation;
	}

	public Context getOriginatingIdentity() {
		return this.originatingIdentity;
	}

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
