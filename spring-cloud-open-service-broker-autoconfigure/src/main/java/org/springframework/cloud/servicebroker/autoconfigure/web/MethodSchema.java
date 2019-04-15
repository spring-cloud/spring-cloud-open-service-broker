/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal class for marshaling {@link ServiceBrokerProperties} configuration properties
 * that describe a JSON Schema for a service broker object method.
 *
 * @author Sam Gunaratne
 * @author Roy Clarkson
 * @see org.springframework.cloud.servicebroker.model.catalog.MethodSchema
 */
public class MethodSchema {

	/**
	 * A map of JSON schema for configuration parameters.
	 */
	private final Map<String, Object> parameters = new HashMap<>();

	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	/**
	 * Converts this object into its corresponding model
	 *
	 * @return a MethodSchema model
	 * @see org.springframework.cloud.servicebroker.model.catalog.MethodSchema
	 */
	public org.springframework.cloud.servicebroker.model.catalog.MethodSchema toModel() {
		return org.springframework.cloud.servicebroker.model.catalog.MethodSchema.builder()
				.parameters(convertPlainMap(this.parameters))
				.build();
	}

	private Map<String, Object> convertPlainMap(Map<String, Object> parameters) {
		Map<String, Object>  convertedMap = new HashMap<>(parameters.size());
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
            Object convertedValue = convertEntry(value);
            convertedMap.put(key, convertedValue);
		}
		return convertedMap;
	}


    private Object convertEntry(Object value) {
        Object convertedValue;
        if (value instanceof Map) {
            @SuppressWarnings("unchecked") //Spring YamlProcessor only provides String keys when loading yml,
            //see https://github.com/spring-projects/spring-framework/blob/604361ee1f8bab4e4720e0fd1d18ca77eefc1b5f/spring-beans/src/main/java/org/springframework/beans/factory/config/YamlProcessor.java#L283-L287
            Map<String, Object> valueMap = (Map<String, Object>) value;
            if (isNumberedMap(valueMap)) {
                convertedValue = convertNumberedMapToArray(valueMap);
            } else {
                convertedValue = convertPlainMap(valueMap);
            }
        } else if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> castedList = (List<Object>) value;
            convertedValue = convertList(castedList);
        } else {
            convertedValue = value;
        }
        return convertedValue;
    }

    private List<Object> convertNumberedMapToArray(Map<String, Object> map) {
        List<Object> list = new ArrayList<>(map.size());
        for (int i=0; map.get(Integer.toString(i)) != null ; i++) {
            Object arrayItem = map.get(Integer.toString(i));
            if (arrayItem instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> castedArrayItem = (Map<String, Object>) arrayItem;
                arrayItem = convertPlainMap(castedArrayItem);
            }
            list.add(arrayItem);
        }
        return list;
    }

    private List<Object> convertList(List<Object> value) {
        List<Object> convertedList = new ArrayList<>(value.size());
        for (Object item : value) {
            convertedList.add(convertEntry(item));
        }
        return convertedList;
    }

    private boolean isNumberedMap(Map<String, Object> map) {
		int matchingKeyIndex = -1;
		for (int i=0; map.get(Integer.toString(i)) != null ; i++) {
			matchingKeyIndex=i;
		}
        boolean atLeastOneIntegerIndexFound = matchingKeyIndex >= 0;
        boolean mapHasOnlyIntegerIndices = matchingKeyIndex == map.size() - 1;

        return atLeastOneIntegerIndexFound && mapHasOnlyIntegerIndices;
	}

}
