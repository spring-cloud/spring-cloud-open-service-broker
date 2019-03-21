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

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Details of a response to a request to retrieve a service instance binding.
 *
 * Service brokers will typically construct one of the subtypes of this class to build a response.
 * 
 * @author Scott Frederick
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GetServiceInstanceBindingResponse {
	private final Map<String, Object> parameters;

	protected GetServiceInstanceBindingResponse(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Get any parameters passed by the user at service binding creation.
	 *
	 * @return the populated {@literal Map}
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GetServiceInstanceBindingResponse)) return false;
		GetServiceInstanceBindingResponse that = (GetServiceInstanceBindingResponse) o;
		return that.canEqual(this) &&
				Objects.equals(parameters, that.parameters);
	}

	public boolean canEqual(Object other) {
		return (other instanceof GetServiceInstanceBindingResponse);
	}

	@Override
	public int hashCode() {
		return Objects.hash(parameters);
	}

	@Override
	public String toString() {
		return "GetServiceInstanceBindingResponse{" +
				"parameters=" + parameters +
				'}';
	}
}
