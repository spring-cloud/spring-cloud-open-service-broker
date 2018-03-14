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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.cloud.servicebroker.model.util.ParameterBeanMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Details of a response to a request to get the details a service instance.
 *
 * @author Scott Frederick
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetServiceInstanceResponse {
	/**
	 * The ID of the service, from the broker catalog.
	 */
	@JsonProperty("service_id")
	private final String serviceDefinitionId;
	
	/**
	 * The ID of the service plan, from the broker catalog.
	 */
	private final String planId;

	/**
	 * The URL of a web-based management user interface for the service instance. Can be <code>null</code> to indicate
	 * that a management dashboard is not provided.
	 */
	private final String dashboardUrl;

	/**
	 * Parameters passed by the user in the form of a JSON structure.
	 */
	private final Map<String, Object> parameters;

	GetServiceInstanceResponse(String serviceDefinitionId, String planId, String dashboardUrl, Map<String, Object> parameters) {
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.dashboardUrl = dashboardUrl;
		this.parameters = parameters;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public String getPlanId() {
		return planId;
	}

	public String getDashboardUrl() {
		return dashboardUrl;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public <T> T getParameters(Class<T> cls) {
		return ParameterBeanMapper.mapParametersToBean(parameters, cls);
	}

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

	public static class GetServiceInstanceResponseBuilder {
		private String serviceDefinitionId;
		private String planId;
		private String dashboardUrl;
		private Map<String, Object> parameters = new HashMap<>();

		GetServiceInstanceResponseBuilder() {
		}

		public GetServiceInstanceResponseBuilder serviceDefinitionId(String serviceDefinitionId) {
			this.serviceDefinitionId = serviceDefinitionId;
			return this;
		}

		public GetServiceInstanceResponseBuilder planId(String planId) {
			this.planId = planId;
			return this;
		}

		public GetServiceInstanceResponseBuilder dashboardUrl(String dashboardUrl) {
			this.dashboardUrl = dashboardUrl;
			return this;
		}

		public GetServiceInstanceResponseBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		public GetServiceInstanceResponseBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		public GetServiceInstanceResponse build() {
			return new GetServiceInstanceResponse(serviceDefinitionId, planId, dashboardUrl, parameters);
		}
	}
}
