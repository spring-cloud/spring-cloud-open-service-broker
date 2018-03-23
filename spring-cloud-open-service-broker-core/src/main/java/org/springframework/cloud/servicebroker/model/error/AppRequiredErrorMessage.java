/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.error;

/**
 * An error returned when a service broker requires that a service binding request include an application GUID.
 *
 * @author Scott Frederick
 */
public class AppRequiredErrorMessage extends ErrorMessage {

	public final static String APP_REQUIRED_ERROR = "RequiresApp";

	/**
	 * Construct an error message with the provided description.
	 *
	 * @param description additional detail of the requirement
	 */
	public AppRequiredErrorMessage(String description) {
		super(APP_REQUIRED_ERROR, description);
	}
}
