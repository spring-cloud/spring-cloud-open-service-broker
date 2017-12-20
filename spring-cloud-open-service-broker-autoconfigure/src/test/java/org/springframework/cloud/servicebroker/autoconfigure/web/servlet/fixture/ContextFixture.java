package org.springframework.cloud.servicebroker.autoconfigure.web.servlet.fixture;

import org.springframework.cloud.servicebroker.model.Context;

public class ContextFixture {
	public static Context getContext() {
		return Context.builder()
				.platform("sample")
				.property("property1", "value1")
				.property("property2", "value2")
				.build();
	}
}
