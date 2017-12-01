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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Details of a response to a request to create a new service instance binding for an application.
 *
 * @author sgreenberg@pivotal.io
 * @author <A href="mailto:josh@joshlong.com">Josh Long</A>
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CreateServiceInstanceAppBindingResponse extends CreateServiceInstanceBindingResponse {

	/**
	 * A free-form hash of credentials that the bound application can use to access the service.
	 */
	@JsonSerialize
	@JsonProperty("credentials")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, Object> credentials;

	/**
	 * The URL to which Cloud Foundry should drain logs for the bound application. Can be <code>null</code> to
	 * indicate that the service binding does not support syslog drains.
	 */
	@JsonSerialize
	@JsonProperty("syslog_drain_url")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String syslogDrainUrl;

	/**
	 * The details of the volume mounts available to applications.
	 */
	@JsonProperty("volume_mounts")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<VolumeMount> volumeMounts;

	public CreateServiceInstanceAppBindingResponse withCredentials(final Map<String, Object> credentials) {
		this.credentials = credentials;
		return this;
	}

	public CreateServiceInstanceAppBindingResponse withSyslogDrainUrl(final String syslogDrainUrl) {
		this.syslogDrainUrl = syslogDrainUrl;
		return this;
	}

	public CreateServiceInstanceAppBindingResponse withVolumeMounts(final List<VolumeMount> volumeMounts) {
		this.volumeMounts = volumeMounts;
		return this;
	}

	public CreateServiceInstanceAppBindingResponse withBindingExisted(final boolean bindingExisted) {
		this.bindingExisted = bindingExisted;
		return this;
	}
}
