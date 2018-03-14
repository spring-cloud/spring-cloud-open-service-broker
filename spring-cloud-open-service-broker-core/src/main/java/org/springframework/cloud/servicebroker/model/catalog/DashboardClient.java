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

package org.springframework.cloud.servicebroker.model.catalog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Objects;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardClient {

	/**
	 * The client ID of the dashboard OAuth2 client that the service intends to use. The name must be unique within the
	 * platform. If the name is already in use, the platform will return an error to the
	 * operator when the service is registered.
	 */
	private final String id;

	/**
	 * The client secret for the dashboard OAuth2 client.
	 */
	private final String secret;

	/**
	 * A domain for the service dashboard that will be whitelisted by the UAA to enable dashboard SSO.
	 */
	private final String redirectUri;

	DashboardClient(String id, String secret, String redirectUri) {
		this.id = id;
		this.secret = secret;
		this.redirectUri = redirectUri;
	}

	public String getId() {
		return this.id;
	}

	public String getSecret() {
		return this.secret;
	}

	public String getRedirectUri() {
		return this.redirectUri;
	}

	public static DashboardClientBuilder builder() {
		return new DashboardClientBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DashboardClient)) return false;
		DashboardClient that = (DashboardClient) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(secret, that.secret) &&
				Objects.equals(redirectUri, that.redirectUri);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(id, secret, redirectUri);
	}

	@Override
	public String toString() {
		return "DashboardClient{" +
				"id='" + id + '\'' +
				", secret='" + secret + '\'' +
				", redirectUri='" + redirectUri + '\'' +
				'}';
	}

	public static class DashboardClientBuilder {
		private String id;
		private String secret;
		private String redirectUri;

		DashboardClientBuilder() {
		}

		public DashboardClient.DashboardClientBuilder id(String id) {
			this.id = id;
			return this;
		}

		public DashboardClient.DashboardClientBuilder secret(String secret) {
			this.secret = secret;
			return this;
		}

		public DashboardClient.DashboardClientBuilder redirectUri(String redirectUri) {
			this.redirectUri = redirectUri;
			return this;
		}

		public DashboardClient build() {
			return new DashboardClient(id, secret, redirectUri);
		}
	}
}
