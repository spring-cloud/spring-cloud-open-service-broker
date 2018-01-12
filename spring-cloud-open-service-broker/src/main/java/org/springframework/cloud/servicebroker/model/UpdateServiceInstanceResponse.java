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

/**
 * Details of a response to a request to update a service instance.
 *
 * @author Scott Frederick
 */
public class UpdateServiceInstanceResponse extends AsyncServiceInstanceResponse {
	private UpdateServiceInstanceResponse(boolean async, String operation) {
		super(async, operation);
	}

	@Override
	public String toString() {
		return super.toString() +
				"UpdateServiceInstanceResponse{" +
				'}';
	}

	public static UpdateServiceInstanceResponseBuilder builder() {
		return new UpdateServiceInstanceResponseBuilder();
	}

	public static class UpdateServiceInstanceResponseBuilder {
		private boolean async;
		private String operation;

		UpdateServiceInstanceResponseBuilder() {
		}

		public UpdateServiceInstanceResponseBuilder async(boolean async) {
			this.async = async;
			return this;
		}

		public UpdateServiceInstanceResponseBuilder operation(String operation) {
			this.operation = operation;
			return this;
		}

		public UpdateServiceInstanceResponse build() {
			return new UpdateServiceInstanceResponse(async, operation);
		}
	}
}
