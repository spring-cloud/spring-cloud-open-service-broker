package org.springframework.cloud.servicebroker.service;

import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;

/**
 * Default implementation of ServiceInstanceBindingService for service brokers that do not support bindable services.
 *
 * See http://docs.cloudfoundry.org/services/api.html#binding
 *
 * @author Scott Frederick
 */
public class NonBindableServiceInstanceBindingService implements ServiceInstanceBindingService {
	@Override
	public CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
		throw nonBindableException();
	}

	@Override
	public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
		throw nonBindableException();
	}

	private UnsupportedOperationException nonBindableException() {
		return new UnsupportedOperationException("This service broker does not support bindable services. " +
				"The service broker should set 'bindable: false' in the service catalog for all service offerings, " +
				"or provide an implementation of this service.");
	}
}
