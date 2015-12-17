package org.springframework.cloud.servicebroker.interceptor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.servicebroker.exception.ServiceBrokerApiVersionException;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BrokerApiVersionInterceptorTest {

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
		BrokerApiVersionInterceptor interceptor = new BrokerApiVersionInterceptor(null);
		assertTrue(interceptor.preHandle(request, response, null));
	}

	@Test
	public void anyVersionAccepted() throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = BrokerApiVersion.API_VERSION_ANY;
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersion()).thenReturn(version);
		when(request.getHeader(header)).thenReturn("version");
		
		BrokerApiVersionInterceptor interceptor = new BrokerApiVersionInterceptor(brokerApiVersion);
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

		BrokerApiVersionInterceptor interceptor = new BrokerApiVersionInterceptor(brokerApiVersion);
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
		
		BrokerApiVersionInterceptor interceptor = new BrokerApiVersionInterceptor(brokerApiVersion);
		interceptor.preHandle(request, response, null);
		verify(brokerApiVersion).getBrokerApiVersionHeader();
		verify(brokerApiVersion).getApiVersion();
	}
	
}
