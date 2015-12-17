package org.springframework.cloud.servicebroker.model.fixture;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.ServiceBindingResource;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;

public class ServiceInstanceBindingFixture {

	public static final String SERVICE_INSTANCE_BINDING_ID = "service_instance_binding_id";
	public static final String SERVICE_INSTANCE_ID = "service-instance-one-id";
	public static final String SYSLOG_DRAIN_URL = "http://syslog.example.com";
	public static final String APP_GUID = "app_guid";
	public static final String ROUTE = "http://route.example.com";

	public static CreateServiceInstanceBindingRequest buildCreateBindingRequestForApp() {
		return new CreateServiceInstanceBindingRequest(
				ServiceFixture.getSimpleService().getId(),
				PlanFixture.getPlanOne().getId(),
				APP_GUID,
				Collections.singletonMap(ServiceBindingResource.BIND_RESOURCE_KEY_APP.toString(), (Object) APP_GUID),
				ParametersFixture.getParameters())
				.withBindingId(SERVICE_INSTANCE_BINDING_ID)
				.withServiceInstanceId(SERVICE_INSTANCE_ID);
	}

	public static CreateServiceInstanceBindingRequest buildCreateBindingRequestForRoute() {
		return new CreateServiceInstanceBindingRequest(
				ServiceFixture.getSimpleService().getId(),
				PlanFixture.getPlanOne().getId(),
				null,
				Collections.singletonMap(ServiceBindingResource.BIND_RESOURCE_KEY_ROUTE.toString(), (Object) ROUTE),
				ParametersFixture.getParameters())
				.withBindingId(SERVICE_INSTANCE_BINDING_ID)
				.withServiceInstanceId(SERVICE_INSTANCE_ID);
	}

	public static CreateServiceInstanceBindingResponse buildCreateBindingResponseForApp() {
		return new CreateServiceInstanceBindingResponse(getCredentials());
	}

	public static CreateServiceInstanceBindingResponse buildCreateBindingResponseForRoute() {
		return new CreateServiceInstanceBindingResponse(ROUTE);
	}

	public static CreateServiceInstanceBindingResponse buildCreateBindingResponseWithSyslog() {
		return new CreateServiceInstanceBindingResponse(getCredentials(), SYSLOG_DRAIN_URL);
	}

	public static DeleteServiceInstanceBindingRequest buildDeleteServiceInstanceBindingRequest() {
		ServiceDefinition service = ServiceFixture.getSimpleService();

		return new DeleteServiceInstanceBindingRequest(SERVICE_INSTANCE_ID, SERVICE_INSTANCE_BINDING_ID,
				service.getId(), service.getPlans().get(0).getId(), service);
	}

	private static Map<String,Object> getCredentials() {
		Map<String,Object> credentials = new HashMap<>();
		credentials.put("uri","http://uri.example.com");
		credentials.put("username", "user1");
		credentials.put("password", "pwd1");
		return credentials;
	}
}
