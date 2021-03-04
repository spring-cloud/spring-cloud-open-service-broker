/*
 * Copyright 2002-2021 the original author or authors.
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

package org.springframework.cloud.servicebroker.acceptance;

import java.util.List;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CatalogControllerTest {

	@Autowired
	private WebTestClient client;

	@SuppressWarnings("unchecked")
	@Test
	void fullCatalog() {
		String body = this.client.get().uri("/v2/catalog")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(body).isNotNull();
		ReadContext ctx = JsonPath.parse(body);
		assertThat(ctx.read("$.services", List.class)).isNotEmpty();
		validateServiceOne(ctx);
		validateServiceOneMetadata(ctx);
		validateServiceOnePlans(ctx);
		validateServiceTwo(ctx);
	}

	private void validateServiceOne(ReadContext ctx) {
		assertThat(ctx.read("$.services[0]", Object.class)).isNotNull();
		assertThat(ctx.read("$.services[0].id", String.class)).isEqualTo("service-one-id");
		assertThat(ctx.read("$.services[0].name", String.class)).isEqualTo("Service One");
		assertThat(ctx.read("$.services[0].description", String.class)).isEqualTo("Description for Service One");
		assertThat(ctx.read("$.services[0].bindable", Boolean.class)).isEqualTo(true);
		assertThat(ctx.read("$.services[0].bindings_retrievable", Boolean.class)).isEqualTo(true);
		assertThat(ctx.read("$.services[0].allow_context_updates", Boolean.class)).isEqualTo(false);
		assertThat(ctx.read("$.services[0].instances_retrievable", Boolean.class)).isEqualTo(true);
		assertThat(ctx.read("$.services[0].plan_updateable", Boolean.class)).isEqualTo(true);
		assertThat(ctx.read("$.services[0].requires[0]", String.class)).isEqualTo("syslog_drain");
		assertThat(ctx.read("$.services[0].requires[1]", String.class)).isEqualTo("route_forwarding");
		assertThat(ctx.read("$.services[0].tags[0]", String.class)).isEqualTo("tag1");
		assertThat(ctx.read("$.services[0].tags[1]", String.class)).isEqualTo("tag2");
		assertThat(ctx.read("$.services[0].dashboard_client.id", String.class)).isEqualTo("dashboard-id");
		assertThat(ctx.read("$.services[0].dashboard_client.secret", String.class)).isEqualTo("dashboard-secret");
		assertThat(ctx.read("$.services[0].dashboard_client.redirect_uri", String.class)).isEqualTo("dashboard" +
				"-redirect-uri");
	}

	private void validateServiceOneMetadata(ReadContext ctx) {
		assertThat(ctx.read("$.services[0].metadata.displayName", String.class)).isEqualTo("service display name");
		assertThat(ctx.read("$.services[0].metadata.imageUrl", String.class)).isEqualTo("image-uri");
		assertThat(ctx.read("$.services[0].metadata.longDescription", String.class)).isEqualTo("service long " +
				"description");
		assertThat(ctx.read("$.services[0].metadata.providerDisplayName", String.class)).isEqualTo("service provider " +
				"display name");
		assertThat(ctx.read("$.services[0].metadata.documentationUrl", String.class)).isEqualTo("service" +
				"-documentation-url");
		assertThat(ctx.read("$.services[0].metadata.supportUrl", String.class)).isEqualTo("service-support-url");
		assertThat(ctx.read("$.services[0].metadata.key1", String.class)).isEqualTo("value1");
		assertThat(ctx.read("$.services[0].metadata.key2", String.class)).isEqualTo("value2");
		assertThat(ctx.read("$.services[0].metadata.licenses[0]", String.class)).isEqualTo("license1");
		assertThat(ctx.read("$.services[0].metadata.licenses[1]", String.class)).isEqualTo("license2");
		assertThat(ctx.read("$.services[0].metadata.features[0]", String.class)).isEqualTo("hosting");
		assertThat(ctx.read("$.services[0].metadata.features[1]", String.class)).isEqualTo("scaling");
	}

	private void validateServiceOnePlans(ReadContext ctx) {
		assertThat(ctx.read("$.services[0].plans[0].id", String.class)).isEqualTo("plan-one-id");
		assertThat(ctx.read("$.services[0].plans[0].name", String.class)).isEqualTo("Plan One");
		assertThat(ctx.read("$.services[0].plans[0].description", String.class)).isEqualTo("Description for Plan One");
		assertThat(ctx.read("$.services[0].plans[0].maintenance_info.version", String.class)).isEqualTo("1.0.1");
		assertThat(ctx.read("$.services[0].plans[0].maintenance_info.description", String.class)).isEqualTo(
				"Description for maintenance info");
		assertThat(ctx.read("$.services[0].plans[1].id", String.class)).isEqualTo("plan-two-id");
		assertThat(ctx.read("$.services[0].plans[1].name", String.class)).isEqualTo("Plan Two");
		assertThat(ctx.read("$.services[0].plans[1].description", String.class)).isEqualTo("Description for Plan Two");
		assertThat(ctx.read("$.services[0].plans[1].metadata.displayName", String.class)).isEqualTo("sample display " +
				"name");
		assertThat(ctx.read("$.services[0].plans[1].metadata.bullets[0]", String.class)).isEqualTo("bullet1");
		assertThat(ctx.read("$.services[0].plans[1].metadata.bullets[1]", String.class)).isEqualTo("bullet2");
		assertThat(ctx.read("$.services[0].plans[1].metadata.costs[0].amount.usd", Number.class)).isEqualTo(649.0);
		assertThat(ctx.read("$.services[0].plans[1].metadata.costs[0].unit", String.class)).isEqualTo("MONTHLY");
		assertThat(ctx.read("$.services[0].plans[1].metadata.key1", String.class)).isEqualTo("value1");
		assertThat(ctx.read("$.services[0].plans[1].metadata.key2", String.class)).isEqualTo("value2");
		assertThat(ctx.read("$.services[0].plans[1].bindable", Boolean.class)).isEqualTo(true);
		assertThat(ctx.read("$.services[0].plans[1].free", Boolean.class)).isEqualTo(true);
		assertThat(ctx.read("$.services[0].plans[1].plan_updateable", Boolean.class)).isEqualTo(true);
		assertThat(ctx.read("$.services[0].plans[1].schemas.service_instance.create.parameters.$schema",
				String.class)).isEqualTo("http://json-schema.org/draft-04/schema#");
		assertThat(ctx.read("$.services[0].plans[1].schemas.service_instance.create.parameters.type", String.class))
				.isEqualTo("string");
		assertThat(ctx.read("$.services[0].plans[1].schemas.service_instance.create.parameters.enum[0]",
				String.class)).isEqualTo("one");
		assertThat(ctx.read("$.services[0].plans[1].schemas.service_instance.create.parameters.enum[1]",
				String.class)).isEqualTo("two");
		assertThat(ctx.read("$.services[0].plans[1].schemas.service_instance.create.parameters.enum[2]",
				String.class)).isEqualTo("three");
		assertThat(ctx.read("$.services[0].plans[1].schemas.service_instance.update.parameters.$schema",
				String.class)).isEqualTo("http://json-schema.org/draft-04/schema#");
		assertThat(ctx.read("$.services[0].plans[1].schemas.service_instance.update.parameters.type", String.class))
				.isEqualTo("object");
		assertThat(ctx.read("$.services[0].plans[1].schemas.service_binding.create.parameters.$schema",
				String.class)).isEqualTo("http://json-schema.org/draft-04/schema#");
		assertThat(ctx.read("$.services[0].plans[1].schemas.service_binding.create.parameters.type", String.class))
				.isEqualTo("object");
		assertThat(ctx.read("$.services[0].plans[1].maximum_polling_duration", Integer.class)).isEqualTo(120);
	}

	private void validateServiceTwo(ReadContext ctx) {
		assertThat(ctx.read("$.services[1].id", String.class)).isEqualTo("service-two-id");
		assertThat(ctx.read("$.services[1].name", String.class)).isEqualTo("Service Two");
		assertThat(ctx.read("$.services[1].description", String.class)).isEqualTo("Description for Service Two");
		assertThat(ctx.read("$.services[1].plans[0].id", String.class)).isEqualTo("plan-one-id");
		assertThat(ctx.read("$.services[1].plans[0].name", String.class)).isEqualTo("Plan One");
		assertThat(ctx.read("$.services[1].plans[0].description", String.class)).isEqualTo("Description for Plan One");
	}

}
