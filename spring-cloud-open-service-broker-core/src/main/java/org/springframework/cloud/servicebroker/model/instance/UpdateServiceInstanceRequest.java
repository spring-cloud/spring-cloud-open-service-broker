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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Details of a request to update a service instance.
 *
 * @author Scott Frederick
 */
public class UpdateServiceInstanceRequest extends AsyncParameterizedServiceInstanceRequest {

	/**
	 * The ID of the service to update, from the broker catalog.
	 */
	@NotEmpty
	@JsonProperty("service_id")
	private final String serviceDefinitionId;

	/**
	 * The ID of the plan to update within the service, from the broker catalog.
	 */
	private final String planId;

	/**
	 * Information about the service instance prior to the update request.
	 */
	private final PreviousValues previousValues;

	/**
	 * The GUID of the service instance to update.
	 */
	@JsonIgnore
	private transient String serviceInstanceId;

	/**
	 * The {@link ServiceDefinition} of the service to update. This is resolved from the
	 * <code>serviceDefinitionId</code> as a convenience to the broker.
	 */
	@JsonIgnore
	private transient ServiceDefinition serviceDefinition;

	@SuppressWarnings("unused")
	UpdateServiceInstanceRequest() {
		this.serviceDefinitionId = null;
		this.planId = null;
		this.previousValues = null;
	}

	UpdateServiceInstanceRequest(String serviceDefinitionId, String serviceInstanceId, String planId,
								 ServiceDefinition serviceDefinition,
								 PreviousValues previousValues, Map<String, Object> parameters,
								 Context context, boolean asyncAccepted,
								 String platformInstanceId, String apiInfoLocation, Context originatingIdentity) {
		super(parameters, context, asyncAccepted, platformInstanceId, apiInfoLocation, originatingIdentity);
		this.serviceDefinitionId = serviceDefinitionId;
		this.serviceInstanceId = serviceInstanceId;
		this.planId = planId;
		this.serviceDefinition = serviceDefinition;
		this.previousValues = previousValues;
	}

	public String getServiceDefinitionId() {
		return this.serviceDefinitionId;
	}

	public String getPlanId() {
		return this.planId;
	}

	public PreviousValues getPreviousValues() {
		return this.previousValues;
	}

	public String getServiceInstanceId() {
		return this.serviceInstanceId;
	}

	public void setServiceInstanceId(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	public ServiceDefinition getServiceDefinition() {
		return this.serviceDefinition;
	}

	public void setServiceDefinition(ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}

	public static UpdateServiceInstanceRequestBuilder builder() {
		return new UpdateServiceInstanceRequestBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UpdateServiceInstanceRequest)) return false;
		if (!super.equals(o)) return false;
		UpdateServiceInstanceRequest that = (UpdateServiceInstanceRequest) o;
		return that.canEqual(this) &&
				Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId) &&
				Objects.equals(previousValues, that.previousValues) &&
				Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
				Objects.equals(serviceDefinition, that.serviceDefinition);
	}

	@Override
	public final boolean canEqual(Object other) {
		return (other instanceof UpdateServiceInstanceRequest);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), serviceDefinitionId, planId, previousValues,
				serviceInstanceId, serviceDefinition);
	}

	@Override
	public String toString() {
		return super.toString() +
				"UpdateServiceInstanceRequest{" +
				"serviceDefinitionId='" + serviceDefinitionId + '\'' +
				", planId='" + planId + '\'' +
				", previousValues=" + previousValues +
				", serviceInstanceId='" + serviceInstanceId + '\'' +
				'}';
	}

	/**
	 * Information about the service instance prior to the update request.
	 */
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class PreviousValues {
		/**
		 * The ID of the service instance plan prior to the update request.
		 */
		@NotEmpty
		private final String planId;

		public PreviousValues() {
			this.planId = null;
		}

		public PreviousValues(String planId) {
			this.planId = planId;
		}

		public String getPlanId() {
			return this.planId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof PreviousValues)) return false;
			PreviousValues that = (PreviousValues) o;
			return Objects.equals(planId, that.planId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(planId);
		}

		@Override
		public String toString() {
			return "PreviousValues{" +
					"planId='" + planId + '\'' +
					'}';
		}
	}

	public static class UpdateServiceInstanceRequestBuilder {
		private String serviceInstanceId;
		private String serviceDefinitionId;
		private String planId;
		private ServiceDefinition serviceDefinition;
		private PreviousValues previousValues;
		private final Map<String, Object> parameters = new HashMap<>();
		private Context context;
		private boolean asyncAccepted;
		private String platformInstanceId;
		private String apiInfoLocation;
		private Context originatingIdentity;

		UpdateServiceInstanceRequestBuilder() {
		}

		public UpdateServiceInstanceRequestBuilder serviceDefinitionId(String serviceDefinitionId) {
			this.serviceDefinitionId = serviceDefinitionId;
			return this;
		}

		public UpdateServiceInstanceRequestBuilder serviceInstanceId(String serviceInstanceId) {
			this.serviceInstanceId = serviceInstanceId;
			return this;
		}

		public UpdateServiceInstanceRequestBuilder planId(String planId) {
			this.planId = planId;
			return this;
		}

		public UpdateServiceInstanceRequestBuilder serviceDefinition(ServiceDefinition serviceDefinition) {
			this.serviceDefinition = serviceDefinition;
			return this;
		}

		public UpdateServiceInstanceRequestBuilder previousValues(PreviousValues previousValues) {
			this.previousValues = previousValues;
			return this;
		}

		public UpdateServiceInstanceRequestBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		public UpdateServiceInstanceRequestBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		public UpdateServiceInstanceRequestBuilder context(Context context) {
			this.context = context;
			return this;
		}

		public UpdateServiceInstanceRequestBuilder asyncAccepted(boolean asyncAccepted) {
			this.asyncAccepted = asyncAccepted;
			return this;
		}

		public UpdateServiceInstanceRequestBuilder platformInstanceId(String platformInstanceId) {
			this.platformInstanceId = platformInstanceId;
			return this;
		}

		public UpdateServiceInstanceRequestBuilder apiInfoLocation(String apiInfoLocation) {
			this.apiInfoLocation = apiInfoLocation;
			return this;
		}

		public UpdateServiceInstanceRequestBuilder originatingIdentity(Context originatingIdentity) {
			this.originatingIdentity = originatingIdentity;
			return this;
		}

		public UpdateServiceInstanceRequest build() {
			return new UpdateServiceInstanceRequest(serviceDefinitionId, serviceInstanceId, planId,
					serviceDefinition, previousValues, parameters, context, asyncAccepted,
					platformInstanceId, apiInfoLocation, originatingIdentity);
		}
	}
}
