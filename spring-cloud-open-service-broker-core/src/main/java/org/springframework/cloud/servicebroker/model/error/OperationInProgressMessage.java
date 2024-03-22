/*
 * Copyright 2002-2024 the original author or authors.
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

package org.springframework.cloud.servicebroker.model.error;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.cloud.servicebroker.model.AsyncServiceBrokerResponse;

/**
 * Returned if a create, update, or delete operation is in progress. The operation string
 * must match that returned for the original request.
 *
 * @author Roy Clarkson
 * @see <a href=
 * "https://github.com/openservicebrokerapi/servicebroker/blob/v2.15/spec.md#response-3">Open
 * Service Broker API specification</a>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OperationInProgressMessage implements Serializable {

	@Serial
	private static final long serialVersionUID = -8178107171824247656L;

	private final String operation;

	/**
	 * Construct a message with no operation.
	 */
	public OperationInProgressMessage() {
		this(null);
	}

	/**
	 * Construct a message with the provided operation.
	 * @param operation an identifier representing the operation
	 */
	public OperationInProgressMessage(String operation) {
		AsyncServiceBrokerResponse.validateOperationLength(operation);
		this.operation = operation;
	}

	/**
	 * Get the operation.
	 * @return the operation.
	 */
	public String getOperation() {
		return this.operation;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof OperationInProgressMessage)) {
			return false;
		}
		OperationInProgressMessage that = (OperationInProgressMessage) o;
		return Objects.equals(this.operation, that.operation);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(this.operation);
	}

	@Override
	public final String toString() {
		return "OperationInProgressMessage{" + "operation='" + this.operation + '\'' + '}';
	}

}
