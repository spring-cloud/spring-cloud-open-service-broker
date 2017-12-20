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

import java.util.Objects;

/**
 * Details of a request that supports asynchronous behavior.
 *
 * @author Scott Frederick
 */
public abstract class AsyncServiceInstanceRequest extends ServiceBrokerRequest {
	public final static String ASYNC_REQUEST_PARAMETER = "accepts_incomplete";

	/**
	 * Indicates whether clients of the service broker allow the broker to complete the request asynchronously. A
	 * <code>false</code> value indicates that clients do not allow asynchronous processing of requests, a
	 * <code>true</code> value indicates that clients do allow asynchronous processing.
	 */
	@JsonIgnore
	protected transient boolean asyncAccepted;

	protected AsyncServiceInstanceRequest() {
	}

	public boolean isAsyncAccepted() {
		return this.asyncAccepted;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AsyncServiceInstanceRequest)) return false;
		if (!super.equals(o)) return false;
		AsyncServiceInstanceRequest that = (AsyncServiceInstanceRequest) o;
		return asyncAccepted == that.asyncAccepted;
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
