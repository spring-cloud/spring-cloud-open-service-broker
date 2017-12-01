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
 * Service instance JSON Schemas.
 *
 * @author sgunaratne@pivotal.io
 * @author Sam Gunaratne
 */
@Getter
@ToString
@EqualsAndHashCode
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceInstanceSchema {

	/**
	 * The JSON schema for configuration parameters when creating a service instance.
	 */
	@JsonSerialize
	@JsonProperty("create")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private MethodSchema createMethodSchema;

	/**
	 * The JSON schema for configuration parameters when updating a service instance.
	 */
	@JsonSerialize
	@JsonProperty("update")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private MethodSchema updateMethodSchema;

	public ServiceInstanceSchema() {
		createMethodSchema = null;
		updateMethodSchema = null;
	}

	public ServiceInstanceSchema(MethodSchema createMethodSchema,
			MethodSchema updateMethodSchema) {
		this.createMethodSchema = createMethodSchema;
		this.updateMethodSchema = updateMethodSchema;
	}
}
