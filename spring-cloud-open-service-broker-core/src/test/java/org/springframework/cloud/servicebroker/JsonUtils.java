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

package org.springframework.cloud.servicebroker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import static org.junit.Assert.fail;

public final class JsonUtils {
	private JsonUtils() {
	}

	public static String toJson(Object object) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			fail("Error creating JSON string from object: " + e);
			throw new IllegalStateException(e);
		}
	}

	public static DocumentContext toJsonPath(Object object) {
		Configuration configuration = Configuration.builder()
				.jsonProvider(new JacksonJsonProvider())
				.mappingProvider(new JacksonMappingProvider())
				.build();

		return JsonPath.parse(toJson(object), configuration);
	}

	public static <T> T fromJson(String json, Class<T> contentType) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readerFor(contentType).readValue(json);
		} catch (IOException e) {
			fail("Error creating object from JSON: " + e);
			throw new IllegalStateException(e);
		}
	}

	public static <T> T readTestDataFile(String filename, Class<T> contentType) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(getTestDataFileReader(filename), contentType);
		} catch (IOException e) {
			fail("Error reading test JSON file: " + e);
			throw new IllegalStateException(e);
		}
	}

	private static Reader getTestDataFileReader(String fileName) {
		return new BufferedReader(new InputStreamReader(JsonUtils.class.getResourceAsStream("/" + fileName)));
	}
}
