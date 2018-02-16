/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.instance;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class CreateServiceInstanceRequestTest {
	@Test
	public void requestWithDefaultsIsBuilt() {
		CreateServiceInstanceRequest request = CreateServiceInstanceRequest.builder()
				.build();

		assertThat(request.getServiceDefinitionId(), nullValue());
		assertThat(request.getServiceInstanceId(), nullValue());
		assertThat(request.getPlanId(), nullValue());
		assertThat(request.getServiceDefinition(), nullValue());
		assertThat(request.getContext(), nullValue());
		assertThat(request.getParameters(), aMapWithSize(0));
		assertThat(request.getApiInfoLocation(), nullValue());
		assertThat(request.getPlatformInstanceId(), nullValue());
		assertThat(request.getOriginatingIdentity(), nullValue());
	}

	@Test
	@SuppressWarnings("serial")
	public void requestWithAllValuesIsBuilt() {
		Map<String, Object> parameters = new HashMap<String, Object>() {{
			put("field4", "value4");
			put("field5", "value5");
		}};
		Context context = Context.builder().build();

		CreateServiceInstanceRequest request = CreateServiceInstanceRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.context(context)
				.parameters("field1", "value1")
				.parameters("field2", 2)
				.parameters("field3", true)
				.parameters(parameters)
				.build();

		assertThat(request.getServiceDefinitionId(), equalTo("service-definition-id"));
		assertThat(request.getPlanId(), equalTo("plan-id"));

		assertThat(request.getParameters(), aMapWithSize(5));
		assertThat(request.getParameters().get("field1"), equalTo("value1"));
		assertThat(request.getParameters().get("field2"), equalTo(2));
		assertThat(request.getParameters().get("field3"), equalTo(true));
		assertThat(request.getParameters().get("field4"), equalTo("value4"));
		assertThat(request.getParameters().get("field5"), equalTo("value5"));

		assertThat(request.getContext(), equalTo(context));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void requestIsDeserializedFromJson() {
		CreateServiceInstanceRequest request =
				DataFixture.readTestDataFile("createRequest.json",
						CreateServiceInstanceRequest.class);

		assertThat(request.getServiceDefinitionId(), equalTo("test-service-id"));
		assertThat(request.getPlanId(), equalTo("test-plan-id"));
		assertThat(request.getOrganizationGuid(), equalTo("test-organization-guid"));
		assertThat(request.getSpaceGuid(), equalTo("test-space-guid"));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(CreateServiceInstanceRequest.class)
				.withRedefinedSuperclass()
				.suppress(Warning.NONFINAL_FIELDS)
				.suppress(Warning.TRANSIENT_FIELDS)
				.verify();
	}
}