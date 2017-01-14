package org.springframework.cloud.servicebroker.config;

import org.springframework.cloud.servicebroker.interceptor.ApiInfoLocationInterceptor;
import org.springframework.cloud.servicebroker.model.ApiInfoLocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

@Configuration
public class ApiInfoLocationConfiguration {
	@Bean
	@Scope(SCOPE_REQUEST)
	public ApiInfoLocation apiInfoLocation() {
		return new ApiInfoLocation();
	}

	@Bean
	public ApiInfoLocationInterceptor apiInfoLocationInterceptor(ApiInfoLocation apiInfoLocation) {
		return new ApiInfoLocationInterceptor(apiInfoLocation);
	}
}