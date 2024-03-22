/*
 * Copyright 2002-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.controller;

import java.util.Base64;

import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidOriginatingIdentityException;
import org.springframework.cloud.servicebroker.model.CloudFoundryContext;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.KubernetesContext;
import org.springframework.cloud.servicebroker.model.PlatformContext;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

class BaseControllerTests {

	private static final String JSON_STRING = "{" + "\"key1\":\"value1\"," + "\"key2\":\"value2\"" + "}";

	private final BasicBaseController controller = new BasicBaseController();

	@Test
	void originatingIdentityWithNoPropertiesThrowsException() {
		assertThatException().isThrownBy(() -> this.controller.validateOriginatingIdentity("platform"))
			.isInstanceOf(ServiceBrokerInvalidOriginatingIdentityException.class);
	}

	@Test
	void originatingIdentityWithNonEncodedPropertiesThrowsException() {
		assertThatException().isThrownBy(() -> this.controller.validateOriginatingIdentity("platform some-properties"))
			.isInstanceOf(ServiceBrokerInvalidOriginatingIdentityException.class);
	}

	@Test
	void originatingIdentityWithNonJsonPropertiesThrowsException() {
		String encodedProperties = encode("some-properties");
		assertThatException()
			.isThrownBy(() -> this.controller.validateOriginatingIdentity("platform " + encodedProperties))
			.isInstanceOf(ServiceBrokerInvalidOriginatingIdentityException.class);
	}

	@Test
	void originatingIdentityWithCloudFoundryPlatform() {
		Context context = this.controller.validateOriginatingIdentity("cloudfoundry " + encode(JSON_STRING));

		assertThat(context).isInstanceOf(CloudFoundryContext.class);
		assertThat(context.getProperty("key1")).isEqualTo("value1");
		assertThat(context.getProperty("key2")).isEqualTo("value2");
	}

	@Test
	void originatingIdentityWithKubernetesPlatform() {
		Context context = this.controller.validateOriginatingIdentity("kubernetes " + encode(JSON_STRING));

		assertThat(context).isInstanceOf(KubernetesContext.class);
		assertThat(context.getProperty("key1")).isEqualTo("value1");
		assertThat(context.getProperty("key2")).isEqualTo("value2");
	}

	@Test
	void originatingIdentityWithUnknownPlatform() {
		Context context = this.controller.validateOriginatingIdentity("test-platform " + encode(JSON_STRING));

		assertThat(context).isInstanceOf(PlatformContext.class);
		assertThat(context.getPlatform()).isEqualTo("test-platform");
		assertThat(context.getProperty("key1")).isEqualTo("value1");
		assertThat(context.getProperty("key2")).isEqualTo("value2");
	}

	private String encode(String json) {
		return Base64.getEncoder().encodeToString(json.getBytes());
	}

	private static class BasicBaseController extends BaseController {

		BasicBaseController() {
			super(null);
		}

		Context validateOriginatingIdentity(String originatingIdentityString) {
			ServiceBrokerRequest request = new ServiceBrokerRequest() {
			};

			configureCommonRequestFields(request, "platform-instance-id", "api-info-location",
					originatingIdentityString, "request-id")
				.block();

			return request.getOriginatingIdentity();
		}

	}

}
