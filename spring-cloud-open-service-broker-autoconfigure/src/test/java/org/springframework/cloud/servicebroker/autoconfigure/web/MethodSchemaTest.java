/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@SuppressWarnings("unchecked")
public class MethodSchemaTest {

	@Test
	public void convertsParametersNumberedMapToArray() {
		//given the properties converted from yml, with array converted to numbered map

		// Simplified example adapted from https://json-schema.org/understanding-json-schema/UnderstandingJSONSchema.pdf
		// section 4.6 "array"
        /*
            {
              "type": "object",
              "properties": {
                "number": { "type": "number" },
                "street_type": {
                  "type": "string",
                  "enum": ["Street", "Avenue", "Boulevard"]
                }
              }
            }

         */


		MethodSchema methodSchema = new MethodSchema();
		methodSchema.getParameters().put("type", "object");
		Map<String, Object> properties = new HashMap<>();
		properties.put("type", "number");
		Map<String, String> number = new HashMap<>();
		number.put("type", "number");
		Map<String, Object> streetType = new HashMap<>();
		streetType.put("type", "string");
		Map<String, String> enumMap = new HashMap<>();
		enumMap.put("0", "Street");
		enumMap.put("1", "Avenue");
		enumMap.put("2", "Boulevard");
		streetType.put("enum", enumMap);
		properties.put("street_type", streetType);
		methodSchema.getParameters().put("properties", properties);

		//when
		org.springframework.cloud.servicebroker.model.catalog.MethodSchema model = methodSchema.toModel();

		//then
		assertThat(model.getParameters()).contains(entry("type", "object"));
		assertThat(model.getParameters().get("properties")).isInstanceOf(Map.class);

		Map<String, Object> readProperties = (Map<String, Object>) model.getParameters()
				.get("properties");
		assertThat(readProperties.get("street_type")).isInstanceOf(Map.class);
		Map<String, Object> readStreetType = (Map<String, Object>) readProperties
				.get("street_type");
		assertThat(readStreetType.get("enum")).isInstanceOf(List.class);
		List<Object> readEnum = (List<Object>) readStreetType.get("enum");
		assertThat(readEnum).contains("Street", "Avenue", "Boulevard");
	}

	@Test
	public void convertsNestedParametersNumberedMapToArray() {
		//given the properties converted from yml, with array converted to numbered map

		// Simplified example adapted from https://json-schema.org/understanding-json-schema/UnderstandingJSONSchema.pdf
        /*
            {
              "type": "array",
              "items": [
                {
                  "type": "string",
                  "enum": ["Street", "Avenue", "Boulevard"]
                }
              ]
            }

         */


		MethodSchema methodSchema = new MethodSchema();
		methodSchema.getParameters().put("type", "array");
		Map<String, Object> itemsArray = new HashMap<>();
		Map<String, Object> firstItem = new HashMap<>();
		firstItem.put("type", "string");
		itemsArray.put("0", firstItem);
		Map<String, String> enumMap = new HashMap<>();
		enumMap.put("0", "Street");
		enumMap.put("1", "Avenue");
		enumMap.put("2", "Boulevard");
		firstItem.put("enum", enumMap);
		methodSchema.getParameters().put("items", itemsArray);

		//when
		org.springframework.cloud.servicebroker.model.catalog.MethodSchema model = methodSchema.toModel();

		//then
		assertThat(model.getParameters()).contains(entry("type", "array"));
		assertThat(model.getParameters().get("items")).isInstanceOf(List.class);
		List<Object> items = (List<Object>) model.getParameters().get("items");
		assertThat(items.get(0)).isInstanceOf(Map.class);
		Map<String, Object> readfirstItem = (Map<String, Object>) items.get(0);
		assertThat(readfirstItem).contains(entry("type", "string"));
		assertThat(readfirstItem.get("enum")).isInstanceOf(List.class);
		List<Object> readEnum = (List<Object>) readfirstItem.get("enum");
		assertThat(readEnum).contains("Street", "Avenue", "Boulevard");
	}

	@Test
	public void doesNotConvertOthersParameters() {
		//given the properties converted from yml, with array converted to numbered map
		MethodSchema methodSchema = new MethodSchema();
		methodSchema.getParameters().put("type", "Object");
		Map<String, String> enumMap = new HashMap<>();
		enumMap.put("property1", "value1");
		enumMap.put("property2", "value2");
		enumMap.put("property3", "value3");
		methodSchema.getParameters().put("properties", enumMap);

		//when
		org.springframework.cloud.servicebroker.model.catalog.MethodSchema model = methodSchema.toModel();

		//then
		assertThat(model.getParameters()).contains(entry("type", "Object"));
		assertThat(model.getParameters().get("properties")).isInstanceOf(Map.class);
		Map<String, Object> propertiesMap = (Map<String, Object>) model.getParameters()
				.get("properties");
		assertThat(propertiesMap).containsOnly(
				entry("property1", "value1"),
				entry("property2", "value2"),
				entry("property3", "value3"));
	}

}
