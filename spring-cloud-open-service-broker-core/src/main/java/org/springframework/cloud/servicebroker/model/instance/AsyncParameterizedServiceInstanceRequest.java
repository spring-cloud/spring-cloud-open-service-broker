/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.instance;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.cloud.servicebroker.model.AsyncServiceBrokerRequest;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.util.ParameterBeanMapperUtils;
import org.springframework.util.CollectionUtils;

/**
 * Details of a request that supports arbitrary parameters and asynchronous behavior.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class AsyncParameterizedServiceInstanceRequest extends AsyncServiceBrokerRequest {

	protected final Map<String, Object> parameters = new HashMap<>();

	private final Context context;

	/**
	 * Construct a new {@link AsyncParameterizedServiceInstanceRequest}
	 */
	protected AsyncParameterizedServiceInstanceRequest() {
		this(null, null, false, null, null, null, null);
	}

	/**
	 * Construct a new {@link AsyncParameterizedServiceInstanceRequest}
	 *
	 * @param parameters the parameters
	 * @param context the context
	 * @param asyncAccepted does the platform accept asynchronous requests
	 * @param platformInstanceId the platform instance ID
	 * @param apiInfoLocation location of the API info endpoint of the platform instance
	 * @param originatingIdentity identity of the user that initiated the request from the platform
	 * @param requestIdentity identity of the request sent from the platform
	 */
	protected AsyncParameterizedServiceInstanceRequest(Map<String, Object> parameters, Context context,
			boolean asyncAccepted, String platformInstanceId, String apiInfoLocation, Context originatingIdentity,
			String requestIdentity) {
		super(asyncAccepted, platformInstanceId, apiInfoLocation, originatingIdentity, requestIdentity);
		if (!CollectionUtils.isEmpty(parameters)) {
			this.parameters.putAll(parameters);
		}
		this.context = context;
	}

	/**
	 * Get any parameters passed by the user, with the user-supplied JSON structure converted to a {@literal Map}.
	 *
	 * <p>
	 * This value is set from the {@literal parameters} field in the body of the request from the platform.
	 *
	 * <p>
	 * The platform will pass the user-supplied JSON structure to the service broker as-is. The service broker is
	 * responsible for validating the contents of the parameters for correctness or applicability.
	 *
	 * @return the populated {@literal Map}
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	/**
	 * Get any parameters passed by the user, with the user-supplied JSON structure mapped to fields of the specified
	 * object type.
	 *
	 * <p>
	 * This value is set from the {@literal parameters} field in the body of the request from the platform.
	 *
	 * <p>
	 * An object of the specified type will be instantiated, and value from the parameters JSON will be mapped to the
	 * object using Java Bean mapping rules.
	 *
	 * <p>
	 * The platform will pass the user-supplied JSON structure to the service broker as-is. The service broker is
	 * responsible for validating the contents of the parameters for correctness or applicability.
	 *
	 * @param cls the {@link Class} representing the type of object to map the parameter key/value pairs to
	 * @param <T> the type of the object to instantiate and populate
	 * @return the instantiated and populated object
	 */
	public <T> T getParameters(Class<T> cls) {
		return ParameterBeanMapperUtils.mapParametersToBean(parameters, cls);
	}

	/**
	 * Get the platform-specific contextual information for the service instance.
	 *
	 * <p>
	 * This value is set from the {@literal context} field in the body of the request from the platform.
	 *
	 * @return the contextual information
	 */
	public Context getContext() {
		return this.context;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AsyncParameterizedServiceInstanceRequest)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		AsyncParameterizedServiceInstanceRequest that = (AsyncParameterizedServiceInstanceRequest) o;
		return that.canEqual(this) &&
				Objects.equals(parameters, that.parameters) &&
				Objects.equals(context, that.context);
	}

	@Override
	public boolean canEqual(Object other) {
		return other instanceof AsyncParameterizedServiceInstanceRequest;
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
