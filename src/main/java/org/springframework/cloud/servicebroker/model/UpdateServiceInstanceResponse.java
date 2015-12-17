package org.springframework.cloud.servicebroker.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a response to a request to update a service instance.
 *
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UpdateServiceInstanceResponse extends AsyncServiceInstanceResponse {
	public UpdateServiceInstanceResponse(boolean async) {
		super(async);
	}

	public UpdateServiceInstanceResponse() {
		this(false);
	}
}
