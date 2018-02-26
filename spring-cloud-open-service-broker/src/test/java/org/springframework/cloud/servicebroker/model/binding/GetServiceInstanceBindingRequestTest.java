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

package org.springframework.cloud.servicebroker.model.binding;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import org.springframework.cloud.servicebroker.model.Context;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class GetServiceInstanceBindingRequestTest {
	@Test
	public void requestWithDefaultsIsBuilt() {
		GetServiceInstanceBindingRequest request = GetServiceInstanceBindingRequest.builder()
				.build();

		assertThat(request.getServiceInstanceId(), nullValue());
		assertThat(request.getBindingId(), nullValue());
		assertThat(request.getApiInfoLocation(), nullValue());
		assertThat(request.getPlatformInstanceId(), nullValue());
		assertThat(request.getOriginatingIdentity(), nullValue());
	}

	@Test
	public void requestWithAllValuesIsBuilt() {
		Context originatingIdentity = Context.builder()
				.platform("test-platform")
				.build();

		GetServiceInstanceBindingRequest request = GetServiceInstanceBindingRequest.builder()
				.serviceInstanceId("service-instance-id")
				.bindingId("binding-id")
				.platformInstanceId("platform-instance-id")
				.apiInfoLocation("https://api.example.com")
				.originatingIdentity(originatingIdentity)
				.build();

		assertThat(request.getServiceInstanceId(), equalTo("service-instance-id"));
		assertThat(request.getBindingId(), equalTo("binding-id"));

		assertThat(request.getPlatformInstanceId(), equalTo("platform-instance-id"));
		assertThat(request.getApiInfoLocation(), equalTo("https://api.example.com"));
		assertThat(request.getOriginatingIdentity(), equalTo(originatingIdentity));
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(GetServiceInstanceBindingRequest.class)
				.withRedefinedSuperclass()
				.suppress(Warning.NONFINAL_FIELDS)
				.suppress(Warning.TRANSIENT_FIELDS)
				.verify();
	}
}