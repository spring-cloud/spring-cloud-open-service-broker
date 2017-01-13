package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Details of a response to a request to create a new service instance binding for volumes.
 * 
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CreateServiceInstanceVolumeBindingResponse extends CreateServiceInstanceBindingResponse {
	/**
	 * The details of the volume mounts available to applications.
	 */
	@JsonProperty("volume_mounts")
	@JsonSerialize(nullsUsing = EmptyListSerializer.class)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<VolumeMount> volumeMounts;

	public CreateServiceInstanceVolumeBindingResponse withVolumeMounts(final List<VolumeMount> volumeMounts) {
		this.volumeMounts = volumeMounts;
		return this;
	}

	public CreateServiceInstanceVolumeBindingResponse withBindingExisted(final boolean bindingExisted) {
		this.bindingExisted = bindingExisted;
		return this;
	}
}
