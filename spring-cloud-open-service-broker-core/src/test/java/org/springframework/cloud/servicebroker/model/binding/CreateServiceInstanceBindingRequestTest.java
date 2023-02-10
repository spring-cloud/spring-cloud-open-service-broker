/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonPathAssert;
import org.springframework.cloud.servicebroker.JsonUtils;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.PlatformContext;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.servicebroker.JsonUtils.fromJson;
import static org.springframework.cloud.servicebroker.JsonUtils.toJson;

@SuppressWarnings("deprecation")
class CreateServiceInstanceBindingRequestTest {

	@Test
	void requestWithDefaultsIsBuilt() {
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
		assertThat(request.getRequestIdentity()).isNull();
	}

	@Test
	void requestWithAllValuesIsBuilt() {
		BindResource bindResource = BindResource.builder().build();
		Context context = PlatformContext.builder().build();
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("field4", "value4");
		parameters.put("field5", "value5");

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
				.apiInfoLocation("https://api.app.local")
				.originatingIdentity(originatingIdentity)
				.requestIdentity("request-id")
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
		assertThat(request.getApiInfoLocation()).isEqualTo("https://api.app.local");
		assertThat(request.getOriginatingIdentity()).isEqualTo(originatingIdentity);
		assertThat(request.getRequestIdentity()).isEqualTo("request-id");
	}

	@Test
	void requestIsDeserializedFromJson() {
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
		assertThat(request.getParameters().get("parameter2")).isEqualTo("param-a");
		assertThat(request.getParameters().get("parameter3")).isEqualTo(true);
	}

	@Test
	void minimalRequiredRequestIsDeserializedFromJson() {
		CreateServiceInstanceBindingRequest request =
				JsonUtils.readTestDataFile("bindRequestWithOnlyRequiredFields.json",
						CreateServiceInstanceBindingRequest.class);

		//ensure required fields are present
		assertThat(request.getServiceDefinitionId()).isEqualTo("test-service-id");
		assertThat(request.getPlanId()).isEqualTo("test-plan-id");

		//ensure default value for missing non required fields is consistent with default value provided by the builder
		CreateServiceInstanceBindingRequest builderDefault = CreateServiceInstanceBindingRequest.builder().
				serviceDefinitionId("test-service-id")
				.planId("test-plan-id")
				.build();

		assertThat(request.getParameters()).isEqualTo(builderDefault.getParameters());
		assertThat(request.getContext()).isEqualTo(builderDefault.getContext());
		assertThat(request.getAppGuid()).isEqualTo(builderDefault.getAppGuid());
		assertThat(request.getBindResource()).isEqualTo(builderDefault.getBindResource());
	}

	@Test
	void requestMatchesWithJsonRoundTrip() {
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
	void minimalRequestMatchesWithJsonRoundTrip() {
		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder()
				.serviceDefinitionId("definition-id")
				.planId("plan-id")
				.build();

		String json = toJson(request);
		CreateServiceInstanceBindingRequest fromJson =
				fromJson(json, CreateServiceInstanceBindingRequest.class);

		assertThat(fromJson).isEqualTo(request);
	}

	@Test
	void requestSerializesToJsonExcludingTransients() {
		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest.builder()
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("api-info-location")
				.originatingIdentity(PlatformContext.builder()
						.platform("sample-platform").build())
				.asyncAccepted(true)
				.serviceDefinitionId("definition-id")
				.serviceDefinition(ServiceDefinition.builder().build())
				.plan(Plan.builder().build())
				.build();

		DocumentContext json = JsonUtils.toJsonPath(request);

		// Fields present in OSB Json body should be present, but no unspecified optional ones.
		JsonPathAssert.assertThat(json).hasPath("$.service_id").isEqualTo("definition-id");

		// other fields mapped outside of json body (typically http headers or request paths)
		// should be excluded
		JsonPathAssert.assertThat(json).hasMapAtPath("$").hasSize(1);
	}

	@Test
	void minimalRequestSerializesToJsonWithoutMissingExcludingTransients() {
		CreateServiceInstanceBindingRequest request = CreateServiceInstanceBindingRequest
				.builder().platformInstanceId("platform-instance-id")
				.apiInfoLocation("api-info-location")
				.originatingIdentity(PlatformContext.builder()
						.platform("sample-platform").build())
				.asyncAccepted(true)
				.serviceDefinitionId("definition-id").build();

		DocumentContext json = JsonUtils.toJsonPath(request);

		// Fields present in OSB Json body should be present
		JsonPathAssert.assertThat(json).hasPath("$.service_id").isEqualTo("definition-id");

		// other fields mapped outside of json body (typically http headers or request paths)
		// should be excluded
		JsonPathAssert.assertThat(json).hasMapAtPath("$").hasSize(1);
	}

	@Test
	void equalsAndHashCode() {
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
