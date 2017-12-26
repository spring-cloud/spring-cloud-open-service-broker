/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.Objects;

/**
 * Kubernetes specific contextual information under which the service instance is to be provisioned or updated.
 *
 * @author Scott Frederick
 */
public final class KubernetesContext extends Context {
	public static final String KUBERNETES_PLATFORM = "kubernetes";

	/**
	 * The Kubernetes namespace for which the operation is requested.
	 */
	@NotEmpty
	private final String namespace;

	private KubernetesContext() {
		this.namespace = null;
	}

	private KubernetesContext(String namespace) {
		this.namespace = namespace;
	}

	public String getNamespace() {
		return this.namespace;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KubernetesContext)) return false;
		if (!super.equals(o)) return false;
		KubernetesContext that = (KubernetesContext) o;
		return Objects.equals(namespace, that.namespace);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), namespace);
	}

	@Override
	public String toString() {
		return super.toString() +
				"KubernetesContext{" +
				"namespace='" + namespace + '\'' +
				'}';
	}
}
