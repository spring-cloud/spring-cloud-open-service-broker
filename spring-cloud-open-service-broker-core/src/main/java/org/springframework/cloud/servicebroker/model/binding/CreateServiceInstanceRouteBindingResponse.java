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

import java.util.Objects;

/**
 * Details of a response to a request to create a new service instance binding associated with a route.
 *
 * <p>
 * Objects of this type are constructed by the service broker application,
 * and used to build the response to the platform.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#response-4">Open Service Broker API specification</a>
 * 
 * @author Scott Frederick
 */
public class CreateServiceInstanceRouteBindingResponse extends CreateServiceInstanceBindingResponse {
	private final String routeServiceUrl;

	CreateServiceInstanceRouteBindingResponse(boolean bindingExisted, String routeServiceUrl) {
		super(bindingExisted);
		this.routeServiceUrl = routeServiceUrl;
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
	 * {@literal CreateServiceInstanceRouteBindingResponse}.
	 *
	 * @return the builder
	 */
	public static CreateServiceInstanceRouteBindingResponseBuilder builder() {
		return new CreateServiceInstanceRouteBindingResponseBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CreateServiceInstanceRouteBindingResponse)) return false;
		if (!super.equals(o)) return false;
		CreateServiceInstanceRouteBindingResponse that = (CreateServiceInstanceRouteBindingResponse) o;
		return Objects.equals(routeServiceUrl, that.routeServiceUrl);
	}

	@Override
	public final boolean canEqual(Object other) {
		return (other instanceof CreateServiceInstanceRouteBindingResponse);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), routeServiceUrl);
	}

	@Override
	public String toString() {
		return super.toString() +
				"CreateServiceInstanceRouteBindingResponse{" +
				"routeServiceUrl='" + routeServiceUrl + '\'' +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link CreateServiceInstanceAppBindingResponse}.
	 */
	public static class CreateServiceInstanceRouteBindingResponseBuilder {
		private String routeServiceUrl;
		private boolean bindingExisted;

		CreateServiceInstanceRouteBindingResponseBuilder() {
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
		public CreateServiceInstanceRouteBindingResponseBuilder routeServiceUrl(String routeServiceUrl) {
			this.routeServiceUrl = routeServiceUrl;
			return this;
		}

		/**
		 * Set a boolean value indicating whether the service binding already exists with the same parameters as the
		 * requested service binding. A {@literal true} value indicates a service binding exists and no new resources
		 * were created by the service broker, <code>false</code> indicates that new resources were created.
		 *
		 * <p>
		 * This value will be used to determine the HTTP response code to the platform. A {@literal true} value will
		 * result in a response code {@literal 200 OK}, and a {@literal false} value will result in a response code
		 * {@literal 201 CREATED}.
		 *
		 * @param bindingExisted {@literal true} to indicate that the binding exists, {@literal false} otherwise
		 * @return the builder
		 */
		public CreateServiceInstanceRouteBindingResponseBuilder bindingExisted(boolean bindingExisted) {
			this.bindingExisted = bindingExisted;
			return this;
		}

		/**
		 * Construct a {@link CreateServiceInstanceRouteBindingResponse} from the provided values.
		 *
		 * @return the newly constructed {@literal CreateServiceInstanceRouteBindingResponse}
		 */
		public CreateServiceInstanceRouteBindingResponse build() {
			return new CreateServiceInstanceRouteBindingResponse(bindingExisted, routeServiceUrl);
		}
	}
}
