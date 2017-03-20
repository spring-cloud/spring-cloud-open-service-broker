package org.springframework.cloud.servicebroker.interceptor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.model.ServiceBrokerApiVersion;

public class ServiceBrokerApiVersionInterceptorTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private ServiceBrokerApiVersion brokerApiVersion;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void noBrokerApiVersionConfigured()
			throws IOException, ServletException, ServiceBrokerApiVersionException {
		ServiceBrokerApiVersionInterceptor interceptor = new ServiceBrokerApiVersionInterceptor(null);
		assertTrue(interceptor.preHandle(request, response, null));
	}

	@Test
	public void anyVersionAccepted()
			throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = ServiceBrokerApiVersion.API_VERSION_ANY;
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersion()).thenReturn(version);
		when(request.getHeader(header)).thenReturn("version");

		ServiceBrokerApiVersionInterceptor interceptor = new ServiceBrokerApiVersionInterceptor(
				brokerApiVersion);
		assertTrue(interceptor.preHandle(request, response, null));
		verify(brokerApiVersion, atLeastOnce()).getApiVersion();
	}

	@Test
	public void versionsMatch()
			throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = "version";
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersion()).thenReturn(version);
		when(request.getHeader(header)).thenReturn(version);

		ServiceBrokerApiVersionInterceptor interceptor = new ServiceBrokerApiVersionInterceptor(
				brokerApiVersion);
		assertTrue(interceptor.preHandle(request, response, null));
		verify(brokerApiVersion, atLeastOnce()).getApiVersion();
	}

	@Test(expected = ServiceBrokerApiVersionException.class)
	public void versionMismatch()
			throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = "version";
		String notVersion = "not_version";
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersion()).thenReturn(version);
		when(request.getHeader(header)).thenReturn(notVersion);

		ServiceBrokerApiVersionInterceptor interceptor = new ServiceBrokerApiVersionInterceptor(
				brokerApiVersion);
		interceptor.preHandle(request, response, null);
		verify(brokerApiVersion).getBrokerApiVersionHeader();
		verify(brokerApiVersion).getApiVersion();
	}

}
