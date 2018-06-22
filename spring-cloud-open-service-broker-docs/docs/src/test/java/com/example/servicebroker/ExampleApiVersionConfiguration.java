package com.example.servicebroker;

import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExampleApiVersionConfiguration {
	@Bean
	public BrokerApiVersion brokerApiVersion() {
		return new BrokerApiVersion("2.13");
	}
}
