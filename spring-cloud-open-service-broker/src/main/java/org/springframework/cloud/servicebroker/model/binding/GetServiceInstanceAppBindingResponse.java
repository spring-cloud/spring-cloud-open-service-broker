/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
 * Details of a response to a request to retrieve a service instance binding for an application.
 *
 * @author Scott Frederick
 */
public class GetServiceInstanceAppBindingResponse extends GetServiceInstanceBindingResponse {

	/**
	 * A free-form hash of credentials that the bound application can use to access the service.
	 */
	private final Map<String, Object> credentials;

	/**
	 * The URL to which the platform should drain logs for the bound application. Can be <code>null</code> to
	 * indicate that the service binding does not support syslog drains.
	 */
	private final String syslogDrainUrl;

	/**
	 * The details of the volume mounts available to applications.
	 */
	private final List<VolumeMount> volumeMounts;

	GetServiceInstanceAppBindingResponse(Map<String, Object> parameters, Map<String, Object> credentials,
										 String syslogDrainUrl, List<VolumeMount> volumeMounts) {
		super(parameters);
		this.credentials = credentials;
		this.syslogDrainUrl = syslogDrainUrl;
		this.volumeMounts = volumeMounts;
	}

	public Map<String, Object> getCredentials() {
		return this.credentials;
	}

	public String getSyslogDrainUrl() {
		return this.syslogDrainUrl;
	}

	public List<VolumeMount> getVolumeMounts() {
		return this.volumeMounts;
	}

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

	public static class GetServiceInstanceAppBindingResponseBuilder {
		private Map<String, Object> credentials = new HashMap<>();
		private String syslogDrainUrl;
		private List<VolumeMount> volumeMounts = new ArrayList<>();
		private Map<String, Object> parameters = new HashMap<>();

		GetServiceInstanceAppBindingResponseBuilder() {
		}

		public GetServiceInstanceAppBindingResponseBuilder credentials(Map<String, Object> credentials) {
			this.credentials.putAll(credentials);
			return this;
		}

		public GetServiceInstanceAppBindingResponseBuilder credentials(String key, Object value) {
			this.credentials.put(key, value);
			return this;
		}

		public GetServiceInstanceAppBindingResponseBuilder syslogDrainUrl(String syslogDrainUrl) {
			this.syslogDrainUrl = syslogDrainUrl;
			return this;
		}

		public GetServiceInstanceAppBindingResponseBuilder volumeMounts(List<VolumeMount> volumeMounts) {
			this.volumeMounts.addAll(volumeMounts);
			return this;
		}

		public GetServiceInstanceAppBindingResponseBuilder volumeMounts(VolumeMount... volumeMounts) {
			Collections.addAll(this.volumeMounts, volumeMounts);
			return this;
		}

		public GetServiceInstanceAppBindingResponseBuilder parameters(Map<String, Object> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		public GetServiceInstanceAppBindingResponseBuilder parameters(String key, Object value) {
			this.parameters.put(key, value);
			return this;
		}

		public GetServiceInstanceAppBindingResponse build() {
			return new GetServiceInstanceAppBindingResponse(parameters, credentials, syslogDrainUrl, volumeMounts);
		}
	}
}
