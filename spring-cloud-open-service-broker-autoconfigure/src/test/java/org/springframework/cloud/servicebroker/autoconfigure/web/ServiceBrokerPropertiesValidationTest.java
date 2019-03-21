/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web;

import java.util.List;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.beans.factory.BeanCreationException;
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

public class ServiceBrokerPropertiesValidationTest {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

	@After
	public void closeContext() {
		this.context.close();
	}

	@Test
	public void bindMinimumValidProperties() {
		this.context.register(ServiceBrokerPropertiesConfiguration.class);
		TestPropertySourceUtils.addPropertiesFilesToEnvironment(this.context, "classpath:catalog-minimal.properties");
		validateMinimumCatalog();
	}

	@Test
	public void bindMinimumValidYaml() throws Exception {
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
		assertThat(catalog.getServices().get(0).getPlans().get(0).getDescription()).isEqualTo("Description for Plan One");
	}

	@Test
	public void bindInvalidProperties() {
		this.context.register(ServiceBrokerPropertiesConfiguration.class);
		TestPropertyValues.of("spring.cloud.openservicebroker.catalog.services[0].id:service-one-id")
				.applyTo(this.context);
		this.thrown.expect(BeanCreationException.class);
		this.context.refresh();
	}

	@Test
	public void bindFullValidYaml() throws Exception {
		this.context.register(ServiceBrokerPropertiesConfiguration.class);
		Resource resource = context.getResource("classpath:catalog-full.yml");
		YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
		List<PropertySource<?>> properties = sourceLoader.load("catalog", resource);
		context.getEnvironment().getPropertySources().addFirst(properties.get(0));

		validateFullCatalog();
	}

	@Test
	public void bindFullValidProperties() {
		this.context.register(ServiceBrokerPropertiesConfiguration.class);
		TestPropertySourceUtils.addPropertiesFilesToEnvironment(this.context, "classpath:catalog-full.properties");
		validateFullCatalog();
	}

	private void validateFullCatalog() {
		this.context.refresh();
		ServiceBrokerProperties properties = this.context.getBean(ServiceBrokerProperties.class);
		Catalog catalog = properties.getCatalog();

		assertThat(catalog).isNotNull();
		assertThat(catalog.getServices()).hasSize(2);
		assertThat(catalog.getServices().get(0).getId()).isEqualTo("service-one-id");
		assertThat(catalog.getServices().get(0).getName()).isEqualTo("Service One");
		assertThat(catalog.getServices().get(0).getDescription()).isEqualTo("Description for Service One");
		assertThat(catalog.getServices().get(0).isBindable()).isTrue();
		assertThat(catalog.getServices().get(0).isBindingsRetrievable()).isTrue();
		assertThat(catalog.getServices().get(0).isInstancesRetrievable()).isTrue();
		assertThat(catalog.getServices().get(0).isPlanUpdateable()).isTrue();
		assertThat(catalog.getServices().get(0).getMetadata()).containsOnly(entry("key1", "value1"), entry("key2", "value2"));
		assertThat(catalog.getServices().get(0).getRequires()).containsOnly("syslog_drain", "route_forwarding");
		assertThat(catalog.getServices().get(0).getTags()).containsOnly("tag1", "tag2");
		assertThat(catalog.getServices().get(0).getDashboardClient().getId()).isEqualTo("dashboard-id");
		assertThat(catalog.getServices().get(0).getDashboardClient().getSecret()).isEqualTo("dashboard-secret");
		assertThat(catalog.getServices().get(0).getDashboardClient().getRedirectUri()).isEqualTo("dashboard-redirect-uri");
		assertThat(catalog.getServices().get(0).getPlans()).hasSize(2);
		assertThat(catalog.getServices().get(0).getPlans().get(0).getId()).isEqualTo("plan-one-id");
		assertThat(catalog.getServices().get(0).getPlans().get(0).getName()).isEqualTo("Plan One");
		assertThat(catalog.getServices().get(0).getPlans().get(0).getDescription()).isEqualTo("Description for Plan One");
		assertThat(catalog.getServices().get(0).getPlans().get(1).getId()).isEqualTo("plan-two-id");
		assertThat(catalog.getServices().get(0).getPlans().get(1).getName()).isEqualTo("Plan Two");
		assertThat(catalog.getServices().get(0).getPlans().get(1).getDescription()).isEqualTo("Description for Plan Two");
		assertThat(catalog.getServices().get(0).getPlans().get(1).getMetadata()).containsOnly(entry("key1", "value1"), entry("key2", "value2"));
		assertThat(catalog.getServices().get(0).getPlans().get(1).isBindable()).isTrue();
		assertThat(catalog.getServices().get(0).getPlans().get(1).isFree()).isTrue();
		assertThat(catalog.getServices().get(0).getPlans().get(1).getSchemas().getServiceInstance().getCreate().getParameters())
				.containsOnly(entry("$schema", "http://example.com/service/create/schema"), entry("type", "object"));
		assertThat(catalog.getServices().get(0).getPlans().get(1).getSchemas().getServiceInstance().getUpdate().getParameters())
				.containsOnly(entry("$schema", "http://example.com/service/update/schema"), entry("type", "object"));
		assertThat(catalog.getServices().get(0).getPlans().get(1).getSchemas().getServiceBinding().getCreate().getParameters())
				.containsOnly(entry("$schema", "http://example.com/service-binding/create/schema"), entry("type", "object"));
		assertThat(catalog.getServices().get(1).getId()).isEqualTo("service-two-id");
		assertThat(catalog.getServices().get(1).getName()).isEqualTo("Service Two");
		assertThat(catalog.getServices().get(1).getDescription()).isEqualTo("Description for Service Two");
		assertThat(catalog.getServices().get(1).getPlans()).hasSize(1);
		assertThat(catalog.getServices().get(1).getPlans().get(0).getId()).isEqualTo("plan-one-id");
		assertThat(catalog.getServices().get(1).getPlans().get(0).getName()).isEqualTo("Plan One");
		assertThat(catalog.getServices().get(1).getPlans().get(0).getDescription()).isEqualTo("Description for Plan One");
	}

	@Configuration
	@EnableConfigurationProperties(ServiceBrokerProperties.class)
	protected static class ServiceBrokerPropertiesConfiguration {

	}

}
