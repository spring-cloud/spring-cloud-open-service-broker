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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.Map;

import static org.springframework.cloud.servicebroker.model.AsyncServiceInstanceRequest.ASYNC_REQUEST_PARAMETER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.API_INFO_LOCATION_HEADER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER;

/**
 * See: http://docs.cloudfoundry.org/services/api.html
 * 
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@RestController
@Slf4j
public class ServiceInstanceController extends BaseController {

	private ServiceInstanceService service;

	@Autowired
 	public ServiceInstanceController(CatalogService catalogService, ServiceInstanceService serviceInstanceService) {
		super(catalogService);
		this.service = serviceInstanceService;
	}

	@RequestMapping(value = {
			"/{cfInstanceId}/v2/service_instances/{instanceId}",
			"/v2/service_instances/{instanceId}"
	}, method = RequestMethod.PUT)
	public ResponseEntity<?> createServiceInstance(@PathVariable Map<String, String> pathVariables,
												   @PathVariable("instanceId") String serviceInstanceId,
												   @RequestParam(value = ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
												   @RequestHeader(value = API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
												   @RequestHeader(value = ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
												   @Valid @RequestBody CreateServiceInstanceRequest request) {
		ServiceDefinition serviceDefinition = getRequiredServiceDefinition(request.getServiceDefinitionId());

		request.withServiceInstanceId(serviceInstanceId)
				.withServiceDefinition(serviceDefinition)
				.withAsyncAccepted(acceptsIncomplete)
				.withCfInstanceId(pathVariables.get("cfInstanceId"))
				.withApiInfoLocation(apiInfoLocation)
				.withOriginatingIdentity(parseOriginatingIdentity(originatingIdentityString));

		log.debug("Creating a service instance: request={}", request);

		CreateServiceInstanceResponse response = service.createServiceInstance(request);

		log.debug("Creating a service instance succeeded: serviceInstanceId={}, response={}",
				serviceInstanceId, response);

		return new ResponseEntity<>(response, getCreateResponseCode(response));
	}

	private HttpStatus getCreateResponseCode(CreateServiceInstanceResponse response) {
		if (response.isAsync()) {
			return HttpStatus.ACCEPTED;
		} else if (response.isInstanceExisted()) {
			return HttpStatus.OK;
		} else {
			return HttpStatus.CREATED;
		}
	}

	@RequestMapping(value = {
			"/{cfInstanceId}/v2/service_instances/{instanceId}/last_operation",
			"/v2/service_instances/{instanceId}/last_operation"
	}, method = RequestMethod.GET)
	public ResponseEntity<?> getServiceInstanceLastOperation(@PathVariable Map<String, String> pathVariables,
															 @PathVariable("instanceId") String serviceInstanceId,
															 @RequestParam("service_id") String serviceDefinitionId,
															 @RequestParam("plan_id") String planId,
															 @RequestParam(value = "operation", required = false) String operation,
															 @RequestHeader(value = API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
															 @RequestHeader(value = ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		GetLastServiceOperationRequest request = new GetLastServiceOperationRequest(serviceInstanceId, serviceDefinitionId, planId, operation)
				.withCfInstanceId(pathVariables.get("cfInstanceId"))
				.withApiInfoLocation(apiInfoLocation)
				.withOriginatingIdentity(parseOriginatingIdentity(originatingIdentityString));

		log.debug("Getting service instance status: request={}", request);

		GetLastServiceOperationResponse response = service.getLastOperation(request);

		log.debug("Getting service instance status succeeded: serviceInstanceId={}, response={}",
				serviceInstanceId, response);

		boolean isSuccessfulDelete = response.getState().equals(OperationState.SUCCEEDED) && response.isDeleteOperation();

		return new ResponseEntity<>(response, isSuccessfulDelete ? HttpStatus.GONE : HttpStatus.OK);
	}

	@RequestMapping(value = {
			"/{cfInstanceId}/v2/service_instances/{instanceId}",
			"/v2/service_instances/{instanceId}"
	}, method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteServiceInstance(@PathVariable Map<String, String> pathVariables,
												   @PathVariable("instanceId") String serviceInstanceId,
												   @RequestParam("service_id") String serviceDefinitionId,
												   @RequestParam("plan_id") String planId,
												   @RequestParam(value = ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
												   @RequestHeader(value = API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
												   @RequestHeader(value = ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString) {
		DeleteServiceInstanceRequest request =
				new DeleteServiceInstanceRequest(serviceInstanceId, serviceDefinitionId, planId, getServiceDefinition(serviceDefinitionId))
				.withAsyncAccepted(acceptsIncomplete)
				.withCfInstanceId(pathVariables.get("cfInstanceId"))
				.withApiInfoLocation(apiInfoLocation)
				.withOriginatingIdentity(parseOriginatingIdentity(originatingIdentityString));

		log.debug("Deleting a service instance: request={}", request);

		try {
			DeleteServiceInstanceResponse response = service.deleteServiceInstance(request);

			log.debug("Deleting a service instance succeeded: serviceInstanceId={}, response={}",
					serviceInstanceId, response);

			return new ResponseEntity<>(response, response.isAsync() ? HttpStatus.ACCEPTED : HttpStatus.OK);
		} catch (ServiceInstanceDoesNotExistException e) {
			log.debug("Service instance does not exist: ", e);
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}
	}

	@RequestMapping(value = {
			"/{cfInstanceId}/v2/service_instances/{instanceId}",
			"/v2/service_instances/{instanceId}"
	}, method = RequestMethod.PATCH)
	public ResponseEntity<?> updateServiceInstance(@PathVariable Map<String, String> pathVariables,
												   @PathVariable("instanceId") String serviceInstanceId,
												   @RequestParam(value = ASYNC_REQUEST_PARAMETER, required = false) boolean acceptsIncomplete,
												   @RequestHeader(value = API_INFO_LOCATION_HEADER, required = false) String apiInfoLocation,
												   @RequestHeader(value = ORIGINATING_IDENTITY_HEADER, required = false) String originatingIdentityString,
												   @Valid @RequestBody UpdateServiceInstanceRequest request) {
		ServiceDefinition serviceDefinition = getServiceDefinition(request.getServiceDefinitionId());

		request.withServiceInstanceId(serviceInstanceId)
				.withServiceDefinition(serviceDefinition)
				.withAsyncAccepted(acceptsIncomplete)
				.withCfInstanceId(pathVariables.get("cfInstanceId"))
				.withApiInfoLocation(apiInfoLocation)
				.withOriginatingIdentity(parseOriginatingIdentity(originatingIdentityString));

		log.debug("Updating a service instance: request={}", request);

		UpdateServiceInstanceResponse response = service.updateServiceInstance(request);

		log.debug("Updating a service instance succeeded: serviceInstanceId={}, response={}",
				serviceInstanceId, response);

		return new ResponseEntity<>(response, response.isAsync() ? HttpStatus.ACCEPTED : HttpStatus.OK);
	}

	@ExceptionHandler(ServiceInstanceExistsException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceExistsException ex) {
		log.debug("Service instance already exists: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ServiceInstanceUpdateNotSupportedException.class)
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceUpdateNotSupportedException ex) {
		log.debug("Service instance update not supported: ", ex);
		return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
