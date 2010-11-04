package org.datacite.mds.web.api;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Allocator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RequestMapping("/*")
@Controller
public class DatacentreApiController {

    Logger log4j = Logger.getLogger(DatacentreApiController.class);
    
	@RequestMapping(value = "datacentre", method = RequestMethod.GET, headers = { "Accept=application/xml" })
	public ResponseEntity<? extends Object> get(@RequestParam String symbol) {

        Datacentre datacentre;
        try {
            datacentre = Datacentre.findDatacentresBySymbolEquals(symbol).getSingleResult();
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // check if datacentre belongs to logged in allocator
	    
	    return new ResponseEntity<Datacentre>(datacentre, new HttpHeaders(), HttpStatus.OK);
	    
	}
	
	@RequestMapping(value = "datacentre", method = RequestMethod.PUT, headers = { "Content-Type=application/xml" })
	public ResponseEntity<? extends Object> createOrUpdate(@RequestBody @Valid Datacentre requestDatacentre,
			@RequestParam(required = false) Boolean testMode) {

        // find logged in allocator
        Allocator allocator = null;
        
        requestDatacentre.setAllocator(allocator);
        
	    
	    return null;
	    
	}
}
