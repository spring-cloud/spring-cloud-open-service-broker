package org.springframework.cloud.servicebroker.interceptor;

import org.springframework.cloud.servicebroker.model.ApiInfoLocation;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiInfoLocationInterceptor extends HandlerInterceptorAdapter {
	static final String API_INFO_LOCATION_HEADER = "X-Api-Info-Location";

	private final ApiInfoLocation apiInfoLocation;

	public ApiInfoLocationInterceptor(ApiInfoLocation apiInfoLocation) {
		this.apiInfoLocation = apiInfoLocation;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String location = request.getHeader(API_INFO_LOCATION_HEADER);
		apiInfoLocation.setLocation(location);
		return true;
	}
}
