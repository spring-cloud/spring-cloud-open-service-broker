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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

/**
 * Details of a response to a request to create a new service instance binding for a route.
 *
 * @author Scott Frederick
 */
public class CreateServiceInstanceRouteBindingResponse extends CreateServiceInstanceBindingResponse {
	/**
	 * A URL to which Cloud Foundry should proxy requests for the bound route. Can be <code>null</code>.
	 */
	@JsonSerialize
	@JsonProperty("route_service_url")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final String routeServiceUrl;

	private CreateServiceInstanceRouteBindingResponse(boolean bindingExisted, String routeServiceUrl) {
		super(bindingExisted);
		this.routeServiceUrl = routeServiceUrl;
	}

	public String getRouteServiceUrl() {
		return this.routeServiceUrl;
	}

	public static CreateServiceInstanceRouteBindingResponseBuilder builder() {
		return new CreateServiceInstanceRouteBindingResponseBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CreateServiceInstanceRouteBindingResponse)) return false;
		if (!super.equals(o)) return false;
		CreateServiceInstanceRouteBindingResponse that = (CreateServiceInstanceRouteBindingResponse) o;
		return Objects.equals(routeServiceUrl, that.routeServiceUrl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), routeServiceUrl);
	}

	@Override
	public String toString() {
		return super.toString() +
				"CreateServiceInstanceRouteBindingResponse{" +
				"routeServiceUrl='" + routeServiceUrl + '\'' +
				'}';
	}

	public static class CreateServiceInstanceRouteBindingResponseBuilder {
		private String routeServiceUrl;
		private boolean bindingExisted;

		CreateServiceInstanceRouteBindingResponseBuilder() {
		}

		public CreateServiceInstanceRouteBindingResponseBuilder routeServiceUrl(String routeServiceUrl) {
			this.routeServiceUrl = routeServiceUrl;
			return this;
		}

		public CreateServiceInstanceRouteBindingResponseBuilder bindingExisted(boolean bindingExisted) {
			this.bindingExisted = bindingExisted;
			return this;
		}

		public CreateServiceInstanceRouteBindingResponse build() {
			return new CreateServiceInstanceRouteBindingResponse(bindingExisted, routeServiceUrl);
		}
	}
}
