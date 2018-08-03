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

import org.springframework.cloud.servicebroker.event.instance.CreateServiceInstanceCompletedEvent;
import org.springframework.cloud.servicebroker.event.instance.CreateServiceInstanceEvent;
import org.springframework.cloud.servicebroker.event.instance.DeleteServiceInstanceCompletedEvent;
import org.springframework.cloud.servicebroker.event.instance.DeleteServiceInstanceEvent;
import org.springframework.cloud.servicebroker.event.instance.UpdateServiceInstanceCompletedEvent;
import org.springframework.cloud.servicebroker.event.instance.UpdateServiceInstanceEvent;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.UpdateServiceInstanceResponse;
import org.springframework.context.ApplicationEvent;

public class ServiceInstanceEventAssert extends AbstractAssert<ServiceInstanceEventAssert, ApplicationEvent> {

	private final Objects objects = Objects.instance();

	public ServiceInstanceEventAssert(ApplicationEvent actual) {
		super(actual, ServiceInstanceEventAssert.class);
	}

	public static ServiceInstanceEventAssert assertThat(ApplicationEvent actual) {
		return new ServiceInstanceEventAssert(actual);
	}

	public ServiceInstanceEventAssert isCreateInstanceEvent(CreateServiceInstanceRequest expectedRequest) {
		isNotNull();

		isInstanceOf(CreateServiceInstanceEvent.class);

		CreateServiceInstanceEvent actualEvent = (CreateServiceInstanceEvent) this.actual;

		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);

		return this;
	}

	public ServiceInstanceEventAssert isCreateInstanceCompletedEvent(CreateServiceInstanceRequest expectedRequest,
																	 CreateServiceInstanceResponse expectedResponse) {
		isNotNull();

		isInstanceOf(CreateServiceInstanceCompletedEvent.class);

		CreateServiceInstanceCompletedEvent actualEvent = (CreateServiceInstanceCompletedEvent) this.actual;

		objects.assertEqual(info, actualEvent.isSuccessful(), true);
		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);
		objects.assertEqual(info, actualEvent.getResponse(), expectedResponse);

		return this;
	}

	public ServiceInstanceEventAssert isCreateInstanceFailedEvent(CreateServiceInstanceRequest expectedRequest,
																  Throwable expectedException) {
		isNotNull();

		isInstanceOf(CreateServiceInstanceCompletedEvent.class);

		CreateServiceInstanceCompletedEvent actualEvent = (CreateServiceInstanceCompletedEvent) this.actual;

		objects.assertEqual(info, actualEvent.isSuccessful(), false);
		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);
		objects.assertEqual(info, actualEvent.getException(), expectedException);

		return this;
	}

	public ServiceInstanceEventAssert isDeleteInstanceEvent(DeleteServiceInstanceRequest expectedRequest) {
		isNotNull();

		isInstanceOf(DeleteServiceInstanceEvent.class);

		DeleteServiceInstanceEvent actualEvent = (DeleteServiceInstanceEvent) this.actual;

		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);

		return this;
	}

	public ServiceInstanceEventAssert isDeleteInstanceCompletedEvent(DeleteServiceInstanceRequest expectedRequest,
																	 DeleteServiceInstanceResponse expectedResponse) {
		isNotNull();

		isInstanceOf(DeleteServiceInstanceCompletedEvent.class);

		DeleteServiceInstanceCompletedEvent actualEvent = (DeleteServiceInstanceCompletedEvent) this.actual;

		objects.assertEqual(info, actualEvent.isSuccessful(), true);
		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);
		objects.assertEqual(info, actualEvent.getResponse(), expectedResponse);

		return this;
	}

	public ServiceInstanceEventAssert isDeleteInstanceFailedEvent(DeleteServiceInstanceRequest expectedRequest,
																  Throwable expectedException) {
		isNotNull();

		isInstanceOf(DeleteServiceInstanceCompletedEvent.class);

		DeleteServiceInstanceCompletedEvent actualEvent = (DeleteServiceInstanceCompletedEvent) this.actual;

		objects.assertEqual(info, actualEvent.isSuccessful(), false);
		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);
		objects.assertEqual(info, actualEvent.getException(), expectedException);

		return this;
	}

	public ServiceInstanceEventAssert isUpdateInstanceEvent(UpdateServiceInstanceRequest expectedRequest) {
		isNotNull();

		isInstanceOf(UpdateServiceInstanceEvent.class);

		UpdateServiceInstanceEvent actualEvent = (UpdateServiceInstanceEvent) this.actual;

		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);

		return this;
	}

	public ServiceInstanceEventAssert isUpdateInstanceCompletedEvent(UpdateServiceInstanceRequest expectedRequest,
																	 UpdateServiceInstanceResponse expectedResponse) {
		isNotNull();

		isInstanceOf(UpdateServiceInstanceCompletedEvent.class);

		UpdateServiceInstanceCompletedEvent actualEvent = (UpdateServiceInstanceCompletedEvent) this.actual;

		objects.assertEqual(info, actualEvent.isSuccessful(), true);
		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);
		objects.assertEqual(info, actualEvent.getResponse(), expectedResponse);

		return this;
	}

	public ServiceInstanceEventAssert isUpdateInstanceFailedEvent(UpdateServiceInstanceRequest expectedRequest,
																  Throwable expectedException) {
		isNotNull();

		isInstanceOf(UpdateServiceInstanceCompletedEvent.class);

		UpdateServiceInstanceCompletedEvent actualEvent = (UpdateServiceInstanceCompletedEvent) this.actual;

		objects.assertEqual(info, actualEvent.isSuccessful(), false);
		objects.assertEqual(info, actualEvent.getRequest(), expectedRequest);
		objects.assertEqual(info, actualEvent.getException(), expectedException);

		return this;
	}
}
