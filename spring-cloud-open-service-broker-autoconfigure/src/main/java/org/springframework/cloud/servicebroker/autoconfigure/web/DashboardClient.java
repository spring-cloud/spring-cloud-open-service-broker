/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web;

/**
 * Internal class for marshaling {@link ServiceBrokerProperties} configuration properties
 * that describe the dashboard URI for a {@link ServiceDefinition}.
 *
 * @author Roy Clarkson
 * @see org.springframework.cloud.servicebroker.model.catalog.DashboardClient
 */
class DashboardClient {

	/**
	 * The client ID of the dashboard OAuth2 client that the service intends to use. The
	 * name must be unique within the platform. If the name is already in use, the
	 * platform will return an error to the operator when the service is registered.
	 */
	private String id;

	/**
	 * The client secret for the dashboard OAuth2 client.
	 */
	private String secret;

	/**
	 * A domain for the service dashboard that will be whitelisted by the UAA to enable
	 * dashboard SSO.
	 */
	private String redirectUri;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecret() {
		return this.secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getRedirectUri() {
		return this.redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	/**
	 * Converts this object into its corresponding model
	 *
	 * @return a DashboardClient model
	 * @see org.springframework.cloud.servicebroker.model.catalog.DashboardClient
	 */
	public org.springframework.cloud.servicebroker.model.catalog.DashboardClient toModel() {
		return org.springframework.cloud.servicebroker.model.catalog.DashboardClient.builder()
				.id(this.id)
				.secret(this.secret)
				.redirectUri(this.redirectUri)
				.build();
	}

}
