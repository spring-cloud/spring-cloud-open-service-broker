/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.servicebroker.controller;

import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * See: http://docs.cloudfoundry.org/services/api.html
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@RestController
@Slf4j
public class CatalogController extends BaseController {

	public CatalogController(CatalogService service) {
		super(service);
	}

	@RequestMapping(value = { "/v2/catalog",
			"{cfInstanceId}/v2/catalog" }, method = RequestMethod.GET)
	public Catalog getCatalog() {
		log.debug("getCatalog()");
		return getCatalogService().getCatalog();
	}
}
