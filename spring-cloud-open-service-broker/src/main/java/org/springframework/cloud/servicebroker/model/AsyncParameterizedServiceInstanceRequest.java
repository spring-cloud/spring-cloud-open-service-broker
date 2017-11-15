package org.springframework.cloud.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Map;

/**
 * Details of a request that supports arbitrary parameters and asynchronous behavior.
 *
 * @author Scott Frederick
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AsyncParameterizedServiceInstanceRequest extends AsyncServiceInstanceRequest {
	/**
	 * Parameters passed by the user in the form of a JSON structure. The service broker is responsible
	 * for validating the contents of the parameters for correctness or applicability.
	 */
	@JsonSerialize
	@JsonProperty("parameters")
	protected final Map<String, Object> parameters;

	/**
	 * Platform specific contextual information under which the service instance is to be provisioned or updated.
	 */
	@JsonSerialize
	@JsonProperty("context")
	private final Context context;

	public AsyncParameterizedServiceInstanceRequest(Map<String, Object> parameters, Context context) {
		this.parameters = parameters;
		this.context = context;
	}

	public <T> T getParameters(Class<T> cls) {
		try {
			T bean = cls.newInstance();
			BeanUtils.populate(bean, parameters);
			return bean;
		} catch (Exception e) {
			throw new IllegalArgumentException("Error mapping parameters to class of type " + cls.getName(), e);
		}
	}
}
