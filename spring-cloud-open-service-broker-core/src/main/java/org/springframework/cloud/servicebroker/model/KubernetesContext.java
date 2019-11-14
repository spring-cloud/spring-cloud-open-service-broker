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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.util.StringUtils;

/**
 * Kubernetes specific contextual information under which the service instance is to be provisioned or updated.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class KubernetesContext extends Context {

	/**
	 * Kubernetes platform name
	 */
	public static final String KUBERNETES_PLATFORM = "kubernetes";

	/**
	 * Kubernetes Namespace key
	 */
	public static final String NAMESPACE_KEY = "namespace";

	/**
	 * Kubernetes Cluster ID key
	 */
	public static final String CLUSTERID_KEY = "clusterid";

	private KubernetesContext() {
		super(KUBERNETES_PLATFORM, null);
	}

	/**
	 * Create a new KubernetesContext
	 *
	 * @param namespace the kubernetes namespace
	 * @param clusterid the kubernetes clusterid
	 * @param properties a collection of properties
	 */
	public KubernetesContext(String namespace, String clusterid, Map<String, Object> properties) {
		super(KUBERNETES_PLATFORM, properties);
		if (StringUtils.hasText(namespace)) {
			setNamespace(namespace);
		}
		if (StringUtils.hasText(clusterid)) {
			setCluserid(clusterid);
		}
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
		properties.remove(CLUSTERID_KEY);
		properties.remove(Context.PLATFORM_KEY);
		return properties;
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

	private void setNamespace(String namespace) {
		properties.put(NAMESPACE_KEY, namespace);
	}

	/**
	 * Retrieve the kubernetes clusterid from the collection of platform properties
	 *
	 * @return the clusterid
	 */
	@JsonProperty
	public String getClusterid() {
		return getStringProperty(CLUSTERID_KEY);
	}

	private void setCluserid(String cluserid) {
		properties.put(CLUSTERID_KEY, cluserid);
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
	public static final class KubernetesContextBuilder
			extends ContextBaseBuilder<KubernetesContext, KubernetesContextBuilder> {

		private String namespace;

		private String clusterid;

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

		/**
		 * Set the kubernetes clusterid
		 *
		 * @param clusterid the clusterid
		 * @return the builder
		 */
		public KubernetesContextBuilder clusterid(String clusterid) {
			this.clusterid = clusterid;
			return this;
		}

		@Override
		public KubernetesContext build() {
			return new KubernetesContext(namespace, clusterid, properties);
		}

	}

}
