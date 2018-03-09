package org.springframework.cloud.servicebroker.example;

import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse.CreateServiceInstanceAppBindingResponseBuilder;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.GetServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceBinding implements ServiceInstanceBindingService {

	/**
	 * A typical binding flow will check if a binding already exists, creating appropriate resources such as credentials
	 * then persisting the binding state. Service binding requests may also be passed arbitrary parameters which may be
	 * used to influence how bindings are created.
	 * <p>
	 * For brevity this example does not show the processes involved in creating resources security credentials
	 * and persisting service bindings.
	 *
	 * @param request Details of a request to create a service instance binding.
	 * @return Details of the created service binding
	 */
	@Override
	public CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {

		CreateServiceInstanceAppBindingResponseBuilder responseBuilder = CreateServiceInstanceAppBindingResponse.builder();

		String bindingUsername = String.format("%s-%s", request.getPlanId(), request.getBindingId());
		String bindingPassword = "supersecret";
		Object securityRole = request.getParameters().getOrDefault("securityRole", "user");

		return responseBuilder.credentials("username", bindingUsername)
							  .credentials("password", bindingPassword)
							  .credentials("role", securityRole)
							  .build();
	}

	/**
	 * Binding deletion typically involves checking if a binding exists, then deleting the persisted record of a binding
	 * and any associated resources.
	 *
	 * @param bindingRequest Details of a request to delete a service instance binding.
	 */
	@Override
	public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest bindingRequest) {
		deleteBinding(bindingRequest.getBindingId());
	}

	/**
	 * Typical flow will attempt to find the binding and return details to the requester.
	 *
	 * @param bindingRequest Details of a request to retrieve a service instance binding.
	 * @return Details of the service binding
	 */
	@Override
	public GetServiceInstanceBindingResponse getServiceInstanceBinding(GetServiceInstanceBindingRequest bindingRequest) {
		return findBindingById(bindingRequest.getBindingId());
	}

	private void deleteBinding(String bindingId) {
		throw new UnsupportedOperationException();
	}

	private GetServiceInstanceBindingResponse findBindingById(String bindingId) {
		throw new UnsupportedOperationException();
	}

}
