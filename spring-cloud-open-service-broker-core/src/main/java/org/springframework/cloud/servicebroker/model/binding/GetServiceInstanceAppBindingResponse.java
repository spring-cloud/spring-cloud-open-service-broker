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
 * Details of a response to a request to retrieve a service instance binding associated with an application.
 *
 * <p>
 * Objects of this type are constructed by the service broker application,
 * and used to build the response to the platform.
 *
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md">Open Service Broker API specification</a>
 *
 * @author Scott Frederick
 */
public class GetServiceInstanceAppBindingResponse extends GetServiceInstanceBindingResponse {

	private final Map<String, Object> credentials;

	private final String syslogDrainUrl;

	private final List<VolumeMount> volumeMounts;

	GetServiceInstanceAppBindingResponse(Map<String, Object> parameters, Map<String, Object> credentials,
										 String syslogDrainUrl, List<VolumeMount> volumeMounts) {
		super(parameters);
		this.credentials = credentials;
		this.syslogDrainUrl = syslogDrainUrl;
		this.volumeMounts = volumeMounts;
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
	 * Create a builder that provides a fluent API for constructing a {@literal GetServiceInstanceAppBindingResponse}.
	 *
	 * @return the builder
	 */
	public static GetServiceInstanceAppBindingResponseBuilder builder() {
		return new GetServiceInstanceAppBindingResponseBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GetServiceInstanceAppBindingResponse)) return false;
		if (!super.equals(o)) return false;
		GetServiceInstanceAppBindingResponse that = (GetServiceInstanceAppBindingResponse) o;
		return that.canEqual(this) &&
				Objects.equals(credentials, that.credentials) &&
				Objects.equals(syslogDrainUrl, that.syslogDrainUrl) &&
				Objects.equals(volumeMounts, that.volumeMounts);
	}

	@Override
	public final boolean canEqual(Object other) {
		return (other instanceof GetServiceInstanceAppBindingResponse);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(super.hashCode(), credentials, syslogDrainUrl, volumeMounts);
	}

	@Override
	public String toString() {
		return super.toString() +
				"GetServiceInstanceAppBindingResponse{" +
				"credentials=" + credentials +
				", syslogDrainUrl='" + syslogDrainUrl + '\'' +
				", volumeMounts=" + volumeMounts +
				'}';
	}

	/**
	 * Provides a fluent API for constructing a {@link GetServiceInstanceAppBindingResponse}.
	 */
	public static class GetServiceInstanceAppBindingResponseBuilder {
		private Map<String, Object> credentials = new HashMap<>();
		private String syslogDrainUrl;
		private List<VolumeMount> volumeMounts = new ArrayList<>();
		private Map<String, Object> parameters = new HashMap<>();

		GetServiceInstanceAppBindingResponseBuilder() {
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
		public GetServiceInstanceAppBindingResponseBuilder credentials(Map<String, Object> credentials) {
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
		public GetServiceInstanceAppBindingResponseBuilder credentials(String key, Object value) {
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
		public GetServiceInstanceAppBindingResponseBuilder syslogDrainUrl(String syslogDrainUrl) {
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
		public GetServiceInstanceAppBindingResponseBuilder volumeMounts(List<VolumeMount> volumeMounts) {
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
		public GetServiceInstanceAppBindingResponseBuilder volumeMounts(VolumeMount... volumeMounts) {
			Collections.addAll(this.volumeMounts, volumeMounts);
			return this;
		}

		/**
		 * Add a set of parameters from the provided {@literal Map} to the request parameters
		 * as were provided by the platform at service binding creation.
		 *
		 * <p>
		 * This value will set the {@literal parameters} field in the body of the response to the platform.
		 *
		 * @param parameters the parameters to add
		 * @return the builder
		 * @see #getParameters()
		 */
		public GetServiceInstanceAppBindingResponseBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		/**
		 * Add a key/value pair to the request parameters as were provided in the request from the platform at
		 * service binding creation.
		 *
		 * <p>
		 * This value will set the {@literal parameters} field in the body of the response to the platform.
		 *
		 * @param key the parameter key to add
		 * @param value the parameter value to add
		 * @return the builder
		 * @see #getParameters()
		 */
		public GetServiceInstanceAppBindingResponseBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		/**
		 * Construct a {@link GetServiceInstanceAppBindingResponse} from the provided values.
		 *
		 * @return the newly constructed {@literal GetServiceInstanceAppBindingResponse}
		 */
		public GetServiceInstanceAppBindingResponse build() {
			return new GetServiceInstanceAppBindingResponse(parameters, credentials, syslogDrainUrl, volumeMounts);
		}
	}
}
