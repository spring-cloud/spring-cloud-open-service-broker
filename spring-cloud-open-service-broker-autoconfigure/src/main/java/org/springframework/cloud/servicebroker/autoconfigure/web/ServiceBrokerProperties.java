/*
 * Copyright 2002-2026 the original author or authors.
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

import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

/**
 * {@link ConfigurationProperties} for a Service Broker.
 *
 * @author Roy Clarkson
 */
@ConfigurationProperties(prefix = "spring.cloud.openservicebroker", ignoreUnknownFields = true)
@Validated
public class ServiceBrokerProperties {

	private @Nullable String apiVersion;

	@NestedConfigurationProperty
	@Valid
	private @Nullable Catalog catalog;

	/**
	 * Get the API version.
	 * @return the API version
	 */
	public @Nullable String getApiVersion() {
		return this.apiVersion;
	}

	/**
	 * Set the API version.
	 * @param apiVersion the API version
	 */
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	/**
	 * Get the service broker catalog.
	 * @return the catalog
	 */
	public @Nullable Catalog getCatalog() {
		return this.catalog;
	}

	/**
	 * Set the service broker catalog.
	 * @param catalog the catalog
	 */
	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

}
