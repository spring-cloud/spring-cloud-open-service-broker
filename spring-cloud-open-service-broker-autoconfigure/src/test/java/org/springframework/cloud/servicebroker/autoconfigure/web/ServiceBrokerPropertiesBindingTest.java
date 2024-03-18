/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.support.TestPropertySourceUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceBrokerPropertiesBindingTest {

	private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

	@AfterEach
	void closeContext() {
		this.context.close();
	}

	@Test
	void bindMinimumValidProperties() {
		this.context.register(ServiceBrokerPropertiesConfiguration.class);
		TestPropertySourceUtils.addPropertiesFilesToEnvironment(this.context, "classpath:catalog-minimal.properties");
		validateMinimumCatalog();
	}

	@Test
	void bindMinimumValidYaml() throws Exception {
		this.context.register(ServiceBrokerPropertiesConfiguration.class);
		Resource resource = context.getResource("classpath:catalog-minimal.yml");
		YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
		List<PropertySource<?>> properties = sourceLoader.load("catalog", resource);
		context.getEnvironment().getPropertySources().addFirst(properties.get(0));
		validateMinimumCatalog();
	}

	private void validateMinimumCatalog() {
		this.context.refresh();
		ServiceBrokerProperties properties = this.context.getBean(ServiceBrokerProperties.class);
		Catalog catalog = properties.getCatalog();
		assertThat(catalog).isNotNull();
		assertThat(catalog.getServices()).hasSize(1);
		assertThat(catalog.getServices().get(0).getId()).isEqualTo("service-one-id");
		assertThat(catalog.getServices().get(0).getName()).isEqualTo("Service One");
		assertThat(catalog.getServices().get(0).getDescription()).isEqualTo("Description for Service One");
		assertThat(catalog.getServices().get(0).getPlans()).hasSize(1);
		assertThat(catalog.getServices().get(0).getPlans().get(0).getId()).isEqualTo("plan-one-id");
		assertThat(catalog.getServices().get(0).getPlans().get(0).getName()).isEqualTo("Plan One");
		assertThat(catalog.getServices().get(0).getPlans().get(0).getDescription())
				.isEqualTo("Description for Plan One");

		//Mandatory fields should have a default set when unspecified in configuration
		assertThat(catalog.getServices().get(0).isBindable()).isNotNull();
		//Optional unspecified fields should not have a default set.
		assertThat(catalog.getServices().get(0).getPlans().get(0).isBindable()).isNull();
		assertThat(catalog.getServices().get(0).getPlans().get(0).isFree()).isNull();
		assertThat(catalog.getServices().get(0).getPlans().get(0).isPlanUpdateable()).isNull();
		assertThat(catalog.getServices().get(0).getPlans().get(0).getMaximumPollingDuration()).isNull();
		assertThat(catalog.getServices().get(0).isInstancesRetrievable()).isNull();
		assertThat(catalog.getServices().get(0).isBindingsRetrievable()).isNull();
		assertThat(catalog.getServices().get(0).isPlanUpdateable()).isNull();
	}

	@Test
	void bindInvalidProperties() {
		this.context.register(ServiceBrokerPropertiesConfiguration.class);
		TestPropertyValues.of("spring.cloud.openservicebroker.catalog.services[0].id:service-one-id")
				.applyTo(this.context);
		assertThrows(ConfigurationPropertiesBindException.class, this.context::refresh);
	}

	@Test
	void bindFullValidYaml() throws Exception {
		this.context.register(ServiceBrokerPropertiesConfiguration.class);
		Resource resource = context.getResource("classpath:catalog-full.yml");
		YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
		List<PropertySource<?>> properties = sourceLoader.load("catalog", resource);
		context.getEnvironment().getPropertySources().addFirst(properties.get(0));
		validateFullCatalog();
	}

	@Test
	void bindFullValidProperties() {
		this.context.register(ServiceBrokerPropertiesConfiguration.class);
		TestPropertySourceUtils.addPropertiesFilesToEnvironment(this.context, "classpath:catalog-full.properties");
		validateFullCatalog();
	}

	private void validateFullCatalog() {
		this.context.refresh();
		ServiceBrokerProperties properties = this.context.getBean(ServiceBrokerProperties.class);
		Catalog catalog = properties.getCatalog();
		assertThat(catalog).isNotNull();
		validateServiceOne(catalog);
		validateServiceOneMetadata(catalog);
		validateServiceOnePlans(catalog);
		validateServiceTwo(catalog);
	}

	private void validateServiceOne(Catalog catalog) {
		assertThat(catalog.getServices()).hasSize(2);
		assertThat(catalog.getServices().get(0).getId()).isEqualTo("service-one-id");
		assertThat(catalog.getServices().get(0).getName()).isEqualTo("Service One");
		assertThat(catalog.getServices().get(0).getDescription()).isEqualTo("Description for Service One");
		assertThat(catalog.getServices().get(0).isBindable()).isTrue();
		assertThat(catalog.getServices().get(0).isBindingsRetrievable()).isTrue();
		assertThat(catalog.getServices().get(0).isInstancesRetrievable()).isTrue();
		assertThat(catalog.getServices().get(0).isPlanUpdateable()).isTrue();
		assertThat(catalog.getServices().get(0).getRequires()).containsOnly("syslog_drain", "route_forwarding");
		assertThat(catalog.getServices().get(0).getTags()).containsOnly("tag1", "tag2");
		assertThat(catalog.getServices().get(0).getDashboardClient().getId()).isEqualTo("dashboard-id");
		assertThat(catalog.getServices().get(0).getDashboardClient().getSecret()).isEqualTo("dashboard-secret");
		assertThat(catalog.getServices().get(0).getDashboardClient().getRedirectUri())
				.isEqualTo("dashboard-redirect-uri");
	}

	private void validateServiceOneMetadata(Catalog catalog) {
		assertThat(catalog.getServices().get(0).getMetadata().getDisplayName()).isEqualTo("service display name");
		assertThat(catalog.getServices().get(0).getMetadata().getImageUrl()).isEqualTo("image-uri");
		assertThat(catalog.getServices().get(0).getMetadata().getLongDescription())
				.isEqualTo("service long description");
		assertThat(catalog.getServices().get(0).getMetadata().getProviderDisplayName())
				.isEqualTo("service provider display name");
		assertThat(catalog.getServices().get(0).getMetadata().getDocumentationUrl())
				.isEqualTo("service-documentation-url");
		assertThat(catalog.getServices().get(0).getMetadata().getSupportUrl()).isEqualTo("service-support-url");

		Object licenses = catalog.getServices().get(0).getMetadata().getProperties().get("licenses");
		assertThat(licenses).isInstanceOf(Map.class);
		@SuppressWarnings("unchecked")
		Map<String, Object> licenseMap = (Map<String, Object>) licenses;
		assertThat(licenseMap).containsOnly(entry("0", "license1"), entry("1", "license2"));

		Object features = catalog.getServices().get(0).getMetadata().getProperties().get("features");
		assertThat(features).isInstanceOf(Map.class);
		@SuppressWarnings("unchecked")
		Map<String, Object> featuresMap = (Map<String, Object>) features;
		assertThat(featuresMap).containsOnly(entry("0", "hosting"), entry("1", "scaling"));

		assertThat(catalog.getServices().get(0).getMetadata().getProperties()).contains(entry("key1", "value1"),
				entry("key2", "value2"));
	}

	private void validateServiceOnePlans(Catalog catalog) {
		assertThat(catalog.getServices().get(0).getPlans()).hasSize(2);
		assertThat(catalog.getServices().get(0).getPlans().get(0).getId()).isEqualTo("plan-one-id");
		assertThat(catalog.getServices().get(0).getPlans().get(0).getName()).isEqualTo("Plan One");
		assertThat(catalog.getServices().get(0).getPlans().get(0).getDescription())
				.isEqualTo("Description for Plan One");
		assertThat(catalog.getServices().get(0).getPlans().get(0).getMaintenanceInfo().getVersion()).isEqualTo("1.0.1");
		assertThat(catalog.getServices().get(0).getPlans().get(0).getMaintenanceInfo().getDescription())
				.isEqualTo("Description for maintenance info");
		assertThat(catalog.getServices().get(0).getPlans().get(1).getId()).isEqualTo("plan-two-id");
		assertThat(catalog.getServices().get(0).getPlans().get(1).getName()).isEqualTo("Plan Two");
		assertThat(catalog.getServices().get(0).getPlans().get(1).getDescription())
				.isEqualTo("Description for Plan Two");
		assertThat(catalog.getServices().get(0).getPlans().get(1).getMetadata().getProperties())
				.containsOnly(entry("key1", "value1"), entry("key2", "value2"));
		assertThat(catalog.getServices().get(0).getPlans().get(1).getMetadata().getCosts()).hasSize(1);
		assertThat(catalog.getServices().get(0).getPlans().get(1).getMetadata().getCosts().get(0).getUnit())
				.isEqualTo("MONTHLY");
		assertThat(catalog.getServices().get(0).getPlans().get(1).getMetadata().getCosts().get(0).getAmount())
				.contains(entry("usd", 649.0));
		assertThat(catalog.getServices().get(0).getPlans().get(1).getMetadata().getDisplayName())
				.isEqualTo("sample display name");
		assertThat(catalog.getServices().get(0).getPlans().get(1).getMetadata().getBullets())
				.containsExactlyInAnyOrder("bullet1", "bullet2");
		assertThat(catalog.getServices().get(0).getPlans().get(1).isBindable()).isTrue();
		assertThat(catalog.getServices().get(0).getPlans().get(1).isFree()).isTrue();
		assertThat(catalog.getServices().get(0).getPlans().get(1).isPlanUpdateable()).isTrue();
		assertThat(catalog.getServices().get(0).getPlans().get(1).getSchemas().getServiceInstance().getCreate()
				.getParameters())
				.contains(entry("$schema", "http://json-schema.org/draft-04/schema#"),
						entry("type", "string"));
		Object enumMap = catalog.getServices().get(0).getPlans().get(1).getSchemas().getServiceInstance().getCreate()
				.getParameters().get("enum");
		assertThat(enumMap).isInstanceOf(Map.class);
		@SuppressWarnings("unchecked")
		Map<String, Object> castedMap = (Map<String, Object>) enumMap;
		assertThat(castedMap).containsOnly(entry("0", "one"), entry("1", "two"), entry("2", "three"));
		assertThat(catalog.getServices().get(0).getPlans().get(1).getSchemas().getServiceInstance().getUpdate()
				.getParameters())
				.containsOnly(entry("$schema", "http://json-schema.org/draft-04/schema#"), entry("type", "object"));
		assertThat(catalog.getServices().get(0).getPlans().get(1).getSchemas().getServiceBinding().getCreate()
				.getParameters())
				.containsOnly(entry("$schema", "http://json-schema.org/draft-04/schema#"), entry("type", "object"));
		assertThat(catalog.getServices().get(0).getPlans().get(1).getMaximumPollingDuration()).isEqualTo(120);
	}

	private void validateServiceTwo(Catalog catalog) {
		assertThat(catalog.getServices().get(1).getId()).isEqualTo("service-two-id");
		assertThat(catalog.getServices().get(1).getName()).isEqualTo("Service Two");
		assertThat(catalog.getServices().get(1).getDescription()).isEqualTo("Description for Service Two");
		assertThat(catalog.getServices().get(1).getPlans()).hasSize(1);
		assertThat(catalog.getServices().get(1).getPlans().get(0).getId()).isEqualTo("plan-one-id");
		assertThat(catalog.getServices().get(1).getPlans().get(0).getName()).isEqualTo("Plan One");
		assertThat(catalog.getServices().get(1).getPlans().get(0).getDescription())
				.isEqualTo("Description for Plan One");
	}

	@Configuration
	@EnableConfigurationProperties(ServiceBrokerProperties.class)
	protected static class ServiceBrokerPropertiesConfiguration {

	}

}
