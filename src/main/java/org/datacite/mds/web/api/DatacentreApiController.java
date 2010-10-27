package org.datacite.mds.web.api;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.Datacentre;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/*")
@Controller
public class DatacentreApiController {

    Logger log4j = Logger.getLogger(DatacentreApiController.class);
    
	@RequestMapping(value = "datacentre", method = RequestMethod.GET, headers = { "Accept=application/xml" })
	public ResponseEntity<? extends Object> get(@RequestParam String symbol) {
	    	    
	    return null;
	    
	}
	
	@RequestMapping(value = "datacentre", method = RequestMethod.PUT, headers = { "Content-Type=application/xml" })
	public ResponseEntity<? extends Object> createOrUpdate(@RequestBody @Valid Datacentre requestDataset,
			@RequestParam(required = false) Boolean testMode) {
	    
	    return null;
	    
	}
}
