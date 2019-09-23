/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.autoconfigure.web.servlet;

import org.junit.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "spring.cloud.openservicebroker.base-path=/broker")
public class BasePathSimpleIntegrationTest extends AbstractBasePathWebApplicationIntegrationTest {

	@Test
	public void basePathFound() throws Exception {
		assertFound("/broker", null);
	}

	@Test
	public void basePathWithPlatformIdFound() throws Exception {
		assertFound("/broker/123", "123");
	}

	@Test
	public void noBasePathNotFound() throws Exception {
		assertNotFound("");
	}
	
	@Test
	public void alternativePathNotFound() throws Exception {
		assertNotFound("/123");
	}
	
	@Test
	public void basePathWithPrefixSegmentNotFound() throws Exception {
		assertNotFound("/api/broker");
	}

	@Test
	public void basePathWithPrefixAndSuffixSegmentsNotFound() throws Exception {
		assertNotFound("/api/broker/123");
	}

	@Test
	public void basePathWithPrefixAndMultipleSuffixSegmentsNotFound() throws Exception {
		assertNotFound("/api/broker/123/456");
	}

}
