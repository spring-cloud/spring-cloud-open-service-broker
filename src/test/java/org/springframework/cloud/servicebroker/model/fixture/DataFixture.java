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
