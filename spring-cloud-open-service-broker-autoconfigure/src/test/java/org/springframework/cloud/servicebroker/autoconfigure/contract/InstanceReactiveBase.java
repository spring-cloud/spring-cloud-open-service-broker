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
package org.springframework.cloud.servicebroker.autoconfigure.contract;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.servicebroker.autoconfigure.web.ServiceBrokerAutoConfiguration;
import org.springframework.cloud.servicebroker.autoconfigure.web.fixture.ServiceFixture;
import org.springframework.cloud.servicebroker.autoconfigure.web.reactive.ServiceBrokerWebFluxAutoConfiguration;
import org.springframework.cloud.servicebroker.autoconfigure.web.TestServiceInstanceService;
import org.springframework.cloud.servicebroker.autoconfigure.web.servlet.ServiceBrokerWebMvcAutoConfiguration;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InstanceReactiveBase.TestApplication.class,
		properties = "spring.main.web-application-type=reactive",
		webEnvironment = RANDOM_PORT)
public class InstanceReactiveBase {

	@LocalServerPort
	int port;

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = this.port;
	}

	@Test
	public void contextLoads() {
		// hooray
	}

	@SpringBootApplication(scanBasePackageClasses = {
			ServiceBrokerAutoConfiguration.class,
			ServiceBrokerWebFluxAutoConfiguration.class }, exclude = ServiceBrokerWebMvcAutoConfiguration.class)
	public static class TestApplication {

		@Bean
		public Catalog catalog() {
			return Catalog.builder()
					.serviceDefinitions(ServiceFixture.getSimpleService())
					.build();
		}

		@Bean
		public ServiceInstanceService serviceInstanceService() {
			return new TestServiceInstanceService();
		}

		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}

	}

}
