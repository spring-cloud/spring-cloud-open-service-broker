package org.springframework.cloud.servicebroker.autoconfigure.web;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.cloud.servicebroker.autoconfigure.web.exception.CatalogDefinitionDoesNotExistException;
import org.springframework.cloud.servicebroker.autoconfigure.web.exception.ServiceInstanceServiceBeanDoesNotExistException;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;

/**
 * @author Roy Clarkson
 */
public class RequiredServiceInstanceServiceBeanFailureAnalyzer
		extends AbstractFailureAnalyzer<ServiceInstanceServiceBeanDoesNotExistException> {

	private static final String REFERENCE_DOC = "https://docs.spring.io/spring-cloud-open-service-broker/docs/current" +
			"/reference/html5/#service-instances";

	@Override
	protected FailureAnalysis analyze(Throwable rootFailure, ServiceInstanceServiceBeanDoesNotExistException cause) {
		String description = String.format("Service brokers must implement the '%s' and " +
				"provide implementations of the required methods of that interface.", ServiceInstanceService.class);
		String action = String.format("Consider defining a bean of type '%s' in your configuration. See " +
						"the reference documentation for more information: " + REFERENCE_DOC,
				ServiceInstanceService.class);
		return new FailureAnalysis(description, action, cause);
	}

}
