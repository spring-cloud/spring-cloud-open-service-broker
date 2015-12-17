package org.springframework.cloud.servicebroker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * See: http://docs.cloudfoundry.org/services/api.html
 *
 * @author sgreenberg@pivotal.io
 * @author Scott Frederick
 */
@RestController
@Slf4j
public class CatalogController extends BaseController {
	@Autowired
	public CatalogController(CatalogService service) {
		super(service);
	}

	@RequestMapping(value = "/v2/catalog", method = RequestMethod.GET)
	public Catalog getCatalog() {
		log.debug("getCatalog()");
		return catalogService.getCatalog();
	}
}
