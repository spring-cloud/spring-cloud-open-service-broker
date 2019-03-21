/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
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
 * Details of a response to a request to create a new service instance binding associated with a route.
 *
 * <p>
 * Objects of this type are constructed by the service broker application,
 * and used to build the response to the platform.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md">Open Service Broker API specification</a>
 * 
 * @author Scott Frederick
 */
public class GetServiceInstanceRouteBindingResponse extends GetServiceInstanceBindingResponse {
	private final String routeServiceUrl;

	GetServiceInstanceRouteBindingResponse(Map<String, Object> parameters, String routeServiceUrl) {
		super(parameters);
		this.routeServiceUrl = routeServiceUrl;
	}

	GetServiceInstanceRouteBindingResponse() {
		this(new HashMap<>(), null);
	}

	/**
	 * Get a URL to which the platform should proxy requests for the bound route.
	 *
	 * @return the route service URL
	 */
	public String getRouteServiceUrl() {
		return this.routeServiceUrl;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a
	 * {@literal GetServiceInstanceRouteBindingResponse}.
	 *
	 * @return the builder
	 */
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

	/**
	 * Provides a fluent API for constructing a {@link GetServiceInstanceRouteBindingResponse}.
	 */
	public static class GetServiceInstanceRouteBindingResponseBuilder {
		private String routeServiceUrl;
		private Map<String, Object> parameters = new HashMap<>();

		GetServiceInstanceRouteBindingResponseBuilder() {
		}

		/**
		 * Set a URL to which the platform should proxy requests for the bound route. Can be {@literal null}.
		 *
		 * <p>
		 * This value will set the {@literal route_service_url} field in the body of the response to the platform
		 *
		 * @param routeServiceUrl the route service URL
		 * @return the builder
		 */
		public GetServiceInstanceRouteBindingResponseBuilder routeServiceUrl(String routeServiceUrl) {
			this.routeServiceUrl = routeServiceUrl;
			return this;
		}

		/**
		 * Add a set of parameters from the provided {@literal Map} to the request parameters
		 * as were provided by the platform at service binding creation.
		 *
		 * <p>
		 * This value will set the {@literal parameters} field in the body of the response to the platform.
		 *
		 * @param parameters the parameters to add
		 * @return the builder
		 * @see #getParameters()
		 */
		public GetServiceInstanceRouteBindingResponseBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		/**
		 * Add a key/value pair to the request parameters as were provided in the request from the platform at
		 * service binding creation.
		 *
		 * <p>
		 * This value will set the {@literal parameters} field in the body of the response to the platform.
		 *
		 * @param key the parameter key to add
		 * @param value the parameter value to add
		 * @return the builder
		 * @see #getParameters()
		 */
		public GetServiceInstanceRouteBindingResponseBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		/**
		 * Construct a {@link GetServiceInstanceRouteBindingResponse} from the provided values.
		 *
		 * @return the newly constructed {@literal GetServiceInstanceRouteBindingResponse}
		 */
		public GetServiceInstanceRouteBindingResponse build() {
			return new GetServiceInstanceRouteBindingResponse(parameters, routeServiceUrl);
		}
	}
}
