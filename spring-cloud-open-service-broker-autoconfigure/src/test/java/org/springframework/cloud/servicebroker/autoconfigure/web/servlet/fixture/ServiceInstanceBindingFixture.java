package org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.servicebroker.model.BindResource;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRouteBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.SharedVolumeDevice;
import org.springframework.cloud.servicebroker.model.VolumeMount;

public class ServiceInstanceBindingFixture {

	public static final String SERVICE_INSTANCE_BINDING_ID = "service-instance-binding-id";
	public static final String SERVICE_INSTANCE_ID = "service-instance-one-id";
	public static final String SYSLOG_DRAIN_URL = "http://syslog.example.com";
	public static final String APP_GUID = "app-guid";
	public static final String ROUTE = "http://route.example.com";

	public static CreateServiceInstanceBindingRequest buildCreateAppBindingRequest() {
		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder()
				.serviceDefinitionId(ServiceFixture.getSimpleService().getId())
				.planId(PlanFixture.getPlanOne().getId())
				.bindResource(BindResource.builder()
						.appGuid(APP_GUID)
						.build())
				.context(ContextFixture.getContext())
				.parameters(ParametersFixture.getParameters())
				.build();

		request.setBindingId(SERVICE_INSTANCE_BINDING_ID);
		request.setServiceInstanceId(SERVICE_INSTANCE_ID);

		return request;
	}

	public static CreateServiceInstanceBindingRequest buildCreateRouteBindingRequest() {
		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder()
				.serviceDefinitionId(ServiceFixture.getSimpleService().getId())
				.planId(PlanFixture.getPlanOne().getId())
				.bindResource(BindResource.builder()
						.route(ROUTE)
						.build())
				.context(ContextFixture.getContext())
				.parameters(ParametersFixture.getParameters())
				.build();

		request.setBindingId(SERVICE_INSTANCE_BINDING_ID);
		request.setServiceInstanceId(SERVICE_INSTANCE_ID);

		return request;
	}

	public static CreateServiceInstanceAppBindingResponse buildCreateAppBindingResponse(boolean existed) {
		return CreateServiceInstanceAppBindingResponse.builder()
				.credentials(getCredentials())
				.bindingExisted(existed)
				.build();
	}

	public static CreateServiceInstanceAppBindingResponse buildCreateAppBindingResponseWithSyslog() {
		return CreateServiceInstanceAppBindingResponse.builder()
				.credentials(getCredentials())
				.syslogDrainUrl(SYSLOG_DRAIN_URL)
				.build();
	}

	public static CreateServiceInstanceAppBindingResponse buildCreateAppBindingResponseWithVolumeMount() {
		return CreateServiceInstanceAppBindingResponse.builder()
				.volumeMounts(VolumeMount.builder()
						.driver("cephdriver")
						.containerDir("/data/images")
						.mode(VolumeMount.Mode.READ_WRITE)
						.deviceType(VolumeMount.DeviceType.SHARED)
						.device(SharedVolumeDevice.builder()
								.volumeId("volumeId")
								.mountConfig("configKey", "configValue")
								.build())
						.build())
				.build();
	}

	public static CreateServiceInstanceRouteBindingResponse buildCreateBindingResponseForRoute(boolean existed) {
		return CreateServiceInstanceRouteBindingResponse.builder()
				.routeServiceUrl(ROUTE)
				.bindingExisted(existed)
				.build();
	}

	public static DeleteServiceInstanceBindingRequest buildDeleteServiceInstanceBindingRequest() {
		ServiceDefinition serviceDefinition = ServiceFixture.getSimpleService();
		DeleteServiceInstanceBindingRequest request = new DeleteServiceInstanceBindingRequest();
		request.setServiceDefinitionId(serviceDefinition.getId());
		request.setPlanId(serviceDefinition.getPlans().get(0).getId());
		request.setServiceInstanceId(SERVICE_INSTANCE_ID);
		request.setBindingId(SERVICE_INSTANCE_BINDING_ID);
		request.setServiceDefinition(serviceDefinition);
		return request;
	}

	private static Map<String,Object> getCredentials() {
		Map<String,Object> credentials = new HashMap<>();
		credentials.put("uri","http://uri.example.com");
		credentials.put("username", "user1");
		credentials.put("password", "pwd1");
		return credentials;
	}
}
