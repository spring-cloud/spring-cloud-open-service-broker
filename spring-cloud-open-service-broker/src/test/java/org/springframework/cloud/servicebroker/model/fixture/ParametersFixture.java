package org.springframework.cloud.servicebroker.model.fixture;

import java.util.HashMap;
import java.util.Map;

public class ParametersFixture {
	public static Map<String, Object> getParameters() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("text", "abcdefg");
		parameters.put("number", 1234);
		parameters.put("flag", true);

		Map<String, Object> nested = new HashMap<String, Object>();
		nested.put("text2", "zyxwvu");
		nested.put("number2", 9876);
		nested.put("flag2", true);

		parameters.put("nested", nested);

		return parameters;
	}
}
