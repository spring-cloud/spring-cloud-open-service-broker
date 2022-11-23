/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.servicebroker.contract;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.servicebroker.autoconfigure.web.ServiceBrokerAutoConfiguration;
import org.springframework.cloud.servicebroker.autoconfigure.web.TestServiceInstanceService;
import org.springframework.cloud.servicebroker.autoconfigure.web.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.autoconfigure.web.reactive.ServiceBrokerWebFluxAutoConfiguration;
import org.springframework.cloud.servicebroker.autoconfigure.web.servlet.ServiceBrokerWebMvcAutoConfiguration;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = InstanceServletBase.TestApplication.class,
		properties = "spring.main.web-application-type=servlet",
		webEnvironment = RANDOM_PORT)
public class InstanceServletBase {

	@LocalServerPort
	private int port;

	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = this.port;
	}

	@Test
	void contextLoads() {
		assertThat(context).isNotNull();
	}

	@SpringBootApplication(scanBasePackageClasses = {
			ServiceBrokerAutoConfiguration.class,
			ServiceBrokerWebMvcAutoConfiguration.class}, exclude = ServiceBrokerWebFluxAutoConfiguration.class)
	protected static class TestApplication {

		@Bean
		protected Catalog catalog() {
			return Catalog.builder()
					.serviceDefinitions(ServiceFixture.getSimpleService())
					.build();
		}

		@Bean
		protected ServiceInstanceService serviceInstanceService() {
			return new TestServiceInstanceService();
		}

		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}

	}

}
