/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kubernetes specific contextual information under which the service instance is to be provisioned or updated.
 *
 * @author Scott Frederick
 */
public final class KubernetesContext extends Context {

	public static final String KUBERNETES_PLATFORM = "kubernetes";

	public static final String NAMESPACE_KEY = "namespace";

	private KubernetesContext() {
		super(KUBERNETES_PLATFORM, null);
	}

	/**
	 * Create a new KubernetesContext
	 * @param namespace the kubernetes namespace
	 * @param properties a collection of properties
	 */
	public KubernetesContext(String namespace, Map<String, Object> properties) {
		super(KUBERNETES_PLATFORM, properties);
		if (namespace != null) {
			setNamespace(namespace);
		}
	}

	/**
	 * Retrieve the kubernetes namespace from the collection of platform properties
	 *
	 * @return the namespace
	 */
	@JsonProperty
	public String getNamespace() {
		return getStringProperty(NAMESPACE_KEY);
	}

	/**
	 * Avoid polluting the serialized context with duplicated keys
	 *
	 * @return properties
	 */
	@JsonAnyGetter
	public Map<String, Object> getSerializableProperties() {
		HashMap<String, Object> properties = new HashMap<>(super.getProperties());
		properties.remove(KUBERNETES_PLATFORM);
		properties.remove(NAMESPACE_KEY);
		properties.remove(Context.PLATFORM_KEY);
		return properties;
	}


	@NotEmpty
	private void setNamespace(String namespace) {
		properties.put(NAMESPACE_KEY, namespace);
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal KubernetesContext}.
	 *
	 * @return the builder
	 */
	public static KubernetesContextBuilder builder() {
		return new KubernetesContextBuilder();
	}

	/**
	 * Builder class for KubernetesContext
	 */
	public static class KubernetesContextBuilder extends ContextBaseBuilder<KubernetesContext, KubernetesContextBuilder> {

		private String namespace;

		private KubernetesContextBuilder() {
			super();
		}

		@Override
		protected KubernetesContextBuilder createBuilder() {
			return this;
		}

		/**
		 * Set the kubernetes namespace
		 *
		 * @param namespace the namespace
		 * @return the builder
		 */
		public KubernetesContextBuilder namespace(String namespace) {
			this.namespace = namespace;
			return this;
		}

		@Override
		public KubernetesContext build() {
			return new KubernetesContext(namespace, properties);
		}
	}}
