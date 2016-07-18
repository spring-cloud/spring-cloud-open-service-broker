package org.springframework.cloud.servicebroker.controller;

import org.mockito.ArgumentCaptor;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRouteBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;
import org.springframework.cloud.servicebroker.model.fixture.ServiceInstanceBindingFixture;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInstanceBindingControllerIntegrationTest extends ServiceInstanceBindingIntegrationTest {

	private MockMvc mockMvc;
	
	@InjectMocks
	private ServiceInstanceBindingController controller;
	
	@Mock
	private ServiceInstanceBindingService serviceInstanceBindingService;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}

	@Test
	public void createBindingToAppSucceeds() throws Exception {
		CreateServiceInstanceAppBindingResponse createResponse = ServiceInstanceBindingFixture.buildCreateAppBindingResponse();

		when(serviceInstanceBindingService.createServiceInstanceBinding(eq(createRequest)))
				.thenReturn(createResponse);

		setupCatalogService(createRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(createRequest, false))
				.content(DataFixture.toJson(createRequest))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.credentials.uri", is(createResponse.getCredentials().get("uri"))))
				.andExpect(jsonPath("$.credentials.username", is(createResponse.getCredentials().get("username"))))
				.andExpect(jsonPath("$.credentials.password", is(createResponse.getCredentials().get("password"))))
				.andExpect(jsonPath("$.syslog_drain_url", nullValue()))
				.andExpect(jsonPath("$.route_service_url", nullValue()));
	}

	@Test
	public void createBindingToAppWithFoundationIdSucceeds() throws Exception {
		CreateServiceInstanceAppBindingResponse createResponse = ServiceInstanceBindingFixture.buildCreateAppBindingResponse();

		when(serviceInstanceBindingService.createServiceInstanceBinding(eq(createRequest)))
				.thenReturn(createResponse);

		setupCatalogService(createRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(createRequest, true))
				.content(DataFixture.toJson(createRequest))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.credentials.uri", is(createResponse.getCredentials().get("uri"))))
				.andExpect(jsonPath("$.credentials.username", is(createResponse.getCredentials().get("username"))))
				.andExpect(jsonPath("$.credentials.password", is(createResponse.getCredentials().get("password"))))
				.andExpect(jsonPath("$.syslog_drain_url", nullValue()))
				.andExpect(jsonPath("$.route_service_url", nullValue()));

		ArgumentCaptor<CreateServiceInstanceBindingRequest> argumentCaptor = ArgumentCaptor.forClass(CreateServiceInstanceBindingRequest.class);
		verify(serviceInstanceBindingService).createServiceInstanceBinding(argumentCaptor.capture());
		assertEquals("123", argumentCaptor.getValue().getFoundationId());
	}

	@Test
	public void createBindingToAppWithExistingSucceeds() throws Exception {
		CreateServiceInstanceAppBindingResponse createResponse =
				ServiceInstanceBindingFixture.buildCreateAppBindingResponse()
				.withBindingExisted(true);

		when(serviceInstanceBindingService.createServiceInstanceBinding(eq(createRequest)))
				.thenReturn(createResponse);

		setupCatalogService(createRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(createRequest, false))
				.content(DataFixture.toJson(createRequest))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.credentials.uri", is(createResponse.getCredentials().get("uri"))))
				.andExpect(jsonPath("$.credentials.username", is(createResponse.getCredentials().get("username"))))
				.andExpect(jsonPath("$.credentials.password", is(createResponse.getCredentials().get("password"))))
				.andExpect(jsonPath("$.syslog_drain_url", nullValue()))
				.andExpect(jsonPath("$.route_service_url", nullValue()));
	}

	@Test
	public void createBindingToRouteSucceeds() throws Exception {
		CreateServiceInstanceBindingRequest request = ServiceInstanceBindingFixture.buildCreateRouteBindingRequest();
		CreateServiceInstanceRouteBindingResponse response = ServiceInstanceBindingFixture.buildCreateBindingResponseForRoute();
		when(serviceInstanceBindingService.createServiceInstanceBinding(eq(request)))
				.thenReturn(response);

		setupCatalogService(request.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(request, false))
				.content(DataFixture.toJson(request))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.credentials", nullValue()))
				.andExpect(jsonPath("$.syslog_drain_url", nullValue()))
				.andExpect(jsonPath("$.route_service_url", is(response.getRouteServiceUrl())));
	}

	@Test
	public void createBindingToRouteWithExistingSucceeds() throws Exception {
		CreateServiceInstanceBindingRequest request = ServiceInstanceBindingFixture.buildCreateRouteBindingRequest();
		CreateServiceInstanceRouteBindingResponse response =
				ServiceInstanceBindingFixture.buildCreateBindingResponseForRoute()
				.withBindingExisted(true);
		when(serviceInstanceBindingService.createServiceInstanceBinding(eq(request)))
				.thenReturn(response);

		setupCatalogService(request.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(request, false))
				.content(DataFixture.toJson(request))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.credentials", nullValue()))
				.andExpect(jsonPath("$.syslog_drain_url", nullValue()))
				.andExpect(jsonPath("$.route_service_url", is(response.getRouteServiceUrl())));
	}

	@Test
	public void createBindingWithSyslogDrainUrlSucceeds() throws Exception {
		CreateServiceInstanceAppBindingResponse response = ServiceInstanceBindingFixture.buildCreateAppBindingResponseWithSyslog();
		when(serviceInstanceBindingService.createServiceInstanceBinding(eq(createRequest)))
			.thenReturn(response);

		setupCatalogService(createRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(createRequest, false))
				.content(DataFixture.toJson(createRequest))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.credentials.uri", is(response.getCredentials().get("uri"))))
				.andExpect(jsonPath("$.credentials.username", is(response.getCredentials().get("username"))))
				.andExpect(jsonPath("$.credentials.password", is(response.getCredentials().get("password"))))
				.andExpect(jsonPath("$.syslog_drain_url", is(response.getSyslogDrainUrl())))
				.andExpect(jsonPath("$.route_service_url", nullValue()));
	}

	@Test
	public void createBindingWithUnknownServiceInstanceIdFails() throws Exception {
		when(serviceInstanceBindingService.createServiceInstanceBinding(eq(createRequest)))
				.thenThrow(new ServiceInstanceDoesNotExistException(createRequest.getServiceInstanceId()));

		setupCatalogService(createRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(createRequest, false))
				.content(DataFixture.toJson(createRequest))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(createRequest.getServiceInstanceId())));
	}

	@Test
	public void createBindingWithUnknownServiceDefinitionIdSucceeds() throws Exception {
		CreateServiceInstanceAppBindingResponse createResponse = ServiceInstanceBindingFixture.buildCreateAppBindingResponse();

		when(serviceInstanceBindingService.createServiceInstanceBinding(eq(createRequest)))
				.thenReturn(createResponse);

		when(catalogService.getServiceDefinition(eq(createRequest.getServiceDefinitionId())))
				.thenReturn(null);

		mockMvc.perform(put(buildUrl(createRequest, false))
				.content(DataFixture.toJson(createRequest))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	public void createBindingWithDuplicateIdFails() throws Exception {
		when(serviceInstanceBindingService.createServiceInstanceBinding(eq(createRequest)))
			.thenThrow(new ServiceInstanceBindingExistsException(createRequest.getServiceInstanceId(), createRequest.getBindingId()));

		setupCatalogService(createRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(createRequest, false))
				.content(DataFixture.toJson(createRequest))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.description", containsString(createRequest.getServiceInstanceId())))
				.andExpect(jsonPath("$.description", containsString(createRequest.getBindingId())));
	}

	@Test
	public void createBindingWithInvalidFieldsFails() throws Exception {
		String body = DataFixture.toJson(createRequest);
		body = body.replace("service_id", "foo");

		mockMvc.perform(put(buildUrl(createRequest, false))
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")));
	}

	@Test
	public void createBindingWithMissingFieldsFails() throws Exception {
		String body = "{}";

		mockMvc.perform(put(buildUrl(createRequest, false))
				.content(body)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")))
				.andExpect(jsonPath("$.description", containsString("planId")));
	}

	@Test
	public void deleteBindingSucceeds() throws Exception {
		setupCatalogService(deleteRequest.getServiceDefinitionId());

		mockMvc.perform(delete(buildUrl(deleteRequest, false))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", is("{}")));

		verify(serviceInstanceBindingService).deleteServiceInstanceBinding(eq(deleteRequest));
	}

	@Test
	public void deleteBindingWithFoundationIdSucceeds() throws Exception {
		setupCatalogService(deleteRequest.getServiceDefinitionId());

		mockMvc.perform(delete(buildUrl(deleteRequest, true))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", is("{}")));

		ArgumentCaptor<DeleteServiceInstanceBindingRequest> argumentCaptor = ArgumentCaptor.forClass(DeleteServiceInstanceBindingRequest.class);
		verify(serviceInstanceBindingService).deleteServiceInstanceBinding(argumentCaptor.capture());
		assertEquals("123", argumentCaptor.getValue().getFoundationId());
	}

	@Test
	public void deleteBindingWithUnknownInstanceIdFails() throws Exception {
		doThrow(new ServiceInstanceDoesNotExistException(deleteRequest.getServiceInstanceId()))
				.when(serviceInstanceBindingService).deleteServiceInstanceBinding(eq(deleteRequest));

		setupCatalogService(deleteRequest.getServiceDefinitionId());

		mockMvc.perform(delete(buildUrl(deleteRequest, false))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(deleteRequest.getServiceInstanceId())));
	}

	@Test
	public void deleteBindingWithUnknownBindingIdFails() throws Exception {
		Mockito.doThrow(new ServiceInstanceBindingDoesNotExistException(deleteRequest.getBindingId()))
				.when(serviceInstanceBindingService).deleteServiceInstanceBinding(eq(deleteRequest));

		setupCatalogService(deleteRequest.getServiceDefinitionId());

		mockMvc.perform(delete(buildUrl(deleteRequest, false))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isGone())
				.andExpect(jsonPath("$", is("{}")));
	}

	@Test
	public void deleteBindingWithUnknownServiceDefinitionIdSucceeds() throws Exception {
		when(catalogService.getServiceDefinition(eq(deleteRequest.getServiceDefinitionId())))
				.thenReturn(null);

		mockMvc.perform(delete(buildUrl(deleteRequest, false))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
}
