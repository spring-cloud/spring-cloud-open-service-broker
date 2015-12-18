
# Overview

This project provides a framework for building a [Spring Boot](http://projects.spring.io/spring-boot/) project to quickly implement a [service broker](http://docs.cloudfoundry.org/services/overview.html) for [Cloud Foundry](http://www.cloudfoundry.org).

This project replaces [Spring Boot CF Service Broker](https://github.com/cloudfoundry-community/spring-boot-cf-service-broker). 

## Compatibility

* [Service Broker API](http://docs.cloudfoundry.org/services/api.html): 2.8

# Getting Started

See the [Spring Boot documentation](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#getting-started-first-application) for getting started building a Spring Boot application.

A sample [MongoDB service broker](https://github.com/spring-cloud-samples/cloudfoundry-service-broker) project is available.

Add dependencies to your project's build file. 

Maven example: 

    <dependencies>
        ...
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-cloudfoundry-service-broker</artifactId>
            <version>${springCloudServiceBrokerVersion}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-cloudfoundry-service-broker</artifactId>
            <version>${springCloudServiceBrokerVersion}</version>
            <classifier>tests</classifier>
            <scope>test</scope>
        </dependency>
        ...
    </dependencies>

Gradle example: 

    dependencies {
        ...
        compile("org.springframework.cloud:spring-cloud-cloudfoundry-service-broker:${springCloudServiceBrokerVersion}")
        testCompile(group: "org.springframework.cloud", name: "spring-cloud-cloudfoundry-service-broker", version: "${springCloudServiceBrokerVersion}", classifier: "tests")
        ...
    }        

# Configuring the broker

The framework provides default implementations of most of the components needed to implement a service broker. In Spring Boot fashion, you can override the default behavior by providing your own implementation of Spring beans, and the framework will back away from its defaults.

To start, use the `@EnableAutoConfiguration` or `@SpringBootApplication` annotation on the broker's main application class:

    @ComponentScan
    @EnableAutoConfiguration
    public class Application {
        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
        }
    }

This will trigger the inclusion of the default configuration.

## Service beans

The Cloud Foundry service broker API has three main endpoint groupings: catalog management, service instance provisioning/deprovisioning, and service instance binding/unbinding. The broker will need to provide one Spring bean to provide the necessary functionality for each endpoint grouping.

For catalog management, the framework provides a default implementation that requires the broker to just provide an implementation of a [`Catalog` bean](src/main/java/org/springframework/cloud/servicebroker/model/Catalog.java). There is an example of this approach in the [MongoDB sample broker](https://github.com/spgreenberg/spring-boot-cf-service-broker-mongo/blob/master/src/main/java/org/springframework/cloud/servicebroker/mongodb/config/CatalogConfig.java). To override this default, provide your own bean that implements the [`CatalogService`](src/main/java/org/springframework/cloud/servicebroker/service/CatalogService.java) interface.

For service instance provisioning/deprovisioning, provide a Spring bean that implements the [`ServiceInstanceService`](src/main/java/org/springframework/cloud/servicebroker/service/ServiceInstanceService.java) interface. There is no default implementation provided.

For service instance binding/unbinding, provide a Spring bean that implements the [`ServiceInstanceBindingService`](src/main/java/org/springframework/cloud/servicebroker/service/ServiceInstanceBindingService.java) interface. There is no default implementation provided.

## Security

The project includes the [`spring-boot-starter-security`](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-starters/spring-boot-starter-security) project.  See the [Spring Boot Security documentation](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-security) for configuration options.

The default behavior creates a user called `user` with a generated password that is logged as an `INFO` message during app startup.  For example:

    2014-04-16T10:08:52.54-0600 [App/0]   OUT Using default password for application endpoints: 7c2969c1-d9c7-47e9-9c9e-2cd94a7b6cf1

If you are deploying your service broker to Cloud Foundry as an app, be aware the password is re-generated every time you push the application.  Therefore, you need to run `cf update-service-broker` with the new password after each push.

To see the generated password in the application logs on Cloud Foundry, use one of the following commands:

    $ cf logs <broker-app-name>
    $ cf logs --recent <broker-app-name>

## API version verification

By default, the framework will verify the version of the service broker API for each request it receives. To disable service broker API version header verification, provide a `BrokerApiVersion` bean that accepts any API version:

    @Bean
    public BrokerApiVersion brokerApiVersion() {
        return new BrokerApiVersion();
    }

# Deploying your broker

Follow the [documentation](http://docs.cloudfoundry.org/services/managing-service-brokers.html) to register the broker to Cloud Foundry.

## Build

The project is built with Gradle. The [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) allows you to build the project on multiple platforms and even if you do not have Gradle installed; run it in place of the `gradle` command (as `./gradlew`) from the root of the main project directory.

### To compile the project and run tests

    ./gradlew build

## Contribute

Before we accept a non-trivial patch or pull request we will need you to sign the [contributor's agreement](https://support.springsource.com/spring_committer_signup). Signing the contributor's agreement does not grant anyone commit rights to the main repository, but it does mean that we can accept your contributions, and you will get an author credit if we do.  Active contributors might be asked to join the core team, and given the ability to merge pull requests.

## License

Spring Cloud Connectors is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).

