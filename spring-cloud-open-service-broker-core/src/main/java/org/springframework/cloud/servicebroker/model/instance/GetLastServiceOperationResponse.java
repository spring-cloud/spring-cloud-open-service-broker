/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.instance;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Details of a response to a request to get the state of the last operation on a service instance.
 *
 * <p>
 * Objects of this type are constructed by the service broker application, and used to build the response to the
 * platform.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#response-1">Open Service
 * 		Broker API specification</a>
 */
@JsonAutoDetect
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetLastServiceOperationResponse {

	private final OperationState state;

	private final String description;

	private final Boolean instanceUsable;

	private final Boolean updateRepeatable;

	private final boolean deleteOperation;

	/**
	 * Construct a new {@link GetLastServiceOperationResponse}
	 */
	public GetLastServiceOperationResponse() {
		this(null, null, null, null, false);
	}

	/**
	 * Construct a new {@link GetLastServiceOperationResponse}
	 *
	 * @param state the current state
	 * @param description the description
	 * @param deleteOperation is delete operation
	 * @deprecated in favor of
	 *        {@link GetLastServiceOperationResponse#GetLastServiceOperationResponse(OperationState, String, Boolean,
	 *        Boolean, boolean)}
	 */
	@Deprecated
	public GetLastServiceOperationResponse(OperationState state, String description, boolean deleteOperation) {
		this(state, description, true, true, deleteOperation);
	}

	/**
	 * Construct a new {@link GetLastServiceOperationResponse}
	 *
	 * @param state the current state
	 * @param description the description
	 * @param instanceUsable is the instance usable
	 * @param updateRepeatable is the update repeatable
	 * @param deleteOperation is delete operation
	 */
	public GetLastServiceOperationResponse(OperationState state, String description, Boolean instanceUsable,
			Boolean updateRepeatable, boolean deleteOperation) {
		this.state = state;
		this.description = description;
		this.instanceUsable = instanceUsable;
		this.updateRepeatable = updateRepeatable;
		this.deleteOperation = deleteOperation;
	}

	/**
	 * Get the current state of an asynchronous operation.
	 *
	 * @return the operation state
	 */
	public OperationState getState() {
		return this.state;
	}

	/**
	 * Get the description of the current asynchronous operation.
	 *
	 * @return the description, or {@literal null} if not provided
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Get a boolean value indicating whether the instance is usable after a failed update or deprovisioning operation
	 *
	 * @return the boolean value
	 */
	public Boolean isInstanceUsable() {
		return this.instanceUsable;
	}

	/**
	 * Get a boolean value indicating whether a failed update is repeatable
	 *
	 * @return the boolean value
	 */
	public Boolean isUpdateRepeatable() {
		return this.updateRepeatable;
	}

	/**
	 * Get a boolean value indicating whether the current operation is a delete operation.
	 *
	 * @return the boolean value
	 */
	@JsonIgnore
	public boolean isDeleteOperation() {
		return this.deleteOperation;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal GetLastServiceOperationResponse}.
	 *
	 * @return the builder
	 */
	public static GetLastServiceOperationResponseBuilder builder() {
		return new GetLastServiceOperationResponseBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof GetLastServiceOperationResponse)) {
			return false;
		}
		GetLastServiceOperationResponse that = (GetLastServiceOperationResponse) o;
		return state == that.state &&
				Objects.equals(description, that.description) &&
				Objects.equals(instanceUsable, that.instanceUsable) &&
				Objects.equals(updateRepeatable, that.updateRepeatable) &&
				deleteOperation == that.deleteOperation;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(state, description, instanceUsable, updateRepeatable, deleteOperation);
	}

	@Override
	public String toString() {
		return "GetLastServiceOperationResponse{" +
				"state=" + state +
				", description='" + description + '\'' +
				", instanceUsable=" + instanceUsable + '\'' +
				", updateRepeatable=" + updateRepeatable + '\'' +
				", deleteOperation=" + deleteOperation +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link GetLastServiceOperationResponse}.
	 */
	public static final class GetLastServiceOperationResponseBuilder {

		private OperationState state;

		private String description;

		private Boolean instanceUsable;

		private Boolean updateRepeatable;

		private boolean deleteOperation;

		private GetLastServiceOperationResponseBuilder() {
		}

		/**
		 * Set the current state of the asynchronous operation.
		 *
		 * <p>
		 * A value of {@link OperationState#IN_PROGRESS} will cause the platform to continue polling the service broker
		 * for status. A value of {@link OperationState#SUCCEEDED} or {@link OperationState#FAILED} will cause the
		 * platform to stop polling the service broker.
		 *
		 * <p>
		 * This value will set the {@literal state} field in the body of the response to the platform.
		 *
		 * @param state the current state
		 * @return the builder
		 */
		public GetLastServiceOperationResponseBuilder operationState(OperationState state) {
			this.state = state;
			return this;
		}

		/**
		 * Set a user-facing description of the operation that the platform can display to the API client. Can be
		 * {@literal null}.
		 *
		 * <p>
		 * This value will set the {@literal description} field in the body of the response to the platform.
		 *
		 * @param description the description
		 * @return the builder
		 */
		public GetLastServiceOperationResponseBuilder description(String description) {
			this.description = description;
			return this;
		}

		/**
		 * Set a boolean that indicates whether or not the Service Instance is still usable after a failed update or
		 * delete action. If true, the Service Instance can still be used, false otherwise.
		 *
		 * <p>
		 * This value will set the {@literal instance_usable} field in the body of the response to the platform.
		 *
		 * @param instanceUsable the boolean value
		 * @return the builder
		 */
		public GetLastServiceOperationResponseBuilder instanceUsable(Boolean instanceUsable) {
			this.instanceUsable = instanceUsable;
			return this;
		}

		/**
		 * Set a boolean that indicates whether this update can be repeated or not. If true, the same update
		 * operation MAY be repeated and MAY succeed; if false, repeating the same update operation will fail again.
		 *
		 * <p>
		 * This value will set the {@literal update_repeatable} field in the body of the response to the platform.
		 *
		 * @param updateRepeatable the boolean value
		 * @return the builder
		 */
		public GetLastServiceOperationResponseBuilder updateRepeatable(Boolean updateRepeatable) {
			this.updateRepeatable = updateRepeatable;
			return this;
		}

		/**
		 * Set a boolean value indicating whether the current asynchronous operation is a delete operation. Should be
		 * set to <code>true</code> in response to a request for the status of an asynchronous delete request, and
		 * <code>false</code> otherwise.
		 *
		 * <p>
		 * This value is used to determine the HTTP response code to the platform. If the {@link
		 * #operationState(OperationState)} is {@link OperationState#SUCCEEDED} and the value provided here is {@literal
		 * true} will result in a response code {@literal 410 GONE}. Otherwise the response code will be {@literal 200
		 * OK}.
		 *
		 * @param deleteOperation the boolean value
		 * @return the builder
		 */
		public GetLastServiceOperationResponseBuilder deleteOperation(boolean deleteOperation) {
			this.deleteOperation = deleteOperation;
			return this;
		}

		/**
		 * Construct a {@link GetLastServiceOperationResponse} from the provided values.
		 *
		 * @return the newly constructed {@literal GetLastServiceOperationResponse}
		 */
		public GetLastServiceOperationResponse build() {
			return new GetLastServiceOperationResponse(state, description, instanceUsable, updateRepeatable,
					deleteOperation);
		}

	}

}
