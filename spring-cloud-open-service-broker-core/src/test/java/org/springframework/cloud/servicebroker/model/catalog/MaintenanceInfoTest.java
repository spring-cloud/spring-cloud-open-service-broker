/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.model.catalog;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MaintenanceInfoTest {

	@Test
	void incorrectVersion() {
		assertThrows(IllegalArgumentException.class, () ->
				constructInfoAndValidate("157.2", "description"));
	}

	@Test
	void simpleVersion() {
		constructInfoAndValidate("1.1.1", "description");
	}

	@Test
	void simpleVersionDescriptionIsNull() {
		constructInfoAndValidate("1.1.1", null);
	}

	@Test
	void versionWithExtension() {
		constructInfoAndValidate("1.0.0-beta+exp.sha.5114f85", "description");
	}

	@Test
	void simpleVersionCompoundVersionConstructor() {
		constructInfoAndValidate(1, 1, 1, "", "description");
	}

	@Test
	void versionWithExtensionCompoundVersionConstructor() {
		constructInfoAndValidate(1, 0, 0, "-beta+exp.sha.5114f85", "description");
	}

	@Test
	void formatsToString() {
		MaintenanceInfo info = MaintenanceInfo.builder()
			.version("1.1.1")
			.description("a description")
			.build();
		String toString = info.toString();
		assertThat(toString).isEqualTo("MaintenanceInfo{version='1.1.1', description='a description'}");
	}

	private void constructInfoAndValidate(String version, String description) {
		MaintenanceInfo info = MaintenanceInfo.builder()
				.version(version)
				.description(description)
				.build();
		assertThat(info.getVersion()).isEqualTo(version);
		assertThat(info.getDescription()).isEqualTo(description);
	}

	private void constructInfoAndValidate(int major, int minor, int patch, String extension, String description) {
		final String version = major + "." + minor + "." + patch + extension;

		MaintenanceInfo info = MaintenanceInfo.builder()
				.version(major, minor, patch, extension)
				.description(description)
				.build();
		assertThat(info.getVersion()).isEqualTo(version);
		assertThat(info.getDescription()).isEqualTo(description);
	}

	@Test
	void equalsAndHashCode() {
		EqualsVerifier
				.simple()
				.forClass(MaintenanceInfo.class)
				.verify();
	}
}
