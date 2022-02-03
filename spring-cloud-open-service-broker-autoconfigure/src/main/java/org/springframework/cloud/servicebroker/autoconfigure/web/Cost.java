/*
 * Copyright 2002-2022 the original author or authors.
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

package org.springframework.cloud.servicebroker.autoconfigure.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.CollectionUtils;

/**
 * Internal class for marshaling {@link ServiceBrokerProperties} configuration properties that describes a service
 * offered by this broker.
 *
 * @author Roy Clarkson
 * @see org.springframework.cloud.servicebroker.model.catalog.Plan
 */
public class Cost {

	private static final String AMOUNT_KEY = "amount";

	private static final String UNIT_KEY = "unit";

	/**
	 * An array of pricing in various currencies for the cost type as key-value pairs where key is currency code and
	 * value (as a float) is currency amount.
	 */
	private final Map<String, Double> amount = new HashMap<>();

	/**
	 * Display name for type of cost, e.g. Monthly, Hourly, Request, GB.
	 */
	private String unit;

	public Map<String, Double> getAmount() {
		return amount;
	}

	/**
	 * Set a single amount key-value pair
	 *
	 * @param amount currency code
	 * @param value currency amount
	 */
	public void setAmount(String amount, Double value) {
		if (amount != null && value != null) {
			this.amount.put(amount, value);
		}
	}

	public void setAmount(Map<String, Double> amount) {
		if (!CollectionUtils.isEmpty(amount)) {
			this.amount.putAll(amount);
		}
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * Converts this object into its corresponding model
	 *
	 * @return a Map model
	 * @see org.springframework.cloud.servicebroker.model.catalog.Plan
	 */
	public Map<String, Object> toModel() {
		Map<String, Object> model = new HashMap<>();
		if (!CollectionUtils.isEmpty(amount)) {
			model.put(AMOUNT_KEY, amount);
		}
		if (unit != null) {
			model.put(UNIT_KEY, unit);
		}
		return model;
	}

}
