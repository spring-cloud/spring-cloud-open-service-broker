/*
 * Copyright 2002-2020 the original author or authors.
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ServiceBrokerPropertiesTest {

	private static final String PREFIX = "spring.cloud.openservicebroker";

	private Map<String, String> map;

	@BeforeEach
	void setUp() {
		this.map = new HashMap<>();
	}

	@Test
	void empty() {
		ServiceBrokerProperties properties = bindProperties();
		assertThat(properties.getApiVersion()).isNull();
		assertThat(properties.getCatalog()).isNull();
	}

	@Test
	void apiVersion() {
		map.put("spring.cloud.openservicebroker.apiVersion", "42.42");
		ServiceBrokerProperties properties = bindProperties();
		assertThat(properties.getApiVersion()).isEqualTo("42.42");
	}

	@Test
	void catalog() {
		setUpCatalogProperties();

		ServiceBrokerProperties properties = bindProperties();
		validateCatalogProperties(properties);

		Catalog catalog = properties.getCatalog().toModel();
		validateCatalogModel(catalog);
	}

	private void setUpCatalogProperties() {
		map.put("spring.cloud.openservicebroker.catalog.services[0].id", "service-one-id");
		map.put("spring.cloud.openservicebroker.catalog.services[0].name", "Service One");
		map.put("spring.cloud.openservicebroker.catalog.services[0].description", "Description for Service One");
		map.put("spring.cloud.openservicebroker.catalog.services[0].bindable", "true");
		map.put("spring.cloud.openservicebroker.catalog.services[0].bindings-retrievable", "true");
		map.put("spring.cloud.openservicebroker.catalog.services[0].allowContextUpdates", "false");
		map.put("spring.cloud.openservicebroker.catalog.services[0].instances-retrievable", "true");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plan-updateable", "true");
		map.put("spring.cloud.openservicebroker.catalog.services[0].metadata.properties[key1]", "value1");
		map.put("spring.cloud.openservicebroker.catalog.services[0].metadata.properties[key2]", "value2");
		map.put("spring.cloud.openservicebroker.catalog.services[0].metadata.displayName", "service display name");
		map.put("spring.cloud.openservicebroker.catalog.services[0].metadata.imageUrl", "image-uri");
		map.put("spring.cloud.openservicebroker.catalog.services[0].metadata.longDescription",
				"service long description");
		map.put("spring.cloud.openservicebroker.catalog.services[0].metadata.providerDisplayName",
				"service provider display name");
		map.put("spring.cloud.openservicebroker.catalog.services[0].metadata.documentationUrl",
				"service-documentation-url");
		map.put("spring.cloud.openservicebroker.catalog.services[0].metadata.supportUrl", "service-support-url");
		map.put("spring.cloud.openservicebroker.catalog.services[0].requires[0]", "syslog_drain");
		map.put("spring.cloud.openservicebroker.catalog.services[0].requires[1]", "route_forwarding");
		map.put("spring.cloud.openservicebroker.catalog.services[0].tags[0]", "tag1");
		map.put("spring.cloud.openservicebroker.catalog.services[0].tags[1]", "tag2");
		map.put("spring.cloud.openservicebroker.catalog.services[0].dashboard-client.id", "dashboard-id");
		map.put("spring.cloud.openservicebroker.catalog.services[0].dashboard-client.secret", "dashboard-secret");
		map.put("spring.cloud.openservicebroker.catalog.services[0].dashboard-client.redirect-uri",
				"dashboard-redirect-uri");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[0].id", "plan-one-id");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[0].name", "Plan One");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[0].description", "Description for Plan One");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[0].maintenance_info.version", "1.0.1");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[0].maintenance_info.description",
				"Description for maintenance info");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].id", "plan-two-id");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].name", "Plan Two");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].description", "Description for Plan Two");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].metadata.properties[key1]", "value1");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].metadata.properties[key2]", "value2");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].metadata.displayName",
				"sample display name");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].metadata.bullets[0]", "bullet1");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].metadata.bullets[1]", "bullet2");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].metadata.costs[0].amount[usd]", "649.0");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].metadata.costs[0].unit", "MONTHLY");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].bindable", "true");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].free", "true");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].planUpdateable", "true");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].schemas.serviceinstance.create.parameters[$schema]",
				"http://json-schema.org/draft-04/schema#");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].schemas.serviceinstance.create.parameters[type]",
				"string");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].schemas.serviceinstance.create.parameters[enum].0",
				"one");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].schemas.serviceinstance.create.parameters[enum].1",
				"two");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].schemas.serviceinstance.create.parameters[enum].2",
				"three");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].schemas.serviceinstance.update.parameters[$schema]",
				"http://json-schema.org/draft-04/schema#");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].schemas.serviceinstance.update.parameters[type]",
				"object");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].schemas.servicebinding.create.parameters[$schema]",
				"http://json-schema.org/draft-04/schema#");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].schemas.servicebinding.create.parameters[type]",
				"object");
		map.put("spring.cloud.openservicebroker.catalog.services[0].plans[1].maximumPollingDuration", "200");
		map.put("spring.cloud.openservicebroker.catalog.services[1].id", "service-two-id");
		map.put("spring.cloud.openservicebroker.catalog.services[1].name", "Service Two");
		map.put("spring.cloud.openservicebroker.catalog.services[1].description", "Description for Service Two");
		map.put("spring.cloud.openservicebroker.catalog.services[1].plans[0].id", "plan-one-id");
		map.put("spring.cloud.openservicebroker.catalog.services[1].plans[0].name", "Plan One");
		map.put("spring.cloud.openservicebroker.catalog.services[1].plans[0].description", "Description for Plan One");
	}

	private void validateCatalogProperties(ServiceBrokerProperties properties) {
		assertThat(properties.getCatalog()).isNotNull();
		assertThat(properties.getCatalog().getServices()).hasSize(2);
		assertThat(properties.getCatalog().getServices().get(0).getId()).isEqualTo("service-one-id");
		assertThat(properties.getCatalog().getServices().get(0).getName()).isEqualTo("Service One");
		assertThat(properties.getCatalog().getServices().get(0).getDescription())
				.isEqualTo("Description for Service One");
		assertThat(properties.getCatalog().getServices().get(0).isBindable()).isTrue();
		assertThat(properties.getCatalog().getServices().get(0).isBindingsRetrievable()).isTrue();
		assertThat(properties.getCatalog().getServices().get(0).isAllowContextUpdates()).isFalse();
		assertThat(properties.getCatalog().getServices().get(0).isInstancesRetrievable()).isTrue();
		assertThat(properties.getCatalog().getServices().get(0).isPlanUpdateable()).isTrue();
		assertThat(properties.getCatalog().getServices().get(0).getMetadata().getDisplayName())
				.isEqualTo("service display name");
		assertThat(properties.getCatalog().getServices().get(0).getMetadata().getImageUrl()).isEqualTo("image-uri");
		assertThat(properties.getCatalog().getServices().get(0).getMetadata().getLongDescription())
				.isEqualTo("service long description");
		assertThat(properties.getCatalog().getServices().get(0).getMetadata().getProviderDisplayName())
				.isEqualTo("service provider display name");
		assertThat(properties.getCatalog().getServices().get(0).getMetadata().getDocumentationUrl())
				.isEqualTo("service-documentation-url");
		assertThat(properties.getCatalog().getServices().get(0).getMetadata().getSupportUrl())
				.isEqualTo("service-support-url");
		assertThat(properties.getCatalog().getServices().get(0).getMetadata().getProperties())
				.containsOnly(entry("key1", "value1"), entry("key2", "value2"));
		assertThat(properties.getCatalog().getServices().get(0).getRequires())
				.containsOnly("syslog_drain", "route_forwarding");
		assertThat(properties.getCatalog().getServices().get(0).getTags()).containsOnly("tag1", "tag2");
		assertThat(properties.getCatalog().getServices().get(0).getDashboardClient().getId()).isEqualTo("dashboard-id");
		assertThat(properties.getCatalog().getServices().get(0).getDashboardClient().getSecret())
				.isEqualTo("dashboard-secret");
		assertThat(properties.getCatalog().getServices().get(0).getDashboardClient().getRedirectUri())
				.isEqualTo("dashboard-redirect-uri");
		assertThat(properties.getCatalog().getServices().get(0).getPlans()).hasSize(2);
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(0).getId()).isEqualTo("plan-one-id");
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(0).getName()).isEqualTo("Plan One");
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(0).getDescription())
				.isEqualTo("Description for Plan One");
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(0).getMaintenanceInfo().getVersion())
				.isEqualTo("1.0.1");
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(0).getMaintenanceInfo().getDescription())
				.isEqualTo("Description for maintenance info");
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).getId()).isEqualTo("plan-two-id");
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).getName()).isEqualTo("Plan Two");
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).getDescription())
				.isEqualTo("Description for Plan Two");
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).getMetadata().getProperties())
				.containsOnly(entry("key1", "value1"), entry("key2", "value2"));
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).getMetadata().getBullets())
				.contains("bullet1", "bullet2");
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).getMetadata().getCosts()).hasSize(1);
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).getMetadata().getCosts().get(0)
				.getAmount().get("usd")).isEqualTo(649.0d);
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).getMetadata().getCosts().get(0)
				.getUnit()).isEqualTo("MONTHLY");
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).isBindable()).isTrue();
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).isFree()).isTrue();
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).isPlanUpdateable()).isTrue();
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).getSchemas().getServiceInstance()
				.getCreate().getParameters())
				.contains(entry("$schema", "http://json-schema.org/draft-04/schema#"), entry("type", "string"));
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).getSchemas().getServiceInstance()
				.getCreate().getParameters().get("enum")).isInstanceOf(Map.class);
		@SuppressWarnings("unchecked") Map<String, String> enumMap = (Map<String, String>) properties.getCatalog()
				.getServices().get(0).getPlans().get(1).getSchemas().getServiceInstance().getCreate().getParameters()
				.get("enum");
		assertThat(enumMap).containsOnly(entry("0", "one"), entry("1", "two"), entry("2", "three"));
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).getSchemas().getServiceInstance()
				.getUpdate().getParameters())
				.containsOnly(entry("$schema", "http://json-schema.org/draft-04/schema#"), entry("type", "object"));
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).getSchemas().getServiceBinding()
				.getCreate().getParameters())
				.containsOnly(entry("$schema", "http://json-schema.org/draft-04/schema#"), entry("type", "object"));
		assertThat(properties.getCatalog().getServices().get(0).getPlans().get(1).getMaximumPollingDuration())
				.isEqualTo(200);
		assertThat(properties.getCatalog().getServices().get(1).getId()).isEqualTo("service-two-id");
		assertThat(properties.getCatalog().getServices().get(1).getName()).isEqualTo("Service Two");
		assertThat(properties.getCatalog().getServices().get(1).getDescription())
				.isEqualTo("Description for Service Two");
		assertThat(properties.getCatalog().getServices().get(1).getPlans()).hasSize(1);
		assertThat(properties.getCatalog().getServices().get(1).getPlans().get(0).getId()).isEqualTo("plan-one-id");
		assertThat(properties.getCatalog().getServices().get(1).getPlans().get(0).getName()).isEqualTo("Plan One");
		assertThat(properties.getCatalog().getServices().get(1).getPlans().get(0).getDescription())
				.isEqualTo("Description for Plan One");
	}

	private void validateCatalogModel(Catalog catalog) {
		assertThat(catalog.getServiceDefinitions().get(0).getId()).isEqualTo("service-one-id");
		assertThat(catalog.getServiceDefinitions().get(0).getName()).isEqualTo("Service One");
		assertThat(catalog.getServiceDefinitions().get(0).getDescription()).isEqualTo("Description for Service One");
		assertThat(catalog.getServiceDefinitions().get(0).isBindable()).isTrue();
		assertThat(catalog.getServiceDefinitions().get(0).isBindingsRetrievable()).isTrue();
		assertThat(catalog.getServiceDefinitions().get(0).isAllowContextUpdates()).isFalse();
		assertThat(catalog.getServiceDefinitions().get(0).isInstancesRetrievable()).isTrue();
		assertThat(catalog.getServiceDefinitions().get(0).isPlanUpdateable()).isTrue();
		assertThat(catalog.getServiceDefinitions().get(0).getMetadata())
				.contains(entry("key1", "value1"), entry("key2", "value2"));
		assertThat(catalog.getServiceDefinitions().get(0).getMetadata().get("displayName"))
				.isEqualTo("service display name");
		assertThat(catalog.getServiceDefinitions().get(0).getMetadata().get("imageUrl")).isEqualTo("image-uri");
		assertThat(catalog.getServiceDefinitions().get(0).getMetadata().get("longDescription"))
				.isEqualTo("service long description");
		assertThat(catalog.getServiceDefinitions().get(0).getMetadata().get("providerDisplayName"))
				.isEqualTo("service provider display name");
		assertThat(catalog.getServiceDefinitions().get(0).getMetadata().get("documentationUrl"))
				.isEqualTo("service-documentation-url");
		assertThat(catalog.getServiceDefinitions().get(0).getMetadata().get("supportUrl"))
				.isEqualTo("service-support-url");
		assertThat(catalog.getServiceDefinitions().get(0).getRequires())
				.containsOnly("syslog_drain", "route_forwarding");
		assertThat(catalog.getServiceDefinitions().get(0).getTags()).containsOnly("tag1", "tag2");
		assertThat(catalog.getServiceDefinitions().get(0).getDashboardClient().getId()).isEqualTo("dashboard-id");
		assertThat(catalog.getServiceDefinitions().get(0).getDashboardClient().getSecret())
				.isEqualTo("dashboard-secret");
		assertThat(catalog.getServiceDefinitions().get(0).getDashboardClient().getRedirectUri())
				.isEqualTo("dashboard-redirect-uri");
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(0).getId()).isEqualTo("plan-one-id");
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(0).getName()).isEqualTo("Plan One");
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(0).getDescription())
				.isEqualTo("Description for Plan One");
		assertThat(
				catalog.getServiceDefinitions().get(0).getPlans().get(0).getMaintenanceInfo().getVersion())
				.isEqualTo("1.0.1");
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(0).getMaintenanceInfo().getDescription())
				.isEqualTo("Description for maintenance info");
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).getId()).isEqualTo("plan-two-id");
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).getName()).isEqualTo("Plan Two");
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).getDescription())
				.isEqualTo("Description for Plan Two");
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).getMetadata())
				.contains(entry("key1", "value1"), entry("key2", "value2"),
						entry("displayName", "sample display name"));
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).getMetadata().get("costs")).isNotNull();
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).getMetadata().get("bullets")).isNotNull();
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).getMetadata())
				.contains(entry("displayName", "sample display name"),
						entry("bullets", Arrays.asList("bullet1", "bullet2")), entry("key1", "value1"),
						entry("key2", "value2"));
		@SuppressWarnings("unchecked")
		Map<String, Object> cost =
				((List<Map<String, Object>>) catalog.getServiceDefinitions().get(0).getPlans().get(1).getMetadata()
						.get("costs")).get(0);
		assertThat(cost.get("unit")).isEqualTo("MONTHLY");
		@SuppressWarnings("unchecked")
		Map<String, Object> amount = (Map<String, Object>) cost.get("amount");
		assertThat(amount).containsOnly(entry("usd", 649.0));
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).isBindable()).isTrue();
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).isFree()).isTrue();
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).isPlanUpdateable()).isTrue();
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).getSchemas().getServiceInstanceSchema()
				.getCreateMethodSchema().getParameters())
				.contains(entry("$schema", "http://json-schema.org/draft-04/schema#"), entry("type", "string"));
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).getSchemas().getServiceInstanceSchema()
				.getCreateMethodSchema().getParameters().get("enum")).isInstanceOf(List.class);
		@SuppressWarnings("unchecked")
		List<String> enumList = (List<String>) catalog.getServiceDefinitions().get(0).getPlans().get(1).getSchemas()
				.getServiceInstanceSchema().getCreateMethodSchema().getParameters().get("enum");
		assertThat(enumList).containsOnly("one", "two", "three");
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).getSchemas().getServiceInstanceSchema()
				.getUpdateMethodSchema().getParameters())
				.containsOnly(entry("$schema", "http://json-schema.org/draft-04/schema#"), entry("type", "object"));
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).getSchemas().getServiceBindingSchema()
				.getCreateMethodSchema().getParameters())
				.containsOnly(entry("$schema", "http://json-schema.org/draft-04/schema#"), entry("type", "object"));
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().get(1).getMaximumPollingDuration()).isEqualTo(200);
		assertThat(catalog.getServiceDefinitions().get(1).getId()).isEqualTo("service-two-id");
		assertThat(catalog.getServiceDefinitions().get(1).getName()).isEqualTo("Service Two");
		assertThat(catalog.getServiceDefinitions().get(1).getDescription()).isEqualTo("Description for Service Two");
		assertThat(catalog.getServiceDefinitions().get(1).getPlans().get(0).getId()).isEqualTo("plan-one-id");
		assertThat(catalog.getServiceDefinitions().get(1).getPlans().get(0).getName()).isEqualTo("Plan One");
		assertThat(catalog.getServiceDefinitions().get(1).getPlans().get(0).getDescription())
				.isEqualTo("Description for Plan One");
	}

	private ServiceBrokerProperties bindProperties() {
		ConfigurationPropertySource source = new MapConfigurationPropertySource(this.map);
		Binder binder = new Binder(source);
		ServiceBrokerProperties serviceBrokerProperties = new ServiceBrokerProperties();
		binder.bind(PREFIX, Bindable.ofInstance(serviceBrokerProperties));
		return serviceBrokerProperties;
	}

}
