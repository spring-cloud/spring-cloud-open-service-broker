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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.Map;

import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.API_INFO_LOCATION_HEADER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER;

/**
 * See: http://docs.cloudfoundry.org/services/api.html
 *
 * @author sgreenberg@pivotal.io
 */
@RestController
@Slf4j
public class ServiceInstanceBindingController extends BaseController {

	private ServiceInstanceBindingService serviceInstanceBindingService;

	@Autowired
	public ServiceInstanceBindingController(CatalogService catalogService,
											ServiceInstanceBindingService serviceInstanceBindingService) {
		super(catalogService);
		this.serviceInstanceBindingService = serviceInstanceBindingService;
	}

	@RequestMapping(value = {
			"/{cfInstanceId}/v2/service_instances/{instanceId}/service_bindings/{bindingId}",
			"/v2/service_instances/{instanceId}/service_bindings/{bindingId}"
	}, method = RequestMethod.PUT)
	public ResponseEntity<?> createServiceInstanceBinding(@PathVariable Map<String, String> pathVariables,
														  @PathVariable("instanceId") String serviceInstanceId,
														  @PathVariable("bindingId") String bindingId,
														  @RequestHeader(value = API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
														  @RequestHeader(value = ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
														  @Valid @RequestBody CreateServiceInstanceBindingRequest request) {
		request.withServiceInstanceId(serviceInstanceId)
				.withBindingId(bindingId)
				.withServiceDefinition(getServiceDefinition(request.getServiceDefinitionId()))
				.withCfInstanceId(pathVariables.get("cfInstanceId"))
				.withApiInfoLocation(apiInfoLocation)
				.withOriginatingIdentity(parseOriginatingIdentity(originatingIdentityString));

		log.debug("Creating a service instance binding: request={}", request);

		CreateServiceInstanceBindingResponse response = serviceInstanceBindingService.createServiceInstanceBinding(request);

		log.debug("Creating a service instance binding succeeded: serviceInstanceId={}, bindingId={}, response={}",
				serviceInstanceId, bindingId, response);

		return new ResponseEntity<>(response, response.isBindingExisted() ? HttpStatus.OK : HttpStatus.CREATED);
	}

	@RequestMapping(value = {
			"/{cfInstanceId}/v2/service_instances/{instanceId}/service_bindings/{bindingId}",
			"/v2/service_instances/{instanceId}/service_bindings/{bindingId}"
	}, method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceInstanceBinding(@PathVariable Map<String, String> pathVariables,
															   @PathVariable("instanceId") String serviceInstanceId,
															   @PathVariable("bindingId") String bindingId,
															   @RequestParam("service_id") String serviceDefinitionId,
															   @RequestParam("plan_id") String planId,
															   @RequestHeader(value = API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
															   @RequestHeader(value = ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		DeleteServiceInstanceBindingRequest request =
				new DeleteServiceInstanceBindingRequest(serviceInstanceId, bindingId, serviceDefinitionId, planId,
						getServiceDefinition(serviceDefinitionId))
				.withCfInstanceId(pathVariables.get("cfInstanceId"))
				.withApiInfoLocation(apiInfoLocation)
				.withOriginatingIdentity(parseOriginatingIdentity(originatingIdentityString));

		log.debug("Deleting a service instance binding: request={}", request);

		try {
			serviceInstanceBindingService.deleteServiceInstanceBinding(request);
		} catch (ServiceInstanceBindingDoesNotExistException e) {
			log.debug("Service instance binding does not exist: ", e);
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}

		log.debug("Deleting a service instance binding succeeded: bindingId={}", bindingId);

		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	@ExceptionHandler(ServiceInstanceBindingExistsException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceBindingExistsException ex) {
		log.debug("Service instance binding already exists: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
}
