/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.cloud.servicebroker.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.springframework.cloud.servicebroker.model.fixture.DataFixture;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.servicebroker.model.fixture.DataFixture.fromJson;
import static org.springframework.cloud.servicebroker.model.fixture.DataFixture.toJson;

@SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"})
public class CreateServiceInstanceBindingRequestTest {
	@Test
	public void requestWithDefaultsIsBuilt() {
		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder().build();

		assertThat(request.getServiceDefinitionId(), nullValue());
		assertThat(request.getServiceDefinition(), nullValue());
		assertThat(request.getPlanId(), nullValue());
		assertThat(request.getServiceInstanceId(), nullValue());
		assertThat(request.getBindResource(), nullValue());
		assertThat(request.getContext(), nullValue());
		assertThat(request.getBindingId(), nullValue());
		assertThat(request.getParameters(), aMapWithSize(0));
		assertThat(request.getApiInfoLocation(), nullValue());
		assertThat(request.getCfInstanceId(), nullValue());
		assertThat(request.getOriginatingIdentity(), nullValue());
	}

	@Test
	public void requestWithAllValuesIsBuilt() {
		BindResource bindResource = BindResource.builder().build();
		Context context = Context.builder().build();
		Map<String, Object> parameters = new HashMap<String, Object>() {{
			put("field4", "value4");
			put("field5", "value5");
		}};


		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.parameters("field1", "value1")
				.parameters("field2", 2)
				.parameters("field3", true)
				.parameters(parameters)
				.bindResource(bindResource)
				.context(context)
				.build();

		assertThat(request.getServiceDefinitionId(), equalTo("service-definition-id"));
		assertThat(request.getServiceDefinition(), nullValue());
		assertThat(request.getPlanId(), equalTo("plan-id"));

		assertThat(request.getParameters(), aMapWithSize(5));
		assertThat(request.getParameters().get("field1"), equalTo("value1"));
		assertThat(request.getParameters().get("field2"), equalTo(2));
		assertThat(request.getParameters().get("field3"), equalTo(true));
		assertThat(request.getParameters().get("field4"), equalTo("value4"));
		assertThat(request.getParameters().get("field5"), equalTo("value5"));

		Parameters boundParameters = request.getParameters(Parameters.class);
		assertThat(boundParameters.getField1(), equalTo("value1"));
		assertThat(boundParameters.getField2(), equalTo(2));
		assertThat(boundParameters.getField3(), equalTo(true));

		assertThat(request.getBindResource(), equalTo(bindResource));

		assertThat(request.getContext(), equalTo(context));
	}

	@Test
	public void requestIsDeserializedFromJson() {
		CreateServiceInstanceBindingRequest request =
				DataFixture.readTestDataFile("bindRequest.json", CreateServiceInstanceBindingRequest.class);

		assertEquals("test-service-id", request.getServiceDefinitionId());
		assertEquals("test-plan-id", request.getPlanId());
		assertEquals("test-app-guid", request.getAppGuid());

		Context context = request.getContext();
		assertEquals("sample-platform", context.getPlatform());
		assertThat(context, instanceOf(Context.class));
		assertEquals("data", context.getProperty("field1"));
		assertEquals(2, context.getProperty("field2"));

		assertEquals("data", request.getBindResource().getProperty("field1"));
		assertEquals(2, request.getBindResource().getProperty("field2"));

		assertEquals(1, request.getParameters().get("parameter1"));
		assertEquals("foo", request.getParameters().get("parameter2"));
		assertEquals(true, request.getParameters().get("parameter3"));
	}

	@Test
	public void requestMatchesWithJsonRoundTrip() {
		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder()
				.serviceDefinitionId("definition-id")
				.planId("plan-id")
				.parameters("param1", "value1")
				.parameters("param2", "value2")
				.bindResource(BindResource.builder()
						.appGuid("app-guid")
						.route("route")
						.parameters("resource-param1", "value1")
						.parameters("resource-param2", "value2")
						.build())
				.context(Context.builder()
						.platform("sample-platform")
						.property("context-property1", "value1")
						.property("context-property2", "value2")
						.build())
				.build();

		CreateServiceInstanceBindingRequest fromJson =
				fromJson(toJson(request), CreateServiceInstanceBindingRequest.class);
		
		assertEquals(request, fromJson);
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(CreateServiceInstanceBindingRequest.class)
				.withRedefinedSuperclass()
				.suppress(Warning.NONFINAL_FIELDS)
				.suppress(Warning.TRANSIENT_FIELDS)
				.verify();
	}

	public static class Parameters {
		private String field1;
		private Integer field2;
		private Boolean field3;

		public String getField1() {
			return field1;
		}

		public Integer getField2() {
			return field2;
		}

		public Boolean getField3() {
			return field3;
		}

		public void setField1(String value) {
			this.field1 = value;
		}

		public void setField2(Integer value) {
			this.field2 = value;
		}

		public void setField3(Boolean value) {
			this.field3 = value;
		}
	}
}