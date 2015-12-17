package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Used to send errors back to the cloud controller.
 * 
 * @author sgreenberg@pivotal.io
 */
@Getter
@ToString
@EqualsAndHashCode
public class ErrorMessage {
	@JsonProperty("description")
	private final String message;

	public ErrorMessage(String message) {
		this.message = message;
	}
}
