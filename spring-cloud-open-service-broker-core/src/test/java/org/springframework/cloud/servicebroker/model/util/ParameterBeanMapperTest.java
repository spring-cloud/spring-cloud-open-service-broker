/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ParameterBeanMapperTest {
	@Test
	@SuppressWarnings("serial")
	public void mapParametersToBean() {
		Map<String, Object> parameters = new HashMap<String, Object>() {{
			put("stringProperty", "value1");
			put("intProperty", 2);
			put("extraProperty", "extra");
			put("nestedBean.booleanProperty", true);
		}};
		TestBean testBean = ParameterBeanMapper.mapParametersToBean(parameters, TestBean.class);

		assertThat(testBean.getStringProperty()).isEqualTo("value1");
		assertThat(testBean.getIntProperty()).isEqualTo(2);
		assertThat(testBean.getUnusedProperty()).isNull();
		assertThat(testBean.getNestedBean().getBooleanProperty()).isEqualTo(true);
	}

	@SuppressWarnings("unused")
	public static final class TestBean {
		private String stringProperty;
		private int intProperty;
		private String unusedProperty;

		private NestedBean nestedBean;

		public TestBean() {
			this.nestedBean = new NestedBean();
		}

		public String getStringProperty() {
			return stringProperty;
		}

		public void setStringProperty(String stringProperty) {
			this.stringProperty = stringProperty;
		}

		public int getIntProperty() {
			return intProperty;
		}

		public void setIntProperty(int intProperty) {
			this.intProperty = intProperty;
		}

		public String getUnusedProperty() {
			return unusedProperty;
		}

		public void setUnusedProperty(String unusedProperty) {
			this.unusedProperty = unusedProperty;
		}

		public NestedBean getNestedBean() {
			return nestedBean;
		}
	}

	@SuppressWarnings("unused")
	public static final class NestedBean {
		private boolean booleanProperty;

		public boolean getBooleanProperty() {
			return booleanProperty;
		}

		public void setBooleanProperty(boolean booleanProperty) {
			this.booleanProperty = booleanProperty;
		}
	}
}
