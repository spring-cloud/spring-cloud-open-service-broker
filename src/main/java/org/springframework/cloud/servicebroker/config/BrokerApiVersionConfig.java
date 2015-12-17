package org.springframework.cloud.servicebroker.config;

import org.springframework.cloud.servicebroker.interceptor.BrokerApiVersionInterceptor;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class BrokerApiVersionConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private BrokerApiVersion brokerApiVersion;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new BrokerApiVersionInterceptor(brokerApiVersion)).addPathPatterns("/v2/**");
	}

}