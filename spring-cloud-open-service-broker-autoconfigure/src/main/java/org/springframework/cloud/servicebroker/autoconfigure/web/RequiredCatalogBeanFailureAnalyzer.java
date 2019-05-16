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
