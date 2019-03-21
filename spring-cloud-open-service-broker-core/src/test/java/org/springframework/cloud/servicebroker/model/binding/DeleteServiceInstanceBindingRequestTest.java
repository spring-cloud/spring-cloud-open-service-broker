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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.PlatformContext;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteServiceInstanceBindingRequestTest {
	@Test
	public void requestWithDefaultsIsBuilt() {
		DeleteServiceInstanceBindingRequest request = DeleteServiceInstanceBindingRequest.builder()
				.build();

		assertThat(request.getServiceInstanceId()).isNull();
		assertThat(request.getServiceDefinitionId()).isNull();
		assertThat(request.getPlanId()).isNull();
		assertThat(request.getBindingId()).isNull();
		assertThat(request.getServiceDefinition()).isNull();
		assertThat(request.getApiInfoLocation()).isNull();
		assertThat(request.getPlatformInstanceId()).isNull();
		assertThat(request.getOriginatingIdentity()).isNull();
	}

	@Test
	public void requestWithAllValuesIsBuilt() {
		Context originatingIdentity = PlatformContext.builder()
				.platform("test-platform")
				.build();

		DeleteServiceInstanceBindingRequest request = DeleteServiceInstanceBindingRequest.builder()
				.serviceInstanceId("service-instance-id")
				.serviceDefinitionId("service-definition-id")
				.planId("plan-id")
				.bindingId("binding-id")
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("https://api.example.com")
				.originatingIdentity(originatingIdentity)
				.build();

		assertThat(request.getServiceInstanceId()).isEqualTo("service-instance-id");
		assertThat(request.getServiceDefinitionId()).isEqualTo("service-definition-id");
		assertThat(request.getPlanId()).isEqualTo("plan-id");
		assertThat(request.getBindingId()).isEqualTo("binding-id");

		assertThat(request.getPlatformInstanceId()).isEqualTo("platform-instance-id");
		assertThat(request.getApiInfoLocation()).isEqualTo("https://api.example.com");
		assertThat(request.getOriginatingIdentity()).isEqualTo(originatingIdentity);
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(DeleteServiceInstanceBindingRequest.class)
				.withRedefinedSuperclass()
				.suppress(Warning.NONFINAL_FIELDS)
				.suppress(Warning.TRANSIENT_FIELDS)
				.verify();
	}
}
