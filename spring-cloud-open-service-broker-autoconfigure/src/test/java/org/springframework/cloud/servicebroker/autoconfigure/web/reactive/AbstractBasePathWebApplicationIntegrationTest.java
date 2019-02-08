package org.springframework.cloud.servicebroker.autoconfigure.web.reactive;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.cloud.servicebroker.JsonUtils;
import org.springframework.cloud.servicebroker.autoconfigure.web.AbstractBasePathIntegrationTest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;


@TestPropertySource(properties = "spring.main.web-application-type=reactive")
@AutoConfigureWebTestClient
public abstract class AbstractBasePathWebApplicationIntegrationTest extends AbstractBasePathIntegrationTest {
	
	@Autowired
	protected WebTestClient client;
	
	@Override
	protected void assertFound(String baseUri, String expectedPlatform) {
		ResponseSpec exchange = createExchange(baseUri);
		exchange
		            .expectStatus().isCreated()
		            .expectBody().jsonPath("operation")
		                         .isEqualTo(format("platform: %s", expectedPlatform))
		;
	}
	
	@Override
	protected void assertNotFound(String baseUri) {
		ResponseSpec exchange = createExchange(baseUri);
		exchange.expectStatus().isNotFound();
	}


	private ResponseSpec createExchange(String baseUri) {
		String request = JsonUtils.toJson(CreateServiceInstanceRequest.builder()
				                                                      .serviceDefinitionId("default-service")
				                                                      .planId("default-plan")
				                                                      .build()
				         );
		ResponseSpec exchange = client.put().uri(format("%s/v2/service_instances/default-service", baseUri))
		            .accept(MediaType.APPLICATION_JSON)
		            .contentType(MediaType.APPLICATION_JSON)
		            .syncBody(request)
		            .exchange();
		return exchange;
	}
}
