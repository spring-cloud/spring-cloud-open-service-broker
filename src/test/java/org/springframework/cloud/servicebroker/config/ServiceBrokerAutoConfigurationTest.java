package org.springframework.cloud.servicebroker.config;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizerBeanPostProcessor;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * Test for {@link ServiceBrokerAutoConfiguration}.
 *
 * @author Toshiaki Maki
 */
public class ServiceBrokerAutoConfigurationTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private AnnotationConfigEmbeddedWebApplicationContext context;

	private static Catalog dummyCatalog = new Catalog();

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void jsonFileDefaultLocation() throws Exception {
		load();
		ServiceBrokerProperties properties = this.context
				.getBean(ServiceBrokerProperties.class);
		assertThat(properties.getCatalogJson().getFilename(), is("catalog.json"));
		Catalog catalog = this.context.getBean(Catalog.class);
		assertThat(catalog.getServiceDefinitions().size(), is(1));
		assertThat(catalog.getServiceDefinitions().get(0).getName(),
				is("app-autoscaler"));
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().size(), is(2));
	}

	@Test
	public void jsonFileCustomLocation() throws Exception {
		load("servicebroker.catalog-json=classpath:mycatalog.json");
		ServiceBrokerProperties properties = this.context
				.getBean(ServiceBrokerProperties.class);
		assertThat(properties.getCatalogJson().getFilename(), is("mycatalog.json"));
		Catalog catalog = this.context.getBean(Catalog.class);
		assertThat(catalog.getServiceDefinitions().size(), is(1));
		assertThat(catalog.getServiceDefinitions().get(0).getName(), is("p-demo"));
		assertThat(catalog.getServiceDefinitions().get(0).getPlans().size(), is(1));
	}

	@Test
	public void jsonFileNotFoundLocation() throws Exception {
		thrown.expect(UnsatisfiedDependencyException.class);
		load("servicebroker.catalog-json=classpath:foo.json");
	}

	@Test
	public void overrideCatalog() throws Exception {
		load(DummyCatalogConfig.class);
		Catalog catalog = this.context.getBean(Catalog.class);
		assertThat(catalog, is(sameInstance(dummyCatalog)));
	}

	@Test
	public void overrideCatalogJsonFileNotFound() throws Exception {
		load(DummyCatalogConfig.class,
				"servicebroker.catalog-json=classpath:mycatalog.json");
		Catalog catalog = this.context.getBean(Catalog.class);
		assertThat(catalog, is(sameInstance(dummyCatalog)));
	}

	private void load(String... environment) {
		load(null, environment);
	}

	private void load(Class<?> config, String... environment) {
		this.context = new AnnotationConfigEmbeddedWebApplicationContext();
		EnvironmentTestUtils.addEnvironment(this.context, environment);
		List<Class<?>> configClasses = new ArrayList<>();
		if (config != null) {
			configClasses.add(config);
		}
		configClasses.addAll(Arrays.asList(Config.class, WebMvcAutoConfiguration.class,
				HttpMessageConvertersAutoConfiguration.class,
				PropertyPlaceholderAutoConfiguration.class,
				ServiceBrokerAutoConfiguration.class));
		this.context.register(configClasses.toArray(new Class<?>[configClasses.size()]));
		this.context.refresh();
	}

	public static class DummyCatalogConfig {
		@Bean
		public Catalog dummyCatalog() {
			return dummyCatalog;
		}
	}

	public static class Config {
		private static final MockEmbeddedServletContainerFactory containerFactory = new MockEmbeddedServletContainerFactory();

		@Bean
		public EmbeddedServletContainerFactory containerFactory() {
			return containerFactory;
		}

		@Bean
		public EmbeddedServletContainerCustomizerBeanPostProcessor embeddedServletContainerCustomizerBeanPostProcessor() {
			return new EmbeddedServletContainerCustomizerBeanPostProcessor();
		}

		@Bean
		public ServiceInstanceService serviceInstanceService() {
			return Mockito.mock(ServiceInstanceService.class);
		}

		@Bean
		public ServiceInstanceBindingService serviceInstanceBindingService() {
			return Mockito.mock(ServiceInstanceBindingService.class);
		}

	}
}