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

package org.springframework.cloud.servicebroker.model.instance;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.springframework.cloud.servicebroker.model.AsyncServiceBrokerResponse;

/**
 * Details of a response to a request to create a new service instance.
 *
 * <p>
 * Objects of this type are constructed by the service broker application,
 * and used to build the response to the platform.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#response-2">Open Service Broker API specification</a>
 *
 * @author Scott Frederick
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateServiceInstanceResponse extends AsyncServiceBrokerResponse {
	@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
	private final String dashboardUrl;

	@JsonIgnore
	private final boolean instanceExisted;

	CreateServiceInstanceResponse(boolean async, String operation, String dashboardUrl, boolean instanceExisted) {
		super(async, operation);
		this.dashboardUrl = dashboardUrl;
		this.instanceExisted = instanceExisted;
	}

	CreateServiceInstanceResponse() {
		this(false, null, null, false);
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
	 * Get the boolean value indicating whether the service instance already exists with the same parameters as the
	 * requested service instance.
	 *
	 * @return the boolean value
	 */
	public boolean isInstanceExisted() {
		return this.instanceExisted;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal CreateServiceInstanceResponse}.
	 *
	 * @return the builder
	 */
	public static CreateServiceInstanceResponseBuilder builder() {
		return new CreateServiceInstanceResponseBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CreateServiceInstanceResponse)) return false;
		if (!super.equals(o)) return false;
		CreateServiceInstanceResponse that = (CreateServiceInstanceResponse) o;
		return that.canEqual(this) &&
				instanceExisted == that.instanceExisted &&
				Objects.equals(dashboardUrl, that.dashboardUrl);
	}

	@Override
	public boolean canEqual(Object other) {
		return (other instanceof CreateServiceInstanceResponse);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), dashboardUrl, instanceExisted);
	}

	@Override
	public String toString() {
		return super.toString() +
				"CreateServiceInstanceResponse{" +
				"dashboardUrl='" + dashboardUrl + '\'' +
				", instanceExisted=" + instanceExisted +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link CreateServiceInstanceResponse}.
	 */
	public static class CreateServiceInstanceResponseBuilder {
		private String dashboardUrl;
		private boolean instanceExisted;
		private boolean async;
		private String operation;

		CreateServiceInstanceResponseBuilder() {
		}

		/**
		 * Set the URL of a web-based management user interface provided by the service broker for the service
		 * instance. Can be {@literal null} to indicate that a management dashboard is not provided.
		 *
		 * <p>
		 * This value will set the {@literal dashboard_url} field in the body of the response to the platform.
		 *
		 * @param dashboardUrl the dashboard URL
		 * @return the builder
		 */
		public CreateServiceInstanceResponseBuilder dashboardUrl(String dashboardUrl) {
			this.dashboardUrl = dashboardUrl;
			return this;
		}

		/**
		 * Set a boolean value indicating whether the service instance already exists with the same parameters as the
		 * requested service instance. A {@literal true} value indicates a service instance exists and no new resources
		 * were created by the service broker, <code>false</code> indicates that new resources were created.
		 *
		 * <p>
		 * This value will be used to determine the HTTP response code to the platform. If the service broker
		 * indicates that it performed the operation synchronously, a {@literal true} value will result in a
		 * response code {@literal 200 OK}, and a {@literal false} value will result in a response code
		 * {@literal 201 CREATED}.
		 *
		 * @param instanceExisted {@literal true} to indicate that the instance exists, {@literal false} otherwise
		 * @return the builder
		 * @see #async(boolean) 
		 */
		public CreateServiceInstanceResponseBuilder instanceExisted(boolean instanceExisted) {
			this.instanceExisted = instanceExisted;
			return this;
		}

		/**
		 * Set a boolean value indicating whether the requested operation is being performed synchronously or
		 * asynchronously.
		 *
		 * <p>
		 * This value will be used to determine the HTTP response code to the platform. A {@literal true} value
		 * will result in a response code {@literal 202 ACCEPTED}; otherwise the response code will be
		 * determined by the value of {@link #instanceExisted(boolean)}.
		 *
		 * @param async {@literal true} to indicate that the operation is being performed asynchronously,
		 * {@literal false} to indicate that the operation was completed
		 * @return the builder
		 * @see #instanceExisted(boolean)
		 */
		public CreateServiceInstanceResponseBuilder async(boolean async) {
			this.async = async;
			return this;
		}

		/**
		 * Set a value to inform the user of the operation being performed in support of an asynchronous response.
		 * This value will be passed back to the service broker in subsequent {@link GetLastServiceOperationRequest}
		 * requests.
		 *
		 * <p>
		 * This value will set the {@literal operation} field in the body of the response to the platform.
		 *
		 * @param operation the informational value
		 * @return the builder
		 */
		public CreateServiceInstanceResponseBuilder operation(String operation) {
			this.operation = operation;
			return this;
		}

		/**
		 * Construct a {@link CreateServiceInstanceResponse} from the provided values.
		 *
		 * @return the newly constructed {@literal CreateServiceInstanceResponse}
		 */
		public CreateServiceInstanceResponse build() {
			return new CreateServiceInstanceResponse(async, operation, dashboardUrl, instanceExisted);
		}
	}
}
