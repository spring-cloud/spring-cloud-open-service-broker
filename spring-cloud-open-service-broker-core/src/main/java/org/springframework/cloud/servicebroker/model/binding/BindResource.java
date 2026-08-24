/*
 * Copyright 2002-2026 the original author or authors.
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

package org.springframework.cloud.servicebroker.model.binding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import org.springframework.util.CollectionUtils;

/**
 * Details of any platform resources that a service binding will be associated with.
 *
 * <p>
 * Objects of this type are constructed by the framework from the message body passed to
 * the service broker by the platform in a service binding request.
 *
 * @author Scott Frederick
 * @see <a
 * href=https://github.com/openservicebrokerapi/servicebroker/blob/v2.16/spec.md#bind-resource-object>Open
 * Service Broker API specification</a>
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BindResource {

	private final @Nullable String appGuid;

	private final @Nullable String route;

	private final @Nullable String credentialClientId;

	private final Map<String, Object> appAnnotations = new HashMap<>();

	private final Map<String, Object> properties = new HashMap<>();

	private BindResource() {
		this(null, null, null, null, null);
	}

	/**
	 * Construct a new {@link BindResource}.
	 * @param appGuid the application GUID
	 * @param route the application URL
	 * @param credentialClientId the CredHub client id
	 * @param appAnnotations the application annotations
	 * @param properties a collection of properties
	 */
	protected BindResource(@JsonProperty("app_guid") @Nullable String appGuid,
			@JsonProperty("route") @Nullable String route,
			@JsonProperty("credential_client_id") @Nullable String credentialClientId,
			@JsonProperty("app_annotations") @Nullable Map<String, Object> appAnnotations,
			@JsonProperty("properties") @Nullable Map<String, Object> properties) {
		this.appGuid = appGuid;
		this.route = route;
		this.credentialClientId = credentialClientId;
		if (!CollectionUtils.isEmpty(appAnnotations)) {
			this.appAnnotations.putAll(appAnnotations);
		}
		if (!CollectionUtils.isEmpty(properties)) {
			this.properties.putAll(properties);
		}
	}

	/**
	 * Set a property in the bind resource.
	 * @param key the property key
	 * @param value the property value
	 */
	@JsonAnySetter
	public void setProperty(String key, Object value) {
		this.properties.put(key, value);
	}

	/**
	 * Get the value of a property in the bind resource with the given key.
	 * @param key the key of the value to retrieve
	 * @return the value of the property, or {@literal null} if the key is not present in
	 * the bind resource
	 */
	public @Nullable Object getProperty(String key) {
		return this.properties.get(key);
	}

	/**
	 * Get all properties in the bind resource.
	 * @return the set of bind resource properties
	 */
	@JsonAnyGetter
	public Map<String, Object> getProperties() {
		return this.properties;
	}

	/**
	 * Get the GUID of an application associated with the binding. May be provided for
	 * credentials bindings.
	 * @return the application GUID
	 */
	public @Nullable String getAppGuid() {
		return this.appGuid;
	}

	/**
	 * Get the URL of an application to be intermediated. May be provided for route
	 * services bindings.
	 * @return the application route
	 */
	public @Nullable String getRoute() {
		return this.route;
	}

	/**
	 * Get the CredHub client id that will be granted read access to the credentials
	 * reference returned in the binding response. May be provided for Service Key
	 * bindings using CredHub.
	 * <p>
	 * Since OSB API 2.16.
	 * @return the CredHub client id
	 */
	public @Nullable String getCredentialClientId() {
		return this.credentialClientId;
	}

	/**
	 * Get the annotations attached to the application that the Service Binding is
	 * associated with. May be provided for credentials bindings.
	 * <p>
	 * Since OSB API 2.16.
	 * @return the application annotations
	 */
	@JsonInclude(Include.NON_EMPTY)
	public Map<String, Object> getAppAnnotations() {
		return this.appAnnotations;
	}

	/**
	 * Create a builder that provides a fluent API for constructing a
	 * {@link BindResource}.
	 *
	 * <p>
	 * This builder is provided to support testing of
	 * {@link org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService}
	 * implementations.
	 * @return the builder
	 */
	public static BindResourceBuilder builder() {
		return new BindResourceBuilder();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BindResource)) {
			return false;
		}
		BindResource that = (BindResource) o;
		return Objects.equals(this.appGuid, that.appGuid) && Objects.equals(this.route, that.route)
				&& Objects.equals(this.credentialClientId, that.credentialClientId)
				&& Objects.equals(this.appAnnotations, that.appAnnotations)
				&& Objects.equals(this.properties, that.properties);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(this.appGuid, this.route, this.credentialClientId, this.appAnnotations, this.properties);
	}

	@Override
	public String toString() {
		return "BindResource{" + "appGuid='" + this.appGuid + '\'' + ", route='" + this.route + '\''
				+ ", credentialClientId='" + this.credentialClientId + '\'' + ", appAnnotations=" + this.appAnnotations
				+ ", properties=" + this.properties + '}';
	}

	/**
	 * Provides a fluent API for constructing a {@literal BindResource}.
	 */
	public static final class BindResourceBuilder {

		private @Nullable String appGuid;

		private @Nullable String route;

		private @Nullable String credentialClientId;

		private final Map<String, Object> appAnnotations = new HashMap<>();

		private final Map<String, Object> properties = new HashMap<>();

		private BindResourceBuilder() {
		}

		/**
		 * Set an application GUID as would be provided in an app binding request from the
		 * platform.
		 * @param appGuid the application GUID
		 * @return the builder
		 */
		public BindResourceBuilder appGuid(String appGuid) {
			this.appGuid = appGuid;
			return this;
		}

		/**
		 * Set an application route as would be provided in a route binding request from
		 * the platform.
		 * @param route the application GUID
		 * @return the builder
		 */
		public BindResourceBuilder route(String route) {
			this.route = route;
			return this;
		}

		/**
		 * Set a CredHub client id as would be provided in an app binding request from the
		 * platform for a Service Key using CredHub.
		 * <p>
		 * Since OSB API 2.16.
		 * @param credentialClientId the CredHub client id
		 * @return the builder
		 */
		public BindResourceBuilder credentialClientId(String credentialClientId) {
			this.credentialClientId = credentialClientId;
			return this;
		}

		/**
		 * Set the application annotations as would be provided in an app binding request
		 * from the platform.
		 * <p>
		 * Since OSB API 2.16.
		 * @param appAnnotations the application annotations
		 * @return the builder
		 */
		public BindResourceBuilder appAnnotations(Map<String, Object> appAnnotations) {
			if (!CollectionUtils.isEmpty(appAnnotations)) {
				this.appAnnotations.clear();
				this.appAnnotations.putAll(appAnnotations);
			}
			return this;
		}

		/**
		 * Add a set of properties from the provided {@literal Map} to the bind resource
		 * properties as would be provided in the request from the platform.
		 * @param properties the properties to add
		 * @return the builder
		 * @see #getProperties()
		 */
		public BindResourceBuilder properties(Map<String, Object> properties) {
			this.properties.putAll(properties);
			return this;
		}

		/**
		 * Add a key/value pair to the bind resource properties as would be provided in
		 * the request from the platform.
		 * @param key the property key to add
		 * @param value the property value to add
		 * @return the builder
		 * @see #getProperties()
		 */
		public BindResourceBuilder properties(String key, Object value) {
			this.properties.put(key, value);
			return this;
		}

		/**
		 * Construct a {@link BindResource} from the provided values.
		 * @return the newly constructed {@literal BindResource}
		 */
		public BindResource build() {
			return new BindResource(this.appGuid, this.route, this.credentialClientId, this.appAnnotations,
					this.properties);
		}

	}

}
