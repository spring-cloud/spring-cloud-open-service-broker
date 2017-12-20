/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

/**
 * Details of a response to a request to create a new service instance.
 *
 * @author Scott Frederick
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class CreateServiceInstanceResponse extends AsyncServiceInstanceResponse {
	/**
	 * The URL of a web-based management user interface for the service instance. Can be <code>null</code> to indicate
	 * that a management dashboard is not provided.
	 */
	@JsonSerialize
	@JsonProperty("dashboard_url")
	private final String dashboardUrl;

	/**
	 * <code>true</code> to indicated that the service instance already existed with the same parameters as the
	 * requested service instance, <code>false</code> to indicate that the instance was created as new
	 */
	@JsonIgnore
	private final boolean instanceExisted;

	private CreateServiceInstanceResponse(boolean async, String operation, String dashboardUrl, boolean instanceExisted) {
		super(async, operation);
		this.dashboardUrl = dashboardUrl;
		this.instanceExisted = instanceExisted;
	}

	public String getDashboardUrl() {
		return this.dashboardUrl;
	}

	public boolean isInstanceExisted() {
		return this.instanceExisted;
	}

	public static CreateServiceInstanceResponseBuilder builder() {
		return new CreateServiceInstanceResponseBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CreateServiceInstanceResponse)) return false;
		if (!super.equals(o)) return false;
		CreateServiceInstanceResponse that = (CreateServiceInstanceResponse) o;
		return instanceExisted == that.instanceExisted &&
				Objects.equals(dashboardUrl, that.dashboardUrl);
	}

	@Override
	public int hashCode() {
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

	public static class CreateServiceInstanceResponseBuilder {
		private String dashboardUrl;
		private boolean instanceExisted;
		private boolean async;
		private String operation;

		CreateServiceInstanceResponseBuilder() {
		}

		public CreateServiceInstanceResponseBuilder dashboardUrl(String dashboardUrl) {
			this.dashboardUrl = dashboardUrl;
			return this;
		}

		public CreateServiceInstanceResponseBuilder instanceExisted(boolean instanceExisted) {
			this.instanceExisted = instanceExisted;
			return this;
		}

		public CreateServiceInstanceResponseBuilder async(boolean async) {
			this.async = async;
			return this;
		}

		public CreateServiceInstanceResponseBuilder operation(String operation) {
			this.operation = operation;
			return this;
		}

		public CreateServiceInstanceResponse build() {
			return new CreateServiceInstanceResponse(async, operation, dashboardUrl, instanceExisted);
		}
	}
}
