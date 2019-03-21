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

package org.springframework.cloud.servicebroker.model.catalog;

import com.jayway.jsonpath.DocumentContext;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.springframework.cloud.servicebroker.JsonUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.springframework.cloud.servicebroker.JsonPathAssert.assertThat;

public class SchemasTest {
	@Test
	public void noSchemasIsSerializedToJson() {
		Schemas schemas = Schemas.builder().build();

		assertThat(schemas.getServiceInstanceSchema()).isNull();
		assertThat(schemas.getServiceBindingSchema()).isNull();

		DocumentContext json = JsonUtils.toJsonPath(schemas);

		assertThat(json).hasNoPath("$.service_instance");
		assertThat(json).hasNoPath("$.service_binding");
	}
	
	@Test
	public void emptySchemasIsSerializedToJson() {
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
	@SuppressWarnings("serial")
	public void allSchemaFieldsIsSerializedToJson() {
		Map<String, Object> schemaProperties = new HashMap<String, Object>() {{
			put("properties", new HashMap<String, Object>() {{
				put("billing-account", new HashMap<String, String>() {{
					put("description", "Billing account number.");
					put("type", "string");
				}});
			}});
		}};

		Schemas schemas = Schemas.builder()
				.serviceInstanceSchema(ServiceInstanceSchema.builder()
						.createMethodSchema(MethodSchema.builder()
								.parameters("$schema", "http://example.com/service/create/schema")
								.parameters("type", "object")
								.parameters(schemaProperties)
								.build())
						.updateMethodSchema(MethodSchema.builder()
								.parameters("$schema", "http://example.com/service/update/schema")
								.parameters("type", "object")
								.build())
						.build())
				.serviceBindingSchema(ServiceBindingSchema.builder()
						.createMethodSchema(MethodSchema.builder()
								.parameters("$schema", "http://example.com/binding/create/schema")
								.parameters("type", "object")
								.build())
						.build())
				.build();

		assertThat(schemas.getServiceInstanceSchema().getCreateMethodSchema().getParameters()
				.get("$schema")).isEqualTo("http://example.com/service/create/schema");
		assertThat(schemas.getServiceInstanceSchema().getCreateMethodSchema().getParameters()
				.get("type")).isEqualTo("object");

		assertThat(schemas.getServiceInstanceSchema().getUpdateMethodSchema().getParameters()
				.get("$schema")).isEqualTo("http://example.com/service/update/schema");
		assertThat(schemas.getServiceInstanceSchema().getUpdateMethodSchema().getParameters()
				.get("type")).isEqualTo("object");

		assertThat(schemas.getServiceBindingSchema().getCreateMethodSchema().getParameters()
				.get("$schema")).isEqualTo("http://example.com/binding/create/schema");
		assertThat(schemas.getServiceBindingSchema().getCreateMethodSchema().getParameters()
				.get("type")).isEqualTo("object");

		DocumentContext json = JsonUtils.toJsonPath(schemas);

		assertThat(json).hasPath("$.service_instance.create.parameters.$schema")
						.isEqualTo("http://example.com/service/create/schema");
		assertThat(json).hasPath("$.service_instance.create.parameters.type")
						.isEqualTo("object");
		assertThat(json).hasPath("$.service_instance.create.parameters.properties.billing-account.description")
						.isEqualTo("Billing account number.");
		assertThat(json).hasPath("$.service_instance.create.parameters.properties.billing-account.type")
						.isEqualTo("string");

		assertThat(json).hasMapAtPath("$.service_instance.update.parameters").contains(
						entry("$schema", "http://example.com/service/update/schema"),
						entry("type", "object")
		);
		assertThat(json).hasMapAtPath("$.service_binding.create.parameters").contains(
						entry("$schema", "http://example.com/binding/create/schema"),
						entry("type", "object")
		);
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(Schemas.class)
				.verify();
	}
}
