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

import reactor.core.publisher.Mono;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerOperationInProgressException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.instance.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;


/**
 * This interface is implemented by service brokers to process requests related to provisioning, updating,
 * and deprovisioning service instances.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 */
public interface ServiceInstanceService {

	/**
	 * Create (provision) a new service instance.
	 *
	 * @param request containing the details of the request
	 * @return a {@link CreateServiceInstanceResponse} on successful processing of the request
	 * @throws ServiceInstanceExistsException if a service instance with the given ID is already known to the broker
	 * @throws ServiceBrokerAsyncRequiredException if the broker requires asynchronous processing of the request
	 * @throws ServiceBrokerInvalidParametersException if any parameters passed in the request are invalid
	 */
	Mono<CreateServiceInstanceResponse> createServiceInstance(CreateServiceInstanceRequest request);

	/**
	 * Get the details of a service instance.
	 *
	 * @param request containing the details of the request
	 * @return a {@link GetServiceInstanceResponse} on successful processing of the request
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the broker
	 * @throws ServiceBrokerOperationInProgressException if a an operation is in progress for the service instance
	 */
	default Mono<GetServiceInstanceResponse> getServiceInstance(GetServiceInstanceRequest request) {
		return Mono.error(new UnsupportedOperationException("This service broker does not support retrieving service instances. " +
				"The service broker should set 'instances_retrievable:false' in the service catalog, " +
				"or provide an implementation of the fetch instance API."));
	}

	/**
	 * Get the status of the last requested operation for a service instance.
	 *
	 * @param request containing the details of the request
	 * @return a {@link GetLastServiceOperationResponse} on successful processing of the request
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the broker
	 */
	default Mono<GetLastServiceOperationResponse> getLastOperation(GetLastServiceOperationRequest request) {
		return Mono.error(new UnsupportedOperationException("This service broker does not support getting the status of " +
				"an asynchronous operation. " +
				"If the service broker returns '202 Accepted' in response to a provision, update, or deprovision" +
				"request, it must also provide an implementation of the get last operation API."));
	}

	/**
	 * Delete (deprovision) a service instance.
	 *
	 * @param request containing the details of the request
	 * @return a {@link DeleteServiceInstanceResponse} on successful processing of the request
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the broker
	 * @throws ServiceBrokerAsyncRequiredException if the broker requires asynchronous processing of the request
	 */
	Mono<DeleteServiceInstanceResponse> deleteServiceInstance(DeleteServiceInstanceRequest request);

	/**
	 * Update a service instance.
	 *
	 * @param request containing the details of the request
	 * @return an {@link UpdateServiceInstanceResponse} on successful processing of the request
	 * @throws ServiceInstanceUpdateNotSupportedException if particular change is not supported
	 *         or if the request can not currently be fulfilled due to the state of the instance
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the broker
	 * @throws ServiceBrokerAsyncRequiredException if the broker requires asynchronous processing of the request
	 * @throws ServiceBrokerInvalidParametersException if any parameters passed in the request are invalid
	 */
	default Mono<UpdateServiceInstanceResponse> updateServiceInstance(UpdateServiceInstanceRequest request) {
		return Mono.error(new UnsupportedOperationException("This service broker does not support updating service instances. " +
				"The service broker should set 'plan_updateable:false' in the service catalog, " +
				"or provide an implementation of the update instance API."));
	}

}
