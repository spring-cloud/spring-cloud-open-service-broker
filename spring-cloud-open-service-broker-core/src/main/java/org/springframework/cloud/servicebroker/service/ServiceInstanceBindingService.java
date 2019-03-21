/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.service;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerBindingRequiresAppException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingResponse;

/**
 * This interface is implemented by service brokers to process requests to create and delete service instance bindings.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
public interface ServiceInstanceBindingService {

	/**
	 * Create a new binding to a service instance.
	 *
	 * @param request containing the details of the request
	 * @return a {@link CreateServiceInstanceBindingResponse} on successful processing of the request
	 * @throws ServiceInstanceBindingExistsException if a binding with the given ID is already known to the broker
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the broker
	 * @throws ServiceBrokerBindingRequiresAppException if the broker only supports application binding but an
	 *                                                  app GUID is not provided in the request
	 */
	CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest request);

	/**
	 * Get the details of a binding to a service instance.
	 *
	 * @param request containing the details of the request
	 * @return a {@link GetServiceInstanceBindingResponse} on successful processing of the request
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the broker
	 * @throws ServiceInstanceBindingDoesNotExistException if a binding with the given ID is not known to the broker
	 * @throws ServiceBrokerOperationInProgressException if a an operation is in progress for the service binding
	 */
	default GetServiceInstanceBindingResponse getServiceInstanceBinding(GetServiceInstanceBindingRequest request) {
		throw new UnsupportedOperationException("This service broker does not support retrieving service bindings. " +
				"The service broker should set 'bindings_retrievable:false' in the service catalog, " +
				"or provide an implementation of the fetch binding API.");
	}

	/**
	 * Delete a service instance binding.
	 *
	 * @param request containing the details of the request
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the broker
	 * @throws ServiceInstanceBindingDoesNotExistException if a binding with the given ID is not known to the broker
	 */
	void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request);
}
