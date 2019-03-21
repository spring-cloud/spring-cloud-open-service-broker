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

package org.springframework.cloud.servicebroker.model.catalog;

/**
 * The list of acceptable values for the <code>requires</code> field of a service definition.
 */
public enum ServiceDefinitionRequires {
	/**
	 * Indicates that the service broker allows the platform to stream logs from bound applications to a
	 * service instance. If this permission is provided in a service definition, the broker should provide a
	 * non-null value in the <code>CreateServiceInstanceBindingResponse.syslogDrainUrl</code> field in response
	 * to a bind request.
	 */
	SERVICE_REQUIRES_SYSLOG_DRAIN("syslog_drain"),

	/**
	 * Indicates that the service broker allows the platform to bind routes to a service instance. If this permission
	 * is provided in a service definition, the broker may receive bind requests with a <code>route</code> value in
	 * the <code>bindResource</code> field of a <code>CreateServiceInstanceBindingRequest</code>.
	 */
	SERVICE_REQUIRES_ROUTE_FORWARDING("route_forwarding"),

	/**
	 * Indicates that the service broker allows the platform to bind volume mounts to an application. If this
	 * permission is provided in a service definition, the broker should provide a non-null value in the
	 * <code>CreateServiceInstanceVolumeBindingResponse.volumeMounts</code> field in response
	 * to a bind request.
	 */
	SERVICE_REQUIRES_VOLUME_MOUNT("volume_mount");

	private final String value;

	ServiceDefinitionRequires(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
