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

/**
 * Details of a response to a request to create a new service instance binding associated with a route.
 *
 * <p>
 * Objects of this type are constructed by the service broker application, and used to build the response to the
 * platform.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#response-6">Open Service
 * 		Broker API specification</a>
 */
public class CreateServiceInstanceRouteBindingResponse extends CreateServiceInstanceBindingResponse {

	private final String routeServiceUrl;

	/**
	 * Construct a new {@link CreateServiceInstanceRouteBindingResponse}
	 */
	public CreateServiceInstanceRouteBindingResponse() {
		this(false, null, false, null, null);
	}

	/**
	 * Construct a new {@link CreateServiceInstanceRouteBindingResponse}
	 *
	 * @param async is the operation asynchronous
	 * @param operation description of the operation being performed
	 * @param bindingStatus does the service binding already exist
	 * @param metadata the service binding metadata
	 * @param routeServiceUrl the route service URL
	 */
	public CreateServiceInstanceRouteBindingResponse(boolean async, String operation, BindingStatus bindingStatus,
			BindingMetadata metadata, String routeServiceUrl) {
		super(async, operation, bindingStatus, metadata);
		this.routeServiceUrl = routeServiceUrl;
	}

	/**
	 * Construct a new {@link CreateServiceInstanceRouteBindingResponse}
	 *
	 * @param async is the operation asynchronous
	 * @param operation description of the operation being performed
	 * @param bindingExisted does the service binding already exist
	 * @param metadata the service binding metadata
	 * @param routeServiceUrl the route service URL
	 */
	@Deprecated
	public CreateServiceInstanceRouteBindingResponse(boolean async, String operation, boolean bindingExisted,
			BindingMetadata metadata, String routeServiceUrl) {
		super(async, operation, bindingExisted, metadata);
		this.routeServiceUrl = routeServiceUrl;
	}

	/**
	 * Get a URL to which the platform should proxy requests for the bound route.
	 *
	 * @return the route service URL
	 */
	public String getRouteServiceUrl() {
		return this.routeServiceUrl;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal CreateServiceInstanceRouteBindingResponse}.
	 *
	 * @return the builder
	 */
	public static CreateServiceInstanceRouteBindingResponseBuilder builder() {
		return new CreateServiceInstanceRouteBindingResponseBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CreateServiceInstanceRouteBindingResponse)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		CreateServiceInstanceRouteBindingResponse that = (CreateServiceInstanceRouteBindingResponse) o;
		return Objects.equals(routeServiceUrl, that.routeServiceUrl);
	}

	@Override
	public final boolean canEqual(Object other) {
		return other instanceof CreateServiceInstanceRouteBindingResponse;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), routeServiceUrl);
	}

	@Override
	public String toString() {
		return super.toString() +
				"CreateServiceInstanceRouteBindingResponse{" +
				"routeServiceUrl='" + routeServiceUrl + '\'' +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link CreateServiceInstanceAppBindingResponse}.
	 */
	public static final class CreateServiceInstanceRouteBindingResponseBuilder {

		private String routeServiceUrl;

		private BindingStatus bindingStatus;

		private BindingMetadata metadata;

		private boolean async;

		private String operation;

		private CreateServiceInstanceRouteBindingResponseBuilder() {
		}

		/**
		 * Set a URL to which the platform should proxy requests for the bound route. Can be {@literal null}.
		 *
		 * <p>
		 * This value will set the {@literal route_service_url} field in the body of the response to the platform
		 *
		 * @param routeServiceUrl the route service URL
		 * @return the builder
		 */
		public CreateServiceInstanceRouteBindingResponseBuilder routeServiceUrl(String routeServiceUrl) {
			this.routeServiceUrl = routeServiceUrl;
			return this;
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
		public CreateServiceInstanceRouteBindingResponseBuilder bindingStatus(BindingStatus bindingStatus) {
			this.bindingStatus = bindingStatus;
			return this;
		}

		/**
		 * Set a boolean value indicating whether the service binding already exists with the same parameters as the
		 * requested service binding. A {@literal true} value indicates a service binding exists and no new resources
		 * were created by the service broker, <code>false</code> indicates that new resources were created.
		 *
		 * <p>
		 * This value will be used to determine the HTTP response code to the platform. A {@literal true} value will
		 * result in a response code {@literal 200 OK}, and a {@literal false} value will result in a response code
		 * {@literal 201 CREATED or 202 ACCEPTED} depending on whether it is an async request or not.
		 *
		 * @param bindingExisted {@literal true} to indicate that the binding exists, {@literal false} otherwise
		 * @return the builder
		 */
		@Deprecated
		public CreateServiceInstanceRouteBindingResponseBuilder bindingExisted(boolean bindingExisted) {
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
		public CreateServiceInstanceRouteBindingResponseBuilder metadata(BindingMetadata metadata) {
			this.metadata = metadata;
			return this;
		}

		/**
		 * Set a boolean value indicating whether the requested operation is being performed synchronously or
		 * asynchronously.
		 *
		 * <p>
		 * This value will be used to determine the HTTP response code to the platform. A {@literal true} value will
		 * result in a response code {@literal 202 ACCEPTED}; otherwise the response code will be determined by the
		 * value of {@link #bindingExisted(boolean)}.
		 *
		 * @param async {@literal true} to indicate that the operation is being performed asynchronously, {@literal
		 * 		false} to indicate that the operation was completed
		 * @return the builder
		 * @see #bindingExisted(boolean)
		 */
		public CreateServiceInstanceRouteBindingResponseBuilder async(boolean async) {
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
		 * @param operation the informational value
		 * @return the builder
		 */
		public CreateServiceInstanceRouteBindingResponseBuilder operation(String operation) {
			this.operation = operation;
			return this;
		}

		/**
		 * Construct a {@link CreateServiceInstanceRouteBindingResponse} from the provided values.
		 *
		 * @return the newly constructed {@literal CreateServiceInstanceRouteBindingResponse}
		 */
		public CreateServiceInstanceRouteBindingResponse build() {
			return new CreateServiceInstanceRouteBindingResponse(async, operation, bindingStatus, metadata,
					routeServiceUrl);
		}

	}

}
