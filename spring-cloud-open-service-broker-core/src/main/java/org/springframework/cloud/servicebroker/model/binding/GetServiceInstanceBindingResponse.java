/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Details of a response to a request to retrieve a service instance binding.
 * <p>
 * Service brokers will typically construct one of the subtypes of this class to build a response.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GetServiceInstanceBindingResponse {

	private final Map<String, Object> parameters;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final BindingMetadata metadata;

	/**
	 * Construct a new {@link GetServiceInstanceBindingResponse}
	 *
	 * @param parameters collection of parameters
	 */
	protected GetServiceInstanceBindingResponse(Map<String, Object> parameters, BindingMetadata metadata) {
		this.parameters = parameters;
		this.metadata = metadata;
	}

	/**
	 * Get any parameters passed by the user at service binding creation.
	 *
	 * @return the populated {@literal Map}
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * Get the service instance binding metadata
	 *
	 * @return the metadata
	 */
	public BindingMetadata getMetadata() {
		return this.metadata;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof GetServiceInstanceBindingResponse)) {
			return false;
		}
		GetServiceInstanceBindingResponse that = (GetServiceInstanceBindingResponse) o;
		return that.canEqual(this) &&
				Objects.equals(parameters, that.parameters) &&
				Objects.equals(metadata,that.metadata);
	}

	/**
	 * Is another object type compatible with this object
	 *
	 * @param other the other object
	 * @return true of compatible
	 */
	public boolean canEqual(Object other) {
		return other instanceof GetServiceInstanceBindingResponse;
	}

	@Override
	public int hashCode() {
		return Objects.hash(parameters, metadata);
	}

	@Override
	public String toString() {
		return "GetServiceInstanceBindingResponse{" +
				"parameters=" + parameters +
				"metadata=" + metadata +
				'}';
	}

}
