package org.springframework.cloud.servicebroker.config;

import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.service.BeanCatalogService;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.springframework.cloud.servicebroker.model.BrokerApiVersion.API_VERSION_CURRENT;

@Configuration
@ComponentScan(basePackages = {"org.springframework.cloud.servicebroker"})
@ConditionalOnWebApplication
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class ServiceBrokerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(BrokerApiVersion.class)
	public BrokerApiVersion brokerApiVersion() {
		return new BrokerApiVersion(API_VERSION_CURRENT);
	}

	@Bean
	@ConditionalOnMissingBean(CatalogService.class)
	public CatalogService beanCatalogService(Catalog catalog) {
		return new BeanCatalogService(catalog);
	}

}