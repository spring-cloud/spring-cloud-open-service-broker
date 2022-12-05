/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.SuppressPropertiesBeanIntrospector;

/**
 * Utilities for mapping parameter maps to Java beans.
 *
 * @author Scott Frederick
 */
public final class ParameterBeanMapperUtils {

	private ParameterBeanMapperUtils() {
	}

	/**
	 * Instantiates an object of the specified type and populates properties of the object from the provided
	 * parameters.
	 *
	 * @param parameters a {@link Map} of values to populate the object from
	 * @param cls the {@link Class} representing the type of the object to instantiate and populate
	 * @param <T> the type of the object to instantiate and populate
	 * @return the instantiated and populated object
	 */
	public static <T> T mapParametersToBean(Map<String, Object> parameters, Class<T> cls) {
		try {
			T bean = cls.getDeclaredConstructor().newInstance();

			BeanUtilsBean beanUtils = new BeanUtilsBean();
			beanUtils.getPropertyUtils().addBeanIntrospector(SuppressPropertiesBeanIntrospector.SUPPRESS_CLASS);
			beanUtils.populate(bean, parameters);

			return bean;
		}
		catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalArgumentException("Error mapping parameters to class of type " + cls.getName(), e);
		}
	}

}
