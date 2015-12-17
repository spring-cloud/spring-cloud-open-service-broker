package org.springframework.cloud.servicebroker.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a response to a request to delete a service instance.
 *
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DeleteServiceInstanceResponse extends AsyncServiceInstanceResponse {
	public DeleteServiceInstanceResponse(boolean async) {
		super(async);
	}

	public DeleteServiceInstanceResponse() {
		this(false);
	}
}
