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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Details of a response to a request to create a new service instance binding for a route.
 *
 * @author Scott Frederick
 */
public class GetServiceInstanceRouteBindingResponse extends GetServiceInstanceBindingResponse {
	/**
	 * A URL to which the platform should proxy requests for the bound route. Can be <code>null</code>.
	 */
	private final String routeServiceUrl;

	GetServiceInstanceRouteBindingResponse(Map<String, Object> parameters, String routeServiceUrl) {
		super(parameters);
		this.routeServiceUrl = routeServiceUrl;
	}

	public String getRouteServiceUrl() {
		return this.routeServiceUrl;
	}

	public static GetServiceInstanceRouteBindingResponseBuilder builder() {
		return new GetServiceInstanceRouteBindingResponseBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GetServiceInstanceRouteBindingResponse)) return false;
		if (!super.equals(o)) return false;
		GetServiceInstanceRouteBindingResponse that = (GetServiceInstanceRouteBindingResponse) o;
		return Objects.equals(routeServiceUrl, that.routeServiceUrl);
	}

	@Override
	public final boolean canEqual(Object other) {
		return (other instanceof GetServiceInstanceRouteBindingResponse);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), routeServiceUrl);
	}

	@Override
	public String toString() {
		return super.toString() +
				"GetServiceInstanceRouteBindingResponse{" +
				"routeServiceUrl='" + routeServiceUrl + '\'' +
				'}';
	}

	public static class GetServiceInstanceRouteBindingResponseBuilder {
		private String routeServiceUrl;
		private Map<String, Object> parameters = new HashMap<>();

		GetServiceInstanceRouteBindingResponseBuilder() {
		}

		public GetServiceInstanceRouteBindingResponseBuilder routeServiceUrl(String routeServiceUrl) {
			this.routeServiceUrl = routeServiceUrl;
			return this;
		}

		public GetServiceInstanceRouteBindingResponseBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		public GetServiceInstanceRouteBindingResponseBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		public GetServiceInstanceRouteBindingResponse build() {
			return new GetServiceInstanceRouteBindingResponse(parameters, routeServiceUrl);
		}
	}
}
