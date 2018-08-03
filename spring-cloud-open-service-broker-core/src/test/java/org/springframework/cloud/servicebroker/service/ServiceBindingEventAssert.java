/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.service;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.internal.Objects;
import org.springframework.cloud.servicebroker.event.binding.CreateServiceInstanceBindingCompletedEvent;
import org.springframework.cloud.servicebroker.event.binding.CreateServiceInstanceBindingEvent;
import org.springframework.cloud.servicebroker.event.binding.DeleteServiceInstanceBindingCompletedEvent;
import org.springframework.cloud.servicebroker.event.binding.DeleteServiceInstanceBindingEvent;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.context.ApplicationEvent;

public class ServiceBindingEventAssert extends AbstractAssert<ServiceBindingEventAssert, ApplicationEvent> {

	private final Objects objects = Objects.instance();

	public ServiceBindingEventAssert(ApplicationEvent actual) {
		super(actual, ServiceBindingEventAssert.class);
	}

	public static ServiceBindingEventAssert assertThat(ApplicationEvent actual) {
		return new ServiceBindingEventAssert(actual);
	}

	public ServiceBindingEventAssert isCreateInstanceBindingEvent(CreateServiceInstanceBindingRequest expectedRequest) {
		isNotNull();

		isInstanceOf(CreateServiceInstanceBindingEvent.class);

		CreateServiceInstanceBindingEvent actualEvent = (CreateServiceInstanceBindingEvent) this.actual;

		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);

		return this;
	}

	public ServiceBindingEventAssert isCreateInstanceBindingCompletedEvent(
			CreateServiceInstanceBindingRequest expectedRequest,
			CreateServiceInstanceBindingResponse expectedResponse) {
		isNotNull();

		isInstanceOf(CreateServiceInstanceBindingCompletedEvent.class);

		CreateServiceInstanceBindingCompletedEvent actualEvent = (CreateServiceInstanceBindingCompletedEvent) this.actual;

		objects.assertEqual(info, actualEvent.isSuccessful(), true);
		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);
		objects.assertEqual(info, actualEvent.getResponse(), expectedResponse);

		return this;
	}

	public ServiceBindingEventAssert isCreateInstanceBindingFailedEvent(
			CreateServiceInstanceBindingRequest expectedRequest,
			Throwable expectedException) {
		isNotNull();

		isInstanceOf(CreateServiceInstanceBindingCompletedEvent.class);

		CreateServiceInstanceBindingCompletedEvent actualEvent = (CreateServiceInstanceBindingCompletedEvent) this.actual;

		objects.assertEqual(info, actualEvent.isSuccessful(), false);
		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);
		objects.assertEqual(info, actualEvent.getException(), expectedException);

		return this;
	}

	public ServiceBindingEventAssert isDeleteInstanceBindingEvent(DeleteServiceInstanceBindingRequest expectedRequest) {
		isNotNull();

		isInstanceOf(DeleteServiceInstanceBindingEvent.class);

		DeleteServiceInstanceBindingEvent actualEvent = (DeleteServiceInstanceBindingEvent) this.actual;

		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);

		return this;
	}

	public ServiceBindingEventAssert isDeleteInstanceBindingCompletedEvent(
			DeleteServiceInstanceBindingRequest expectedRequest) {
		isNotNull();

		isInstanceOf(DeleteServiceInstanceBindingCompletedEvent.class);

		DeleteServiceInstanceBindingCompletedEvent actualEvent = (DeleteServiceInstanceBindingCompletedEvent) this.actual;

		objects.assertEqual(info, actualEvent.isSuccessful(), true);
		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);

		return this;
	}

	public ServiceBindingEventAssert isDeleteInstanceBindingFailedEvent(
			DeleteServiceInstanceBindingRequest expectedRequest,
			Throwable expectedException) {
		isNotNull();

		isInstanceOf(DeleteServiceInstanceBindingCompletedEvent.class);

		DeleteServiceInstanceBindingCompletedEvent actualEvent = (DeleteServiceInstanceBindingCompletedEvent) this.actual;

		objects.assertEqual(info, actualEvent.isSuccessful(), false);
		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);
		objects.assertEqual(info, actualEvent.getException(), expectedException);

		return this;
	}

}
