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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Objects;

/**
 * Details of a response to a service instance binding create request.
 *
 * @author Scott Frederick
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateServiceInstanceBindingResponse {
	/**
	 * <code>true</code> to indicated that the service instance binding already existed with the same parameters as the
	 * requested service instance binding, <code>false</code> to indicate that the binding was created as new
	 */
	@JsonIgnore
	protected final boolean bindingExisted;

	protected CreateServiceInstanceBindingResponse(boolean bindingExisted) {
		this.bindingExisted = bindingExisted;
	}

	public boolean isBindingExisted() {
		return this.bindingExisted;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CreateServiceInstanceBindingResponse)) return false;
		CreateServiceInstanceBindingResponse that = (CreateServiceInstanceBindingResponse) o;
		return that.canEqual(this) &&
				bindingExisted == that.bindingExisted;
	}

	public boolean canEqual(Object other) {
		return (other instanceof CreateServiceInstanceBindingResponse);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bindingExisted);
	}

	@Override
	public String toString() {
		return "CreateServiceInstanceBindingResponse{" +
				"bindingExisted=" + bindingExisted +
				'}';
	}

}
