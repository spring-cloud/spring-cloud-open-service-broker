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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * JSON Schemas available for a Plan.
 *
 * @author sgunaratne@pivotal.io
 * @author Sam Gunaratne
 */
@Getter
@ToString
@EqualsAndHashCode
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Schemas {

	/**
	 * The schemas available on a service instance.
	 */
	@JsonSerialize
	@JsonProperty("service_instance")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ServiceInstanceSchema serviceInstanceSchema;

	/**
	 * The schemas available on a service binding.
	 */
	@JsonSerialize
	@JsonProperty("service_binding")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ServiceBindingSchema serviceBindingSchema;

	public Schemas() {
		serviceInstanceSchema = null;
        serviceBindingSchema = null;
    }

	public Schemas(ServiceInstanceSchema serviceInstanceSchema,
			ServiceBindingSchema serviceBindingSchema) {
		this.serviceInstanceSchema = serviceInstanceSchema;
		this.serviceBindingSchema = serviceBindingSchema;
	}
}
