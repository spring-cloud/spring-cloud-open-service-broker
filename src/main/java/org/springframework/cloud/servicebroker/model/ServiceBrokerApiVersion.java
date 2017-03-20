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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class ServiceBrokerApiVersion {

	public final static String DEFAULT_API_VERSION_HEADER = "X-Broker-Api-Version";
	public final static String API_VERSION_ANY = "*";
	public final static String API_VERSION_CURRENT = "2.10";

	/**
	 * The name of the HTTP header field expected to contain the API version of the
	 * service broker client.
	 */
	private final String brokerApiVersionHeader;

	/**
	 * The version of the broker API supported by the broker. A value of <code>null</code>
	 * or <code>API_VERSION_ANY</code> will disable API version validation.
	 */
	private final String apiVersion;

	public ServiceBrokerApiVersion(String brokerApiVersionHeader, String apiVersion) {
		this.brokerApiVersionHeader = brokerApiVersionHeader;
		this.apiVersion = apiVersion;
	}

	public ServiceBrokerApiVersion(String apiVersion) {
		this(DEFAULT_API_VERSION_HEADER, apiVersion);
	}

	public ServiceBrokerApiVersion() {
		this(DEFAULT_API_VERSION_HEADER, API_VERSION_ANY);
	}
}
