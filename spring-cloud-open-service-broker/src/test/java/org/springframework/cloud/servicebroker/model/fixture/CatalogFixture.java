package org.springframework.cloud.servicebroker.model.fixture;

import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;

import java.util.Collections;
import java.util.List;

public class CatalogFixture {

	public static Catalog getCatalog() {
		List<ServiceDefinition> services = Collections.singletonList(ServiceFixture.getSimpleService());
		return new Catalog(services);
	}

	public static Catalog getCatalogWithRequires() {
		List<ServiceDefinition> services = Collections.singletonList(ServiceFixture.getServiceWithRequires());
		return new Catalog(services);
	}

}
