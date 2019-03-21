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
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;

/**
 * Details of a request to update a service instance.
 *
 * <p>
 * Objects of this type are constructed by the framework from the headers, path variables, query parameters
 * and message body passed to the service broker by the platform.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#request-3">Open Service Broker API specification</a>
 *
 * @author Scott Frederick
 */
public class UpdateServiceInstanceRequest extends AsyncParameterizedServiceInstanceRequest {

	@NotEmpty
	@JsonProperty("service_id")
	private final String serviceDefinitionId;

	private final String planId;

	private final PreviousValues previousValues;

	@JsonIgnore
	private transient String serviceInstanceId;

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

	/**
	 * Get the ID of the service instance to update. This value is assigned by the platform. It must be unique within
	 * the platform and can be used to correlate any resources associated with the service instance.
	 *
	 * <p>
	 * This value is set from the {@literal :instance_id} path element of the request from the platform.
	 *
	 * @return the service instance ID
	 */
	public String getServiceInstanceId() {
		return this.serviceInstanceId;
	}

	/**
	 * This method is intended to be used internally only; use {@link #builder()} to construct an object of this
	 * type and set all field values.
	 *
	 * @param serviceInstanceId the ID of the service instance to update
	 */
	public void setServiceInstanceId(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	/**
	 * Get the ID of the service definition for to the service instance to update. This will match one of the service
	 * definition IDs provided in the {@link org.springframework.cloud.servicebroker.model.catalog.Catalog}.
	 *
	 * <p>
	 * This value is set from the {@literal service_id} field in the body of the request from the platform
	 *
	 * @return the service definition ID
	 */
	public String getServiceDefinitionId() {
		return this.serviceDefinitionId;
	}

	/**
	 * Get the ID of the plan for to the service instance to update. This will match one of the plan IDs provided
	 * in the {@link org.springframework.cloud.servicebroker.model.catalog.Catalog} within the specified
	 * {@link ServiceDefinition}.
	 *
	 * <p>
	 * This value is set from the {@literal plan_id} field in the body of the request from the platform.
	 *
	 * @return the plan ID
	 */
	public String getPlanId() {
		return this.planId;
	}

	/**
	 * Get information about the service instance prior to the update request.
	 *
	 * <p>
	 * This value is set from the {@literal previous_values} field in the body of the request from the platform.
	 * 
	 * @return the prior service instance details
	 */
	public PreviousValues getPreviousValues() {
		return this.previousValues;
	}

	/**
	 * Get the service definition of the service instance to update.
	 *
	 * <p>
	 * The service definition is retrieved from the
	 * {@link org.springframework.cloud.servicebroker.model.catalog.Catalog} as a convenience.
	 *
	 * @return the service definition
	 */
	public ServiceDefinition getServiceDefinition() {
		return this.serviceDefinition;
	}

	/**
	 * This method is intended to be used internally only; use {@link #builder()} to construct an object of this
	 * type and set all field values.
	 *
	 * @param serviceDefinition the service definition of the service instance to update
	 */
	public void setServiceDefinition(ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}

	/**
	 * Create a builder that provides a fluent API for constructing an {@literal UpdateServiceInstanceRequest}.
	 *
	 * <p>
	 * This builder is provided to support testing of
	 * {@link org.springframework.cloud.servicebroker.service.ServiceInstanceService} implementations.
	 *
	 * @return the builder
	 */
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
		@NotEmpty
		private final String planId;

		@SuppressWarnings("unused")
		private PreviousValues() {
			this.planId = null;
		}

		public PreviousValues(String planId) {
			this.planId = planId;
		}

		/**
		 * Get the ID of the plan prior to the update request.
		 *
		 * <p>
		 * This value is set from the {@literal plan_id} field in the {@literal previous_values} field in the
		 * body of the request from the platform.
		 *
		 * @return the plan ID
		 */
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

	/**
	 * Provides a fluent API for constructing a {@link UpdateServiceInstanceRequest}.
	 */
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

		/**
		 * Set the service instance ID as would be provided in the request from the platform.
		 *
		 * @param serviceInstanceId the service instance ID
		 * @return the builder
		 * @see #getServiceInstanceId()
		 */
		public UpdateServiceInstanceRequestBuilder serviceInstanceId(String serviceInstanceId) {
			this.serviceInstanceId = serviceInstanceId;
			return this;
		}

		/**
		 * Set the service definition ID as would be provided in the request from the platform.
		 *
		 * @param serviceDefinitionId the service definition ID
		 * @return the builder
		 * @see #getServiceDefinitionId()
		 */
		public UpdateServiceInstanceRequestBuilder serviceDefinitionId(String serviceDefinitionId) {
			this.serviceDefinitionId = serviceDefinitionId;
			return this;
		}

		/**
		 * Set the fully resolved service definition.
		 *
		 * @param serviceDefinition the service definition
		 * @return the builder
		 * @see #getServiceDefinition()
		 */
		public UpdateServiceInstanceRequestBuilder serviceDefinition(ServiceDefinition serviceDefinition) {
			this.serviceDefinition = serviceDefinition;
			return this;
		}

		/**
		 * Set the plan ID as would be provided in the request from the platform.
		 *
		 * @param planId the plan ID
		 * @return the builder
		 * @see #getPlanId()
		 */
		public UpdateServiceInstanceRequestBuilder planId(String planId) {
			this.planId = planId;
			return this;
		}

		/**
		 * Set the previous values of the service instance details as would be
		 * provided in the request from the platform.
		 *
		 * @param previousValues the previous values
		 * @return the builder
		 * @see #getPreviousValues()
		 */
		public UpdateServiceInstanceRequestBuilder previousValues(PreviousValues previousValues) {
			this.previousValues = previousValues;
			return this;
		}

		/**
		 * Add a set of parameters from the provided {@literal Map} to the request parameters
		 * as would be provided in the request from the platform.
		 *
		 * @param parameters the parameters to add
		 * @return the builder
		 * @see #getParameters()
		 */
		public UpdateServiceInstanceRequestBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		/**
		 * Add a key/value pair to the request parameters as would be provided in the request from the platform.
		 *
		 * @param key the parameter key to add
		 * @param value the parameter value to add
		 * @return the builder
		 * @see #getParameters()
		 */
		public UpdateServiceInstanceRequestBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		/**
		 * Set the {@link Context} of the request as would be provided in the request from the platform.
		 *
		 * @param context the context
		 * @return the builder
		 * @see #getContext()
		 */
		public UpdateServiceInstanceRequestBuilder context(Context context) {
			this.context = context;
			return this;
		}

		/**
		 * Set the value of the flag indicating whether the platform supports asynchronous operations
		 * as would be provided in the request from the platform.
		 *
		 * @param asyncAccepted the boolean value of the flag
		 * @return the builder
		 * @see #isAsyncAccepted()
		 */
		public UpdateServiceInstanceRequestBuilder asyncAccepted(boolean asyncAccepted) {
			this.asyncAccepted = asyncAccepted;
			return this;
		}

		/**
		 * Set the ID of the platform instance as would be provided in the request from the platform.
		 *
		 * @param platformInstanceId the platform instance ID
		 * @return the builder
		 * @see #getPlatformInstanceId()
		 */
		public UpdateServiceInstanceRequestBuilder platformInstanceId(String platformInstanceId) {
			this.platformInstanceId = platformInstanceId;
			return this;
		}

		/**
		 * Set the location of the API info endpoint as would be provided in the request from the platform.
		 *
		 * @param apiInfoLocation the API info endpoint location
		 * @return the builder
		 * @see #getApiInfoLocation()
		 */
		public UpdateServiceInstanceRequestBuilder apiInfoLocation(String apiInfoLocation) {
			this.apiInfoLocation = apiInfoLocation;
			return this;
		}

		/**
		 * Set the identity of the user making the request as would be provided in the request from the platform.
		 *
		 * @param originatingIdentity the user identity
		 * @return the builder
		 * @see #getOriginatingIdentity()
		 */
		public UpdateServiceInstanceRequestBuilder originatingIdentity(Context originatingIdentity) {
			this.originatingIdentity = originatingIdentity;
			return this;
		}

		/**
		 * Construct a {@link UpdateServiceInstanceRequest} from the provided values.
		 *
		 * @return the newly constructed {@literal UpdateServiceInstanceRequest}
		 */
		public UpdateServiceInstanceRequest build() {
			return new UpdateServiceInstanceRequest(serviceDefinitionId, serviceInstanceId, planId,
					serviceDefinition, previousValues, parameters, context, asyncAccepted,
					platformInstanceId, apiInfoLocation, originatingIdentity);
		}
	}
}
