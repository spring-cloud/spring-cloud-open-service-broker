/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.cloud.servicebroker.model.catalog;

import java.util.HashMap;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.servicebroker.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

class SchemasTest {

	@Test
	void noSchemasIsSerializedToJson() {
		Schemas schemas = Schemas.builder().build();

		assertThat(schemas.getServiceInstanceSchema()).isNull();
		assertThat(schemas.getServiceBindingSchema()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(schemas);

		assertThat(json).hasNoPath("$.service_instance");
		assertThat(json).hasNoPath("$.service_binding");
	}

	@Test
	void emptySchemasIsSerializedToJson() {
		Schemas schemas = Schemas.builder()
				.serviceInstanceSchema(ServiceInstanceSchema.builder().build())
				.serviceBindingSchema(ServiceBindingSchema.builder().build())
				.build();

		assertThat(schemas.getServiceInstanceSchema().getCreateMethodSchema()).isNull();
		assertThat(schemas.getServiceInstanceSchema().getUpdateMethodSchema()).isNull();
		assertThat(schemas.getServiceBindingSchema().getCreateMethodSchema()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(schemas);

		assertThat(json).hasPath("$.service_instance");
		assertThat(json).hasNoPath("$.service_instance.create");
		assertThat(json).hasNoPath("$.service_instance.update");
		assertThat(json).hasPath("$.service_binding");
		assertThat(json).hasNoPath("$.service_binding.create");
	}

	@Test
	void allSchemaFieldsIsSerializedToJson() {
		Map<String, Object> account = new HashMap<>();
		account.put("description", "Billing account number.");
		account.put("type", "string");

		Map<String, Object> properties = new HashMap<>();
		properties.put("billing-account", account);

		Map<String, Object> schemaProperties = new HashMap<>();
		schemaProperties.put("properties", properties);

		Schemas schemas = Schemas.builder()
				.serviceInstanceSchema(ServiceInstanceSchema.builder()
						.createMethodSchema(MethodSchema.builder()
								.parameters("$schema", "https://json-schema.org/draft-04/schema#")
								.parameters("type", "object")
								.parameters("description", "create time schema")
								.parameters(schemaProperties)
								.build())
						.updateMethodSchema(MethodSchema.builder()
								.parameters("$schema", "https://json-schema.org/draft-04/schema#")
								.parameters("type", "object")
								.parameters("description", "update time schema")
								.build())
						.build())
				.serviceBindingSchema(ServiceBindingSchema.builder()
						.createMethodSchema(MethodSchema.builder()
								.parameters("$schema", "https://json-schema.org/draft-04/schema#")
								.parameters("type", "object")
								.parameters("description", "bind create time schema")
								.build())
						.build())
				.build();

		assertThat(schemas.getServiceInstanceSchema().getCreateMethodSchema().getParameters()
				.get("$schema")).isEqualTo("https://json-schema.org/draft-04/schema#");
		assertThat(schemas.getServiceInstanceSchema().getCreateMethodSchema().getParameters()
				.get("type")).isEqualTo("object");
		assertThat(schemas.getServiceInstanceSchema().getCreateMethodSchema().getParameters()
				.get("description")).isEqualTo("create time schema");

		assertThat(schemas.getServiceInstanceSchema().getUpdateMethodSchema().getParameters()
				.get("$schema")).isEqualTo("https://json-schema.org/draft-04/schema#");
		assertThat(schemas.getServiceInstanceSchema().getUpdateMethodSchema().getParameters()
				.get("type")).isEqualTo("object");
		assertThat(schemas.getServiceInstanceSchema().getUpdateMethodSchema().getParameters()
				.get("description")).isEqualTo("update time schema");

		assertThat(schemas.getServiceBindingSchema().getCreateMethodSchema().getParameters()
				.get("$schema")).isEqualTo("https://json-schema.org/draft-04/schema#");
		assertThat(schemas.getServiceBindingSchema().getCreateMethodSchema().getParameters()
				.get("type")).isEqualTo("object");
		assertThat(schemas.getServiceBindingSchema().getCreateMethodSchema().getParameters()
				.get("description")).isEqualTo("bind create time schema");

		DocumentContext json = JsonUtils.toJsonPath(schemas);

		assertThat(json).hasPath("$.service_instance.create.parameters.$schema")
				.isEqualTo("https://json-schema.org/draft-04/schema#");
		assertThat(json).hasPath("$.service_instance.create.parameters.type")
				.isEqualTo("object");
		assertThat(json).hasPath("$.service_instance.create.parameters.properties.billing-account.description")
				.isEqualTo("Billing account number.");
		assertThat(json).hasPath("$.service_instance.create.parameters.properties.billing-account.type")
				.isEqualTo("string");
		assertThat(json).hasPath("$.service_instance.create.parameters.description")
				.isEqualTo("create time schema");

		assertThat(json).hasMapAtPath("$.service_instance.update.parameters").contains(
				entry("$schema", "https://json-schema.org/draft-04/schema#"),
				entry("type", "object"),
				entry("description", "update time schema")
		);
		assertThat(json).hasMapAtPath("$.service_binding.create.parameters").contains(
				entry("$schema", "https://json-schema.org/draft-04/schema#"),
				entry("type", "object"),
				entry("description", "bind create time schema")
		);
	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier
				.forClass(Schemas.class)
				.verify();
	}

}
