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

package org.springframework.cloud.servicebroker.model.binding;

import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;

import java.util.Objects;

public class GetServiceInstanceBindingRequest extends ServiceBrokerRequest {
	/**
	 * The GUID of the service instance to retrieve.
	 */
	private transient String serviceInstanceId;

	/**
	 * The GUID of the service binding to retrieve.
	 */
	private transient String bindingId;

	GetServiceInstanceBindingRequest(String serviceInstanceId, String bindingId,
									String platformInstanceId, String apiInfoLocation, Context originatingIdentity) {
		super(platformInstanceId, apiInfoLocation, originatingIdentity);
		this.serviceInstanceId = serviceInstanceId;
		this.bindingId = bindingId;
	}

	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public String getBindingId() {
		return bindingId;
	}

	public static GetServiceInstanceBindingRequestBuilder builder() {
		return new GetServiceInstanceBindingRequestBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GetServiceInstanceBindingRequest)) return false;
		if (!super.equals(o)) return false;
		GetServiceInstanceBindingRequest that = (GetServiceInstanceBindingRequest) o;
		return Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(bindingId, that.bindingId);
	}

	@Override
	public final boolean canEqual(Object other) {
		return (other instanceof GetServiceInstanceBindingRequest);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceInstanceId, bindingId);
	}

	@Override
	public String toString() {
		return super.toString() +
				"GetServiceInstanceBindingRequest{" +
				"serviceInstanceId='" + serviceInstanceId + '\'' +
				", bindingId='" + bindingId + '\'' +
				'}';
	}

	public static class GetServiceInstanceBindingRequestBuilder {
		private String serviceInstanceId;
		private String bindingId;
		private String platformInstanceId;
		private String apiInfoLocation;
		private Context originatingIdentity;

		GetServiceInstanceBindingRequestBuilder() {
		}

		public GetServiceInstanceBindingRequestBuilder serviceInstanceId(String serviceInstanceId) {
			this.serviceInstanceId = serviceInstanceId;
			return this;
		}

		public GetServiceInstanceBindingRequestBuilder bindingId(String bindingId) {
			this.bindingId = bindingId;
			return this;
		}

		public GetServiceInstanceBindingRequestBuilder platformInstanceId(String platformInstanceId) {
			this.platformInstanceId = platformInstanceId;
			return this;
		}

		public GetServiceInstanceBindingRequestBuilder apiInfoLocation(String apiInfoLocation) {
			this.apiInfoLocation = apiInfoLocation;
			return this;
		}

		public GetServiceInstanceBindingRequestBuilder originatingIdentity(Context originatingIdentity) {
			this.originatingIdentity = originatingIdentity;
			return this;
		}

		public GetServiceInstanceBindingRequest build() {
			return new GetServiceInstanceBindingRequest(serviceInstanceId, bindingId,
					platformInstanceId, apiInfoLocation, originatingIdentity);
		}
	}
}
