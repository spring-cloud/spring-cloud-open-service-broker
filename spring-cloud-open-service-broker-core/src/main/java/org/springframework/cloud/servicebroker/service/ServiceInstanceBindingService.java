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

package org.springframework.cloud.servicebroker.service;

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerBindingRequiresAppException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerCreateOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerDeleteOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationRequest;
import org.springframework.cloud.servicebroker.model.binding.GetLastServiceBindingOperationResponse;
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
	 * Create a new binding to a service instance. In the case of a request to create a new service instance binding
	 * with an existing binding id, the implementor should throw an {@link ServiceInstanceBindingExistsException},
	 * which will return a {@literal HTTP 409}. If the request includes identical parameters as an existing binding,
	 * then the implementor should set {@link CreateServiceInstanceBindingResponse#isBindingExisted()}, which will
	 * result in an {@literal HTTP 200} with the populated response body.
	 *
	 * @param request containing the details of the request
	 * @return a {@link CreateServiceInstanceBindingResponse} on successful processing of the request
	 * @throws ServiceInstanceBindingExistsException if a binding with the given ID is already known to the broker
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the
	 * 		broker
	 * @throws ServiceBrokerBindingRequiresAppException if the broker only supports application binding but an app
	 * 		GUID is not provided in the request
	 * @throws ServiceBrokerAsyncRequiredException if the broker requires asynchronous processing of the request
	 * @throws ServiceBrokerCreateOperationInProgressException if an operation is in progress for the service
	 * 		binding
	 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/v2.16/spec.md#response-6">
	 * 		https://github.com/openservicebrokerapi/servicebroker/blob/v2.16/spec.md#response-6</a>
	 */
	default Mono<CreateServiceInstanceBindingResponse> createServiceInstanceBinding(
			CreateServiceInstanceBindingRequest request) {
		return Mono.error(new UnsupportedOperationException(
				"This service broker does not support creating service bindings."));
	}

	/**
	 * Get the details of a binding to a service instance.
	 *
	 * @param request containing the details of the request
	 * @return a {@link GetServiceInstanceBindingResponse} on successful processing of the request
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the
	 * 		broker
	 * @throws ServiceInstanceBindingDoesNotExistException if a binding with the given ID is not known to the broker
	 * @throws ServiceBrokerOperationInProgressException if a an operation is in progress for the service binding
	 */
	default Mono<GetServiceInstanceBindingResponse> getServiceInstanceBinding(
			GetServiceInstanceBindingRequest request) {
		return Mono.error(new UnsupportedOperationException(
				"This service broker does not support retrieving service bindings. " +
						"The service broker should set 'bindings_retrievable:false' in the service catalog, " +
						"or provide an implementation of the fetch binding API."));
	}

	/**
	 * Get the status of the last requested operation for a service instance.
	 *
	 * @param request containing the details of the request
	 * @return a {@link GetLastServiceBindingOperationResponse} on successful processing of the request
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the
	 * 		broker
	 * @throws ServiceInstanceBindingDoesNotExistException if a binding with the given ID is not known to the broker
	 */
	default Mono<GetLastServiceBindingOperationResponse> getLastOperation(
			GetLastServiceBindingOperationRequest request) {
		return Mono
				.error(new UnsupportedOperationException("This service broker does not support getting the status of " +
						"an asynchronous operation. " +
						"If the service broker returns '202 Accepted' in response to a bind or unbind request, " +
						"it must also provide an implementation of the get last operation API."));
	}

	/**
	 * Delete a service instance binding.
	 *
	 * @param request containing the details of the request
	 * @return a {@link DeleteServiceInstanceBindingResponse} on successful processing of the request
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the
	 * 		broker
	 * @throws ServiceInstanceBindingDoesNotExistException if a binding with the given ID is not known to the broker
	 * @throws ServiceBrokerDeleteOperationInProgressException if a an operation is in progress for the service
	 * 		binding
	 * @throws ServiceBrokerAsyncRequiredException if the broker requires asynchronous processing of the request
	 */
	default Mono<DeleteServiceInstanceBindingResponse> deleteServiceInstanceBinding(
			DeleteServiceInstanceBindingRequest request) {
		return Mono.error(new UnsupportedOperationException(
				"This service broker does not support deleting service bindings."));
	}

}
