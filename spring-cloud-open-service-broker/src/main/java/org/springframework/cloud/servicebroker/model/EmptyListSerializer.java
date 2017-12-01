/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * This {@link JsonSerializer} can be used to serialize an empty array (e.g. <code>{}</code>) into a JSON string instead
 * of a <code>null</code> value for collection and map types.
 *
 * Example:
 * <code>
 *     {@literal @JsonSerialize(nullsUsing = EmptyListSerializer.class)}
 *     public List&lt;String&gt; strings;
 * </code>
 */
public class EmptyListSerializer extends JsonSerializer<List<?>> {
	public EmptyListSerializer() {
	}

	@Override
	public void serialize(List<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeStartArray();
		jgen.writeEndArray();
	}
}
