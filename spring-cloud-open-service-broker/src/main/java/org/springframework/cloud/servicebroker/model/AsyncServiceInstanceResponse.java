/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

/**
 * Details of a response that support asynchronous behavior.
 *
 * @author Scott Frederick
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AsyncServiceInstanceResponse {
	/**
	 * Indicates whether the request to the service broker is complete. A <code>false</code> value indicates that the
	 * request was completed, a <code>true</code> value indicates that the broker is processing the request
	 * asynchronously.
	 */
	@JsonIgnore
	protected final boolean async;

	/**
	 * For async responses, service brokers can return operation state as a string. This field will be provided back to
	 * the service broker on last_operation requests as a URL encoded query param. Can be <code>null</code> to indicate
	 * that an operation state is not provided.
	 */
	protected final String operation;

	protected AsyncServiceInstanceResponse(boolean async, String operation) {
		this.async = async;
		this.operation = operation;
	}

	public boolean isAsync() {
		return this.async;
	}

	public String getOperation() {
		return this.operation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AsyncServiceInstanceResponse)) return false;
		AsyncServiceInstanceResponse that = (AsyncServiceInstanceResponse) o;
		return that.canEqual(this) &&
				async == that.async &&
				Objects.equals(operation, that.operation);
	}

	public boolean canEqual(Object other) {
		return (other instanceof AsyncServiceInstanceResponse);
	}

	@Override
	public int hashCode() {
		return Objects.hash(async, operation);
	}

	@Override
	public String toString() {
		return "AsyncServiceInstanceResponse{" +
				"async=" + async +
				", operation='" + operation + '\'' +
				'}';
	}

}
