package org.springframework.cloud.servicebroker.model;

import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Details of a request to bind to a service instance binding.
 * 
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@Getter
@ToString
@EqualsAndHashCode
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@SuppressWarnings("deprecation")
public class CreateServiceInstanceBindingRequest {
	/**
	 * The ID of the service being bound, from the broker catalog.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("service_id")
	private final String serviceDefinitionId;

	/**
	 * The ID of the plan being bound within the service, from the broker catalog.
	 */
	@NotEmpty
	@JsonSerialize
	@JsonProperty("plan_id")
	private final String planId;

	/**
	 * The Cloud Controller GUID of the application the service instance will be bound to. Will be provided when
	 * users bind applications to service instances, or <code>null</code> if an application is not being bound.
	 *
	 * @deprecated The <code>bindResource</code> field will contain references to the resource being bound, and should
	 * be used instead of this field.
	 */
	@JsonSerialize
	@JsonProperty("app_guid")
	private final String appGuid;

	/**
	 * The resource being bound to the service instance.
	 */
	@JsonSerialize
	@JsonProperty("bind_resource")
	private final Map<String, Object> bindResource;

	/**
	 * Parameters passed by the user in the form of a JSON structure. The service broker is responsible
	 * for validating the contents of the parameters for correctness or applicability.
	 */
	@JsonSerialize
	@JsonProperty("parameters")
	private final Map<String, Object> parameters;

	/**
	 * The Cloud Controller GUID of the service instance to being bound.
	 */
	@JsonIgnore
	private transient String serviceInstanceId;

	/**
	 * The Cloud Controller GUID of the service binding being created. This ID will be used for future
	 * requests for the same service instance binding, so the broker must use it to correlate any resource it creates.
	 */
	@JsonIgnore
	private transient String bindingId;

	/**
	 * The {@link ServiceDefinition} of the service to provision. This is resolved from the
	 * <code>serviceDefinitionId</code> as a convenience to the broker.
	 */
	@JsonIgnore
	private transient ServiceDefinition serviceDefinition;

	public CreateServiceInstanceBindingRequest() {
		serviceDefinitionId = null;
		planId = null;
		appGuid = null;
		bindResource = null;
		parameters = null;
	}
	
	public CreateServiceInstanceBindingRequest(String serviceDefinitionId, String planId,
											   String appGuid, Map<String, Object> bindResource,
											   Map<String, Object> parameters) {
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.appGuid = appGuid;
		this.bindResource = bindResource;
		this.parameters = parameters;
	}

	public CreateServiceInstanceBindingRequest(String serviceDefinitionId, String planId,
											   String appGuid, Map<String, Object> bindResource) {
		this(serviceDefinitionId, planId, appGuid, bindResource, null);
	}

	public <T> T getParameters(Class<T> cls) {
		try {
			T bean = cls.newInstance();
			BeanUtils.populate(bean, parameters);
			return bean;
		} catch (Exception e) {
			throw new IllegalArgumentException("Error mapping parameters to class of type " + cls.getName());
		}
	}

	public CreateServiceInstanceBindingRequest withServiceDefinition(final ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
		return this;
	}

	public CreateServiceInstanceBindingRequest withServiceInstanceId(final String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
		return this;
	}

	public CreateServiceInstanceBindingRequest withBindingId(final String bindingId) {
		this.bindingId = bindingId;
		return this;
	}

	public String getBoundAppGuid() {
		if (bindResource == null) {
			return null;
		}
		return (String) bindResource.get(ServiceBindingResource.BIND_RESOURCE_KEY_APP.toString());
	}

	public String getBoundRoute() {
		if (bindResource == null) {
			return null;
		}
		return (String) bindResource.get(ServiceBindingResource.BIND_RESOURCE_KEY_ROUTE.toString());
	}
}
