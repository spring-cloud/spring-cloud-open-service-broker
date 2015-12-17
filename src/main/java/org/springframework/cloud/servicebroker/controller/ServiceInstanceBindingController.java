package org.springframework.cloud.servicebroker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.ErrorMessage;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
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
@RequestMapping("/v2/service_instances/{instanceId}/service_bindings/{bindingId}")
@Slf4j
public class ServiceInstanceBindingController extends BaseController {

	private ServiceInstanceBindingService serviceInstanceBindingService;

	@Autowired
	public ServiceInstanceBindingController(CatalogService catalogService,
											ServiceInstanceBindingService serviceInstanceBindingService) {
		super(catalogService);
		this.serviceInstanceBindingService = serviceInstanceBindingService;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<?> createServiceInstanceBinding(@PathVariable("instanceId") String serviceInstanceId,
														  @PathVariable("bindingId") String bindingId,
														  @Valid @RequestBody CreateServiceInstanceBindingRequest request) {
		log.debug("Creating a service instance binding: "
				+ "serviceInstanceId=" + serviceInstanceId
				+ ", bindingId=" + bindingId);

		request.withServiceInstanceId(serviceInstanceId)
				.withBindingId(bindingId)
				.withServiceDefinition(getServiceDefinition(request.getServiceDefinitionId()));

		CreateServiceInstanceBindingResponse response = serviceInstanceBindingService.createServiceInstanceBinding(request);

		log.debug("Creating a service instance binding succeeded: "
				+ "serviceInstanceId=" + serviceInstanceId
				+ "bindingId=" + bindingId);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceInstanceBinding(@PathVariable("instanceId") String serviceInstanceId,
															   @PathVariable("bindingId") String bindingId,
															   @RequestParam("service_id") String serviceDefinitionId,
															   @RequestParam("plan_id") String planId) {
		log.debug("Deleting a service instance binding: "
				+ "serviceInstanceId=" + serviceInstanceId
				+ ", bindingId=" + bindingId
				+ ", serviceDefinitionId=" + serviceDefinitionId
				+ ", planId=" + planId);

		try {
			DeleteServiceInstanceBindingRequest request =
					new DeleteServiceInstanceBindingRequest(serviceInstanceId, bindingId, serviceDefinitionId, planId,
							getServiceDefinition(serviceDefinitionId));

			serviceInstanceBindingService.deleteServiceInstanceBinding(request);
		} catch (ServiceInstanceBindingDoesNotExistException e) {
			log.debug("Service instance binding does not exist: " + e);
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}

		log.debug("Deleting a service instance binding succeeded: bindingId=" + bindingId);

		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	@ExceptionHandler(ServiceInstanceBindingExistsException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceBindingExistsException ex) {
		log.debug("Service instance binding already exists: " + ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
}
