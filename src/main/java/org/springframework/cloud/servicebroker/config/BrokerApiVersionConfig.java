package org.springframework.cloud.servicebroker.config;

import org.springframework.cloud.servicebroker.interceptor.BrokerApiVersionInterceptor;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerApiVersionConfig {
	@Bean
	public BrokerApiVersionInterceptor brokerApiVersionInterceptor(BrokerApiVersion brokerApiVersion) {
		return new BrokerApiVersionInterceptor(brokerApiVersion);
	}
}