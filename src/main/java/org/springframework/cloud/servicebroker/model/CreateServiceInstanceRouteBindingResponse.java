package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of a response to a request to create a new service instance binding for a route.
 * 
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CreateServiceInstanceRouteBindingResponse extends CreateServiceInstanceBindingResponse {
	/**
	 * A URL to which Cloud Foundry should proxy requests for the bound route. Can be <code>null</code>.
	 */
	@JsonSerialize
	@JsonProperty("route_service_url")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String routeServiceUrl;

	public CreateServiceInstanceRouteBindingResponse withRouteServiceUrl(final String routeServiceUrl) {
		this.routeServiceUrl = routeServiceUrl;
		return this;
	}

	public CreateServiceInstanceRouteBindingResponse withBindingExisted(final boolean bindingExisted) {
		this.bindingExisted = bindingExisted;
		return this;
	}
}
