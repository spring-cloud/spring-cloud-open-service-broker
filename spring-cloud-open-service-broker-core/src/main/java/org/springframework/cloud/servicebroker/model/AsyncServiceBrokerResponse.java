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

package org.springframework.cloud.servicebroker.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.jspecify.annotations.Nullable;

import org.springframework.util.StringUtils;

/**
 * Details of a response that support asynchronous behavior.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href=
 * "https://github.com/openservicebrokerapi/servicebroker/blob/v2.16/spec.md#asynchronous-operations">Open
 * Service Broker API specification</a>
 */
@JsonInclude(Include.NON_NULL)
public class AsyncServiceBrokerResponse {

	private static final int MAX_OPERATION_LENGTH = 10_000;

	/**
	 * Whether the operation is asynchronous.
	 */
	protected final @Nullable Boolean async;

	/**
	 * An identifier representing the operation in progress.
	 */
	protected final @Nullable String operation;

	/**
	 * Create a new AsyncServiceBrokerResponse.
	 * @param async is the operation asynchronous
	 * @param operation an identifier representing the operation in progress
	 * @throws IllegalArgumentException if operation length exceeds 10,000 characters
	 */
	protected AsyncServiceBrokerResponse(@Nullable Boolean async, @Nullable String operation) {
		validateOperationLength(operation);
		this.async = async;
		this.operation = operation;
	}

	/**
	 * Get a boolean value indicating whether the requested operation is being performed
	 * synchronously or asynchronously.
	 * <p>
	 * Since OSB API 2.7.
	 * @return the boolean value
	 */
	@JsonIgnore // not sent on the wire as json payload, but as http status instead
	public boolean isAsync() {
		return (this.async != null) ? this.async : false;
	}

	/**
	 * Get a description of the operation being performed in support of an asynchronous
	 * response.
	 * <p>
	 * Since OSB API 2.9.
	 * @return the operation description
	 */
	@JsonInclude(Include.NON_EMPTY)
	public @Nullable String getOperation() {
		return this.operation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AsyncServiceBrokerResponse)) {
			return false;
		}
		AsyncServiceBrokerResponse that = (AsyncServiceBrokerResponse) o;
		return that.canEqual(this) && this.async == that.async && Objects.equals(this.operation, that.operation);
	}

	/**
	 * Is another object type compatible with this object.
	 * @param other the other object
	 * @return true of compatible
	 */
	public boolean canEqual(Object other) {
		return other instanceof AsyncServiceBrokerResponse;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.async, this.operation);
	}

	@Override
	public String toString() {
		return "AsyncServiceInstanceResponse{" + "async=" + this.async + ", operation='" + this.operation + '\'' + '}';
	}

	/**
	 * Validate the length of the operation string to be within the 10,000 character
	 * limit.
	 * @param operation an identifier representing the operation in progress
	 * @throws IllegalArgumentException if the operation is longer than 10,000 characters
	 */
	public static void validateOperationLength(@Nullable String operation) {
		if (StringUtils.hasLength(operation) && operation.length() > MAX_OPERATION_LENGTH) {
			throw new IllegalArgumentException(
					"operation strings are restricted to 10,000 characters in the response" + " body");
		}
	}

}
