package org.springframework.cloud.servicebroker.webmvc.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.cloud.servicebroker.controller.ServiceInstanceController;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.springframework.cloud.servicebroker.model.AsyncRequiredErrorMessage;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;
import org.springframework.cloud.servicebroker.model.fixture.ServiceInstanceFixture;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.API_INFO_LOCATION_HEADER;
import static org.springframework.cloud.servicebroker.model.ServiceBrokerRequest.ORIGINATING_IDENTITY_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInstanceControllerIntegrationTest extends ControllerIntegrationTest {

	private static final String SERVICE_INSTANCES_ROOT_PATH = "/v2/service_instances/";

	private MockMvc mockMvc;

	@InjectMocks
	private ServiceInstanceController controller;

	@Mock
	private ServiceInstanceService serviceInstanceService;

	private UriComponentsBuilder uriBuilder;
	private UriComponentsBuilder cfInstanceIdUriBuilder;

	private CreateServiceInstanceRequest syncCreateRequest;
	private CreateServiceInstanceRequest asyncCreateRequest;
	private CreateServiceInstanceResponse syncCreateResponse;
	private CreateServiceInstanceResponse asyncCreateResponse;

	private DeleteServiceInstanceRequest syncDeleteRequest;
	private DeleteServiceInstanceRequest asyncDeleteRequest;
	private DeleteServiceInstanceResponse syncDeleteResponse;
	private DeleteServiceInstanceResponse asyncDeleteResponse;

	private UpdateServiceInstanceRequest syncUpdateRequest;
	private UpdateServiceInstanceRequest asyncUpdateRequest;
	private UpdateServiceInstanceResponse syncUpdateResponse;
	private UpdateServiceInstanceResponse asyncUpdateResponse;

	private GetLastServiceOperationRequest lastOperationRequest;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setMessageConverters(new MappingJackson2HttpMessageConverter())
				.build();

		uriBuilder = UriComponentsBuilder.fromPath(SERVICE_INSTANCES_ROOT_PATH);
		cfInstanceIdUriBuilder = UriComponentsBuilder.fromPath("/").path(CF_INSTANCE_ID).path(SERVICE_INSTANCES_ROOT_PATH);

		syncCreateRequest = ServiceInstanceFixture.buildCreateServiceInstanceRequest(false);
		syncCreateResponse = ServiceInstanceFixture.buildCreateServiceInstanceResponse(false);
		asyncCreateRequest = ServiceInstanceFixture.buildCreateServiceInstanceRequest(true);
		asyncCreateResponse = ServiceInstanceFixture.buildCreateServiceInstanceResponse(true);

		syncDeleteRequest = ServiceInstanceFixture.buildDeleteServiceInstanceRequest(false);
		syncDeleteResponse = ServiceInstanceFixture.buildDeleteServiceInstanceResponse(false);
		asyncDeleteRequest = ServiceInstanceFixture.buildDeleteServiceInstanceRequest(true);
		asyncDeleteResponse = ServiceInstanceFixture.buildDeleteServiceInstanceResponse(true);

		syncUpdateRequest = ServiceInstanceFixture.buildUpdateServiceInstanceRequest(false);
		syncUpdateResponse = ServiceInstanceFixture.buildUpdateServiceInstanceResponse(false);
		asyncUpdateRequest = ServiceInstanceFixture.buildUpdateServiceInstanceRequest(true);
		asyncUpdateResponse = ServiceInstanceFixture.buildUpdateServiceInstanceResponse(true);

		lastOperationRequest = ServiceInstanceFixture.buildGetLastOperationRequest();
	}

	@Test
	public void createServiceInstanceSucceeds() throws Exception {
		when(serviceInstanceService.createServiceInstance(eq(syncCreateRequest)))
				.thenReturn(syncCreateResponse);

		setupCatalogService(syncCreateRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(syncCreateRequest, false))
				.content(DataFixture.toJson(syncCreateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.dashboard_url", is(syncCreateResponse.getDashboardUrl())));

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertFalse(actualRequest.isAsyncAccepted());
		assertNull(actualRequest.getCfInstanceId());
		assertEquals(API_INFO_LOCATION, actualRequest.getApiInfoLocation());
		assertEquals(ORIGINATING_IDENTITY_PLATFORM, actualRequest.getOriginatingIdentity().getPlatform());
		assertEquals(ORIGINATING_USER_VALUE, actualRequest.getOriginatingIdentity().getProperty(ORIGINATING_USER_KEY));
		assertEquals(ORIGINATING_EMAIL_VALUE, actualRequest.getOriginatingIdentity().getProperty(ORIGINATING_EMAIL_KEY));
	}

	@Test
	public void createServiceInstanceWithCfInstanceIdSucceeds() throws Exception {
		when(serviceInstanceService.createServiceInstance(eq(syncCreateRequest)))
				.thenReturn(syncCreateResponse);

		setupCatalogService(syncCreateRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(syncCreateRequest, true))
				.content(DataFixture.toJson(syncCreateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.dashboard_url", is(syncCreateResponse.getDashboardUrl())));

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertEquals(CF_INSTANCE_ID, actualRequest.getCfInstanceId());
		assertNull(actualRequest.getOriginatingIdentity());
	}

	@Test
	public void createServiceInstanceWithAsyncSucceeds() throws Exception {
		when(serviceInstanceService.createServiceInstance(eq(asyncCreateRequest)))
				.thenReturn(asyncCreateResponse);

		setupCatalogService(asyncCreateRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(asyncCreateRequest, false))
				.content(DataFixture.toJson(asyncCreateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.dashboard_url", is(asyncCreateResponse.getDashboardUrl())))
				.andExpect(jsonPath("$.operation", is(asyncCreateResponse.getOperation())));

		CreateServiceInstanceRequest actualRequest = verifyCreateServiceInstance();
		assertTrue(actualRequest.isAsyncAccepted());
	}

	@Test
	public void createServiceInstanceWithExistingInstanceSucceeds() throws Exception {
		when(serviceInstanceService.createServiceInstance(eq(syncCreateRequest)))
				.thenReturn(syncCreateResponse.withInstanceExisted(true));

		setupCatalogService(syncCreateRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(syncCreateRequest, false))
				.content(DataFixture.toJson(syncCreateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.dashboard_url", is(syncCreateResponse.getDashboardUrl())));
	}

	@Test
	public void createServiceInstanceWithUnknownServiceDefinitionIdFails() throws Exception {
		when(catalogService.getServiceDefinition(eq(syncCreateRequest.getServiceDefinitionId())))
				.thenReturn(null);

		mockMvc.perform(put(buildUrl(syncCreateRequest, false))
				.content(DataFixture.toJson(syncCreateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(syncCreateRequest.getServiceDefinitionId())));
	}

	@Test
	public void createDuplicateServiceInstanceIdFails() throws Exception {
		when(serviceInstanceService.createServiceInstance(eq(syncCreateRequest)))
				.thenThrow(new ServiceInstanceExistsException(syncCreateRequest.getServiceInstanceId(),
						syncCreateRequest.getServiceDefinitionId()));

		setupCatalogService(syncCreateRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(syncCreateRequest, false))
				.content(DataFixture.toJson(syncCreateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.description", containsString(syncCreateRequest.getServiceInstanceId())));
	}

	@Test
	public void createServiceInstanceWithAsyncRequiredFails() throws Exception {
		when(serviceInstanceService.createServiceInstance(eq(syncCreateRequest)))
				.thenThrow(new ServiceBrokerAsyncRequiredException("async required description"));

		setupCatalogService(syncCreateRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(syncCreateRequest, false))
				.content(DataFixture.toJson(syncCreateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.error", is(AsyncRequiredErrorMessage.ASYNC_REQUIRED_ERROR)))
				.andExpect(jsonPath("$.description", is("async required description")));
	}

	@Test
	public void createServiceInstanceWithInvalidParametersFails() throws Exception {
		when(serviceInstanceService.createServiceInstance(eq(syncCreateRequest)))
				.thenThrow(new ServiceBrokerInvalidParametersException("invalid parameters description"));

		setupCatalogService(syncCreateRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(syncCreateRequest, false))
				.content(DataFixture.toJson(syncCreateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", is("invalid parameters description")));
	}

	@Test
	public void createServiceInstanceWithInvalidFieldsFails() throws Exception {
		setupCatalogService(syncCreateRequest.getServiceDefinitionId());

		String body = DataFixture.toJson(syncCreateRequest);
		body = body.replace("service_id", "foo");

		mockMvc.perform(put(buildUrl(syncCreateRequest, false))
				.content(body)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")));
	}

	@Test
	public void createServiceInstanceWithMissingFieldsFails() throws Exception {
		String body = "{}";

		mockMvc.perform(put(buildUrl(syncCreateRequest, false))
				.content(body)
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString("serviceDefinitionId")))
				.andExpect(jsonPath("$.description", containsString("planId")))
				.andExpect(jsonPath("$.description", containsString("organizationGuid")))
				.andExpect(jsonPath("$.description", containsString("spaceGuid")));
	}

	@Test
	public void createServiceInstanceWithMissingIdentityPropertiesFails() throws Exception {
		setupCatalogService(syncCreateRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(syncCreateRequest, false))
				.content(DataFixture.toJson(syncCreateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, "test-platform")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString("Expected platform and properties")))
				.andExpect(jsonPath("$.description", containsString(ORIGINATING_IDENTITY_HEADER)));
	}

	@Test
	public void createServiceInstanceWithMalformedIdentityPropertiesFails() throws Exception {
		setupCatalogService(syncCreateRequest.getServiceDefinitionId());

		mockMvc.perform(put(buildUrl(syncCreateRequest, false))
				.content(DataFixture.toJson(syncCreateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, "test-platform nonBase64EncodedString")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString("Error parsing JSON properties")))
				.andExpect(jsonPath("$.description", containsString(ORIGINATING_IDENTITY_HEADER)));
	}

	@Test
	public void deleteServiceInstanceSucceeds() throws Exception {
		when(serviceInstanceService.deleteServiceInstance(eq(syncDeleteRequest)))
				.thenReturn(syncDeleteResponse);

		setupCatalogService(syncDeleteRequest.getServiceDefinitionId());

		mockMvc.perform(delete(buildUrl(syncDeleteRequest, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("{}"));

		DeleteServiceInstanceRequest actualRequest = verifyDeleteServiceInstance();
		assertFalse(actualRequest.isAsyncAccepted());
		assertNull(actualRequest.getCfInstanceId());
		assertEquals(API_INFO_LOCATION, actualRequest.getApiInfoLocation());
		assertEquals(ORIGINATING_IDENTITY_PLATFORM, actualRequest.getOriginatingIdentity().getPlatform());
		assertEquals(ORIGINATING_USER_VALUE, actualRequest.getOriginatingIdentity().getProperty(ORIGINATING_USER_KEY));
		assertEquals(ORIGINATING_EMAIL_VALUE, actualRequest.getOriginatingIdentity().getProperty(ORIGINATING_EMAIL_KEY));
	}

	@Test
	public void deleteServiceInstanceWithCfInstanceIdSucceeds() throws Exception {
		when(serviceInstanceService.deleteServiceInstance(eq(syncDeleteRequest)))
				.thenReturn(syncDeleteResponse);

		setupCatalogService(syncDeleteRequest.getServiceDefinitionId());

		mockMvc.perform(delete(buildUrl(syncDeleteRequest, true))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		DeleteServiceInstanceRequest actualRequest = verifyDeleteServiceInstance();
		assertEquals(CF_INSTANCE_ID, actualRequest.getCfInstanceId());
		assertNull(actualRequest.getOriginatingIdentity());
	}

	@Test
	public void deleteServiceInstanceWithAsyncSucceeds() throws Exception {
		when(serviceInstanceService.deleteServiceInstance(eq(asyncDeleteRequest)))
				.thenReturn(asyncDeleteResponse);

		setupCatalogService(asyncDeleteRequest.getServiceDefinitionId());

		mockMvc.perform(delete(buildUrl(asyncDeleteRequest, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", is(asyncDeleteResponse.getOperation())));

		DeleteServiceInstanceRequest actualRequest = verifyDeleteServiceInstance();
		assertTrue(actualRequest.isAsyncAccepted());
	}

	@Test
	public void deleteServiceInstanceWithUnknownIdFails() throws Exception {
		when(serviceInstanceService.deleteServiceInstance(eq(syncDeleteRequest)))
				.thenThrow(new ServiceInstanceDoesNotExistException(syncDeleteRequest.getServiceInstanceId()));

		setupCatalogService(syncDeleteRequest.getServiceDefinitionId());

		mockMvc.perform(delete(buildUrl(syncDeleteRequest, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isGone())
				.andExpect(jsonPath("$", is("{}")));
	}

	@Test
	public void deleteServiceInstanceWithUnknownServiceDefinitionIdSucceeds() throws Exception {
		when(serviceInstanceService.deleteServiceInstance(eq(syncDeleteRequest)))
				.thenReturn(syncDeleteResponse);

		when(catalogService.getServiceDefinition(eq(syncDeleteRequest.getServiceDefinitionId())))
				.thenReturn(null);

		mockMvc.perform(delete(buildUrl(syncDeleteRequest, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteServiceInstanceWithAsyncRequiredFails() throws Exception {
		when(serviceInstanceService.deleteServiceInstance(eq(syncDeleteRequest)))
				.thenThrow(new ServiceBrokerAsyncRequiredException("async required description"));

		setupCatalogService(syncDeleteRequest.getServiceDefinitionId());

		mockMvc.perform(delete(buildUrl(syncDeleteRequest, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.error", is(AsyncRequiredErrorMessage.ASYNC_REQUIRED_ERROR)))
				.andExpect(jsonPath("$.description", is("async required description")));
	}

	@Test
	public void updateServiceInstanceSucceeds() throws Exception {
		when(serviceInstanceService.updateServiceInstance(eq(syncUpdateRequest)))
				.thenReturn(syncUpdateResponse);

		setupCatalogService(syncUpdateRequest.getServiceDefinitionId());

		mockMvc.perform(patch(buildUrl(syncUpdateRequest, false))
				.content(DataFixture.toJson(syncUpdateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("{}"));

		UpdateServiceInstanceRequest actualRequest = verifyUpdateServiceInstance();
		assertFalse(actualRequest.isAsyncAccepted());
		assertNull(actualRequest.getCfInstanceId());
		assertEquals(API_INFO_LOCATION, actualRequest.getApiInfoLocation());
		assertEquals(ORIGINATING_IDENTITY_PLATFORM, actualRequest.getOriginatingIdentity().getPlatform());
		assertEquals(ORIGINATING_USER_VALUE, actualRequest.getOriginatingIdentity().getProperty(ORIGINATING_USER_KEY));
		assertEquals(ORIGINATING_EMAIL_VALUE, actualRequest.getOriginatingIdentity().getProperty(ORIGINATING_EMAIL_KEY));
	}

	@Test
	public void updateServiceInstanceWithCfInstanceIdSucceeds() throws Exception {
		when(serviceInstanceService.updateServiceInstance(eq(syncUpdateRequest)))
				.thenReturn(syncUpdateResponse);

		setupCatalogService(syncUpdateRequest.getServiceDefinitionId());

		mockMvc.perform(patch(buildUrl(syncUpdateRequest, true))
				.content(DataFixture.toJson(syncUpdateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("{}"));

		UpdateServiceInstanceRequest actualRequest = verifyUpdateServiceInstance();
		assertEquals(CF_INSTANCE_ID, actualRequest.getCfInstanceId());
		assertNull(actualRequest.getOriginatingIdentity());
	}

	@Test
	public void updateServiceInstanceWithAsyncSucceeds() throws Exception {
		when(serviceInstanceService.updateServiceInstance(eq(asyncUpdateRequest)))
				.thenReturn(asyncUpdateResponse);

		setupCatalogService(asyncUpdateRequest.getServiceDefinitionId());

		mockMvc.perform(patch(buildUrl(asyncUpdateRequest, false))
				.content(DataFixture.toJson(asyncUpdateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.operation", is(asyncUpdateResponse.getOperation())));

		UpdateServiceInstanceRequest actualRequest = verifyUpdateServiceInstance();
		assertTrue(actualRequest.isAsyncAccepted());
	}

	@Test
	public void updateServiceInstanceWithAsyncRequiredFails() throws Exception {
		when(serviceInstanceService.updateServiceInstance(eq(syncUpdateRequest)))
				.thenThrow(new ServiceBrokerAsyncRequiredException("async required description"));

		setupCatalogService(syncUpdateRequest.getServiceDefinitionId());

		mockMvc.perform(patch(buildUrl(syncUpdateRequest, false))
				.content(DataFixture.toJson(syncUpdateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.error", is(AsyncRequiredErrorMessage.ASYNC_REQUIRED_ERROR)))
				.andExpect(jsonPath("$.description", is("async required description")));
	}

	@Test
	public void updateServiceInstanceWithUnknownIdFails() throws Exception {
		when(serviceInstanceService.updateServiceInstance(eq(syncUpdateRequest)))
				.thenThrow(new ServiceInstanceDoesNotExistException(syncUpdateRequest.getServiceInstanceId()));

		setupCatalogService(syncUpdateRequest.getServiceDefinitionId());

		mockMvc.perform(patch(buildUrl(syncUpdateRequest, false))
				.content(DataFixture.toJson(syncUpdateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.description", containsString(syncUpdateRequest.getServiceInstanceId())));
	}

	@Test
	public void updateServiceInstanceWithUnknownServiceDefinitionIdSucceeds() throws Exception {
		when(serviceInstanceService.updateServiceInstance(eq(syncUpdateRequest)))
				.thenReturn(syncUpdateResponse);

		when(catalogService.getServiceDefinition(eq(syncUpdateRequest.getServiceDefinitionId())))
				.thenReturn(null);

		mockMvc.perform(patch(buildUrl(syncUpdateRequest, false))
				.content(DataFixture.toJson(syncUpdateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void updateServiceInstanceWithUnknownPlanIdFails() throws Exception {
		when(serviceInstanceService.updateServiceInstance(eq(syncUpdateRequest)))
				.thenThrow(new ServiceInstanceUpdateNotSupportedException("description"));

		setupCatalogService(syncUpdateRequest.getServiceDefinitionId());

		mockMvc.perform(patch(buildUrl(syncUpdateRequest, false))
				.content(DataFixture.toJson(syncUpdateRequest))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.description", containsString("description")));
	}

	@Test
	public void lastOperationHasInProgressStatus() throws Exception {
		when(serviceInstanceService.getLastOperation(eq(lastOperationRequest)))
				.thenReturn(new GetLastServiceOperationResponse()
						.withOperationState(OperationState.IN_PROGRESS)
						.withDescription("working on it"));

		mockMvc.perform(get(buildUrl(lastOperationRequest, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION)
				.header(ORIGINATING_IDENTITY_HEADER, buildOriginatingIdentityHeader()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state", is(OperationState.IN_PROGRESS.toString())))
				.andExpect(jsonPath("$.description", is("working on it")));

		GetLastServiceOperationRequest actualRequest = verifyLastOperation();
		assertNull(actualRequest.getCfInstanceId());
		assertEquals(API_INFO_LOCATION, actualRequest.getApiInfoLocation());
		assertEquals(ORIGINATING_IDENTITY_PLATFORM, actualRequest.getOriginatingIdentity().getPlatform());
		assertEquals(ORIGINATING_USER_VALUE, actualRequest.getOriginatingIdentity().getProperty(ORIGINATING_USER_KEY));
		assertEquals(ORIGINATING_EMAIL_VALUE, actualRequest.getOriginatingIdentity().getProperty(ORIGINATING_EMAIL_KEY));
	}

	@Test
	public void lastOperationHasSucceededStatus() throws Exception {
		GetLastServiceOperationResponse response = new GetLastServiceOperationResponse()
				.withOperationState(OperationState.SUCCEEDED)
				.withDescription("all good");

		when(serviceInstanceService.getLastOperation(eq(lastOperationRequest))).thenReturn(response);

		mockMvc.perform(get(buildUrl(lastOperationRequest, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state", is(OperationState.SUCCEEDED.toString())))
				.andExpect(jsonPath("$.description", is("all good")));
	}

	@Test
	public void lastOperationHasSucceededStatusWithCfInstanceId() throws Exception {
		GetLastServiceOperationResponse response = new GetLastServiceOperationResponse()
				.withOperationState(OperationState.SUCCEEDED)
				.withDescription("all good");

		when(serviceInstanceService.getLastOperation(eq(lastOperationRequest))).thenReturn(response);

		mockMvc.perform(get(buildUrl(lastOperationRequest, true))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state", is(OperationState.SUCCEEDED.toString())))
				.andExpect(jsonPath("$.description", is("all good")));

		GetLastServiceOperationRequest actualRequest = verifyLastOperation();
		assertEquals(CF_INSTANCE_ID, actualRequest.getCfInstanceId());
		assertNull(actualRequest.getOriginatingIdentity());
	}

	@Test
	public void lastOperationHasSucceededStatusWithDeletionComplete() throws Exception {
		GetLastServiceOperationResponse response = new GetLastServiceOperationResponse()
				.withOperationState(OperationState.SUCCEEDED)
				.withDescription("all gone")
				.withDeleteOperation(true);

		when(serviceInstanceService.getLastOperation(eq(lastOperationRequest)))
				.thenReturn(response);

		mockMvc.perform(get(buildUrl(lastOperationRequest, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION))
				.andExpect(status().isGone())
				.andExpect(jsonPath("$.state", is(OperationState.SUCCEEDED.toString())))
				.andExpect(jsonPath("$.description", is("all gone")));
	}

	@Test
	public void lastOperationHasFailedStatus() throws Exception {
		GetLastServiceOperationResponse response = new GetLastServiceOperationResponse()
				.withOperationState(OperationState.FAILED)
				.withDescription("not so good");

		when(serviceInstanceService.getLastOperation(eq(lastOperationRequest)))
				.thenReturn(response);

		mockMvc.perform(get(buildUrl(lastOperationRequest, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.state", is(OperationState.FAILED.toString())))
				.andExpect(jsonPath("$.description", is("not so good")));
	}

	@Test
	public void lastOperationWithUnknownIdFails() throws Exception {
		when(serviceInstanceService.getLastOperation(eq(lastOperationRequest)))
				.thenThrow(new ServiceInstanceDoesNotExistException(lastOperationRequest.getServiceInstanceId()));

		mockMvc.perform(get(buildUrl(lastOperationRequest, false))
				.header(API_INFO_LOCATION_HEADER, API_INFO_LOCATION))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.description", containsString(lastOperationRequest.getServiceInstanceId())));
	}

	private String buildUrl(CreateServiceInstanceRequest request, Boolean withCfInstanceId) {
		UriComponentsBuilder builder = withCfInstanceId ? cfInstanceIdUriBuilder : uriBuilder;
		return builder.path(request.getServiceInstanceId())
				.queryParam("accepts_incomplete", request.isAsyncAccepted())
				.toUriString();
	}

	private String buildUrl(DeleteServiceInstanceRequest request, Boolean withCfInstanceId) {
		UriComponentsBuilder builder = withCfInstanceId ? cfInstanceIdUriBuilder : uriBuilder;
		return builder.path(request.getServiceInstanceId())
				.queryParam("service_id", request.getServiceDefinitionId())
				.queryParam("plan_id", request.getPlanId())
				.queryParam("accepts_incomplete", request.isAsyncAccepted())
				.toUriString();
	}

	private String buildUrl(UpdateServiceInstanceRequest request, Boolean withCfInstanceId) {
		UriComponentsBuilder builder = withCfInstanceId ? cfInstanceIdUriBuilder : uriBuilder;
		return builder.path(request.getServiceInstanceId())
				.queryParam("accepts_incomplete", request.isAsyncAccepted())
				.toUriString();
	}

	private String buildUrl(GetLastServiceOperationRequest request, Boolean withCfInstanceId) {
		UriComponentsBuilder builder = withCfInstanceId ? cfInstanceIdUriBuilder : uriBuilder;
		return builder.pathSegment(request.getServiceInstanceId(), "last_operation")
				.queryParam("service_id", request.getServiceDefinitionId())
				.queryParam("plan_id", request.getPlanId())
				.queryParam("operation", request.getOperation())
				.toUriString();
	}

	private CreateServiceInstanceRequest verifyCreateServiceInstance() {
		ArgumentCaptor<CreateServiceInstanceRequest> argumentCaptor = ArgumentCaptor.forClass(CreateServiceInstanceRequest.class);
		Mockito.verify(serviceInstanceService).createServiceInstance(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	private DeleteServiceInstanceRequest verifyDeleteServiceInstance() {
		ArgumentCaptor<DeleteServiceInstanceRequest> argumentCaptor = ArgumentCaptor.forClass(DeleteServiceInstanceRequest.class);
		Mockito.verify(serviceInstanceService).deleteServiceInstance(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	private UpdateServiceInstanceRequest verifyUpdateServiceInstance() {
		ArgumentCaptor<UpdateServiceInstanceRequest> argumentCaptor = ArgumentCaptor.forClass(UpdateServiceInstanceRequest.class);
		Mockito.verify(serviceInstanceService).updateServiceInstance(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}

	private GetLastServiceOperationRequest verifyLastOperation() {
		ArgumentCaptor<GetLastServiceOperationRequest> argumentCaptor = ArgumentCaptor.forClass(GetLastServiceOperationRequest.class);
		Mockito.verify(serviceInstanceService).getLastOperation(argumentCaptor.capture());
		return argumentCaptor.getValue();
	}
}
