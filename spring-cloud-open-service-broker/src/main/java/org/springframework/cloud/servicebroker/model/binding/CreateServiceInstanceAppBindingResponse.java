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
 * Details of a response to a request to create a new service instance binding for an application.
 *
 * @author sgreenberg@pivotal.io
 * @author Josh Long
 * @author Scott Frederick
 */
public class CreateServiceInstanceAppBindingResponse extends CreateServiceInstanceBindingResponse {

	/**
	 * A free-form hash of credentials that the bound application can use to access the service.
	 */
	private final Map<String, Object> credentials;

	/**
	 * The URL to which Cloud Foundry should drain logs for the bound application. Can be <code>null</code> to
	 * indicate that the service binding does not support syslog drains.
	 */
	private final String syslogDrainUrl;

	/**
	 * The details of the volume mounts available to applications.
	 */
	private final List<VolumeMount> volumeMounts;

	CreateServiceInstanceAppBindingResponse(boolean bindingExisted, Map<String, Object> credentials,
											String syslogDrainUrl, List<VolumeMount> volumeMounts) {
		super(bindingExisted);
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

	public static class CreateServiceInstanceAppBindingResponseBuilder {
		private Map<String, Object> credentials;
		private String syslogDrainUrl;
		private List<VolumeMount> volumeMounts;
		private boolean bindingExisted;

		CreateServiceInstanceAppBindingResponseBuilder() {
		}

		public CreateServiceInstanceAppBindingResponseBuilder credentials(Map<String, Object> credentials) {
			if (this.credentials == null) {
				this.credentials = new HashMap<>();
			}
			this.credentials.putAll(credentials);
			return this;
		}

		public CreateServiceInstanceAppBindingResponseBuilder credentials(String key, Object value) {
			if (this.credentials == null) {
				this.credentials = new HashMap<>();
			}
			this.credentials.put(key, value);
			return this;
		}

		public CreateServiceInstanceAppBindingResponseBuilder syslogDrainUrl(String syslogDrainUrl) {
			this.syslogDrainUrl = syslogDrainUrl;
			return this;
		}

		public CreateServiceInstanceAppBindingResponseBuilder volumeMounts(List<VolumeMount> volumeMounts) {
			if (this.volumeMounts == null) {
				this.volumeMounts = new ArrayList<>();
			}
			this.volumeMounts.addAll(volumeMounts);
			return this;
		}

		public CreateServiceInstanceAppBindingResponseBuilder volumeMounts(VolumeMount... volumeMounts) {
			if (this.volumeMounts == null) {
				this.volumeMounts = new ArrayList<>();
			}
			Collections.addAll(this.volumeMounts, volumeMounts);
			return this;
		}

		public CreateServiceInstanceAppBindingResponseBuilder bindingExisted(boolean bindingExisted) {
			this.bindingExisted = bindingExisted;
			return this;
		}

		public CreateServiceInstanceAppBindingResponse build() {
			return new CreateServiceInstanceAppBindingResponse(bindingExisted, credentials, syslogDrainUrl, volumeMounts);
		}
	}
}
