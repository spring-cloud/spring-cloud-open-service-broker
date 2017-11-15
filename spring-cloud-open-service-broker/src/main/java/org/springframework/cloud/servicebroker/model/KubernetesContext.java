package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Kubernetes specific contextual information under which the service instance is to be provisioned or updated.
 *
 * @author Scott Frederick
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public final class KubernetesContext extends Context {
	public static final String KUBERNETES_PLATFORM = "kubernetes";

	/**
	 * The Kubernetes namespace for which the operation is requested.
	 */
	@NotEmpty
	@JsonSerialize
	private final String namespace;

	public KubernetesContext() {
		this.namespace = null;
	}

	public KubernetesContext(String namespace) {
		this.namespace = namespace;
	}
}
