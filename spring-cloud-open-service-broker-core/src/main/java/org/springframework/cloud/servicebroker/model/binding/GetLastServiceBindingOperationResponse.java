/*
 * Copyright 2002-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.binding;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

import org.springframework.cloud.servicebroker.model.instance.OperationState;

/**
 * Details of a response to a request to get the state of the last operation on a service
 * instance binding.
 *
 * <p>
 * Objects of this type are constructed by the service broker application, and used to
 * build the response to the platform.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href=
 * "https://github.com/openservicebrokerapi/servicebroker/blob/v2.16/spec.md#polling-last-operation-for-service-bindings">Open
 * Service Broker API specification</a>
 */
@JsonAutoDetect
@JsonInclude(Include.NON_NULL)
public class GetLastServiceBindingOperationResponse {

	private final @Nullable OperationState state;

	private final @Nullable String description;

	private final @Nullable Boolean deleteOperation;

	private final @Nullable Integer retryAfter;

	/**
	 * Construct a new {@link GetLastServiceBindingOperationResponse}.
	 */
	public GetLastServiceBindingOperationResponse() {
		this(null, null, false, null);
	}

	/**
	 * Construct a new {@link GetLastServiceBindingOperationResponse}.
	 * @param state the current state
	 * @param description the description
	 * @param deleteOperation is delete operation
	 * @deprecated in favor of
	 * {@link GetLastServiceBindingOperationResponse#GetLastServiceBindingOperationResponse(OperationState, String, Boolean, Integer)}
	 */
	@Deprecated
	public GetLastServiceBindingOperationResponse(OperationState state, String description,
			@Nullable Boolean deleteOperation) {
		this(state, description, deleteOperation, null);
	}

	/**
	 * Construct a new {@link GetLastServiceBindingOperationResponse}.
	 * @param state the current state
	 * @param description the description
	 * @param deleteOperation is delete operation
	 * @param retryAfter the number of seconds the platform should wait before polling
	 * again
	 */
	@JsonCreator
	public GetLastServiceBindingOperationResponse(@JsonProperty("state") @Nullable OperationState state,
			@JsonProperty("description") @Nullable String description,
			@JsonProperty("delete_operation") @Nullable Boolean deleteOperation,
			@JsonProperty("retry_after") @Nullable Integer retryAfter) {
		this.state = state;
		this.description = description;
		this.deleteOperation = (deleteOperation != null) ? deleteOperation : false;
		this.retryAfter = retryAfter;
	}

	/**
	 * Get the current state of an asynchronous operation.
	 * <p>
	 * Since OSB API 2.14.
	 * @return the operation state
	 */
	public @Nullable OperationState getState() {
		return this.state;
	}

	/**
	 * Get the description of the current asynchronous operation.
	 * @return the description, or {@literal null} if not provided
	 */
	public @Nullable String getDescription() {
		return this.description;
	}

	/**
	 * Get a boolean value indicating whether the current operation is a delete operation.
	 * @return the boolean value
	 */
	@JsonIgnore
	public boolean isDeleteOperation() {
		return (this.deleteOperation != null) ? this.deleteOperation : false;
	}

