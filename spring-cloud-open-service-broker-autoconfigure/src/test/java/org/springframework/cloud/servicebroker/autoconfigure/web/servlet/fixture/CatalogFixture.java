package org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture;

import org.springframework.cloud.servicebroker.model.Catalog;

public class CatalogFixture {

	public static Catalog getCatalog() {
		return Catalog.builder()
				.serviceDefinitions(ServiceFixture.getSimpleService())
				.build();
	}

	public static Catalog getCatalogWithRequires() {
		return Catalog.builder()
				.serviceDefinitions(ServiceFixture.getServiceWithRequires())
				.build();
	}

}
