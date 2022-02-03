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

package org.springframework.cloud.servicebroker.acceptance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.context.annotation.Bean;

/**
 * NoOp Web MVC Open Service Broker
 *
 * @author Roy Clarkson
 */
@SpringBootApplication
public class WebMvcApplication {

	/**
	 * main application entry point
	 *
	 * @param args the args
	 */
	public static void main(final String[] args) {
		SpringApplication.run(WebMvcApplication.class, args);
	}

	/**
	 * NoOp ServiceInstanceService Bean
	 *
	 * @return the bean
	 */
	@Bean
	public ServiceInstanceService serviceInstanceService() {
		return new NoOpServiceInstanceService();
	}

}
