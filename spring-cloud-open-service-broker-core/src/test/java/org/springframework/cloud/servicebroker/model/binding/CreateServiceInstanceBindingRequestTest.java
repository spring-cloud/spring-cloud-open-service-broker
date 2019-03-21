/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.binding;

import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import org.springframework.cloud.servicebroker.JsonUtils;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.PlatformContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.JsonUtils.fromJson;
import static org.springframework.cloud.servicebroker.JsonUtils.toJson;

@SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"})
public class CreateServiceInstanceBindingRequestTest {
	@Test
	public void requestWithDefaultsIsBuilt() {
		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder().build();

		assertThat(request.getServiceDefinitionId()).isNull();
		assertThat(request.getServiceDefinition()).isNull();
		assertThat(request.getPlanId()).isNull();
		assertThat(request.getServiceInstanceId()).isNull();
		assertThat(request.getBindResource()).isNull();
		assertThat(request.getContext()).isNull();
		assertThat(request.getBindingId()).isNull();
		assertThat(request.getParameters()).hasSize(0);
		assertThat(request.isAsyncAccepted()).isEqualTo(false);
		assertThat(request.getApiInfoLocation()).isNull();
		assertThat(request.getPlatformInstanceId()).isNull();
		assertThat(request.getOriginatingIdentity()).isNull();
	}

	@Test
	@SuppressWarnings("serial")
	public void requestWithAllValuesIsBuilt() {
		BindResource bindResource = BindResource.builder().build();
		Context context = PlatformContext.builder().build();
		Map<String, Object> parameters = new HashMap<String, Object>() {{
			put("field4", "value4");
			put("field5", "value5");
		}};

		Context originatingIdentity = PlatformContext.builder()
				.platform("test-platform")
				.build();

		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder()
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.parameters("field1", "value1")
				.parameters("field2", 2)
				.parameters("field3", true)
				.parameters(parameters)
				.asyncAccepted(true)
				.bindResource(bindResource)
				.context(context)
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("https://api.example.com")
				.originatingIdentity(originatingIdentity)
				.build();

		assertThat(request.getServiceDefinitionId()).isEqualTo("service-definition-id");
		assertThat(request.getServiceDefinition()).isNull();
		assertThat(request.getPlanId()).isEqualTo("plan-id");

		assertThat(request.getParameters()).hasSize(5);
		assertThat(request.getParameters().get("field1")).isEqualTo("value1");
		assertThat(request.getParameters().get("field2")).isEqualTo(2);
		assertThat(request.getParameters().get("field3")).isEqualTo(true);
		assertThat(request.getParameters().get("field4")).isEqualTo("value4");
		assertThat(request.getParameters().get("field5")).isEqualTo("value5");

		Parameters boundParameters = request.getParameters(Parameters.class);
		assertThat(boundParameters.getField1()).isEqualTo("value1");
		assertThat(boundParameters.getField2()).isEqualTo(2);
		assertThat(boundParameters.getField3()).isEqualTo(true);

		assertThat(request.getBindResource()).isEqualTo(bindResource);

		assertThat(request.getContext()).isEqualTo(context);
		assertThat(request.isAsyncAccepted()).isEqualTo(true);

		assertThat(request.getPlatformInstanceId()).isEqualTo("platform-instance-id");
		assertThat(request.getApiInfoLocation()).isEqualTo("https://api.example.com");
		assertThat(request.getOriginatingIdentity()).isEqualTo(originatingIdentity);
	}

	@Test
	public void requestIsDeserializedFromJson() {
		CreateServiceInstanceBindingRequest request =
				JsonUtils.readTestDataFile("bindRequest.json", CreateServiceInstanceBindingRequest.class);

		assertThat(request.getServiceDefinitionId()).isEqualTo("test-service-id");
		assertThat(request.getPlanId()).isEqualTo("test-plan-id");
		assertThat(request.getAppGuid()).isEqualTo("test-app-guid");

		Context context = request.getContext();
		assertThat(context.getPlatform()).isEqualTo("sample-platform");
		assertThat(context).isInstanceOf(Context.class);
		assertThat(context.getProperty("field1")).isEqualTo("data");
		assertThat(context.getProperty("field2")).isEqualTo(2);

		assertThat(request.getBindResource().getProperty("field1")).isEqualTo("data");
		assertThat(request.getBindResource().getProperty("field2")).isEqualTo(2);

		assertThat(request.getParameters().get("parameter1")).isEqualTo(1);
		assertThat(request.getParameters().get("parameter2")).isEqualTo("foo");
		assertThat(request.getParameters().get("parameter3")).isEqualTo(true);
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
						.properties("resource-param1", "value1")
						.properties("resource-param2", "value2")
						.build())
				.context(PlatformContext.builder()
						.platform("sample-platform")
						.property("context-property1", "value1")
						.property("context-property2", "value2")
						.build())
				.build();

		CreateServiceInstanceBindingRequest fromJson =
				fromJson(toJson(request), CreateServiceInstanceBindingRequest.class);
		
		assertThat(fromJson).isEqualTo(request);
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
