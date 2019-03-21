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

package org.springframework.cloud.servicebroker.controller;

import java.util.Base64;

import org.junit.Test;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidOriginatingIdentityException;
import org.springframework.cloud.servicebroker.model.CloudFoundryContext;
import org.springframework.cloud.servicebroker.model.Context;
import org.springframework.cloud.servicebroker.model.KubernetesContext;
import org.springframework.cloud.servicebroker.model.PlatformContext;
import org.springframework.cloud.servicebroker.model.ServiceBrokerRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseControllerTest {
	private static final String JSON_STRING = "{" +
			"\"key1\":\"value1\"," +
			"\"key2\":\"value2\"" +
			"}";

	private TestBaseController controller = new TestBaseController();

	@Test(expected = ServiceBrokerInvalidOriginatingIdentityException.class)
	public void originatingIdentityWithNoPropertiesThrowsException() {
		controller.testOriginatingIdentity("platform");
	}

	@Test(expected = ServiceBrokerInvalidOriginatingIdentityException.class)
	public void originatingIdentityWithNonEncodedPropertiesThrowsException() {
		controller.testOriginatingIdentity("platform some-properties");
	}

	@Test(expected = ServiceBrokerInvalidOriginatingIdentityException.class)
	public void originatingIdentityWithNonJsonPropertiesThrowsException() {
		String encodedProperties = encode("some-properties");

		controller.testOriginatingIdentity("platform " + encodedProperties);
	}

	@Test
	public void originatingIdentityWithCloudFoundryPlatform() {
		Context context = controller.testOriginatingIdentity("cloudfoundry " + encode(JSON_STRING));

		assertThat(context).isInstanceOf(CloudFoundryContext.class);
		assertThat(context.getProperty("key1")).isEqualTo("value1");
		assertThat(context.getProperty("key2")).isEqualTo("value2");
	}

	@Test
	public void originatingIdentityWithKubernetesPlatform() {
		Context context = controller.testOriginatingIdentity("kubernetes " + encode(JSON_STRING));

		assertThat(context).isInstanceOf(KubernetesContext.class);
		assertThat(context.getProperty("key1")).isEqualTo("value1");
		assertThat(context.getProperty("key2")).isEqualTo("value2");
	}

	@Test
	public void originatingIdentityWithUnknownPlatform() {
		Context context = controller.testOriginatingIdentity("test-platform " + encode(JSON_STRING));

		assertThat(context).isInstanceOf(PlatformContext.class);
		assertThat(context.getPlatform()).isEqualTo("test-platform");
		assertThat(context.getProperty("key1")).isEqualTo("value1");
		assertThat(context.getProperty("key2")).isEqualTo("value2");
	}

	private String encode(String JSON_STRING) {
		return Base64.getEncoder().encodeToString(JSON_STRING.getBytes());
	}

	private static class TestBaseController extends BaseController {
		public TestBaseController() {
			super(null);
		}

		public Context testOriginatingIdentity(String originatingIdentityString) {
			ServiceBrokerRequest request = new ServiceBrokerRequest() {
			};

			setCommonRequestFields(request, "platform-instance-id", "api-info-location", originatingIdentityString);

			return request.getOriginatingIdentity();
		}
	}
}
