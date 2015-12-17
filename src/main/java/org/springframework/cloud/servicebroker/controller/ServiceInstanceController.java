package org.springframework.cloud.servicebroker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.ErrorMessage;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * See: http://docs.cloudfoundry.org/services/api.html
 * 
 * @author sgreenberg@pivotal.io
 */
@RestController
@RequestMapping("/v2/service_instances/{instanceId}")
@Slf4j
public class ServiceInstanceController extends BaseController {

	private ServiceInstanceService service;

	@Autowired
 	public ServiceInstanceController(CatalogService catalogService, ServiceInstanceService serviceInstanceService) {
		super(catalogService);
		this.service = serviceInstanceService;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<?> createServiceInstance(@PathVariable("instanceId") String serviceInstanceId,
												   @Valid @RequestBody CreateServiceInstanceRequest request,
												   @RequestParam(value = "accepts_incomplete", required = false) boolean acceptsIncomplete) {
		log.debug("Creating a service instance: serviceInstanceId=" + serviceInstanceId);

		ServiceDefinition serviceDefinition = getRequiredServiceDefinition(request.getServiceDefinitionId());

		request.withServiceInstanceId(serviceInstanceId)
				.withServiceDefinition(serviceDefinition)
				.withAsyncAccepted(acceptsIncomplete);

		CreateServiceInstanceResponse response = service.createServiceInstance(request);

		log.debug("Creating a service instance succeeded: serviceInstanceId=" + serviceInstanceId);

		return new ResponseEntity<>(response, response.isAsync() ? HttpStatus.ACCEPTED : HttpStatus.CREATED);
	}

	@RequestMapping(value = "/last_operation", method = RequestMethod.GET)
	public ResponseEntity<?> getServiceInstanceLastOperation(@PathVariable("instanceId") String serviceInstanceId) {

		log.debug("Getting service instance status: serviceInstanceId=" + serviceInstanceId);

		GetLastServiceOperationRequest request = new GetLastServiceOperationRequest(serviceInstanceId);

		GetLastServiceOperationResponse response = service.getLastOperation(request);

		log.debug("Getting service instance status succeeded: serviceInstanceId=" + serviceInstanceId
				+ ", state=" + response.getState()
				+ ", description=" + response.getDescription());

		boolean isSuccessfulDelete = response.getState().equals(OperationState.SUCCEEDED) && response.isDeleteOperation();

		return new ResponseEntity<>(response, isSuccessfulDelete ? HttpStatus.GONE : HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteServiceInstance(@PathVariable("instanceId") String serviceInstanceId,
												   @RequestParam("service_id") String serviceDefinitionId,
												   @RequestParam("plan_id") String planId,
												   @RequestParam(value = "async", required = false) boolean acceptsIncomplete) {
		log.debug("Deleting a service instance: "
				+ "serviceInstanceId=" + serviceInstanceId
				+ ", serviceDefinitionId=" + serviceDefinitionId
				+ ", planId=" + planId
				+ ", acceptsIncomplete=" + acceptsIncomplete);

		try {
			DeleteServiceInstanceRequest request =
					new DeleteServiceInstanceRequest(serviceInstanceId, serviceDefinitionId, planId,
							getServiceDefinition(serviceDefinitionId), acceptsIncomplete);

			DeleteServiceInstanceResponse response = service.deleteServiceInstance(request);

			log.debug("Deleting a service instance succeeded: "
					+ "serviceInstanceId=" + serviceInstanceId);

			return new ResponseEntity<>("{}", response.isAsync() ? HttpStatus.ACCEPTED : HttpStatus.OK);
		} catch (ServiceInstanceDoesNotExistException e) {
			log.debug("Service instance does not exist: " + e);
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}
	}

	@RequestMapping(method = RequestMethod.PATCH)
	public ResponseEntity<String> updateServiceInstance(@PathVariable("instanceId") String serviceInstanceId,
														@Valid @RequestBody UpdateServiceInstanceRequest request,
														@RequestParam(value = "accepts_incomplete", required = false) boolean acceptsIncomplete) {
		log.debug("Updating a service instance: "
				+ "serviceInstanceId = " + serviceInstanceId
				+ ", planId = " + request.getPlanId());

		ServiceDefinition serviceDefinition = getServiceDefinition(request.getServiceDefinitionId());

		request.withServiceInstanceId(serviceInstanceId)
				.withServiceDefinition(serviceDefinition)
				.withAsyncAccepted(acceptsIncomplete);

		UpdateServiceInstanceResponse response = service.updateServiceInstance(request);

		log.debug("Updating a service instance succeeded: "
				+ "serviceInstanceId=" + serviceInstanceId);

		return new ResponseEntity<>("{}", response.isAsync() ? HttpStatus.ACCEPTED : HttpStatus.OK);
	}

	@ExceptionHandler(ServiceInstanceExistsException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceExistsException ex) {
		log.debug("Service instance already exists: " + ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ServiceInstanceUpdateNotSupportedException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceUpdateNotSupportedException ex) {
		log.debug("Service instance update not supported: " + ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
