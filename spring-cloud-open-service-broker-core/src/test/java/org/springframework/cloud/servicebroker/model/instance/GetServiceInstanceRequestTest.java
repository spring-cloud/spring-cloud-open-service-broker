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

package org.springframework.cloud.servicebroker.model.instance;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.PlatformContext;

import static org.assertj.core.api.Assertions.assertThat;

public class GetServiceInstanceRequestTest {
	@Test
	public void requestWithDefaultsIsBuilt() {
		GetServiceInstanceRequest request = GetServiceInstanceRequest.builder()
				.build();

		assertThat(request.getServiceInstanceId()).isNull();
		assertThat(request.getApiInfoLocation()).isNull();
		assertThat(request.getPlatformInstanceId()).isNull();
		assertThat(request.getOriginatingIdentity()).isNull();
	}

	@Test
	public void requestWithAllValuesIsBuilt() {
		Context originatingIdentity = PlatformContext.builder()
				.platform("test-platform")
				.build();

		GetServiceInstanceRequest request = GetServiceInstanceRequest.builder()
				.serviceInstanceId("service-instance-id")
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("https://api.example.com")
				.originatingIdentity(originatingIdentity)
				.build();

		assertThat(request.getServiceInstanceId()).isEqualTo("service-instance-id");

		assertThat(request.getPlatformInstanceId()).isEqualTo("platform-instance-id");
		assertThat(request.getApiInfoLocation()).isEqualTo("https://api.example.com");
		assertThat(request.getOriginatingIdentity()).isEqualTo(originatingIdentity);
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(GetServiceInstanceRequest.class)
				.withRedefinedSuperclass()
				.suppress(Warning.NONFINAL_FIELDS)
				.suppress(Warning.TRANSIENT_FIELDS)
				.verify();
	}
}
