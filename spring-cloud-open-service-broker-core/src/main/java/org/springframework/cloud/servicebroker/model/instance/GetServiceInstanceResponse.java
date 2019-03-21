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

package org.springframework.cloud.servicebroker.model.instance;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Details of a response to a request to get the details a service instance.
 *
 * <p>
 * Objects of this type are constructed by the service broker application,
 * and used to build the response to the platform.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md">Open Service Broker API specification</a>
 *
 * @author Scott Frederick
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetServiceInstanceResponse {
	@JsonProperty("service_id")
	private final String serviceDefinitionId;
	
	private final String planId;

	private final String dashboardUrl;

	private final Map<String, Object> parameters;

	GetServiceInstanceResponse(String serviceDefinitionId, String planId, String dashboardUrl, Map<String, Object> parameters) {
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.dashboardUrl = dashboardUrl;
		this.parameters = parameters;
	}

	GetServiceInstanceResponse() {
		this(null, null, null, new Hashtable<>());
	}

	/**
	 * Get the ID of the service definition associated with the service instance.
	 *
	 * @return the service definition ID
	 */
	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	/**
	 * Get the ID of the plan associated with the service instance.
	 *
	 * @return the plan ID
	 */
	public String getPlanId() {
		return planId;
	}

	/**
	 * Get the URL of a web-based management user interface for the service instance.
	 *
	 * @return the dashboard URL, or {@literal null} if not provided
	 */
	public String getDashboardUrl() {
		return dashboardUrl;
	}

	/**
	 * Get any parameters provided to the service broker when the service instance was created.
	 *
	 * @return the parameters, or {@literal null} if parameters were not provided at creation or retrieval
	 * of parameters is not supported by the service broker
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal GetServiceInstanceResponse}.
	 *
	 * @return the builder
	 */
	public static GetServiceInstanceResponseBuilder builder() {
		return new GetServiceInstanceResponseBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GetServiceInstanceResponse)) return false;
		GetServiceInstanceResponse that = (GetServiceInstanceResponse) o;
		return that.canEqual(this) &&
				Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId) &&
				Objects.equals(dashboardUrl, that.dashboardUrl) &&
				Objects.equals(parameters, that.parameters);
	}

	public final boolean canEqual(Object other) {
		return (other instanceof GetServiceInstanceResponse);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(serviceDefinitionId, planId, dashboardUrl, parameters);
	}

	@Override
	public String toString() {
		return "GetServiceInstanceResponse{" +
				"serviceDefinitionId='" + serviceDefinitionId + '\'' +
				", planId='" + planId + '\'' +
				", dashboardUrl='" + dashboardUrl + '\'' +
				", parameters=" + parameters +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link GetServiceInstanceResponse}.
	 */
	public static class GetServiceInstanceResponseBuilder {
		private String serviceDefinitionId;
		private String planId;
		private String dashboardUrl;
		private Map<String, Object> parameters = new HashMap<>();

		GetServiceInstanceResponseBuilder() {
		}

		/**
		 * Set the ID of the service definition associated with the service instance.
		 *
		 * <p>
		 * This value will set the {@literal service_id} field in the body of the response to the platform.
		 *
		 * @param serviceDefinitionId the service definition ID
		 * @return the builder
		 */
		public GetServiceInstanceResponseBuilder serviceDefinitionId(String serviceDefinitionId) {
			this.serviceDefinitionId = serviceDefinitionId;
			return this;
		}

		/**
		 * Set the ID of the plan associated with the service instance.
		 *
		 * <p>
		 * This value will set the {@literal plan_id} field in the body of the response to the platform.
		 *
		 * @param planId the plan ID
		 * @return the builder
		 */
		public GetServiceInstanceResponseBuilder planId(String planId) {
			this.planId = planId;
			return this;
		}

		/**
		 * Set the URL of a web-based management user interface provided by the service broker for the service
		 * instance. Can be {@literal null} to indicate that a management dashboard is not provided.
		 *
		 * <p>
		 * This value will set the {@literal dashboard_url} field in the body of the response to the platform.
		 *
		 * @param dashboardUrl the dashboard URL
		 * @return the builder
		 */
		public GetServiceInstanceResponseBuilder dashboardUrl(String dashboardUrl) {
			this.dashboardUrl = dashboardUrl;
			return this;
		}

		/**
		 * Add a set of parameters from the provided {@literal Map} to the parameters
		 * as were provided to the service broker at service instance creation.
		 *
		 * <p>
		 * This value will set the {@literal parameters} field in the body of the response to the platform
		 *
		 * @param parameters the parameters {@literal Map}
		 * @return the builder
		 */
		public GetServiceInstanceResponseBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		/**
		 * Add a key/value pair to the parameters as were provided to the service broker at service instance creation.
		 *
		 * <p>
		 * This value will set the {@literal parameters} field in the body of the response to the platform
		 *
		 * @param key the parameter key
		 * @param value the parameter value
		 * @return the builder
		 */
		public GetServiceInstanceResponseBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		/**
		 * Construct a {@link GetServiceInstanceResponse} from the provided values.
		 *
		 * @return the newly constructed {@literal GetServiceInstanceResponse}
		 */
		public GetServiceInstanceResponse build() {
			return new GetServiceInstanceResponse(serviceDefinitionId, planId, dashboardUrl, parameters);
		}
	}
}
