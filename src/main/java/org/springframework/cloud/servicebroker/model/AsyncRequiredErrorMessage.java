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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An error returned when a broker requires an asynchronous request.
 *
 * @author krujos
 */
public class AsyncRequiredErrorMessage extends ErrorMessage {

	public final static String ASYNC_REQUIRED_ERROR = "AsyncRequired";

	/**
	 * This broker requires asynchronous processing.
	 *
	 * @param description user facing error message.
	 */
	public AsyncRequiredErrorMessage(String description) {
		super(description);
	}

	@JsonProperty("error")
	public String getError() {
		return ASYNC_REQUIRED_ERROR;
	}
}
