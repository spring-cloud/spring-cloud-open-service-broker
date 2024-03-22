/*
 * Copyright 2002-2024 the original author or authors.
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
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.springframework.util.CollectionUtils;

/**
 * Kubernetes specific contextual information under which the service instance is to be
 * provisioned or updated.
 *
 * @author Scott Frederick
 * @author Roy Clarkson
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class KubernetesContext extends Context {

	/**
	 * Kubernetes platform name.
	 */
	public static final String KUBERNETES_PLATFORM = "kubernetes";

	/**
	 * Kubernetes Namespace key.
	 */
	public static final String NAMESPACE_KEY = "namespace";

	/**
	 * Kubernetes Namespace Annotations key.
	 */
	public static final String NAMESPACE_ANNOTATIONS_KEY = "namespaceAnnotations";

	/**
	 * Kubernetes Instance Annotations key.
	 */
	public static final String INSTANCE_ANNOTATIONS_KEY = "instanceAnnotations";

	/**
	 * Kubernetes Instance Name.
	 */
	public static final String INSTANCE_NAME_KEY = "instanceName";

	/**
	 * Kubernetes Cluster ID key.
	 */
	public static final String CLUSTERID_KEY = "clusterid";

	private KubernetesContext() {
		super(KUBERNETES_PLATFORM, null);
	}

	/**
	 * Create a new KubernetesContext.
	 * @param namespace the kubernetes namespace
	 * @param clusterid the kubernetes clusterid
	 * @param properties a collection of properties
	 */
	public KubernetesContext(String namespace, String clusterid, Map<String, Object> properties) {
		super(KUBERNETES_PLATFORM, properties);
		setNamespace(namespace);
		setClusterid(clusterid);
	}

	/**
	 * Create a new KubernetesContext.
	 * @param namespace the kubernetes namespace
	 * @param instanceName the service instance name
	 * @param namespaceAnnotations the annotations attached to the namespace in which the
	 * Service Instance will be visible
	 * @param instanceAnnotations the annotations attached to the service instance
	 * @param clusterid the kubernetes clusterid
	 * @param properties a collection of properties
	 */
	public KubernetesContext(String namespace, String clusterid, String instanceName,
			Map<String, Object> namespaceAnnotations, Map<String, Object> instanceAnnotations,
			Map<String, Object> properties) {
		super(KUBERNETES_PLATFORM, properties);
		setNamespace(namespace);
		setClusterid(clusterid);
		setInstanceName(instanceName);
		setNamespaceAnnotations(namespaceAnnotations);
		setInstanceAnnotations(instanceAnnotations);
	}

	/**
	 * Avoid polluting the serialized context with duplicated keys.
	 * @return properties
	 */
	@JsonAnyGetter
	public Map<String, Object> getSerializableProperties() {
		HashMap<String, Object> properties = new HashMap<>(super.getProperties());
		properties.remove(KUBERNETES_PLATFORM);
		properties.remove(NAMESPACE_KEY);
		properties.remove(CLUSTERID_KEY);
		properties.remove(INSTANCE_NAME_KEY);
		properties.remove(NAMESPACE_ANNOTATIONS_KEY);
		properties.remove(INSTANCE_ANNOTATIONS_KEY);
		properties.remove(Context.PLATFORM_KEY);
		return properties;
	}

	/**
	 * Retrieve the kubernetes namespace from the collection of platform properties.
	 * @return the namespace
	 */
	@JsonProperty
	public String getNamespace() {
		return getStringProperty(NAMESPACE_KEY);
	}

	private void setNamespace(String namespace) {
		setStringProperty(NAMESPACE_KEY, namespace);
	}

	/**
	 * Retrieve the kubernetes clusterid from the collection of platform properties.
	 * @return the clusterid
	 */
	@JsonProperty
	public String getClusterid() {
		return getStringProperty(CLUSTERID_KEY);
	}

	private void setClusterid(String clusterid) {
		setStringProperty(CLUSTERID_KEY, clusterid);
	}

	/**
	 * Retrieve the kubernetes instance name from the collection of platform properties.
	 * @return the instance name
	 */
	@JsonProperty
	public String getInstanceName() {
		return getStringProperty(INSTANCE_NAME_KEY);
	}

	private void setInstanceName(String instanceName) {
		setStringProperty(INSTANCE_NAME_KEY, instanceName);
	}

	/**
	 * Retrieve the kubernetes namespace annotations from the collection of platform
	 * properties.
	 * @return the namespace annotations
	 */
	@JsonProperty
	public Map<String, Object> getNamespaceAnnotations() {
		return getMapProperty(NAMESPACE_ANNOTATIONS_KEY);
	}

	private void setNamespaceAnnotations(Map<String, Object> namespaceAnnotations) {
		setMapProperty(NAMESPACE_ANNOTATIONS_KEY, namespaceAnnotations);
	}

	/**
	 * Retrieve the kubernetes instance annotations from the collection of platform
	 * properties.
	 * @return the instance annotations
	 */
	public Map<String, Object> getInstanceAnnotations() {
		return getMapProperty(INSTANCE_ANNOTATIONS_KEY);
	}

	private void setInstanceAnnotations(Map<String, Object> instanceAnnotations) {
		setMapProperty(INSTANCE_ANNOTATIONS_KEY, instanceAnnotations);
	}

	/**
	 * Create a builder that provides a fluent API for constructing a
	 * {@literal KubernetesContext}.
	 * @return the builder
	 */
	public static KubernetesContextBuilder builder() {
		return new KubernetesContextBuilder();
	}

	/**
	 * Builder class for KubernetesContext.
	 */
	public static final class KubernetesContextBuilder
			extends ContextBaseBuilder<KubernetesContext, KubernetesContextBuilder> {

		private String namespace;

		private final Map<String, Object> namespaceAnnotations = new HashMap<>();

		private final Map<String, Object> instanceAnnotations = new HashMap<>();

		private String instanceName;

		private String clusterid;

		private KubernetesContextBuilder() {
			super();
		}

		@Override
		protected KubernetesContextBuilder createBuilder() {
			return this;
		}

		/**
		 * Set the kubernetes namespace.
		 * @param namespace the namespace
		 * @return the builder
		 */
		public KubernetesContextBuilder namespace(String namespace) {
			this.namespace = namespace;
			return this;
		}

		/**
		 * Set the kubernetes clusterid.
		 * @param clusterid the clusterid
		 * @return the builder
		 */
		public KubernetesContextBuilder clusterid(String clusterid) {
			this.clusterid = clusterid;
			return this;
		}

		/**
		 * Set the kubernetes instance name.
		 * @param instanceName the clusterid
		 * @return the builder
		 */
		public KubernetesContextBuilder instanceName(String instanceName) {
			this.instanceName = instanceName;
			return this;
		}

		/**
		 * Set the namespace annotations.
		 * @param namespaceAnnotations the namespace annotations
		 * @return the builder
		 */
		public KubernetesContextBuilder namespaceAnnotations(Map<String, Object> namespaceAnnotations) {
			if (!CollectionUtils.isEmpty(namespaceAnnotations)) {
				this.namespaceAnnotations.clear();
				this.namespaceAnnotations.putAll(namespaceAnnotations);
			}
			return this;
		}

		/**
		 * Set the instance annotations.
		 * @param instanceAnnotations the instance annotations
		 * @return the builder
		 */
		public KubernetesContextBuilder instanceAnnotations(Map<String, Object> instanceAnnotations) {
			if (!CollectionUtils.isEmpty(instanceAnnotations)) {
				this.instanceAnnotations.clear();
				this.instanceAnnotations.putAll(instanceAnnotations);
			}
			return this;
		}

		@Override
		public KubernetesContext build() {
			return new KubernetesContext(this.namespace, this.clusterid, this.instanceName, this.namespaceAnnotations,
					this.instanceAnnotations, this.properties);
		}

	}

}
