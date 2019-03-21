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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Objects;

/**
 * Details of a response to a request to get the state of the last operation on a service instance.
 *
 * <p>
 * Objects of this type are constructed by the service broker application,
 * and used to build the response to the platform.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#response-1">Open Service Broker API specification</a>
 * 
 * @author Scott Frederick
 */
@JsonAutoDetect
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetLastServiceOperationResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private final OperationState state;

	private final String description;

	@JsonIgnore
	private final boolean deleteOperation;

	GetLastServiceOperationResponse(OperationState state, String description, boolean deleteOperation) {
		this.state = state;
		this.description = description;
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
	 * Get a boolean value indicating whether the current operation is a delete operation.
	 *
	 * @return the boolean value
	 */
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
		if (this == o) return true;
		if (!(o instanceof GetLastServiceOperationResponse)) return false;
		GetLastServiceOperationResponse that = (GetLastServiceOperationResponse) o;
		return deleteOperation == that.deleteOperation &&
				state == that.state &&
				Objects.equals(description, that.description);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(state, description, deleteOperation);
	}

	@Override
	public String toString() {
		return "GetLastServiceOperationResponse{" +
				"state=" + state +
				", description='" + description + '\'' +
				", deleteOperation=" + deleteOperation +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link GetLastServiceOperationResponse}.
	 */
	public static class GetLastServiceOperationResponseBuilder {
		private OperationState state;
		private String description;
		private boolean deleteOperation;

		GetLastServiceOperationResponseBuilder() {
		}

		/**
		 * Set the current state of the asynchronous operation.
		 *
		 * <p>
		 * A value of {@link OperationState#IN_PROGRESS} will cause the platform to continue polling the service
		 * broker for status. A value of {@link OperationState#SUCCEEDED} or {@link OperationState#FAILED} will
		 * cause the platform to stop polling the service broker.
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
		 * Set a user-facing description of the operation that the platform can display to the API client.
		 * Can be {@literal null}.
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
		 * Set a boolean value indicating whether the current asynchronous operation is a delete operation.
		 * Should be set to <code>true</code> in response to a request for the status of an asynchronous
		 * delete request, and <code>false</code> otherwise.
		 *
		 * <p>
		 * This value is used to determine the HTTP response code to the platform. If the
		 * {@link #operationState(OperationState)} is {@link OperationState#SUCCEEDED} and the value provided
		 * here is {@literal true} will result in a response code {@literal 410 GONE}. Otherwise the response
		 * code will be {@literal 200 OK}.
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
			return new GetLastServiceOperationResponse(state, description, deleteOperation);
		}
	}
}
