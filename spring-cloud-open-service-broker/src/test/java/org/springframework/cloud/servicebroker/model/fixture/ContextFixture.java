package org.springframework.cloud.servicebroker.model.fixture;

import org.springframework.cloud.servicebroker.model.CloudFoundryContext;

class ContextFixture {
	static CloudFoundryContext getCloudFoundryContext() {
		return new CloudFoundryContext("org-guid-one", "space-guid-one");
	}
}
