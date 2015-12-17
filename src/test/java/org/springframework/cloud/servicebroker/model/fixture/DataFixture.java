package org.springframework.cloud.servicebroker.model.fixture;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DataFixture {

	public static String getOrgOneGuid() {
		return "org-guid-one";
	}
	
	public static String getSpaceOneGuid() {
		return "space-guid-one";
	}
	
	public static String toJson(Object object) throws IOException {
		 ObjectMapper mapper = new ObjectMapper();
		 return mapper.writeValueAsString(object);
	}
	
}
