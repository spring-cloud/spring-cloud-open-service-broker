/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.cloud.servicebroker.autoconfigure.web.exception.CatalogDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.service.CatalogService;

/**
 * @author Roy Clarkson
 */
public class RequiredCatalogBeanFailureAnalyzer extends AbstractFailureAnalyzer<CatalogDefinitionDoesNotExistException> {

	private static final String REFERENCE_DOC = "https://docs.spring.io/spring-cloud-open-service-broker/docs/current" +
			"/reference/html5/#service-catalog";

	@Override
	protected FailureAnalysis analyze(Throwable rootFailure, CatalogDefinitionDoesNotExistException cause) {
		String description = "A 'service broker catalog' is required for Spring Cloud Open Service Broker applications";
		String action = String.format("Consider defining a catalog in properties or a bean of type" +
						" '%s' in your configuration. Alternatively, you may implement a service of type '%s'. See " +
						"the reference documentation for more information: " + REFERENCE_DOC, Catalog.class,
				CatalogService.class);
		return new FailureAnalysis(description, action, cause);
	}

}
