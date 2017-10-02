package org.springframework.cloud.servicebroker.model;

import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Details of a request to update a service instance.
 *
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true, exclude = {"serviceDefinition"})
@EqualsAndHashCode(callSuper = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateServiceInstanceRequest extends AsyncParameterizedServiceInstanceRequest {

	/**
	 * The ID of the service to update, from the broker catalog.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("service_id")
	private final String serviceDefinitionId;

	/**
	 * The ID of the plan to update within the service, from the broker catalog.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("plan_id")
	private final String planId;

	/**
	 * Information about the service instance prior to the update request.
	 */
	@JsonSerialize
	@JsonProperty("previous_values")
	private final PreviousValues previousValues;

	/**
	 * The Cloud Controller GUID of the service instance to update.
	 */
	@JsonIgnore
	private transient String serviceInstanceId;

	/**
	 * The {@link ServiceDefinition} of the service to update. This is resolved from the
	 * <code>serviceDefinitionId</code> as a convenience to the broker.
	 */
	@JsonIgnore
	private transient ServiceDefinition serviceDefinition;

	public UpdateServiceInstanceRequest() {
		super(null, null);
		this.serviceDefinitionId = null;
		this.planId = null;
		this.previousValues = null;
	}

	public UpdateServiceInstanceRequest(String serviceDefinitionId, String planId,
										Map<String, Object> parameters, PreviousValues previousValues,
										Context context) {
		super(parameters, context);
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.previousValues = previousValues;
	}

	public UpdateServiceInstanceRequest(String serviceDefinitionId, String planId,
										Map<String, Object> parameters, PreviousValues previousValues) {
		this(serviceDefinitionId, planId, parameters, previousValues, null);
	}

	public UpdateServiceInstanceRequest(String serviceDefinitionId, String planId,
										Map<String, Object> parameters) {
		this(serviceDefinitionId, planId, parameters, null);
	}

	public UpdateServiceInstanceRequest(String serviceDefinitionId, String planId) {
		this(serviceDefinitionId, planId, null);
	}

	public UpdateServiceInstanceRequest withServiceInstanceId(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
		return this;
	}

	public UpdateServiceInstanceRequest withServiceDefinition(ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
		return this;
	}

	public UpdateServiceInstanceRequest withAsyncAccepted(boolean asyncAccepted) {
		this.asyncAccepted = asyncAccepted;
		return this;
	}

	public UpdateServiceInstanceRequest withCfInstanceId(String cfInstanceId) {
		this.cfInstanceId = cfInstanceId;
		return this;
	}

	public UpdateServiceInstanceRequest withApiInfoLocation(String apiInfoLocation) {
		this.apiInfoLocation = apiInfoLocation;
		return this;
	}

	public UpdateServiceInstanceRequest withOriginatingIdentity(Context originatingIdentity) {
		this.originatingIdentity = originatingIdentity;
		return this;
	}

	/**
	 * Information about the service instance prior to the update request.
	 */
	@Getter
	@ToString
	@EqualsAndHashCode
	public static class PreviousValues {
		/**
		 * The ID of the service instance plan prior to the update request.
		 */
		@NotEmpty
		@JsonSerialize
		@JsonProperty("plan_id")
		private final String planId;

		public PreviousValues() {
			this.planId = null;
		}

		public PreviousValues(String planId) {
			this.planId = planId;
		}
	}
}
