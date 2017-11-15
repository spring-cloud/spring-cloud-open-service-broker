package org.springframework.cloud.servicebroker.model.fixture;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

	public static final String SERVICE_INSTANCE_BINDING_ID = "service_instance_binding_id";
	public static final String SERVICE_INSTANCE_ID = "service-instance-one-id";
	public static final String SYSLOG_DRAIN_URL = "http://syslog.example.com";
	public static final String APP_GUID = "app_guid";
	public static final String ROUTE = "http://route.example.com";

	public static CreateServiceInstanceBindingRequest buildCreateAppBindingRequest() {
		return new CreateServiceInstanceBindingRequest(
				ServiceFixture.getSimpleService().getId(),
				PlanFixture.getPlanOne().getId(),
				new BindResource(APP_GUID, null, null),
				ContextFixture.getCloudFoundryContext(),
				ParametersFixture.getParameters())
				.withBindingId(SERVICE_INSTANCE_BINDING_ID)
				.withServiceInstanceId(SERVICE_INSTANCE_ID);
	}

	public static CreateServiceInstanceBindingRequest buildCreateRouteBindingRequest() {
		return new CreateServiceInstanceBindingRequest(
				ServiceFixture.getSimpleService().getId(),
				PlanFixture.getPlanOne().getId(),
				new BindResource(null, ROUTE, null),
				ContextFixture.getCloudFoundryContext(),
				ParametersFixture.getParameters())
				.withBindingId(SERVICE_INSTANCE_BINDING_ID)
				.withServiceInstanceId(SERVICE_INSTANCE_ID);
	}

	public static CreateServiceInstanceAppBindingResponse buildCreateAppBindingResponse() {
		return new CreateServiceInstanceAppBindingResponse()
				.withCredentials(getCredentials());
	}

	public static CreateServiceInstanceAppBindingResponse buildCreateAppBindingResponseWithSyslog() {
		return new CreateServiceInstanceAppBindingResponse()
				.withCredentials(getCredentials())
				.withSyslogDrainUrl(SYSLOG_DRAIN_URL);
	}

	public static CreateServiceInstanceAppBindingResponse buildCreateAppBindingResponseWithVolumeMount() {
		Map<String, Object> volumeDeviceConfig = Collections.singletonMap("configKey", (Object) "configValue");
		List<VolumeMount> volumeMounts = Collections.singletonList(
				new VolumeMount("cephdriver", "/data/images", VolumeMount.Mode.READ_WRITE,
						VolumeMount.DeviceType.SHARED,
						new SharedVolumeDevice("volumeId", volumeDeviceConfig))
		);
		return new CreateServiceInstanceAppBindingResponse()
				.withVolumeMounts(volumeMounts);
	}

	public static CreateServiceInstanceRouteBindingResponse buildCreateBindingResponseForRoute() {
		return new CreateServiceInstanceRouteBindingResponse()
				.withRouteServiceUrl(ROUTE);
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
