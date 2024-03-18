/*
 * Copyright 2002-2023 the original author or authors.
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

import org.springframework.cloud.servicebroker.model.AsyncServiceBrokerResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationRequest;

/**
 * Details of a response to a request to delete a service instance binding.
 *
 * <p>
 * Objects of this type are constructed by the service broker application, and used to
 * build the response to the platform.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href=
 * "https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#response-8">Open
 * Service Broker API specification</a>
 */
public class DeleteServiceInstanceBindingResponse extends AsyncServiceBrokerResponse {

	/**
	 * Construct a new {@link DeleteServiceInstanceBindingResponse}.
	 */
	public DeleteServiceInstanceBindingResponse() {
		this(false, null);
	}

	/**
	 * Construct a new {@link DeleteServiceInstanceBindingResponse}.
	 * @param async is the operation asynchronous
	 * @param operation description of the operation being performed
	 */
	public DeleteServiceInstanceBindingResponse(boolean async, String operation) {
		super(async, operation);
	}

	/**
	 * Create a builder that provides a fluent API for constructing a
	 * {@literal DeleteServiceInstanceBindingResponse}.
	 * @return the builder
	 */
	public static DeleteServiceInstanceBindingResponseBuilder builder() {
		return new DeleteServiceInstanceBindingResponseBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DeleteServiceInstanceBindingResponse)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		DeleteServiceInstanceBindingResponse that = (DeleteServiceInstanceBindingResponse) o;
		return that.canEqual(this);
	}

	@Override
	public boolean canEqual(Object other) {
		return other instanceof DeleteServiceInstanceBindingResponse;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode());
	}

	@Override
	public String toString() {
		return super.toString() + "DeleteServiceInstanceBindingResponse{" + '}';
	}

	/**
	 * Provides a fluent API for constructing a
	 * {@link DeleteServiceInstanceBindingResponse}.
	 */
	public static final class DeleteServiceInstanceBindingResponseBuilder {

		private boolean async;

		private String operation;

		private DeleteServiceInstanceBindingResponseBuilder() {
		}

		/**
		 * Set a boolean value indicating whether the requested operation is being
		 * performed synchronously or asynchronously.
		 *
		 * <p>
		 * This value will be used to determine the HTTP response code to the platform. A
		 * {@literal true} value will result in a response code {@literal 202 ACCEPTED},
		 * and a {@literal false} value will result in a response code {@literal 200 OK}.
		 * @param async {@literal true} to indicate that the operation is being performed
		 * asynchronously, {@literal
		 * 		false} to indicate that the operation was completed
		 * @return the builder
		 */
		public DeleteServiceInstanceBindingResponseBuilder async(boolean async) {
			this.async = async;
			return this;
		}

		/**
		 * Set a value to inform the user of the operation being performed in support of
		 * an asynchronous response. This value will be passed back to the service broker
		 * in subsequent {@link GetLastServiceOperationRequest} requests.
		 *
		 * <p>
		 * This value will set the {@literal operation} field in the body of the response
		 * to the platform.
		 * @param operation the informational value
		 * @return the builder
		 */
		public DeleteServiceInstanceBindingResponseBuilder operation(String operation) {
			this.operation = operation;
			return this;
		}

		/**
		 * Construct a {@link DeleteServiceInstanceBindingResponse} from the provided
		 * values.
		 * @return the newly constructed {@literal DeleteServiceInstanceResponse}
		 */
		public DeleteServiceInstanceBindingResponse build() {
			return new DeleteServiceInstanceBindingResponse(this.async, this.operation);
		}

	}

}
