package org.springframework.cloud.servicebroker.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class BrokerApiVersionInterceptor extends HandlerInterceptorAdapter {

	private final BrokerApiVersion version;

	public BrokerApiVersionInterceptor() {
		this(null);
	}

	public BrokerApiVersionInterceptor(BrokerApiVersion version) {
		this.version = version;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws ServiceBrokerApiVersionException {
		if (version != null && !anyVersionAllowed()) {
			String apiVersion = request.getHeader(version.getBrokerApiVersionHeader());
			if (!version.getApiVersion().equals(apiVersion)) {
				throw new ServiceBrokerApiVersionException(version.getApiVersion(), apiVersion);
			} 
		}
		return true;
	}

	private boolean anyVersionAllowed() {
		return BrokerApiVersion.API_VERSION_ANY.equals(version.getApiVersion());
	}

}
