package org.springframework.cloud.servicebroker.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.context.embedded.AbstractEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerException;
import org.springframework.boot.context.embedded.ServletContextInitializer;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Mock {@link org.springframework.boot.context.embedded.EmbeddedServletContainerFactory}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class MockEmbeddedServletContainerFactory
		extends AbstractEmbeddedServletContainerFactory {

	private MockEmbeddedServletContainer container;

	@Override
	public EmbeddedServletContainer getEmbeddedServletContainer(
			ServletContextInitializer... initializers) {
		this.container = spy(new MockEmbeddedServletContainer(
				mergeInitializers(initializers), getPort()));
		return this.container;
	}

	public MockEmbeddedServletContainer getContainer() {
		return this.container;
	}

	public ServletContext getServletContext() {
		return getContainer() == null ? null : getContainer().servletContext;
	}

	public RegisteredServlet getRegisteredServlet(int index) {
		return getContainer() == null ? null
				: getContainer().getRegisteredServlets().get(index);
	}

	public RegisteredFilter getRegisteredFilter(int index) {
		return getContainer() == null ? null
				: getContainer().getRegisteredFilters().get(index);
	}

	public static class MockEmbeddedServletContainer implements EmbeddedServletContainer {

		private ServletContext servletContext;

		private final ServletContextInitializer[] initializers;

		private final List<RegisteredServlet> registeredServlets = new ArrayList<RegisteredServlet>();

		private final List<RegisteredFilter> registeredFilters = new ArrayList<RegisteredFilter>();

		private final int port;

		private boolean stopped = false;

		public MockEmbeddedServletContainer(ServletContextInitializer[] initializers,
				int port) {
			this.initializers = initializers;
			this.port = port;
			initialize();
		}

		private void initialize() {
			try {
				this.servletContext = mock(ServletContext.class);
				given(this.servletContext.addServlet(anyString(), (Servlet) anyObject()))
						.willAnswer(new Answer<ServletRegistration.Dynamic>() {
							@Override
							public ServletRegistration.Dynamic answer(
									InvocationOnMock invocation) throws Throwable {
								RegisteredServlet registeredServlet = new RegisteredServlet(
										(Servlet) invocation.getArguments()[1]);
								MockEmbeddedServletContainer.this.registeredServlets
										.add(registeredServlet);
								return registeredServlet.getRegistration();
							}
						});
				given(this.servletContext.addFilter(anyString(), (Filter) anyObject()))
						.willAnswer(new Answer<FilterRegistration.Dynamic>() {
							@Override
							public FilterRegistration.Dynamic answer(
									InvocationOnMock invocation) throws Throwable {
								RegisteredFilter registeredFilter = new RegisteredFilter(
										(Filter) invocation.getArguments()[1]);
								MockEmbeddedServletContainer.this.registeredFilters
										.add(registeredFilter);
								return registeredFilter.getRegistration();
							}
						});
				final Map<String, String> initParameters = new HashMap<String, String>();
				given(this.servletContext.setInitParameter(anyString(), anyString()))
						.will(new Answer<Void>() {
							@Override
							public Void answer(InvocationOnMock invocation)
									throws Throwable {
								initParameters.put(
										invocation.getArgumentAt(0, String.class),
										invocation.getArgumentAt(1, String.class));
								return null;
							}

						});
				given(this.servletContext.getInitParameterNames())
						.willReturn(Collections.enumeration(initParameters.keySet()));
				given(this.servletContext.getInitParameter(anyString()))
						.willAnswer(new Answer<String>() {
							@Override
							public String answer(InvocationOnMock invocation)
									throws Throwable {
								return initParameters
										.get(invocation.getArgumentAt(0, String.class));
							}
						});
				given(this.servletContext.getAttributeNames()).willReturn(
						MockEmbeddedServletContainer.<String> emptyEnumeration());
				given(this.servletContext.getNamedDispatcher("default"))
						.willReturn(mock(RequestDispatcher.class));
				for (ServletContextInitializer initializer : this.initializers) {
					initializer.onStartup(this.servletContext);
				}
			}
			catch (ServletException ex) {
				throw new RuntimeException(ex);
			}
		}

		@SuppressWarnings("unchecked")
		public static <T> Enumeration<T> emptyEnumeration() {
			return (Enumeration<T>) EmptyEnumeration.EMPTY_ENUMERATION;
		}

		private static class EmptyEnumeration<E> implements Enumeration<E> {
			static final EmptyEnumeration<Object> EMPTY_ENUMERATION = new EmptyEnumeration<Object>();

			@Override
			public boolean hasMoreElements() {
				return false;
			}

			@Override
			public E nextElement() {
				throw new NoSuchElementException();
			}
		}

		@Override
		public void start() throws EmbeddedServletContainerException {
		}

		@Override
		public void stop() {
			this.servletContext = null;
			this.registeredServlets.clear();
			this.stopped = true;
		}

		public boolean isStopped() {
			return this.stopped;
		}

		public Servlet[] getServlets() {
			Servlet[] servlets = new Servlet[this.registeredServlets.size()];
			for (int i = 0; i < servlets.length; i++) {
				servlets[i] = this.registeredServlets.get(i).getServlet();
			}
			return servlets;
		}

		public List<RegisteredServlet> getRegisteredServlets() {
			return this.registeredServlets;
		}

		public List<RegisteredFilter> getRegisteredFilters() {
			return this.registeredFilters;
		}

		@Override
		public int getPort() {
			return this.port;
		}
	}

	public static class RegisteredServlet {

		private final Servlet servlet;

		private final ServletRegistration.Dynamic registration;

		public RegisteredServlet(Servlet servlet) {
			this.servlet = servlet;
			this.registration = mock(ServletRegistration.Dynamic.class);
		}

		public ServletRegistration.Dynamic getRegistration() {
			return this.registration;
		}

		public Servlet getServlet() {
			return this.servlet;
		}
	}

	public static class RegisteredFilter {

		private final Filter filter;

		private final FilterRegistration.Dynamic registration;

		public RegisteredFilter(Filter filter) {
			this.filter = filter;
			this.registration = mock(FilterRegistration.Dynamic.class);
		}

		public FilterRegistration.Dynamic getRegistration() {
			return this.registration;
		}

		public Filter getFilter() {
			return this.filter;
		}
	}
}