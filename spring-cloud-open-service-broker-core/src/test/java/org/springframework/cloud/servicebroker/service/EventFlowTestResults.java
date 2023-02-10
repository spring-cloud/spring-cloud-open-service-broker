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

package org.springframework.cloud.servicebroker.service;

import reactor.core.publisher.Mono;

class EventFlowTestResults {

	private String beforeCreate;

	private String afterCreate;

	private String errorCreate;

	private String beforeDelete;

	private String afterDelete;

	private String errorDelete;

	private String beforeUpdate;

	private String afterUpdate;

	private String errorUpdate;

	private String beforeLastOperation;

	private String afterLastOperation;

	private String errorLastOperation;

	public String getBeforeCreate() {
		return beforeCreate;
	}

	public Mono<Void> setBeforeCreate(String s) {
		return Mono.fromCallable(() -> this.beforeCreate = s)
				.then();
	}

	public String getAfterCreate() {
		return afterCreate;
	}

	public Mono<Void> setAfterCreate(String s) {
		return Mono.fromCallable(() -> this.afterCreate = s)
				.then();
	}

	public String getErrorCreate() {
		return errorCreate;
	}

	public Mono<Void> setErrorCreate(String s) {
		return Mono.fromCallable(() -> this.errorCreate = s)
				.then();
	}

	public String getBeforeDelete() {
		return beforeDelete;
	}

	public Mono<Void> setBeforeDelete(String s) {
		return Mono.fromCallable(() -> this.beforeDelete = s)
				.then();
	}

	public String getAfterDelete() {
		return afterDelete;
	}

	public Mono<Void> setAfterDelete(String s) {
		return Mono.fromCallable(() -> this.afterDelete = s)
				.then();
	}

	public String getErrorDelete() {
		return errorDelete;
	}

	public Mono<Void> setErrorDelete(String s) {
		return Mono.fromCallable(() -> this.errorDelete = s)
				.then();
	}

	public String getBeforeUpdate() {
		return beforeUpdate;
	}

	public Mono<Void> setBeforeUpdate(String s) {
		return Mono.fromCallable(() -> this.beforeUpdate = s)
				.then();
	}

	public String getAfterUpdate() {
		return afterUpdate;
	}

	public Mono<Void> setAfterUpdate(String s) {
		return Mono.fromCallable(() -> this.afterUpdate = s)
				.then();
	}

	public String getErrorUpdate() {
		return errorUpdate;
	}

	public Mono<Void> setErrorUpdate(String s) {
		return Mono.fromCallable(() -> this.errorUpdate = s)
				.then();
	}

	public String getBeforeLastOperation() {
		return beforeLastOperation;
	}

	public Mono<Void> setBeforeLastOperation(String s) {
		return Mono.fromCallable(() -> this.beforeLastOperation = s)
				.then();
	}

	public String getAfterLastOperation() {
		return afterLastOperation;
	}

	public Mono<Void> setAfterLastOperation(String s) {
		return Mono.fromCallable(() -> this.afterLastOperation = s)
				.then();
	}

	public String getErrorLastOperation() {
		return errorLastOperation;
	}

	public Mono<Void> setErrorLastOperation(String s) {
		return Mono.fromCallable(() -> this.errorLastOperation = s)
				.then();
	}

}
