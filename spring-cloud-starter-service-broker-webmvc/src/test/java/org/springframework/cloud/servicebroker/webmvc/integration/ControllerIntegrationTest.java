package org.springframework.cloud.servicebroker.webmvc.integration;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mock;

import org.springframework.cloud.servicebroker.model.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.crypto.codec.Base64;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public abstract class ControllerIntegrationTest {
	protected static final String API_INFO_LOCATION = "https://api.cf.example.com";

	protected static final String ORIGINATING_IDENTITY_PLATFORM = "test-platform";
	protected static final String ORIGINATING_USER_KEY = "user_id";
	protected static final String ORIGINATING_USER_VALUE = "user_id";
	protected static final String ORIGINATING_EMAIL_KEY = "email";
	protected static final String ORIGINATING_EMAIL_VALUE = "user@example.com";
	protected static final Map<String, Object> ORIGINATING_IDENTITY_PROPERTIES = new HashMap<String, Object>() {{
		put(ORIGINATING_USER_KEY, ORIGINATING_USER_VALUE);
		put(ORIGINATING_EMAIL_KEY, ORIGINATING_EMAIL_VALUE);
	}};

	protected static final String CF_INSTANCE_ID = "cf-abc";

	@Mock
	protected CatalogService catalogService;

	protected void setupCatalogService(String serviceDefinitionId) {
		when(catalogService.getServiceDefinition(eq(serviceDefinitionId)))
				.thenReturn(ServiceFixture.getSimpleService());
	}

	protected String buildOriginatingIdentityHeader() throws JsonProcessingException {
		ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
		String properties = mapper.writeValueAsString(ORIGINATING_IDENTITY_PROPERTIES);
		String encodedProperties = new String(Base64.encode(properties.getBytes()));
		return ORIGINATING_IDENTITY_PLATFORM + " " + encodedProperties;
	}
}
