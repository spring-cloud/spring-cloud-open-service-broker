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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Details of a response to a request to create a new service instance binding associated with an application.
 *
 * <p>
 * Objects of this type are constructed by the service broker application,
 * and used to build the response to the platform.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#response-4">Open Service Broker API specification</a>
 * 
 * @author sgreenberg@pivotal.io
 * @author Josh Long
 * @author Scott Frederick
 * @author Roy Clarkson
 */
public class CreateServiceInstanceAppBindingResponse extends CreateServiceInstanceBindingResponse {

	private final Map<String, Object> credentials;

	private final String syslogDrainUrl;

	private final List<VolumeMount> volumeMounts;

	CreateServiceInstanceAppBindingResponse(boolean async, String operation, boolean bindingExisted,
											Map<String, Object> credentials,
											String syslogDrainUrl, List<VolumeMount> volumeMounts) {
		super(async, operation, bindingExisted);
		this.credentials = credentials;
		this.syslogDrainUrl = syslogDrainUrl;
		this.volumeMounts = volumeMounts;
	}

	CreateServiceInstanceAppBindingResponse() {
		this(false, null, false, new HashMap<>(), null, new ArrayList<>());
	}

	/**
	 * Get the credentials that the bound application can use to access the service instance.
	 *
	 * @return the service binding credentials
	 */
	public Map<String, Object> getCredentials() {
		return this.credentials;
	}

	/**
	 * Get the URL to which the platform should drain logs for the bound application.
	 *
	 * @return the syslog drain URL
	 */
	public String getSyslogDrainUrl() {
		return this.syslogDrainUrl;
	}

	/**
	 * Get the set of volume mounts that can be used in an application container file system.
	 *
	 * @return the set of volume mounts
	 */
	 public List<VolumeMount> getVolumeMounts() {
		return this.volumeMounts;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal CreateServiceInstanceAppBindingResponse}.
	 *
	 * @return the builder
	 */
	public static CreateServiceInstanceAppBindingResponseBuilder builder() {
		return new CreateServiceInstanceAppBindingResponseBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CreateServiceInstanceAppBindingResponse)) return false;
		if (!super.equals(o)) return false;
		CreateServiceInstanceAppBindingResponse that = (CreateServiceInstanceAppBindingResponse) o;
		return that.canEqual(this) &&
				Objects.equals(credentials, that.credentials) &&
				Objects.equals(syslogDrainUrl, that.syslogDrainUrl) &&
				Objects.equals(volumeMounts, that.volumeMounts);
	}

	@Override
	public final boolean canEqual(Object other) {
		return (other instanceof CreateServiceInstanceAppBindingResponse);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), credentials, syslogDrainUrl, volumeMounts);
	}

	@Override
	public String toString() {
		return super.toString() +
				"CreateServiceInstanceAppBindingResponse{" +
				"credentials=" + credentials +
				", syslogDrainUrl='" + syslogDrainUrl + '\'' +
				", volumeMounts=" + volumeMounts +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link CreateServiceInstanceAppBindingResponse}.
	 */
	public static class CreateServiceInstanceAppBindingResponseBuilder {
		private Map<String, Object> credentials = new HashMap<>();
		private String syslogDrainUrl;
		private List<VolumeMount> volumeMounts = new ArrayList<>();
		private boolean bindingExisted;
		private boolean async;
		private String operation;

		CreateServiceInstanceAppBindingResponseBuilder() {
		}

		/**
		 * Add a set of credentials from the provided {@literal Map} to the credentials that the bound application
		 * can use to access the service instance.
		 *
		 * <p>
		 * This value will set the {@literal credentials} field in the body of the response to the platform
		 *
		 * @param credentials a {@literal Map} of credentials
		 * @return the builder
		 */
		public CreateServiceInstanceAppBindingResponseBuilder credentials(Map<String, Object> credentials) {
			this.credentials.putAll(credentials);
			return this;
		}

		/**
		 * Add a key/value pair to the that the bound application can use to access the service instance.
		 *
		 * <p>
		 * This value will set the {@literal credentials} field in the body of the response to the platform
		 *
		 * @param key the credential key
		 * @param value the credential value
		 * @return the builder
		 */
		public CreateServiceInstanceAppBindingResponseBuilder credentials(String key, Object value) {
			this.credentials.put(key, value);
			return this;
		}

		/**
		 * Set the URL to which the platform should drain logs for the bound application. Can be {@literal null} to
		 * indicate that the service binding does not support syslog drains.
		 *
		 * <p>
		 * This value will set the {@literal syslog_drain_url} field in the body of the response to the platform
		 *
		 * @param syslogDrainUrl the syslog URL
		 * @return the builder
		 */
		public CreateServiceInstanceAppBindingResponseBuilder syslogDrainUrl(String syslogDrainUrl) {
			this.syslogDrainUrl = syslogDrainUrl;
			return this;
		}

		/**
		 * Add a set of volume mounts from the provided {@literal List} to the volume mounts that can be used in
		 * an application container file system.
		 *
		 * <p>
		 * This value will set the {@literal volume_mounts} field in the body of the response to the platform.
		 *
		 * @param volumeMounts a {@literal List} of volume mounts
		 * @return the builder
		 */
		public CreateServiceInstanceAppBindingResponseBuilder volumeMounts(List<VolumeMount> volumeMounts) {
			this.volumeMounts.addAll(volumeMounts);
			return this;
		}

		/**
		 * Add a set of volume mounts from the provided array to the volume mounts that can be used in
		 * an application container file system.
		 *
		 * <p>
		 * This value will set the {@literal volume_mounts} field in the body of the response to the platform.
		 *
		 * @param volumeMounts one more volume mounts
		 * @return the builder
		 */
		public CreateServiceInstanceAppBindingResponseBuilder volumeMounts(VolumeMount... volumeMounts) {
			Collections.addAll(this.volumeMounts, volumeMounts);
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
		 * {@literal 201 CREATED}.
		 *
		 * @param bindingExisted {@literal true} to indicate that the binding exists, {@literal false} otherwise
		 * @return the builder
		 */
		public CreateServiceInstanceAppBindingResponseBuilder bindingExisted(boolean bindingExisted) {
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
		public CreateServiceInstanceAppBindingResponseBuilder async(boolean async) {
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
		public CreateServiceInstanceAppBindingResponseBuilder operation(String operation) {
			this.operation = operation;
			return this;
		}

		/**
		 * Construct a {@link CreateServiceInstanceAppBindingResponse} from the provided values.
		 *
		 * @return the newly constructed {@literal CreateServiceInstanceAppBindingResponse}
		 */
		public CreateServiceInstanceAppBindingResponse build() {
			return new CreateServiceInstanceAppBindingResponse(async, operation, bindingExisted, credentials,
					syslogDrainUrl, volumeMounts);
		}
	}
}
