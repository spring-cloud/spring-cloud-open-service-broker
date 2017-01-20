package org.springframework.cloud.servicebroker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.interceptor.BrokerApiVersionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
	private static final String V2_API_PATH_PATTERN = "/v2/**";

	@Autowired
	private BrokerApiVersionInterceptor brokerApiVersionInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(brokerApiVersionInterceptor).addPathPatterns(V2_API_PATH_PATTERN);
	}
}