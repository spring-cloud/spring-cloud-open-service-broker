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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a response to a request to create a new service instance binding for a route.
 *
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CreateServiceInstanceRouteBindingResponse extends CreateServiceInstanceBindingResponse {
	/**
	 * A URL to which Cloud Foundry should proxy requests for the bound route. Can be <code>null</code>.
	 */
	@JsonSerialize
	@JsonProperty("route_service_url")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String routeServiceUrl;

	public CreateServiceInstanceRouteBindingResponse withRouteServiceUrl(final String routeServiceUrl) {
		this.routeServiceUrl = routeServiceUrl;
		return this;
	}

	public CreateServiceInstanceRouteBindingResponse withBindingExisted(final boolean bindingExisted) {
		this.bindingExisted = bindingExisted;
		return this;
	}
}
