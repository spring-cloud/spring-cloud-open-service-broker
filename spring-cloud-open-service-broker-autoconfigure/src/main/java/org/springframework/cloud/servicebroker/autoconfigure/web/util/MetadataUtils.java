/*
 * Copyright 2002-2022 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal class for marshaling abstract catalog metadata. Certain catalog metadata that is defined in YAML properties
 * needs to be adjusted to produce the expected JSON output.
 *
 * @author Roy Clarkson
 */
public final class MetadataUtils {

	private MetadataUtils() {
	}

	/**
	 * Traverses the tree and converts numbered maps into arrays for proper JSON serialization
	 *
	 * @param parameters a {@link Map} to convert
	 * @return the modified parameters
	 */
	public static Map<String, Object> convertMap(Map<String, Object> parameters) {
		Map<String, Object> convertedMap = new HashMap<>(parameters.size());
		parameters.forEach((key, value) -> convertedMap.put(key, convertEntry(value)));
		return convertedMap;
	}

	private static Object convertEntry(Object value) {
		Object convertedValue;
		if (value instanceof Map) {
			@SuppressWarnings("unchecked") //Spring YamlProcessor only provides String keys when loading yml,
					//see https://github.com/spring-projects/spring-framework/blob/604361ee1f8bab4e4720e0fd1d18ca77eefc1b5f/spring-beans/src/main/java/org/springframework/beans/factory/config/YamlProcessor.java#L283-L287
					Map<String, Object> valueMap = (Map<String, Object>) value;
			if (isNumberedMap(valueMap)) {
				convertedValue = convertNumberedMapToArray(valueMap);
			}
			else {
				convertedValue = convertMap(valueMap);
			}
		}
		else if (value instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> castedList = (List<Object>) value;
			convertedValue = convertList(castedList);
		}
		else {
			convertedValue = value;
		}
		return convertedValue;
	}

	private static List<Object> convertNumberedMapToArray(Map<String, Object> map) {
		List<Object> list = new ArrayList<>(map.size());
		for (int i = 0; map.get(Integer.toString(i)) != null; i++) {
			Object arrayItem = map.get(Integer.toString(i));
			if (arrayItem instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> castedArrayItem = (Map<String, Object>) arrayItem;
				arrayItem = convertMap(castedArrayItem);
			}
			list.add(arrayItem);
		}
		return list;
	}

	private static List<Object> convertList(List<Object> value) {
		List<Object> convertedList = new ArrayList<>(value.size());
		value.forEach(item -> convertedList.add(convertEntry(item)));
		return convertedList;
	}

	private static boolean isNumberedMap(Map<String, Object> map) {
		int matchingKeyIndex = -1;
		for (int i = 0; map.get(Integer.toString(i)) != null; i++) {
			matchingKeyIndex = i;
		}
		boolean atLeastOneIntegerIndexFound = matchingKeyIndex >= 0;
		boolean mapHasOnlyIntegerIndices = matchingKeyIndex == map.size() - 1;
		return atLeastOneIntegerIndexFound && mapHasOnlyIntegerIndices;
	}

}
