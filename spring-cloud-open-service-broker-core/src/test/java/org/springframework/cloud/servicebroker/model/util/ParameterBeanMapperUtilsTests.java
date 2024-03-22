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

package org.springframework.cloud.servicebroker.model.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParameterBeanMapperUtilsTests {

	@Test
	void mapParametersToBean() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("stringProperty", "value1");
		parameters.put("intProperty", 2);
		parameters.put("extraProperty", "extra");
		parameters.put("nestedBean.booleanProperty", true);

		BasicBean basicBean = ParameterBeanMapperUtils.mapParametersToBean(parameters, BasicBean.class);

		assertThat(basicBean.getStringProperty()).isEqualTo("value1");
		assertThat(basicBean.getIntProperty()).isEqualTo(2);
		assertThat(basicBean.getUnusedProperty()).isNull();
		assertThat(basicBean.getNestedBean().isBooleanProperty()).isEqualTo(true);
	}

	public static final class BasicBean {

		private String stringProperty;

		private int intProperty;

		private String unusedProperty;

		private final NestedBean nestedBean;

		BasicBean() {
			this.nestedBean = new NestedBean();
		}

		public String getStringProperty() {
			return this.stringProperty;
		}

		public void setStringProperty(String stringProperty) {
			this.stringProperty = stringProperty;
		}

		public int getIntProperty() {
			return this.intProperty;
		}

		public void setIntProperty(int intProperty) {
			this.intProperty = intProperty;
		}

		public String getUnusedProperty() {
			return this.unusedProperty;
		}

		public void setUnusedProperty(String unusedProperty) {
			this.unusedProperty = unusedProperty;
		}

		public NestedBean getNestedBean() {
			return this.nestedBean;
		}

	}

	public static final class NestedBean {

		private boolean booleanProperty;

		public boolean isBooleanProperty() {
			return this.booleanProperty;
		}

		public void setBooleanProperty(boolean booleanProperty) {
			this.booleanProperty = booleanProperty;
		}

	}

}
