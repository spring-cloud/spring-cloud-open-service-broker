package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

/**
 * This {@link JsonSerializer} can be used to serialize an empty array (e.g. <code>{}</code>) into a JSON string instead
 * of a <code>null</code> value for collection types.
 *
 * Example:
 * <code>
 *     {@literal @JsonSerialize(nullsUsing = EmptyMapSerializer.class)}
 *     public List<String> strings;
 * </code>
 */
class EmptyMapSerializer extends JsonSerializer<List<?>> {
	public EmptyMapSerializer() {
	}

	@Override
	public void serialize(List<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeStartObject();
		jgen.writeEndObject();
	}
}
