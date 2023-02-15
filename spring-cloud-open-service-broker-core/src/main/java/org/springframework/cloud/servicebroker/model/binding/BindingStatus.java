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

package org.springframework.cloud.servicebroker.model.binding;

/**
 * The status of a new service instance binding request
 */
public enum BindingStatus {

	/**
	 * The request to create a new service instance binding is new and does not conflict with an existing binding
	 */
	NEW,

	/**
	 * The Service Binding already exists and the requested parameters are identical to the existing Service Binding
	 */
	EXISTS_WITH_IDENTICAL_PARAMETERS,

	/**
	 * A Service Binding with the same id, for the same Service Instance, already exists or is being created but with
	 * different parameters
	 */
	EXISTS_WITH_DIFFERENT_PARAMETERS

}
