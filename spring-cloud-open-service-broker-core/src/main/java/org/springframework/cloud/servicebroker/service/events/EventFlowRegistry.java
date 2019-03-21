/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.cloud.servicebroker.service.events;

import java.util.ArrayList;
import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Abstract class for defining an event flow registry
 *
 * @param <I> type of initialization flow
 * @param <C> type of completion flow
 * @param <E> type of error flow
 * @param <R> type of request
 * @param <S> type of response
 *
 * @author Roy Clarkson
 */
public abstract class EventFlowRegistry<I, C, E, R, S> {

	private final List<Mono<I>> initializationFlows = new ArrayList<>();

	private final List<Mono<C>> completionFlows = new ArrayList<>();

	private final List<Mono<E>> errorFlows = new ArrayList<>();

	public Mono<Void> addInitializationFlow(I object) {
		final Mono<I> mono = Mono.just(object);
		return Mono.fromCallable(() -> this.initializationFlows.add(mono))
				.then();
	}

	public abstract Flux<Void> getInitializationFlows(R request);

	Flux<I> getInitializationFlowsInternal() {
		return Flux.merge(this.initializationFlows);
	}

	public Mono<Void> addCompletionFlow(C object) {
		final Mono<C> mono = Mono.just(object);
		return Mono.fromCallable(() -> this.completionFlows.add(mono))
				.then();
	}

	public abstract Flux<Void> getCompletionFlows(R request, S response);

	Flux<C> getCompletionFlowsInternal() {
		return Flux.merge(this.completionFlows);
	}

	public Mono<Void> addErrorFlow(E object) {
		final Mono<E> mono = Mono.just(object);
		return Mono.fromCallable(() -> this.errorFlows.add(mono))
				.then();
	}

	public abstract Flux<Void> getErrorFlows(R request, Throwable t);

	Flux<E> getErrorFlowsInternal() {
		return Flux.merge(this.errorFlows);
	}

}
