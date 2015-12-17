package org.springframework.cloud.servicebroker.service;

import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;

/**
 * This interface is implemented by service brokers to process requests to retrieve the service catalog.
 * 
 * @author sgreenberg@pivotal.io
 */
public interface CatalogService {

	/**
	 * Return the catalog of services provided by the service broker.
	 *
	 * @return the catalog of services
	 */
	Catalog getCatalog();

	/**
	 * Get a service definition from the catalog by ID.
	 *
	 * @param serviceId  The ID of the service definition in the catalog
	 * @return the service definition, or null if it doesn't exist
	 */
	ServiceDefinition getServiceDefinition(String serviceId);
	
}
