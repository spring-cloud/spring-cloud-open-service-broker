package org.springframework.cloud.servicebroker.service;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;

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
	 * @param request containing parameters sent from Cloud Controller
	 * @return a CreateServiceInstanceBindingResponse
	 * @throws ServiceInstanceBindingExistsException if a binding with the given ID is already known to the broker
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the broker
	 * @throws ServiceBrokerException on internal failure
	 */
	CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest request);

	/**
	 * Delete a service instance binding.
	 *
	 * @param request containing parameters sent from Cloud Controller
	 * @throws ServiceInstanceDoesNotExistException if a service instance with the given ID is not known to the broker
	 * @throws ServiceInstanceBindingDoesNotExistException if a binding with the given ID is not known to the broker
	 */
	void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request);
}
