/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.springframework.cloud.servicebroker.model.AsyncServiceBrokerResponse;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;

/**
 * Details of a response to a service instance binding create request.
 * <p>
 * Service brokers will typically construct one of the subtypes of this class to build a response.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreateServiceInstanceBindingResponse extends AsyncServiceBrokerResponse {

	@JsonIgnore
	private final BindingStatus bindingStatus;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final BindingMetadata metadata;

	/**
	 * Construct a new {@link CreateServiceInstanceBindingResponse}
	 *
	 * @param async is the operation asynchronous
	 * @param operation description of the operation being performed
	 * @param bindingStatus does the service binding already exist
	 * @param metadata the service instance binding metadata
	 */
	protected CreateServiceInstanceBindingResponse(boolean async, String operation, BindingStatus bindingStatus,
			BindingMetadata metadata) {
		super(async, operation);
		this.bindingStatus = bindingStatus;
		this.metadata = metadata;
	}

	/**
	 * Construct a new {@link CreateServiceInstanceBindingResponse}
	 *
	 * @param async is the operation asynchronous
	 * @param operation description of the operation being performed
	 * @param bindingExisted does the service binding already exist
	 * @param metadata the service instance binding metadata
	 * @deprecated in favor of {@link #CreateServiceInstanceBindingResponse(boolean, String, BindingStatus, BindingMetadata)}
	 */
	@Deprecated
	protected CreateServiceInstanceBindingResponse(boolean async, String operation, boolean bindingExisted,
			BindingMetadata metadata) {
		super(async, operation);
		if (bindingExisted) {
			this.bindingStatus = BindingStatus.EXISTS_WITH_IDENTICAL_PARAMETERS;
		}
		else {
			this.bindingStatus = BindingStatus.NEW;
		}
		this.metadata = metadata;
	}

	/**
	 * Get the boolean value indicating whether the service binding already exists with the same parameters as the
	 * requested service binding.
	 *
	 * @return the boolean value
	 * @deprecated in favor of {@link #getBindingStatus()}
	 */
	@Deprecated
	@JsonIgnore
	public boolean isBindingExisted() {
		return this.bindingStatus != null && this.bindingStatus != BindingStatus.NEW;
	}

	/**
	 * Get whether the service binding request is a new request, or whether there is an existing binding with same or
	 * different parameters
	 *
	 * @return the binding status
	 */
	@JsonIgnore
	public BindingStatus getBindingStatus() {
		return this.bindingStatus;
	}

	/**
	 * Get the service instance binding metadata
	 *
	 * @return the metadata
	 */
	public BindingMetadata getMetadata() {
		return this.metadata;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CreateServiceInstanceBindingResponse)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		CreateServiceInstanceBindingResponse that = (CreateServiceInstanceBindingResponse) o;
		return that.canEqual(this) &&
				bindingStatus == that.bindingStatus &&
				Objects.equals(metadata,that.metadata);
	}

	@Override
	public boolean canEqual(Object other) {
		return other instanceof CreateServiceInstanceBindingResponse;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), bindingStatus, metadata);
	}

	@Override
	public String toString() {
		return "CreateServiceInstanceBindingResponse{" +
				"bindingStatus=" + bindingStatus +
				"metadata=" + metadata +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link CreateServiceInstanceResponse}.
	 */
	public static final class CreateServiceInstanceBindingResponseBuilder {

		private BindingStatus bindingStatus = BindingStatus.NEW;

		private BindingMetadata metadata;

		private boolean async;

		private String operation;

		private CreateServiceInstanceBindingResponseBuilder() {
		}

		/**
		 * Set the binding status indicating whether the service binding already exists with the same
		 * parameters, different parameters, or is a new binding.
		 * <p>
		 * This value will be used to determine the HTTP response code to the platform. A {@literal NEW} value will
		 * result in a response code {@literal 201 CREATED or 202 ACCEPTED} depending on whether it is an async
		 * request or not, a {@literal EXISTS_WITH_IDENTICAL_PARAMETERS} value will result in a response code
		 * {@literal 200 OK}, and a {@literal EXISTS_WITH_DIFFERENT_PARAMETERS} value will result in a response code
		 * {@literal 409 CONFLICT}.
		 *
		 * @param bindingStatus the status indicating whether the request is a new service binding or already exists
		 * @return the builder
		 */
		public CreateServiceInstanceBindingResponseBuilder bindingStatus(BindingStatus bindingStatus) {
			this.bindingStatus = bindingStatus;
			return this;
		}

		/**
		 * Set a boolean value indicating whether the service instance binding already exists with the same parameters
		 * as the requested service instance binding. A {@literal true} value indicates a service instance binding
		 * exists and no new resources were created by the service broker, <code>false</code> indicates that new
		 * resources were created.
		 *
		 * <p>
		 * This value will be used to determine the HTTP response code to the platform. A {@literal true} value will
		 * result in a response code {@literal 200 OK}, and a {@literal false} value will result in a response code
		 * {@literal 201 CREATED or 202 ACCEPTED}, depending on the value of {@link #async(boolean)}.
		 *
		 * @param bindingExisted {@literal true} to indicate that the binding exists, {@literal false} otherwise
		 * @return the builder
		 * @deprecated in favor of {@link #bindingStatus(BindingStatus)}
		 */
		@Deprecated
		public CreateServiceInstanceBindingResponseBuilder bindingExisted(boolean bindingExisted) {
			if (bindingExisted) {
				this.bindingStatus = BindingStatus.EXISTS_WITH_IDENTICAL_PARAMETERS;
			}
			else {
				this.bindingStatus = BindingStatus.NEW;
			}
			return this;
		}

		/**
		 * Set the service instance binding metadata
		 *
		 * <p>
		 * This value will set the {@literal metadata} field in the body of the response to the platform.
		 *
		 * @param metadata metadata about this service binding
		 * @return the builder
		 */
		public CreateServiceInstanceBindingResponseBuilder metadata(BindingMetadata metadata) {
			this.metadata = metadata;
			return this;
		}

		/**
		 * Set a boolean value indicating whether the requested operation is being performed synchronously or
		 * asynchronously.
		 *
		 * <p>
		 * This value will be used to determine the HTTP response code to the platform. If the service broker indicates
		 * that it performed the operation synchronously, a {@literal true} value will result in a response code
		 * {@literal 200 OK}, and a {@literal false} value will result in a response code {@literal 201 CREATED}.
		 *
		 * @param async {@literal true} to indicate that the operation is being performed asynchronously, {@literal
		 * 		false} to indicate that the operation was completed
		 * @return the builder
		 */
		public CreateServiceInstanceBindingResponseBuilder async(boolean async) {
			this.async = async;
			return this;
		}

		/**
		 * Set a value to inform the user of the operation being performed in support of an asynchronous response. This
		 * value will be passed back to the service broker in subsequent {@link GetLastServiceBindingOperationRequest}
		 * requests.
		 *
		 * <p>
		 * This value will set the {@literal operation} field in the body of the response to the platform.
		 *
		 * @param operation description of the operation being performed
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
			return new CreateServiceInstanceBindingResponse(async, operation, bindingStatus, metadata);
		}

	}

}
