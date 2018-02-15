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

import java.util.Map;
import java.util.Objects;

/**
 * Details of a request that supports arbitrary parameters and asynchronous behavior.
 *
 * @author Scott Frederick
 */
public abstract class AsyncParameterizedServiceInstanceRequest extends AsyncServiceInstanceRequest {
	/**
	 * Parameters passed by the user in the form of a JSON structure. The service broker is responsible
	 * for validating the contents of the parameters for correctness or applicability.
	 */
	protected final Map<String, Object> parameters;

	/**
	 * Platform specific contextual information under which the service instance is to be provisioned or updated.
	 */
	private final Context context;

	protected AsyncParameterizedServiceInstanceRequest() {
		this.parameters = null;
		this.context = null;
	}

	protected AsyncParameterizedServiceInstanceRequest(Map<String, Object> parameters, Context context,
													   boolean asyncAccepted, String platformInstanceId,
													   String apiInfoLocation, Context originatingIdentity) {
		super(asyncAccepted, platformInstanceId, apiInfoLocation, originatingIdentity);
		this.parameters = parameters;
		this.context = context;
	}

	public <T> T getParameters(Class<T> cls) {
		return ParameterBeanMapper.mapParametersToBean(parameters, cls);
	}

	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	public Context getContext() {
		return this.context;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AsyncParameterizedServiceInstanceRequest)) return false;
		if (!super.equals(o)) return false;
		AsyncParameterizedServiceInstanceRequest that = (AsyncParameterizedServiceInstanceRequest) o;
		return that.canEqual(this) &&
				Objects.equals(parameters, that.parameters) &&
				Objects.equals(context, that.context);
	}

	@Override
	public boolean canEqual(Object other) {
		return (other instanceof AsyncParameterizedServiceInstanceRequest);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parameters, context);
	}

	@Override
	public String toString() {
		return super.toString() +
				"AsyncParameterizedServiceInstanceRequest{" +
				"parameters=" + parameters +
				", context=" + context +
				'}';
	}
}
