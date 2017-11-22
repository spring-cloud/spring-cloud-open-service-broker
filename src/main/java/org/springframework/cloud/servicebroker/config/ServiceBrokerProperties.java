package org.springframework.cloud.servicebroker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Configuration properties for service broker.
 *
 * @author Toshiaki Maki
 */
@ConfigurationProperties("servicebroker")
public class ServiceBrokerProperties {
	/**
	 * Location of the json file which is the result of /v2/catalog.
	 */
	private Resource catalogJson = new ClassPathResource("catalog.json");

	public Resource getCatalogJson() {
		return catalogJson;
	}

	public void setCatalogJson(Resource catalogJson) {
		this.catalogJson = catalogJson;
	}
}
