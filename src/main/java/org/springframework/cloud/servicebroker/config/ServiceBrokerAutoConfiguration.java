package org.springframework.cloud.servicebroker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.service.BeanCatalogService;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.cloud.servicebroker.service.NonBindableServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
@ComponentScan(basePackages = { "org.springframework.cloud.servicebroker" })
@ConditionalOnWebApplication
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(ServiceBrokerProperties.class)
public class ServiceBrokerAutoConfiguration {
	@Autowired
	ServiceBrokerProperties properties;

	@Bean
	@ConditionalOnMissingBean(BrokerApiVersion.class)
	public BrokerApiVersion brokerApiVersion() {
		return new BrokerApiVersion();
	}

	@Bean
	@ConditionalOnMissingBean(CatalogService.class)
	public CatalogService beanCatalogService(Catalog catalog) {
		return new BeanCatalogService(catalog);
	}

	@Bean
	@ConditionalOnMissingBean(ServiceInstanceBindingService.class)
	public ServiceInstanceBindingService nonBindableServiceInstanceBindingService() {
		return new NonBindableServiceInstanceBindingService();
	}

	@Bean
	@ConditionalOnMissingBean(Catalog.class)
	@ConditionalOnResource(resources = "${servicebroker.catalog-json:classpath:catalog.json}")
	public Catalog catalog() throws Exception {
		try (InputStream stream = properties.getCatalogJson().getInputStream()) {
			return new ObjectMapper().readValue(stream, Catalog.class);
		}
	}
}