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

package org.springframework.cloud.servicebroker.service.events;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Abstract class for defining an event flow registry
 *
 * @author Roy Clarkson
 * @param <I> type of initialization flow
 * @param <C> type of completion flow
 * @param <E> type of error flow
 * @param <R> type of request
 * @param <S> type of response
 */
public abstract class EventFlowRegistry<I, C, E, R, S> {

	private final List<Mono<I>> initializationFlows = new ArrayList<>();

	private final List<Mono<C>> completionFlows = new ArrayList<>();

	private final List<Mono<E>> errorFlows = new ArrayList<>();

	/**
	 * Construct a new {@link EventFlowRegistry}
	 */
	@Deprecated
	public EventFlowRegistry() {
		// This constructor is intentionally empty to indicate it is deprecated
	}

	/**
	 * Construct a new {@link EventFlowRegistry}
	 *
	 * @param initializationFlows the initialization flows
	 * @param completionFlows the completion flows
	 * @param errorFlows the error flows
	 */
	protected EventFlowRegistry(List<I> initializationFlows, List<C> completionFlows, List<E> errorFlows) {
		if (CollectionUtils.isNotEmpty(initializationFlows)) {
			initializationFlows.forEach(flow -> this.initializationFlows.add(Mono.just(flow)));
		}
		if (CollectionUtils.isNotEmpty(completionFlows)) {
			completionFlows.forEach(flow -> this.completionFlows.add(Mono.just(flow)));
		}
		if (CollectionUtils.isNotEmpty(errorFlows)) {
			errorFlows.forEach(flow -> this.errorFlows.add(Mono.just(flow)));
		}
	}

	/**
	 * Add an initialization flow
	 *
	 * @param object the initialization flow
	 * @return an empty Mono
	 */
	public Mono<Void> addInitializationFlow(I object) {
		return Mono.justOrEmpty(object)
				.map(flow -> this.initializationFlows.add(Mono.just(flow)))
				.then();
	}

	/**
	 * Retrieve the initialization flows as a Flux
	 *
	 * @param request the service broker request
	 * @return a Flux of initialization flows
	 */
	public abstract Flux<Void> getInitializationFlows(R request);

	/**
	 * Merges the initialization flows into a Flux
	 *
	 * @return a Flux of initialization flows
	 */
	protected Flux<I> getInitializationFlowsInternal() {
		return Flux.merge(this.initializationFlows);
	}

	/**
	 * Add a completion flow
	 *
	 * @param object the completion flow
	 * @return an empty Mono
	 */
	public Mono<Void> addCompletionFlow(C object) {
		return Mono.justOrEmpty(object)
				.map(flow -> this.completionFlows.add(Mono.just(flow)))
				.then();
	}

	/**
	 * Retrieve the completion flows as a Flux
	 *
	 * @param request the service broker request
	 * @param response the service broker response
	 * @return a Flux of completion flows
	 */
	public abstract Flux<Void> getCompletionFlows(R request, S response);

	/**
	 * Merges the completion flows into a Flux
	 *
	 * @return a Flux of completion flows
	 */
	protected Flux<C> getCompletionFlowsInternal() {
		return Flux.merge(this.completionFlows);
	}

	/**
	 * Add an error flow
	 *
	 * @param object the error flow
	 * @return an empty Mono
	 */
	public Mono<Void> addErrorFlow(E object) {
		return Mono.justOrEmpty(object)
				.map(flow -> this.errorFlows.add(Mono.just(flow)))
				.then();
	}

	/**
	 * Retrieve the error flows as a Flux
	 *
	 * @param request the service broker request
	 * @param t the error
	 * @return a Flux of error flows
	 */
	public abstract Flux<Void> getErrorFlows(R request, Throwable t);

	/**
	 * Merges the error flows into a Flux
	 *
	 * @return a Flux of error flows
	 */
	protected Flux<E> getErrorFlowsInternal() {
		return Flux.merge(this.errorFlows);
	}

}
