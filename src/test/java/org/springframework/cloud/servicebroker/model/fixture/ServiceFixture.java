package org.springframework.cloud.servicebroker.model.fixture;

import java.util.Arrays;

import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.model.ServiceDefinitionRequires;

public class ServiceFixture {

	public static ServiceDefinition getSimpleService() {
		return new ServiceDefinition(
				"service-one-id", 
				"Service One", 
				"Description for Service One", 
				true,
				PlanFixture.getAllPlans());
	}

	public static ServiceDefinition getServiceWithRequires() {
		return new ServiceDefinition(
				"service-one-id",
				"Service One",
				"Description for Service One",
				true,
				true,
				PlanFixture.getAllPlans(),
				null,
				null,
				Arrays.asList(
						ServiceDefinitionRequires.SERVICE_REQUIRES_SYSLOG_DRAIN.toString(),
						ServiceDefinitionRequires.SERVICE_REQUIRES_ROUTE_FORWARDING.toString()
				),
				null);
	}

}
