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

package org.springframework.cloud.servicebroker.model.binding;

import jakarta.validation.constraints.NotEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * The network endpoints that the Application uses to connect to the Service Instance. If present, all Service Instance
 * endpoints that are relevant for the Application MUST be in this list, even if endpoints are not reachable or host
 * names are not resolvable from outside the service network.
 *
 * @author Roy Clarkson
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#endpoint-object">Open Service
 * 		Broker API specification</a>
 */
public class Endpoint {

	/**
	 * A host name or a single IP address.
	 */
	@NotEmpty
	private String host;

	/**
	 * A non-empty array. Each element is either a single port (for example "443") or a port range (for example
	 * "9000-9010").
	 */
	@NotEmpty
	private List<String> ports;

	/**
	 * The network protocol. Valid values are tcp, udp, or all. The default value is tcp.
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@JsonDeserialize(using = ProtocolDeserializer.class)
	private Protocol protocol;

	/**
	 * Get the host
	 *
	 * @return the host
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * Get the list of ports
	 *
	 * @return the ports
	 */
	public List<String> getPorts() {
		return this.ports;
	}

	/**
	 * Get the network protocol
	 *
	 * @return the protocol
	 */
	public Protocol getProtocol() {
		return this.protocol;
	}

	/**
	 * Construct a new {@link Endpoint}
	 */
	public Endpoint() {
		this(null, new ArrayList<>(), null);
	}

	/**
	 * Construct a new {@link Endpoint}
	 *
	 * @param host the host
	 * @param ports the list of ports
	 * @param protocol the network protocol
	 */
	public Endpoint(String host, List<String> ports, Protocol protocol) {
		this.host = host;
		this.ports = ports;
		this.protocol = protocol;
	}

	/**
	 * Values designating the network protocol
	 */
	public enum Protocol {
		/**
		 * Supports TCP network protocol
		 */
		TCP("tcp"),

		/**
		 * Supports UDP network protocol
		 */
		UDP("udp"),

		/**
		 * Accepts all network protocols
		 */
		ALL("all");

		private final String value;

		Protocol(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	/**
	 * Create a builder that provides a fluent API for constructing an {@literal Endpoint}.
	 *
	 * @return the builder
	 */
	public static EndpointBuilder builder() {
		return new EndpointBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Endpoint)) {
			return false;
		}
		Endpoint that = (Endpoint) o;
		return Objects.equals(host, that.host) &&
				Objects.equals(ports, that.ports) &&
				Objects.equals(protocol, that.protocol);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(host, ports, protocol);
	}

	@Override
	public String toString() {
		return "Endpoint{" +
				"host='" + host + '\'' +
				", ports='" + ports + '\'' +
				", protocol=" + protocol + '\'' +
				'}';
	}

	/**
	 * Provides a fluid API for constructing an {@link Endpoint}
	 */
	public static final class EndpointBuilder {

		private String host;

		private final List<String> ports = new ArrayList<>();

		private Protocol protocol;

		/**
		 * Set the host name.
		 *
		 * <p>
		 * This value will set the {@literal host} field in the body of the response to the platform.
		 *
		 * @param host the host name
		 * @return the builder
		 */
		public EndpointBuilder host(String host) {
			this.host = host;
			return this;
		}

		/**
		 * Set the list of ports.
		 *
		 * <p>
		 * This value will set the {@literal ports} field in the body of the response to the platform.
		 *
		 * @param ports the ports
		 * @return the builder
		 */
		public EndpointBuilder ports(List<String> ports) {
			this.ports.addAll(ports);
			return this;
		}

		/**
		 * Set the list of ports.
		 *
		 * <p>
		 * This value will set the {@literal ports} field in the body of the response to the platform.
		 *
		 * @param ports the ports
		 * @return the builder
		 */
		public EndpointBuilder ports(String... ports) {
			Collections.addAll(this.ports, ports);
			return this;
		}

		/**
		 * Set the network protocol.
		 *
		 * <p>
		 * This value will set the {@literal protocol} field in the body of the response to the platform.
		 *
		 * @param protocol the protocol
		 * @return the builder
		 */
		public EndpointBuilder protocol(Protocol protocol) {
			this.protocol = protocol;
			return this;
		}

		/**
		 * Construct an {@link Endpoint} from the provided values
		 *
		 * @return the newly constructed {@link Endpoint}
		 */
		public Endpoint build() {
			return new Endpoint(host, ports, protocol);
		}

	}

	/**
	 * Custom {@link Protocol} Jackson Deserializer
	 */
	private static class ProtocolDeserializer extends StdDeserializer<Protocol> {

		private static final long serialVersionUID = -4451409880666814449L;

		/**
		 * Construct a new {@link ProtocolDeserializer}
		 */
		public ProtocolDeserializer() {
			this(null);
		}

		/**
		 * Construct a new {@link ProtocolDeserializer}
		 *
		 * @param c the type
		 */
		public ProtocolDeserializer(Class<?> c) {
			super(c);
		}

		@Override
		public Protocol deserialize(JsonParser jsonParser, DeserializationContext
				deserializationContext) throws IOException {
			for (Protocol m : Protocol.values()) {
				if (m.toString().equalsIgnoreCase(jsonParser.getText())) {
					return m;
				}
			}
			throw new IllegalArgumentException("Protocol not defined");
		}

	}

}
