package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApiVersionInterceptorTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private BrokerApiVersion brokerApiVersion;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void noBrokerApiVersionConfigured() throws IOException, ServletException, ServiceBrokerApiVersionException {
		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(null);
		assertTrue(interceptor.preHandle(request, response, null));
	}

	@Test
	public void anyVersionAccepted() throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = BrokerApiVersion.API_VERSION_ANY;
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersion()).thenReturn(version);
		when(request.getHeader(header)).thenReturn("version");

		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		assertTrue(interceptor.preHandle(request, response, null));
		verify(brokerApiVersion, atLeastOnce()).getApiVersion();
	}

	@Test
	public void versionsMatch() throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = "version";
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersion()).thenReturn(version);
		when(request.getHeader(header)).thenReturn(version);

		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		assertTrue(interceptor.preHandle(request, response, null));
		verify(brokerApiVersion, atLeastOnce()).getApiVersion();
	}

	@Test(expected = ServiceBrokerApiVersionException.class)
	public void versionMismatch() throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = "version";
		String notVersion = "not_version";
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersion()).thenReturn(version);
		when(request.getHeader(header)).thenReturn(notVersion);

		ApiVersionInterceptor interceptor = new ApiVersionInterceptor(brokerApiVersion);
		interceptor.preHandle(request, response, null);
		verify(brokerApiVersion).getBrokerApiVersionHeader();
		verify(brokerApiVersion).getApiVersion();
	}

}
