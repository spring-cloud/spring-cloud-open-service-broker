package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class SharedVolumeDevice extends VolumeDevice {
	@JsonSerialize
	@JsonProperty("volume_id")
	private String volumeId;

	@JsonSerialize
	@JsonProperty("mount_config")
	private Map<String, Object> mountConfig;
}
