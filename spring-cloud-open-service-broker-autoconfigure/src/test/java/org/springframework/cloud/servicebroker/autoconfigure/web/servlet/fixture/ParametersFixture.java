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

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture;

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
