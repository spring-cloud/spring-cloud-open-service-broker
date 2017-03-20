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

/**
 * Details of a response to a request to delete a service instance.
 *
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DeleteServiceInstanceResponse extends AsyncServiceInstanceResponse {
	public DeleteServiceInstanceResponse withAsync(final boolean async) {
		this.async = async;
		return this;
	}

	public DeleteServiceInstanceResponse withOperation(final String operation) {
		this.operation = operation;
		return this;
	}
}
