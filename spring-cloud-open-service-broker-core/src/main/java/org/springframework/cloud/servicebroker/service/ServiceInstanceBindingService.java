/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.cloud.servicebroker.service;

import reactor.core.publisher.Mono;

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
 * @author Roy Clarkson
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
	default Mono<CreateServiceInstanceBindingResponse> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
		return Mono.error(new UnsupportedOperationException("This service broker does not support creating service bindings."));
	}

	/**
	 * Get the details of a binding to a service instance.
	 *
	 * @param request containing the details of the request
	 * @return a {@link GetServiceInstanceBindingResponse} on successful processing of the request
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the broker
	 * @throws ServiceInstanceBindingDoesNotExistException if a binding with the given ID is not known to the broker
	 * @throws ServiceBrokerOperationInProgressException if a an operation is in progress for the service binding
	 */
	default Mono<GetServiceInstanceBindingResponse> getServiceInstanceBinding(GetServiceInstanceBindingRequest request) {
		return Mono.error(new UnsupportedOperationException("This service broker does not support retrieving service bindings. " +
				"The service broker should set 'bindings_retrievable:false' in the service catalog, " +
				"or provide an implementation of the fetch binding API."));
	}

	/**
	 * Delete a service instance binding.
	 *
	 * @param request containing the details of the request
	 * @return an empty Mono
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the broker
	 * @throws ServiceInstanceBindingDoesNotExistException if a binding with the given ID is not known to the broker
	 */
	default Mono<Void> deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
		return Mono.error(new UnsupportedOperationException("This service broker does not support deleting service bindings."));
	}

	// create hooks

	/**
	 * Hook for performing an action before a service instance binding is created
	 * @return a completed Mono
	 * @param request the request to create a service instance binding
	 */
	default Mono<Void> getBeforeCreateFlow(CreateServiceInstanceBindingRequest request) {
		return Mono.empty();
	}

	/**
	 * Hook for performing an action after a service instance binding is created
	 * @return a completed Mono
	 * @param request the request to create a service instance binding
	 * @param response the response resulting from a successful service instance binding create
	 */
	default Mono<Void> getAfterCreateFlow(CreateServiceInstanceBindingRequest request,
			CreateServiceInstanceBindingResponse response) {
		return Mono.empty();
	}

	/**
	 * Hook for performing an action when creating a service instance binding produces an error
	 * @return a completed Mono
	 * @param request the request to create a service instance binding
	 * @param error the error resulting from a failed service instance binding create
	 */
	default Mono<Void> getErrorCreateFlow(CreateServiceInstanceBindingRequest request, Throwable error) {
		return Mono.empty();
	}

	// delete hooks

	/**
	 * Hook for performing an action before a service instance binding is deleted
	 * @return a completed Mono
	 * @param request the request to delete a service instance binding
	 */
	default Mono<Void> getBeforeDeleteFlow(DeleteServiceInstanceBindingRequest request) {
		return Mono.empty();
	}

	/**
	 * Hook for performing an action after a service instance binding is deleted
	 * @return a completed Mono
	 * @param request the request to delete a service instance binding
	 */
	default Mono<Void> getAfterDeleteFlow(DeleteServiceInstanceBindingRequest request) {
		return Mono.empty();
	}

	/**
	 * Hook for performing an action when deleting a service instance binding produces an error
	 * @return a completed Mono
	 * @param request the request to delete a service instance binding
	 * @param error the error resulting from a failed service instance binding delete
	 */
	default Mono<Void> getErrorDeleteFlow(DeleteServiceInstanceBindingRequest request, Throwable error) {
		return Mono.empty();
	}

}
