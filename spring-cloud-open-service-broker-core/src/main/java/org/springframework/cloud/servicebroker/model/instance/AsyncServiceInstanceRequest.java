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

package org.springframework.cloud.servicebroker.model.instance;

import java.util.Objects;

import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;

/**
 * Details of a request that supports asynchronous operations.
 *
 * @author Scott Frederick
 */
public abstract class AsyncServiceInstanceRequest extends ServiceBrokerRequest {
	public final static String ASYNC_REQUEST_PARAMETER = "accepts_incomplete";

	protected transient boolean asyncAccepted;

	protected AsyncServiceInstanceRequest() {
	}

	protected AsyncServiceInstanceRequest(boolean asyncAccepted, String platformInstanceId,
										  String apiInfoLocation, Context originatingIdentity) {
		super(platformInstanceId, apiInfoLocation, originatingIdentity);
		this.asyncAccepted = asyncAccepted;
	}

	/**
	 * Get the value indicating whether the platform allows the broker to complete the request
	 * asynchronously.
	 *
	 * <p>
	 * This value is set from the {@literal async_accepted} request parameter of the request from the platform.
	 *
	 * <p>
	 * A <code>false</code> value indicates that clients do not allow asynchronous processing of requests, a
	 * <code>true</code> value indicates that clients do allow asynchronous processing.
	 *
	 * @return the boolean value
	 */
	public boolean isAsyncAccepted() {
		return this.asyncAccepted;
	}

	/**
	 * This method is intended to be used internally only; use a {@literal builder} to construct an object of this
	 * type and set all field values.
	 *
	 * @param asyncAccepted the value indicating whether the platform allows the broker to complete the request
	 * asynchronously
	 */
	public void setAsyncAccepted(boolean asyncAccepted) {
		this.asyncAccepted = asyncAccepted;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AsyncServiceInstanceRequest)) return false;
		if (!super.equals(o)) return false;
		AsyncServiceInstanceRequest that = (AsyncServiceInstanceRequest) o;
		return that.canEqual(this) &&
				asyncAccepted == that.asyncAccepted;
	}

	@Override
	public boolean canEqual(Object other) {
		return (other instanceof AsyncServiceInstanceRequest);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), asyncAccepted);
	}

	@Override
	public String toString() {
		return super.toString() +
				"AsyncServiceInstanceRequest{" +
				"asyncAccepted=" + asyncAccepted +
				'}';
	}

}
