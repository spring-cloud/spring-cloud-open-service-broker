/*
 * Copyright 2002-2018 the original author or authors.
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

import java.util.Map;

/**
 * Platform specific contextual information under which the service instance is to be provisioned or updated.
 *
 * @author Scott Frederick
 */
public class PlatformContext extends Context {

	private PlatformContext() {
		super(null, null);
	}

	private PlatformContext(String platform, Map<String, Object> properties) {
		super(platform, properties);
	}

	/**
	 * Create a builder that provides a fluent API for constructing a {@literal Context}.
	 *
	 * <p>
	 * This builder is provided to support testing of service implementations.
	 *
	 * @return the builder
	 */
	public static PlatformContextBuilder builder() {
		return new PlatformContextBuilder();
	}

	/**
	 * Provides a fluent API for constructing a {@link PlatformContext}.
	 */
	public static class PlatformContextBuilder extends ContextBaseBuilder<PlatformContext, PlatformContextBuilder> {
		PlatformContextBuilder() {
		}

		@Override
		protected PlatformContextBuilder createBuilder() {
			return this;
		}

		/**
		 * Construct a {@link PlatformContext} from the provided values.
		 *
		 * @return the newly constructed {@literal Context}
		 */
		public PlatformContext build() {
			return new PlatformContext(platform, properties);
		}
	}
}
