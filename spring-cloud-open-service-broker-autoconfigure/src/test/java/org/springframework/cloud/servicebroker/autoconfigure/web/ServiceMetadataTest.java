/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceMetadataTest {

	private static final String BASE64_ENCODED_IMAGE_DATA = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB" +
			"AAAAAQCAYAAAAf8/9hAAAB10lEQVQ4T32TyS8DYRiHEVcnXP0BEjcJQiSIGyci4SLR9GBJCIcKehCa2OJE4maP2GKLrTHBQR" +
			"BBbGkpKqWNTKmlVKumr/ed9Jt0vgy/5Elnpu/zm/lmiYriUj6cmoz0IGeIF3lG9pFGJC48U4DE82Is0o1ICPzBLZKCXCLGSD" +
			"kamdEQtPBVTeTS7wMSwwqaNQY1IfnafQYVo+m0n01yEuLnB7UwzBWB69UOlF6hjo61UoGRH+RpWiiBtYtxCErfskzZts3Tfw" +
			"IV0B1WCS2LpTB7PADnrj14//IoEksoJIHz9ZZmnVQgMrF+phCsj0f8vCqi1ykXBH785ASowMcK6Iz/xRfwwoZ1St6WsASdLy" +
			"q4YgWRa+Tz9OGCLnMVeD5Fef/xzUGOhQpGWAGti8+PFATBOg2VEzlwcCcox5fPh8npp4JsVtC3ZQB/0Cev795jg6XTQWiYLQ" +
			"TdaAbs3CwrMt3Ymsl8crLYi7RIBe2rOuhYr1Q9kbaVCrh7tiiy2+sC41KZfPlINCtINK3q7SEIyUNd5mr5UR46NuEj8Abi+w" +
			"McObZhcNcE+rFMkul7yVO+BYpNPMllBZ1m9VVwvCDFKpkF3TR842pxYAixh89EkhsREAOSEOn8AvxeddDQ853AAAAAAElFTk" +
			"SuQmCC";

	private ServiceMetadata metadata;

	@BeforeEach
	void setUp() {
		this.metadata = new ServiceMetadata();
	}

	@Test
	void imageUrlResourceMissing() {
		metadata.setImageUrlResource("missing.png");
		Map<String, Object> model = metadata.toModel();
		assertThat(model.get("imageUrl")).isNull();
	}

	@Test
	void imageUrlPriorityOverResource() {
		metadata.setImageUrl("image-url");
		metadata.setImageUrlResource("missing.png");
		Map<String, Object> model = metadata.toModel();
		String actual = (String) model.get("imageUrl");
		assertThat(actual).isEqualTo("image-url");
	}

	@Test
	void imageUrlResource() throws Exception {
		metadata.setImageUrlResource("spring-logo.png");
		Map<String, Object> model = metadata.toModel();
		String actual = (String) model.get("imageUrl");
		assertThat(actual).isEqualTo(BASE64_ENCODED_IMAGE_DATA);
	}

}
