/*
 * Copyright 2002-2023 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.cloud.servicebroker.autoconfigure.web.exception.ServiceInstanceServiceBeanDoesNotExistException;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;

/**
 * Verifies that a ServiceInstanceService is configured.
 *
 * @author Roy Clarkson
 */
public class RequiredServiceInstanceServiceBeanFailureAnalyzer
		extends AbstractFailureAnalyzer<ServiceInstanceServiceBeanDoesNotExistException> {

	private static final String REFERENCE_DOC = "https://docs.spring.io/spring-cloud-open-service-broker/docs/current"
			+ "/reference/html5/#service-instances";

	@Override
	protected FailureAnalysis analyze(Throwable rootFailure, ServiceInstanceServiceBeanDoesNotExistException cause) {
		String description = String.format(
				"Service brokers must implement the '%s' and "
						+ "provide implementations of the required methods of that interface.",
				ServiceInstanceService.class);
		String action = String.format(
				"Consider defining a bean of type '%s' in your configuration. See "
						+ "the reference documentation for more information: " + REFERENCE_DOC,
				ServiceInstanceService.class);
		return new FailureAnalysis(description, action, cause);
	}

}
