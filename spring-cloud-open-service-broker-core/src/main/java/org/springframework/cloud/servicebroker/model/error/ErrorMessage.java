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

package org.springframework.cloud.servicebroker.model.error;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details of an error reported to the platform from a service broker.
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 * @author Roy Clarkson
 * @see <a href="https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#service-broker-errors">Open
 * 		Service Broker API specification</a>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorMessage {

	private final String error;

	@JsonProperty("description")
	private final String message;

	/**
	 * Construct an error message with no error code or description.
	 */
	public ErrorMessage() {
		this(null, null);
	}

	/**
	 * Construct an error message with the provided description.
	 *
	 * @param message a user-facing error message explaining why the request failed
	 */
	public ErrorMessage(String message) {
		this(null, message);
	}

	/**
	 * Construct an error message with the provided error code and description.
	 *
	 * @param error a single word in camel case that uniquely identifies the error condition
	 * @param message a user-facing error message explaining why the request failed
	 */
	public ErrorMessage(String error, String message) {
		this.error = error;
		this.message = message;
	}

	/**
	 * Get the error code.
	 *
	 * @return the error code
	 */
	public String getError() {
		return this.error;
	}

	/**
	 * Get the description.
	 *
	 * @return the description.
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Create a builder that provides a fluent API for constructing an {@literal ErrorMessage}.
	 *
	 * @return the builder
	 */
	public static ErrorMessageBuilder builder() {
		return new ErrorMessageBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ErrorMessage)) {
			return false;
		}
		ErrorMessage that = (ErrorMessage) o;
		return Objects.equals(error, that.error) &&
				Objects.equals(message, that.message);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(error, message);
	}

	@Override
	public final String toString() {
		return "ErrorMessage{" +
				"error='" + error + '\'' +
				", message='" + message + '\'' +
				'}';
	}

	/**
	 * Provides a fluent API for constructing an {@link ErrorMessage}.
	 */
	public static final class ErrorMessageBuilder {

		private String error;

		private String message;

		/**
		 * Set the error code
		 *
		 * @param error the error code
		 * @return builder
		 * @see #getError()
		 */
		public ErrorMessageBuilder error(String error) {
			this.error = error;
			return this;
		}

		/**
		 * Set the description
		 *
		 * @param message the error description
		 * @return the builder
		 * @see #getMessage()
		 */
		public ErrorMessageBuilder message(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Construct an {@link ErrorMessage} from the provided values
		 *
		 * @return the newly constructed {@link ErrorMessage}
		 */
		public ErrorMessage build() {
			return new ErrorMessage(error, message);
		}
	}

}
