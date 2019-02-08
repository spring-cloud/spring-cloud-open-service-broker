package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cloud.servicebroker.autoconfigure.web.AbstractBasePathIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;


@TestPropertySource(properties = "spring.main.web-application-type=servlet")
@AutoConfigureMockMvc
public abstract class AbstractBasePathWebApplicationIntegrationTest extends AbstractBasePathIntegrationTest {
	
	@Autowired
    private MockMvc mvc;
	
	protected void assertFound(String baseUri, String expectedPlatform) {
		ResultActions response = makeRequest(baseUri, true);
		try {
			response.andExpect(status().isCreated())
			        .andExpect(jsonPath("operation").value(format("platform: %s", expectedPlatform)))
			;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void assertNotFound(String baseUri) {
		ResultActions response = makeRequest(baseUri, false);
		try {
			response.andExpect(status().isNotFound());
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private ResultActions makeRequest(String baseUri, boolean async) {
		try {
			ResultActions actions = mvc.perform(put(format("%s/v2/service_instances/default-service", baseUri))
					                               .accept(MediaType.APPLICATION_JSON)
					                               .contentType(MediaType.APPLICATION_JSON)
					                               .content(serviceCreationPayload())
			                           );
			if (!async) {
				return actions;
			}
			MvcResult result = actions.andExpect(request().asyncStarted())
                                      .andReturn();
			return mvc.perform(asyncDispatch(result));
		} catch (RuntimeException e) {
				throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
