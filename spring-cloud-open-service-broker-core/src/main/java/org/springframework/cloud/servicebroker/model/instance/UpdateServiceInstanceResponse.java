/*
 * Copyright 2002-2020 the original author or authors.
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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.springframework.cloud.servicebroker.model.AsyncServiceBrokerResponse;

/**
 * Details of a response to a request to update a service instance.
 *
 * <p>
 * Objects of this type are constructed by the service broker application, and used to build the response to the
 * platform.
 *
 * @author Scott Frederick
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#response-3">Open Service
 * 		Broker API specification</a>
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateServiceInstanceResponse extends AsyncServiceBrokerResponse {

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private final String dashboardUrl;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final ServiceInstanceMetadata metadata;

	/**
	 * Construct a new {@link UpdateServiceInstanceResponse}
	 */
	public UpdateServiceInstanceResponse() {
		this(false, null, null, null);
	}

	/**
	 * Construct a new {@link UpdateServiceInstanceResponse}
	 *
	 * @param async is the operation asynchronous
	 * @param operation description of the operation being performed
	 * @param dashboardUrl the dashboard URL
	 * @param metadata containing metadata for the service instance
	 */
	public UpdateServiceInstanceResponse(boolean async, String operation, String dashboardUrl, ServiceInstanceMetadata metadata) {
		super(async, operation);
		this.dashboardUrl = dashboardUrl;
		this.metadata = metadata;
	}

	/**
	 * Get the URL of a web-based management user interface for the service instance.
	 *
	 * @return the dashboard URL, or {@literal null} if not provided
	 */
	public String getDashboardUrl() {
		return this.dashboardUrl;
	}

	/**
	 * Get object containing metadata for the service instance
	 *
	 * @return the service instance metadata
	 */
	public ServiceInstanceMetadata getMetadata() {
		return this.metadata;
	}

	/**
	 * Create a builder that provides a fluent API for constructing an {@literal UpdateServiceInstanceResponse}.
	 *
	 * @return the builder
	 */
	public static UpdateServiceInstanceResponseBuilder builder() {
		return new UpdateServiceInstanceResponseBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof UpdateServiceInstanceResponse)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		UpdateServiceInstanceResponse that = (UpdateServiceInstanceResponse) o;
		return that.canEqual(this) &&
				Objects.equals(dashboardUrl, that.dashboardUrl) &&
				Objects.equals(metadata, that.metadata);
	}

	@Override
	public boolean canEqual(Object other) {
		return other instanceof UpdateServiceInstanceResponse;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), dashboardUrl, metadata);
	}

	@Override
	public String toString() {
		return super.toString() +
				"UpdateServiceInstanceResponse{" +
				"dashboardUrl='" + dashboardUrl + '\'' +
				", metadata=" + metadata +
				'}';
	}

	/**
	 * Provides a fluent API for constructing an {@link UpdateServiceInstanceResponse}.
	 */
	public static final class UpdateServiceInstanceResponseBuilder {

		private String dashboardUrl;

		private boolean async;

		private String operation;

		private ServiceInstanceMetadata metadata;

		private UpdateServiceInstanceResponseBuilder() {
		}

		/**
		 * Set the URL of a web-based management user interface provided by the service broker for the service instance.
		 * Can be {@literal null} to indicate that a management dashboard is not provided.
		 *
		 * <p>
		 * This value will set the {@literal dashboard_url} field in the body of the response to the platform.
		 *
		 * @param dashboardUrl the dashboard URL
		 * @return the builder
		 */
		public UpdateServiceInstanceResponse.UpdateServiceInstanceResponseBuilder dashboardUrl(String dashboardUrl) {
			this.dashboardUrl = dashboardUrl;
			return this;
		}

		/**
		 * Object containing metadata for the service instance
		 * Can be {@literal null} to indicate that metadata was not provided for the service instance.
		 *
		 * <p>
		 * This value will set the {@literal metadata} field in the body of the response to the platform.
		 *
		 * @param metadata the service instance metadata
		 * @return the builder
		 */
		public UpdateServiceInstanceResponse.UpdateServiceInstanceResponseBuilder metadata(ServiceInstanceMetadata metadata) {
			this.metadata = metadata;
			return this;
		}

		/**
		 * Set a boolean value indicating whether the requested operation is being performed synchronously or
		 * asynchronously.
		 *
		 * <p>
		 * This value will be used to determine the HTTP response code to the platform. A {@literal true} value will
		 * result in a response code {@literal 202 ACCEPTED}, and a {@literal false} value will result in a response
		 * code {@literal 200 OK}.
		 *
		 * @param async {@literal true} to indicate that the operation is being performed asynchronously, {@literal
		 * 		false} to indicate that the operation was completed
		 * @return the builder
		 */
		public UpdateServiceInstanceResponseBuilder async(boolean async) {
			this.async = async;
			return this;
		}

		/**
		 * Set a value to inform the user of the operation being performed in support of an asynchronous response. This
		 * value will be passed back to the service broker in subsequent {@link GetLastServiceOperationRequest}
		 * requests.
		 *
		 * <p>
		 * This value will set the {@literal operation} field in the body of the response to the platform.
		 *
		 * @param operation the informational value
		 * @return the builder
		 */
		public UpdateServiceInstanceResponseBuilder operation(String operation) {
			this.operation = operation;
			return this;
		}

		/**
		 * Construct an {@link UpdateServiceInstanceResponse} from the provided values.
		 *
		 * @return the newly constructed {@literal UpdateServiceInstanceResponse}
		 */
		public UpdateServiceInstanceResponse build() {
			return new UpdateServiceInstanceResponse(async, operation, dashboardUrl, metadata);
		}

	}

}
