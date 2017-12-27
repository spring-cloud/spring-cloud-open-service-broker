/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.cloud.servicebroker.model.fixture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.fail;

public class DataFixture {
	public static String toJson(Object object) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(object);
	}

	public static <T> T fromJson(String json, Class<T> contentType) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readerFor(contentType).readValue(json);
	}

	public static <T> T readTestDataFile(String filename, Class<T> contentType) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(getTestDataFileReader(filename), contentType);
		} catch (IOException e) {
			fail("Error reading test JSON file: " + e);
			return null;
		}
	}

	private static Reader getTestDataFileReader(String fileName) {
		return new BufferedReader(new InputStreamReader(DataFixture.class.getResourceAsStream("/" + fileName)));
	}

}
