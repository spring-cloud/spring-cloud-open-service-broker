/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.instance;

import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;

import java.util.Objects;

/**
 * Details of a request to get the details a service instance.
 *
 * @author Scott Frederick
 */
public class GetServiceInstanceRequest  extends ServiceBrokerRequest {
	/**
	 * The Cloud Controller GUID of the service instance to retrieve.
	 */
	private transient String serviceInstanceId;

	public GetServiceInstanceRequest(String serviceInstanceId, String platformInstanceId,
									 String apiInfoLocation, Context originatingIdentity) {
		super(platformInstanceId, apiInfoLocation, originatingIdentity);
		this.serviceInstanceId = serviceInstanceId;
	}

	public String getServiceInstanceId() {
		return this.serviceInstanceId;
	}

	public static GetServiceInstanceRequestBuilder builder() {
		return new GetServiceInstanceRequestBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GetServiceInstanceRequest)) return false;
		if (!super.equals(o)) return false;
		GetServiceInstanceRequest that = (GetServiceInstanceRequest) o;
		return Objects.equals(serviceInstanceId, that.serviceInstanceId);
	}

	@Override
	public final boolean canEqual(Object other) {
		return (other instanceof GetServiceInstanceRequest);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceInstanceId);
	}

	@Override
	public String toString() {
		return super.toString() +
				"GetServiceInstanceRequest{" +
				"serviceInstanceId='" + serviceInstanceId + '\'' +
				'}';
	}

	public static class GetServiceInstanceRequestBuilder {
		private String serviceInstanceId;
		private String platformInstanceId;
		private String apiInfoLocation;
		private Context originatingIdentity;

		GetServiceInstanceRequestBuilder() {
		}

		public GetServiceInstanceRequestBuilder serviceInstanceId(String serviceInstanceId) {
			this.serviceInstanceId = serviceInstanceId;
			return this;
		}

		public GetServiceInstanceRequestBuilder platformInstanceId(String platformInstanceId) {
			this.platformInstanceId = platformInstanceId;
			return this;
		}

		public GetServiceInstanceRequestBuilder apiInfoLocation(String apiInfoLocation) {
			this.apiInfoLocation = apiInfoLocation;
			return this;
		}

		public GetServiceInstanceRequestBuilder originatingIdentity(Context originatingIdentity) {
			this.originatingIdentity = originatingIdentity;
			return this;
		}

		public GetServiceInstanceRequest build() {
			return new GetServiceInstanceRequest(serviceInstanceId,
					platformInstanceId, apiInfoLocation, originatingIdentity);
		}
	}

}
