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

package org.springframework.cloud.servicebroker.model.binding;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.springframework.cloud.servicebroker.model.AsyncServiceBrokerResponse;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;

/**
 * Details of a response to a service instance binding create request.
 *
 * Service brokers will typically construct one of the subtypes of this class to build a response.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreateServiceInstanceBindingResponse extends AsyncServiceBrokerResponse {
	@JsonIgnore
	protected final boolean bindingExisted;

	protected CreateServiceInstanceBindingResponse(boolean async, String operation, boolean bindingExisted) {
		super(async, operation);
		this.bindingExisted = bindingExisted;
	}

	/**
	 * Get the boolean value indicating whether the service binding already exists with the same parameters as the
	 * requested service binding.
	 *
	 * @return the boolean value
	 */
	public boolean isBindingExisted() {
		return this.bindingExisted;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CreateServiceInstanceBindingResponse)) return false;
		if (!super.equals(o)) return false;
		CreateServiceInstanceBindingResponse that = (CreateServiceInstanceBindingResponse) o;
		return that.canEqual(this) &&
				bindingExisted == that.bindingExisted;
	}

	public boolean canEqual(Object other) {
		return (other instanceof CreateServiceInstanceBindingResponse);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), bindingExisted);
	}

	@Override
	public String toString() {
		return "CreateServiceInstanceBindingResponse{" +
				"bindingExisted=" + bindingExisted +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link CreateServiceInstanceResponse}.
	 */
	public static class CreateServiceInstanceBindingResponseBuilder {
		private boolean bindingExisted;
		private boolean async;
		private String operation;

		CreateServiceInstanceBindingResponseBuilder() {
		}

		/**
		 * Set a boolean value indicating whether the service instance binding already exists with the same parameters
		 * as the requested service instance binding. A {@literal true} value indicates a service instance binding
		 * exists and no new resources were created by the service broker, <code>false</code> indicates that new
		 * resources were created.
		 *
		 * <p>
		 * This value will be used to determine the HTTP response code to the platform. If the service broker
		 * indicates that it performed the operation synchronously, a {@literal true} value will result in a
		 * response code {@literal 200 OK}, and a {@literal false} value will result in a response code
		 * {@literal 201 CREATED}.
		 *
		 * @param bindingExisted {@literal true} to indicate that the binding exists, {@literal false} otherwise
		 * @return the builder
		 * @see #async(boolean)
		 */
		public CreateServiceInstanceBindingResponseBuilder bindingExisted(boolean bindingExisted) {
			this.bindingExisted = bindingExisted;
			return this;
		}

		/**
		 * Set a boolean value indicating whether the requested operation is being performed synchronously or
		 * asynchronously.
		 *
		 * <p>
		 * This value will be used to determine the HTTP response code to the platform. A {@literal true} value
		 * will result in a response code {@literal 202 ACCEPTED}; otherwise the response code will be
		 * determined by the value of {@link #bindingExisted(boolean)}.
		 *
		 * @param async {@literal true} to indicate that the operation is being performed asynchronously,
		 * {@literal false} to indicate that the operation was completed
		 * @return the builder
		 * @see #bindingExisted(boolean)
		 */
		public CreateServiceInstanceBindingResponseBuilder async(boolean async) {
			this.async = async;
			return this;
		}

		/**
		 * Set a value to inform the user of the operation being performed in support of an asynchronous response.
		 * This value will be passed back to the service broker in subsequent
		 * {@link GetLastServiceBindingOperationRequest} requests.
		 *
		 * <p>
		 * This value will set the {@literal operation} field in the body of the response to the platform.
		 *
		 * @param operation the informational value
		 * @return the builder
		 */
		public CreateServiceInstanceBindingResponseBuilder operation(String operation) {
			this.operation = operation;
			return this;
		}

		/**
		 * Construct a {@link CreateServiceInstanceBindingResponse} from the provided values.
		 *
		 * @return the newly constructed {@literal CreateServiceInstanceBindingResponse}
		 */
		public CreateServiceInstanceBindingResponse build() {
			return new CreateServiceInstanceBindingResponse(async, operation, bindingExisted);
		}
	}

}
