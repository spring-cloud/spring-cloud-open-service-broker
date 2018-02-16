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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Objects;

/**
 * Details of a response to a request to get the state of the last operation on a service instance.
 *
 * @author Scott Frederick
 */
@JsonAutoDetect
public class GetLastServiceOperationResponse {
	/**
	 * The current state of the asynchronous request.
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private final OperationState state;

	/**
	 * A user-facing message displayed to the Cloud Controller API client. Can be used to tell the user details
	 * about the status of the operation. Can be <code>null</code>.
	 */
	private final String description;

	/**
	 * Should be set to <code>true</code> in response to a request for the status of an asynchronous delete request,
	 * and <code>false</code> otherwise.
	 */
	@JsonIgnore
	private final boolean deleteOperation;

	GetLastServiceOperationResponse(OperationState state, String description, boolean deleteOperation) {
		this.state = state;
		this.description = description;
		this.deleteOperation = deleteOperation;
	}

	public OperationState getState() {
		return this.state;
	}

	public String getDescription() {
		return this.description;
	}

	public boolean isDeleteOperation() {
		return this.deleteOperation;
	}

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

	public static class GetLastServiceOperationResponseBuilder {
		private OperationState state;
		private String description;
		private boolean deleteOperation;

		GetLastServiceOperationResponseBuilder() {
		}

		public GetLastServiceOperationResponseBuilder operationState(OperationState state) {
			this.state = state;
			return this;
		}

		public GetLastServiceOperationResponseBuilder description(String description) {
			this.description = description;
			return this;
		}

		public GetLastServiceOperationResponseBuilder deleteOperation(boolean deleteOperation) {
			this.deleteOperation = deleteOperation;
			return this;
		}

		public GetLastServiceOperationResponse build() {
			return new GetLastServiceOperationResponse(state, description, deleteOperation);
		}
	}
}