	/**
	 * Get the number of seconds the platform should wait before polling again. This value
	 * is used to set the {@literal Retry-After} HTTP header on the response, and is not
	 * included in the response body.
	 * <p>
	 * Since OSB API 2.15.
	 * @return the number of seconds, or {@literal null} if not provided
	 * @see <a href=
	 * "https://github.com/openservicebrokerapi/servicebroker/blob/v2.15/spec.md#polling-last-operation-for-service-bindings">Open
	 * Service Broker API specification: Polling Last Operation for Service Bindings</a>
	 */
	@JsonIgnore
	public @Nullable Integer getRetryAfter() {
		return this.retryAfter;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a
	 * {@literal GetLastServiceBindingOperationResponse}.
	 * @return the builder
	 */
	public static GetLastServiceBindingOperationResponseBuilder builder() {
		return new GetLastServiceBindingOperationResponseBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof GetLastServiceBindingOperationResponse)) {
			return false;
		}
		GetLastServiceBindingOperationResponse that = (GetLastServiceBindingOperationResponse) o;
		return this.deleteOperation == that.deleteOperation && this.state == that.state
				&& Objects.equals(this.description, that.description)
				&& Objects.equals(this.retryAfter, that.retryAfter);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(this.state, this.description, this.deleteOperation, this.retryAfter);
	}

	@Override
	public String toString() {
		return "GetLastServiceBindingOperationResponse{" + "state=" + this.state + ", description='" + this.description
				+ '\'' + ", deleteOperation=" + this.deleteOperation + ", retryAfter=" + this.retryAfter + '}';
	}

	/**
	 * Provides a fluent API for constructing a
	 * {@link GetLastServiceBindingOperationResponse}.
	 */
	public static final class GetLastServiceBindingOperationResponseBuilder {

		private @Nullable OperationState state;

		private @Nullable String description;

		private boolean deleteOperation;

		private @Nullable Integer retryAfter;

		private GetLastServiceBindingOperationResponseBuilder() {
		}

		/**
		 * Set the current state of the asynchronous operation.
		 *
		 * <p>
		 * A value of {@link OperationState#IN_PROGRESS} will cause the platform to
		 * continue polling the service broker for status. A value of
		 * {@link OperationState#SUCCEEDED} or {@link OperationState#FAILED} will cause
		 * the platform to stop polling the service broker.
		 *
		 * <p>
		 * This value will set the {@literal state} field in the body of the response to
		 * the platform.
		 * <p>
		 * Since OSB API 2.14.
		 * @param state the current state
		 * @return the builder
		 */
		public GetLastServiceBindingOperationResponseBuilder operationState(OperationState state) {
			this.state = state;
			return this;
		}

		/**
		 * Set a user-facing description of the operation that the platform can display to
		 * the API client. Can be {@literal null}.
		 *
		 * <p>
		 * This value will set the {@literal description} field in the body of the
		 * response to the platform.
		 * @param description the description
		 * @return the builder
		 */
		public GetLastServiceBindingOperationResponseBuilder description(String description) {
			this.description = description;
			return this;
		}

		/**
		 * Set a boolean value indicating whether the current asynchronous operation is a
		 * delete operation. Should be set to <code>true</code> in response to a request
		 * for the status of an asynchronous delete request, and <code>false</code>
		 * otherwise.
		 *
		 * <p>
		 * This value is used to determine the HTTP response code to the platform. If the
		 * {@link #operationState(OperationState)} is {@link OperationState#SUCCEEDED} and
		 * the value provided here is {@literal
		 * true} will result in a response code {@literal 410 GONE}. Otherwise the
		 * response code will be {@literal 200
		 * OK}.
		 * @param deleteOperation the boolean value
		 * @return the builder
		 */
		public GetLastServiceBindingOperationResponseBuilder deleteOperation(boolean deleteOperation) {
			this.deleteOperation = deleteOperation;
			return this;
		}

		/**
		 * Set the number of seconds the platform should wait before polling again. This
		 * value is used to set the {@literal Retry-After} HTTP header on the response,
		 * and is not included in the response body. It is RECOMMENDED that this be a
		 * duration rather than a timestamp.
		 * <p>
		 * Since OSB API 2.15.
		 * @param retryAfter the number of seconds
		 * @return the builder
		 */
		public GetLastServiceBindingOperationResponseBuilder retryAfter(Integer retryAfter) {
			this.retryAfter = retryAfter;
			return this;
		}

		/**
		 * Construct a {@link GetLastServiceBindingOperationResponse} from the provided
		 * values.
		 * @return the newly constructed {@literal GetLastServiceOperationResponse}
		 */
		public GetLastServiceBindingOperationResponse build() {
			return new GetLastServiceBindingOperationResponse(this.state, this.description, this.deleteOperation,
					this.retryAfter);
		}

	}

}
