package org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture;

import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.ServiceDefinitionRequires;

public class ServiceFixture {

	public static ServiceDefinition getSimpleService() {
		return ServiceDefinition.builder()
				.id("service-one-id")
				.name("Service One")
				.description("Description for Service One")
				.bindable(true)
				.plans(PlanFixture.getAllPlans())
				.build();
	}

	public static ServiceDefinition getServiceWithRequires() {
		return ServiceDefinition.builder()
				.id("service-one-id")
				.name("Service One")
				.description("Description for Service One")
				.bindable(true)
				.planUpdateable(true)
				.plans(PlanFixture.getAllPlans())
				.requires(ServiceDefinitionRequires.SERVICE_REQUIRES_SYSLOG_DRAIN.toString(),
						ServiceDefinitionRequires.SERVICE_REQUIRES_ROUTE_FORWARDING.toString())
				.build();
	}

}
